package roll;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import basic.DiceRoll;
import basic.ListRoll;
import basic.TableRoll;
import util.AbsParser;
import util.Pair;

public class RollParser extends AbsParser<Rollable> {


	//TODO parse unabhängig von lineseperators machen
	//TODO isNextDiceRoll, isNextListRoll, isNextTableRoll, isNext...
	//TODO verbesserte parseException weitergabe
	//TODO new sytax, siehe rollsyntax.rpg
	//TODO rollables may be enclosed with ( ) to make clear what's what
	
	public RollParser(String chars) {
		super(chars.trim());
		// skipNextSpaces(); // go to the beginning
	}

	public static Pair<Rollable, String> tryParse(String input) throws ParseException {
		// will return null and throw exc
		// input = input.replace(" ", "").replace("\t", "");
		if (input == null || input.isEmpty())
			throw new ParseException("input may not be empty", 0);
		
		RollParser p = new RollParser(input);
		return new Pair<Rollable, String>( p.parse(), p.getRest());

	}

	@Override
	public Rollable parse() throws ParseException {
		//TODO maybe add isNext for all rollables...
		try {
			RollParser drP = new RollParser(getRest());
			DiceRoll dr = drP.parseDiceRoll();
			setRest(drP.getRest());
			return dr;
			
		} catch (ParseException e1) {
			try {
				RollParser lrP = new RollParser(getRest());
				ListRoll lr = lrP.parseListRoll();
				setRest(lrP.getRest());
				return lr;
				
			} catch (ParseException e2) {
				try {
					RollParser trP = new RollParser(getRest());
					TableRoll tr = trP.parseTableRoll();
					setRest(trP.getRest());
					return tr;
					
				} catch (ParseException e3) {
					try {
						RollParser rnP = new RollParser(getRest());
						RollName rn = rnP.parseRollName();
						setRest(rnP.getRest());
						return rn;
						
					} catch (ParseException e4) {
						return null;
					}

				}
			}
		}
	}
	
	public RollName parseRollName() throws ParseException {
		if (!isNextText())
			throw new ParseException("expecting text as a rollname", getOffset());
		
		String text = parseText();
		if (text.isEmpty())
			throw new ParseException("empty text", getOffset());
		
		return new RollName(text);
	}

	public DiceRoll parseDiceRoll() throws ParseException {

		final String name;
		final Integer n, die, dh, dl, mod;
		final boolean exploding;

		// 10d20! dh2 dl2 +5 Some Name

		n = isNextDigit() ? parseNatural() : 1;
		if (!isNextAnyOf('d', 'D'))
			throw new ParseException("expected d or D", getOffset());
		skip(1);
		die = parseNatural();

		if (isNext('!')) {
			skip(1);
			exploding = DiceRoll.ROLLTYPE_EXPLODING;
		} else
			exploding = DiceRoll.ROLLTYPE_NORMAL;

		skipNextWhitespaces();

		if (isNextSeq("dh")) {
			skip(2);
			dh = parseNatural();
		} else
			dh = 0;

		skipNextWhitespaces();

		if (isNextSeq("dl")) {
			skip(2);
			dl = parseNatural();
		} else
			dl = 0;

		skipNextWhitespaces();

		if (isNextAnyOf('+', '-') && isNextInt()) {
			mod = parseInteger();
		} else
			mod = 0;

		skipNextWhitespaces();

		if (isNextText())
			name = parseText();
		else
			name = "";

		// name = nextUntilIsNextAnySeqOf(";", ",", ":", ".", "<", ">", "[", "]", "(",
		// ")", System.lineSeparator());

		return new DiceRoll(n, die, dl, dh, mod, exploding, name);
	}

	public ListRoll parseListRoll() throws ParseException {

		Rollable[] rs;
		String name;

		if (isNextDigit()) {
			// 5[2d20]
			int n = parseNatural();
			if (isNext('[')) {
				skip(1);

				skipNextWhitespaces();

				Rollable rollable = parse();

				skipNextWhitespaces();

				if (!isNext(']'))
					throw new ParseException("no end of list detected", getOffset());
				skip(1);

				skipNextWhitespaces();

				rs = new Rollable[n];
				Arrays.fill(rs, rollable);
			} else
				throw new ParseException("no valid listroll", getOffset());

		} else if (isNext('[')) {
			// [2d20, 8d30 "Name", d4 "Test Name"]
			skip(1);

			ArrayList<Rollable> rolls = new ArrayList<>();

			while (true) {
				skipNextWhitespaces();

				Rollable rollables = parse();
				rolls.add(rollables);

				skipNextWhitespaces();

				if (isNext(']')) {
					skip(1); // list end
					break;
				} else if (isNext(',')) {
					skip(1);
					continue; // next roll
				} else
					throw new ParseException("unexpected char in list", getOffset());
			}

			rs = rolls.toArray(new Rollable[rolls.size()]);

		} else
			throw new ParseException("no valid listroll", getOffset());

		if (rs.length == 0)
			throw new ParseException("parsed empty list of rolls", getOffset());

		skipNextWhitespaces();

		if (isNextText())
			name = parseText();
		else
			name = "";

		// name = nextUntilIsNextAnySeqOf(";", ",", ":", ".", "<", ">", "[", "]", "(",
		// ")", System.lineSeparator());

		return new ListRoll(rs, name);
	}

