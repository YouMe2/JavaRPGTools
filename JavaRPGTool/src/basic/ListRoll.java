package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

import basic.DiceRoll.DiceRollParser;

public class ListRoll implements Rollable {

	private final DiceRoll[] rolls;
	
	public ListRoll(DiceRoll[] rolls) {
		Objects.requireNonNull(rolls);
		if (rolls.length == 0) throw new IllegalArgumentException("no empty listrolls");
		this.rolls = rolls;
	}

	@Override
	public int[] roll() {
		int[] res = new int[rolls.length];
		
		for (int i = 0; i < res.length; i++) {
			res[i] = rolls[i].roll();
		}
		Arrays.sort(res);
		return res;
	}

	public static ListRoll valueOf(String input){
		if (input == null || input.isEmpty())
			throw new IllegalArgumentException("input may not be empty");
		
		try {
			return new ListRollParser(input.replace(" ", "")).parse();
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("ErrOffSET: " + e.getErrorOffset());
		}
		return new ListRoll(new DiceRoll[] {new DiceRoll(1, 1)});
		
	}
	
	@Override
	public String toString() {
		
		if(Arrays.stream(rolls).allMatch(roll -> roll == rolls[0]))
			return rolls.length + "[" + rolls[0] + "]"; 
		return Arrays.toString(rolls);
	}
	
	public static void main(String[] args) {
		
		String[] examples
		= {"6[4d6dl1]", "[d20, d17, 23d5]"};
		
		for (String exa : examples) {
			
			System.out.println("Example: "+exa);
			
			ListRoll l = ListRoll.valueOf(exa);
			
			System.out.println("Roll:    "+l);
			
			
			assert l.equals(ListRoll.valueOf(l.toString()));
			
			int[] res = l.roll();
			
			System.out.println("Result:  " +Arrays.toString(res));
			
		}
	}
	
	private static class ListRollParser extends AbsParser<ListRoll>{

		public ListRollParser(CharSequence chars) {
			super(chars);
		}
		
		public ListRoll parse() throws ParseException {
			
			if(isNextDigit()) {
				// 5[2d20]
				int n = parseInteger();
				if(isNext('[')) {
					skip(1);
					DiceRoll r = DiceRoll.valueOf(getRest().toString());
					DiceRoll[] rs = new DiceRoll[n];
					Arrays.fill(rs, r);
					return new ListRoll(rs);
				} else
					throw new ParseException("no valid listroll", getOffset());

			} else if (isNext('[')) {
				// [2d20,8d30]
				skip(1);
				String[] ss = getRest().toString().split(",");
				DiceRoll[] rs = new DiceRoll[ss.length];
				for (int i = 0; i < rs.length; i++) {
					rs[i] = DiceRoll.valueOf(ss[i]);
				}
				return new ListRoll(rs);
				
			} else
				throw new ParseException("no valid listroll", getOffset());
			
			
		}
		
	}
	
}
