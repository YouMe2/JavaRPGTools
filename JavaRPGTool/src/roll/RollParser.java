package roll;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import basic.DiceRoll;
import basic.DiceRoll.DieRoll;
import basic.ListRoll;
import basic.NameRoll;
import basic.TableRoll;
import basic.TextRoll;
import tools.RPGTools;
import util.AbsParser;
import util.Pair;

public class RollParser extends AbsParser<Rollable> {

	//TODO better ParseExceptionMessages
	
	public RollParser(String chars) {
		super(chars.trim());
	}

	public static Pair<Rollable, String> tryParse(String input) throws ParseException {
		// will return null and throw exc
		if (input == null || input.isEmpty())
			throw new ParseException("input may not be empty", 0);
		
		RollParser p = new RollParser(input);
		return new Pair<Rollable, String>( p.parse(), p.getRest());

	}

	@Override
	public Rollable parse() throws ParseException {

		if (isNextDiceRoll()) {
			return parseDiceRoll();
			
		} else if (isNextTableRoll()) {
			return parseTableRoll();
			
		} else if (isNextListRoll()) {
			return parseListRoll();
			
		} else if (isNextNameRoll()) {
			return parseNameRoll();
				
		} else if (isNextTextRoll()) {
			return parseTextRoll();
		} else
			throw new ParseException("No rollable parsable. For more information on the roll syntax see the \'" + RPGTools.SYNTAXFILE + "\' file.", getOffset());
		
	}
	
	public List<Rollable> parseAll() throws ParseException {
		List<Rollable> rolls = new ArrayList<>();
		
		skipNextSpaces(); 
		while (hasNext()) {
			rolls.add(parse());
			skipNextSpaces();	//skips comments
		}
		return rolls;
	}

	public NameRoll parseNameRoll() throws ParseException {
		
		skipExpectedNext(NameRoll.OPENER);
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
		skipExpectedNext(ListRoll.OPENER);
		skipNextSpaces();
		
		String name = null;
		DiceRoll lengthroll = null;
		ArrayList<Rollable> rolls = new ArrayList<>();
		
		if (isNextDiceRoll()) {	// lengthroll or listed roll
			
			DiceRoll roll = parseDiceRoll();
			
			if (isNextSeq(ListRoll.NAMESEPERATOR)) { //lengthroll
				skip(ListRoll.NAMESEPERATOR.length());
				lengthroll = roll;
				assert lengthroll != null;
			} else if (isNextSeq(ListRoll.SEPERATOR)) { //listedroll
				skip(ListRoll.SEPERATOR.length());
				rolls.add(roll);
			} else
				throw new ParseException("expecting a nameseperator or seperator: " + ListRoll.NAMESEPERATOR + " or " + ListRoll.SEPERATOR, getOffset());
			
		} else if (isNextText()) {	//only name
			name = parseText();
			
			skipNextSpaces();
			
			if (!isNextSeq(ListRoll.NAMESEPERATOR))
				throw new ParseException("expected name seperator", getOffset());
			skip(ListRoll.NAMESEPERATOR.length());
			
			assert name != null;
		} else
			throw new ParseException("expecting a name, a rollable lenghth or a listed rollable", getOffset());
		
		skipNextSpaces();
		
		while (! isNextSeq(ListRoll.CLOSER)){
			
			Rollable rollable = parse();
			if (rollable == null)
				throw new ParseException("no parse for inner listed roll: ", getOffset());
			rolls.add(rollable);
			
			skipNextSpaces();
			
			if (isNextSeq(ListRoll.SEPERATOR)) {
				skip(ListRoll.SEPERATOR.length());
				skipNextSpaces();
			} else if (isNextSeq(ListRoll.CLOSER)) {
//				break;
			} else
				throw new ParseException("expected listseperator or closer: " + ListRoll.SEPERATOR + " or "+ ListRoll.CLOSER, getOffset());

		}
		
		skipExpectedNext(ListRoll.CLOSER);
		
		
		if (lengthroll != null)
			return new ListRoll(lengthroll, rolls.toArray(new Rollable[rolls.size()]));
		else
			return new ListRoll(name, rolls.toArray(new Rollable[rolls.size()]));
		
	}
	