	public TableRoll parseTableRoll() throws ParseException {
		// String name;
		DiceRoll tableroll;
		RollResult[] entries;

		if (!isNext('<'))
			throw new ParseException("expected < opening the table", getOffset());
		skip(1);

		skipNextWhitespaces();

		tableroll = parseDiceRoll();
		if (tableroll == null || tableroll.isExploding() || !tableroll.hasName())
			throw new ParseException("illegal table roll", getOffset());

		entries = new RollResult[tableroll.maxResult() - tableroll.minResult() + 1];
		skipNextWhitespaces();

		// parse entries:
		// inline table:
		// <d10 TestTableB; 1 Gold; 2 Nothing; 3-10 Some Shit>
		// lined table:
		// <d10 TestTable
		// 1 Gold
		// 2 Nothing
		// 3-10 Some Shit>

		do {

			if (isNext(';')) {
				skip(1);
				skipNextSpaces();
			}
			else if (isNextSeq(System.lineSeparator()))
				skip(System.lineSeparator().length());
			else
				throw new ParseException("expected entries opener/next entrie", getOffset());

			int lower, upper;
			//  incl,  incl
			
			skipNextWhitespaces();
			lower = parseNatural();

			if (lower < tableroll.minResult() || lower > tableroll.maxResult())
				throw new ParseException("lower value is out of bounds for the given table roll: " + lower,
						getOffset());

			skipNextWhitespaces();
			if (isNext('-')) {
				skip(1);
				skipNextWhitespaces();
				upper = parseNatural();
				if (upper < tableroll.minResult() || upper > tableroll.maxResult())
					throw new ParseException("upper value is out of bounds for the given table roll: " + upper,
							getOffset());
				skipNextWhitespaces();
			} else {
				upper = lower;
			}

			//parsing the entrie:
			ArrayList<Rollable> inlineRolls = new ArrayList<>();
			ArrayList<String> texts = new ArrayList<>();
			RollResult entrie;
			
			do {
				texts.add(nextUntilIsNextAnySeqOf(";", ">", "/", System.lineSeparator()));
					
				if (isNext('/')) {
					skip(1);
					Rollable inlineRoll = parse();
					if (inlineRoll == null)
						throw new ParseException("no parse for the in line roll", getOffset());
					
					inlineRolls.add(inlineRoll);
				}
					
			} while(!isNextAnySeqOf(";", ">", System.lineSeparator()));
			
			assert isNextAnySeqOf(";", ">", System.lineSeparator());
			
			entrie = new RollResult() {
				
				@Override
				public String simple() {
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < texts.size(); i++) {
						builder.append(texts.get(i));
						
						if (inlineRolls.size() > i)
							builder.append(inlineRolls.get(i).getRollMessage(SIMPLE));
						
					}	
					
					return builder.toString();
				}
				
				@Override
				public String plain() {
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < texts.size(); i++) {
						builder.append(texts.get(i));
						
						if (inlineRolls.size() > i) {
							builder.append('/');
							builder.append(inlineRolls.get(i).getInlineToString());							
						}
					}	
					
					return builder.toString();
				}
				
				@Override
				public String detailed() {
					return simple();
				}
			};
			
			if (texts.isEmpty() && inlineRolls.isEmpty())
				throw new ParseException("empty Entrie at: " + lower + "-" + upper, getOffset());

			for (int i = lower - tableroll.minResult(); i < upper; i++) {
				entries[i] = entrie;
			}

		} while (isNextAnySeqOf(";", System.lineSeparator()));

		if (!isNext('>'))
			throw new ParseException("expected > closing table syntax", getOffset());
		skip(1);

		for (int i = 0; i < entries.length; i++) {
			if (entries[i] == null)
				throw new ParseException("no entire for a roll of: " + (i + tableroll.minResult()), getOffset());
		}

		return new TableRoll(tableroll, entries);
	}

}
