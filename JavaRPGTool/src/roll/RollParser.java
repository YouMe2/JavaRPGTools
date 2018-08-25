package roll;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.swing.text.Position;

import basic.DiceRoll;
import basic.DiceRoll.DieRoll;
import basic.InlineRoll;
import basic.ListRoll;
import basic.NameRoll;
import basic.TableRoll;
import util.AbsParser;
import util.Pair;

public class RollParser extends AbsParser<Rollable> {

//TODO rollables may be enclosed with ( ) to make clear what's what !!
//TODO parseFromFile() -> liste von rollables ignoriert comments

	//TODO parse unabhängig von lineseperators machen
	//TODO isNextDiceRoll, isNextListRoll, isNextTableRoll, isNext...
	//TODO verbesserte parseException weitergabe
	
	
	
	public RollParser(String chars) {
		super(chars.trim());
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
						NameRoll rn = rnP.parseNameRoll();
						setRest(rnP.getRest());
						return rn;
						
					} catch (ParseException e4) {
						return null;
					}

				}
			}
		}
	}
	
	//done
	public NameRoll parseNameRoll() throws ParseException {
		try {
			NameRoll nr = new NameRoll(parseText());
			return nr;
		} catch (IllegalArgumentException e) {
			throw new ParseException(e.getMessage(), getOffset());
		}	
	}

	public DiceRoll parseDiceRoll() throws ParseException {

		//dieroll + ... + dieroll "Some Name"
		
		final String name;
		ArrayList<DieRoll> dice = new ArrayList<>();
		
		
		while (isNextDieRoll()) {		
			dice.add(parseDieRoll());
			skipNextSpaces();
		}
		
		if (isNextText())
			name = parseText();
		else
			name = "";

		return new DiceRoll(name, dice.toArray(new DieRoll[dice.size()]));
	}

	public ListRoll parseListRoll() throws ParseException {

		
		String name;
		
		if (isNextText())
			name = parseText();
		else
			name = "";
		
		skipNextSpaces();
		

		if (isNextDiceRoll()) {
			//diceroll[rollable]
			
			DiceRoll lengthroll = parseDiceRoll();
			
			if (!isNextSeq(ListRoll.OPENER))
				throw new ParseException("expected listopener: " + ListRoll.OPENER, getOffset());
			
			skip(ListRoll.OPENER.length());

			skipNextSpaces();

			Rollable rollable = parse();

			if (rollable == null)
				throw new ParseException("no parse for inner listed roll", getOffset());
			
			skipNextSpaces();

			if (!isNextSeq(ListRoll.CLOSER))
				throw new ParseException("expected listcloser: " + ListRoll.CLOSER, getOffset());
			
			skip(ListRoll.CLOSER.length());
			
			return new ListRoll(name, lengthroll, rollable);

		} else if (isNextSeq(ListRoll.OPENER)) {
			//[rollable, ...]
			
			skip(ListRoll.OPENER.length());
			
			ArrayList<Rollable> rolls = new ArrayList<>();

			do {
				skipNextSpaces();
				
				Rollable rollable = parse();
				if (rollable == null)
					throw new ParseException("no parse for inner listed roll", getOffset());
				
				skipNextSpaces();
				
				if (isNextSeq(ListRoll.SEPERATOR))
					skip(ListRoll.SEPERATOR.length());
				else if (isNextSeq(ListRoll.CLOSER)) {}
				else
					throw new ParseException("expected listseperator: " + ListRoll.SEPERATOR, getOffset());

			} while (! isNextSeq(ListRoll.CLOSER));
			
			if (!isNextSeq(ListRoll.CLOSER))
				throw new ParseException("expected listcloser: " + ListRoll.CLOSER, getOffset());

			return new ListRoll(name, rolls.toArray(new Rollable[rolls.size()]));
			
		} else
			throw new ParseException("expected listopener: " + ListRoll.OPENER, getOffset());

	}

	public TableRoll parseTableRoll() throws ParseException {
		
		DiceRoll tableroll;
		InlineRoll[] entries;

		if (!isNextSeq(TableRoll.OPENER))
			throw new ParseException("expected opener for the table: "+ TableRoll.OPENER, getOffset());
		skip(TableRoll.OPENER.length());

		skipNextSpaces();

		tableroll = parseDiceRoll();
		if (tableroll == null || tableroll.isExploding() || !tableroll.hasName())
			throw new ParseException("illegal table roll: " + tableroll, getOffset());

		entries = new InlineRoll[tableroll.getMaxResult() - tableroll.getMinResult() + 1];
		skipNextWhitespaces();

		// parse entries:
		// inline table:
		// <TestTableB d10; 1 Gold; 2 Nothing; 3-10 Some Shit>
		// lined table:
		// <TestTable d10 
		// 1 Gold
		// 2 Nothing
		// 3-10 Some Shit>

		do {

			if (isNextSeq(TableRoll.SEPERATOR)) {
				skip(TableRoll.SEPERATOR.length());
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

			if (lower < tableroll.getMinResult() || lower > tableroll.getMaxResult())
				throw new ParseException("lower value is out of bounds for the given table roll: " + lower, getOffset());

			skipNextWhitespaces();
			
			if (isNext('-')) {
				skip(1);
				skipNextWhitespaces();
				upper = parseNatural();
				if (upper < tableroll.getMinResult() || upper > tableroll.getMaxResult())
					throw new ParseException("upper value is out of bounds for the given table roll: " + upper, getOffset());
				skipNextWhitespaces();
			} else {
				upper = lower;
			}

			//parsing the entrie:
			InlineRoll ilr = parseInlineRoll();

			for (int i = lower - tableroll.getMinResult(); i < upper; i++) {
				entries[i] = ilr;
			}

		} while (isNextAnySeqOf(TableRoll.SEPERATOR, System.lineSeparator()));

		if (!isNextSeq(TableRoll.CLOSER))
			throw new ParseException("expected tableroll closer: "+TableRoll.CLOSER, getOffset());
		skip(TableRoll.CLOSER.length());

		for (int i = 0; i < entries.length; i++) {
			if (entries[i] == null)
				throw new ParseException("no entire for a roll of: " + (i + tableroll.getMinResult()), getOffset());
		}

		return new TableRoll(tableroll, entries);
	}

	public InlineRoll parseInlineRoll() throws ParseException {
		ArrayList<Rollable> rolls = new ArrayList<>();
		ArrayList<String> texts = new ArrayList<>();
		
		do {
			texts.add(nextUntilIsNextAnySeqOf(
					TableRoll.SEPERATOR,
					TableRoll.CLOSER,
					InlineRoll.OPENER,
					System.lineSeparator()));
				
			if (isNextSeq(InlineRoll.OPENER)) {
				skip(InlineRoll.OPENER.length());
				Rollable inlineRoll = parse();
				if (inlineRoll == null)
					throw new ParseException("no parse for the in lineroll", getOffset());
				
				if (isNextSeq(InlineRoll.CLOSER))
					skip(InlineRoll.CLOSER.length());
				else
					throw new ParseException("expected inline roll closed with: "+ InlineRoll.CLOSER, getOffset());
				rolls.add(inlineRoll);
			}
				
		} while(!isNextAnySeqOf(TableRoll.SEPERATOR, TableRoll.CLOSER, System.lineSeparator()));
		
		assert isNextAnySeqOf(TableRoll.SEPERATOR, TableRoll.CLOSER, System.lineSeparator());
		
		if (texts.isEmpty() && rolls.isEmpty())
			throw new ParseException("empty inlineroll", getOffset());
		
		return new InlineRoll(texts.toArray(new String[texts.size()]), rolls.toArray(new Rollable[rolls.size()]));
	}
	
	public DieRoll parseDieRoll() throws ParseException {

		final Integer n, die, dh, dl;
		final boolean exploding, positiv;

		//Syntax:
		//1d20 ! dh1 dl1
		//+ 1d20 ! dh1 dl1
		//- 12
		
		if (isNext('+')) {
			positiv = true;
			skip(1);
			skipNextSpaces();
		} else if (isNext('-')) {
			positiv = false;
			skip(1);
			skipNextSpaces();
		} else if (isNextDigit() || isNextAnyOf(DiceRoll.DIE)) {
			positiv = true;
			
		} else
			throw new ParseException("expected some number or some dice", getOffset());
		
		if (isNextDigit()) {
			n = parseNatural();
			skipNextSpaces();
		} else if (isNextAnyOf(DiceRoll.DIE)){
			n = 1;			
		} else
			throw new ParseException("expected some number or some dice", getOffset());
		
		
		if(isNextAnyOf(DiceRoll.DIE)) { //parse die		
			skip(1);
			
			die = parseNatural();
			
			if (die < 2)
				throw new ParseException("dice need to be atleast 2 sided", getOffset());
			
			skipNextSpaces();
			
			if (isNextSeq(DiceRoll.EXPLODING)) {
				skip(DiceRoll.EXPLODING.length());
				exploding = true;
			} else
				exploding = false;
			
			skipNextSpaces();
			
			if (isNextSeq(DiceRoll.DROPHIGHEST)) {
				skip(DiceRoll.DROPHIGHEST.length());
				dh = parseNatural();
				
				skipNextSpaces();
				
				if (isNextSeq(DiceRoll.DROPLOWEST)) {
					skip(DiceRoll.DROPLOWEST.length());
					dl = parseNatural();
				} else
					dl = 0;
				
			} else if (isNextSeq(DiceRoll.DROPLOWEST)) {
				skip(DiceRoll.DROPLOWEST.length());
				dl = parseNatural();
				
				skipNextSpaces();
				
				if (isNextSeq(DiceRoll.DROPHIGHEST)) {
					skip(DiceRoll.DROPHIGHEST.length());
					dh = parseNatural();
				} else
					dh = 0;
			} else {
				dl = 0;
				dh = 0;
			}
			
			DieRoll dr = new DieRoll(n, die, dl, dh, exploding, positiv);
			return dr;
		} else { //parse constant
			return new DieRoll(n, positiv);
		}
	}
	
//	private boolean isNextNameRoll() throws ParseException {
//		return isNextText();
//	}
//	
	private boolean isNextDiceRoll() throws ParseException {
		return isNextDieRoll();
	}
	
	private boolean isNextDieRoll() throws ParseException {		
		return isNextInt() || isNextAnyOf('d', 'D');
	}
	
	private boolean isNextListRoll() throws ParseException {
		return isNextDiceRoll() || isNextSeq(ListRoll.OPENER);

	}
	
	private boolean isNextTableRoll() throws ParseException {
		return isNextSeq(TableRoll.OPENER);

	}
//	
//	private void isNextInlineRoll() {
//		// TODO Auto-generated method stub
//
//	}
	
	
}