	public TableRoll parseTableRoll() throws ParseException {
		
		DiceRoll tableroll;
		TextRoll[] entries;

		skipExpectedNext(TableRoll.OPENER);

		skipNextSpaces();

		tableroll = parseDiceRoll();
		if (tableroll == null || tableroll.isExploding() || !tableroll.hasName())
			throw new ParseException("illegal table roll: " + tableroll, getOffset());

		entries = new TextRoll[tableroll.getMaxResult() - tableroll.getMinResult() + 1];

		// parse entries:
		// inline table:
		// <TestTableB d10; 1 Gold; 2 Nothing; 3-10 Some Shit>
		// lined table:
		// <TestTable d10 
		// 1 Gold
		// 2 Nothing
		// 3-10 Some Shit>

		do {
			skipNextWhitespaces();
			if (isNextCommentLine()) {
				skipNextComments();
				skipNextSpaces();
			} else if (isNextSeq(TableRoll.SEPERATOR)) {
				skip(TableRoll.SEPERATOR.length());
				skipNextSpaces();
			} else if (isNextLineSeperator())
				skipNextLineSeperator();
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
			TextRoll textroll;
			if (isNextTextRoll())
				textroll = parseTextRoll();
			else if (isNextInlineRoll())
				textroll = new TextRoll(null, new Rollable[] {parseInlineRoll()});
			else
				//unnice hardcoded lineseperator thingy...
				textroll = new TextRoll(new String[] {nextUntilIsNextAnySeqOf(TableRoll.SEPERATOR, TableRoll.CLOSER, LINESEPERATORS[0], LINESEPERATORS[1])}, null);

			for (int i = lower - tableroll.getMinResult(); i < upper; i++) {
				entries[i] = textroll;
			}

		} while (isNextSeq(TableRoll.SEPERATOR) || isNextLineSeperator());

		if (!isNextSeq(TableRoll.CLOSER))
			throw new ParseException("expected tableroll closer: "+TableRoll.CLOSER, getOffset());
		skip(TableRoll.CLOSER.length());

		for (int i = 0; i < entries.length; i++) {
			if (entries[i] == null)
				throw new ParseException("no entire for a roll of: " + (i + tableroll.getMinResult()), getOffset());
		}

		return new TableRoll(tableroll, entries);
	}

	public TextRoll parseTextRoll() throws ParseException {
		
		skipExpectedNext(TextRoll.TEXTOPENER);
		
		ArrayList<Rollable> rolls = new ArrayList<>();
		ArrayList<String> texts = new ArrayList<>();
		
		while(hasNext() && !isNextSeq(TextRoll.TEXTCLOSER)){
			texts.add(nextUntilIsNextAnySeqOf(TextRoll.INLINEOPENER, TextRoll.TEXTCLOSER));				
			if (isNextInlineRoll()) {
				rolls.add(parseInlineRoll());
			}			
		}
		
		skipExpectedNext(TextRoll.TEXTCLOSER);
		
		if (texts.isEmpty() && rolls.isEmpty())
			throw new ParseException("empty inlineroll", getOffset());
		
		return new TextRoll(texts.toArray(new String[texts.size()]), rolls.toArray(new Rollable[rolls.size()]));
	}
	
	public Rollable parseInlineRoll() throws ParseException {
		
		skipExpectedNext(TextRoll.INLINEOPENER);	
		skipNextSpaces();
			
		Rollable inlineRoll = parse();
	
		if (inlineRoll == null)
			throw new ParseException("no parse for the in lineroll", getOffset());
		
		skipNextSpaces();		
		skipExpectedNext(TextRoll.INLINECLOSER);	
		
		return inlineRoll;
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
	
	
	
	public boolean isNextNameRoll() throws ParseException {
		return isNextSeq(NameRoll.OPENER);
	}
	
	public boolean isNextDiceRoll() throws ParseException {
		return isNextDieRoll();
	}
	
	public boolean isNextDieRoll() throws ParseException {
		
		//TODO fix this EVIL workaround
		
		try {
			RollParser dierP = new RollParser(getRest());
			dierP.parseDieRoll();
			return true;
		} catch (Exception e) {
			return false;
		}
		
//		return isNextInt() || isNextAnyOf(DiceRoll.DIE);
	}
	
	public boolean isNextListRoll() throws ParseException {
		return isNextSeq(ListRoll.OPENER);

	}
	
	public boolean isNextTableRoll() throws ParseException {
		return isNextSeq(TableRoll.OPENER);

	}
	
	public boolean isNextInlineRoll() throws ParseException {
		return isNextSeq(TextRoll.INLINEOPENER);
	}
	
	public boolean isNextTextRoll() throws ParseException {
		return isNextSeq(TextRoll.TEXTOPENER);
	}
	
	
}
