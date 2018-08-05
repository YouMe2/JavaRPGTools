package basic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class RollParser extends AbsParser<Rollable> {

		public RollParser(String chars) {
			super(chars);
		}
		
		public static Rollable valueOf(String input) throws ParseException {
			// will never return null -> throw exc

			Pair<Rollable, String> p = tryParse(input);
//			if(p == null)
//				throw new IllegalStateException("wtf");
			if (p.left == null)
				throw new ParseException("no parse", 0);
			
			assert p.left != null;
			return p.left;
		}

		public static Pair<Rollable, String> tryParse(String input) throws ParseException {
			// will return null and throw exc
			//input = input.replace(" ", "").replace("\t", "");
			if (input == null || input.isEmpty())
				throw new ParseException("input may not be empty", 0);
			
			return new RollParser(input).parse();

		}

		@Override
		public Pair<Rollable, String> parse() throws ParseException {
			
			try {
				RollParser drP = new RollParser(getRest());
				DiceRoll dr = drP.parseDiceRoll();
				return new Pair<Rollable, String>(dr, drP.getRest());
			} catch (ParseException e1) {
				try {
					RollParser lrP = new RollParser(getRest());
					ListRoll lr = lrP.parseListRoll();
					return new Pair<Rollable, String>(lr, lrP.getRest());
				} catch (ParseException e2) {
					try {
						RollParser tableP = new RollParser(getRest());
						RollableTable t = tableP.parseRollableTable();
						return new Pair<Rollable, String>(t, tableP.getRest());
					} catch (ParseException e3) {					
						return new Pair<Rollable, String>(null, getRest());
					}
				}
			}
		}
		
		public DiceRoll parseDiceRoll() throws ParseException {

			final String name;
			final Integer n, die, dh, dl, mod;
			final boolean exploding;

			// 10d20! dh2 dl2 +5
			
			n = isNextDigit() ? parseNatural() : 1;
			if (!isNextAnyOf('d', 'D'))
				throw new ParseException("expected d or D", getOffset());
			skip(1);	
			die = parseInteger();

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
			
			if (isNextText()) {
				name = parseText();
			} else
				name = ""; 
			
			return new DiceRoll(n, die, dl, dh, mod, exploding, name);
		}

		public ListRoll parseListRoll() throws ParseException {

			if (isNextDigit()) {
				// 5[2d20]
				int n = parseNatural();
				if (isNext('[')) {
					skip(1);
					
					skipNextWhitespaces();
					
					DiceRoll dr = parseDiceRoll();
					
					skipNextWhitespaces();
					
					if (!isNext(']'))
						throw new ParseException("no end of list detected", getOffset());
					skip(1);
					
					skipNextWhitespaces();
					
					DiceRoll[] rs = new DiceRoll[n];
					Arrays.fill(rs, dr);
					
					if (isNextText()) {			
						return new ListRoll(rs, parseText());
					} else
						return new ListRoll(rs);
				
					
				} else
					throw new ParseException("no valid listroll", getOffset());

			} else if (isNext('[')) {
				// [2d20, 8d30 "Name", d4 "Test Name"]
				skip(1);
				
				ArrayList<DiceRoll> rolls = new ArrayList<>();
							
				
				while (true) {
					skipNextWhitespaces();
					
					DiceRoll dr = parseDiceRoll();
					rolls.add(dr);		

					skipNextWhitespaces();
					
					if (isNext(']')) {
						skip(1);			// list end
						break;
					} else if(isNext(',')) {
						skip(1);
						continue;			// next roll
					} else
						throw new ParseException("unexpected char in list", getOffset());	
				}
				
				DiceRoll[] rs = rolls.toArray(new DiceRoll[rolls.size()]);
				if (rs.length == 0)
					throw new ParseException("parsed empty list of rolls", getOffset());
				
				skipNextWhitespaces();
				
				if (isNextText()) {			
					return new ListRoll(rs, parseText());
				} else
					return new ListRoll(rs);
				
			}
			throw new ParseException("no valid listroll", getOffset());

		}

		public RollableTable parseRollableTable() throws ParseException {			
			
			String name;
			DiceRoll tableroll;
			String[] entries;
			
			if (!isNextSeq(RollableTable.PREFIX))
				throw new ParseException("expected Rollable Table Prefix: \""+RollableTable.PREFIX+"\"", getOffset());
			skip(RollableTable.PREFIX.length());
			
			
			tableroll = parseDiceRoll();
			if (tableroll == null || tableroll.isExploding())
				throw new ParseException("no valid tableroll", getOffset());
			
			entries = new String[tableroll.maxResult()];
			name = tableroll.getName();
			
			skipNextWhitespaces();
			
			while (isNext(';') || isNextSeq(System.lineSeparator())) {
				
				if (isNext(';'))
					skip(1);
				else
					skip(System.lineSeparator().length());
				
				
				int lower=0, upper;
				//  incl   excl
				
				skipNextWhitespaces();
				try {
					lower = parseNatural();
				} catch (ParseException e) {
					break;
				}
				
				skipNextWhitespaces();
				if (isNext('-')) {
					skip(1);
					skipNextWhitespaces();
					upper = parseNatural();
				} else {
					upper = lower;
				}
				
				skipNextWhitespaces();
				
				if (isNext(','))
					skip(1);

				skipNextWhitespaces();
				
				String entrie = parseText();
				
				skipNextWhitespaces();
				
				for (int i = lower - 1; i < upper; i++) {
					entries[i] = entrie;
				}
	
			}
			
			for (int i = 0; i < entries.length; i++) {
				if (entries[i] == null || entries[i].isEmpty())
					throw new ParseException("no entire for a roll of: " + (i+1), getOffset());
			}
			
			return new RollableTable(name, tableroll, entries);
		}
		
		
	
	
}
