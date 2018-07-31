package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

public class ListRoll implements Rollable {

	private final DiceRoll[] rolls;
	private final String name;
	
	// immutable
	public ListRoll(DiceRoll[] rolls, String name) {
		Objects.requireNonNull(rolls);
		if (rolls.length == 0)
			throw new IllegalArgumentException("no empty listrolls");
		this.rolls = rolls;
		this.name = name;
	}
	
	public ListRoll(DiceRoll[] rolls) {
		this(rolls, null);
	}

	@Override
	public int[] roll() {
		return roll(false);
	}

	public int[] roll(boolean sorted) {
		int[] res = new int[rolls.length];

		for (int i = 0; i < res.length; i++) {
			res[i] = rolls[i].roll();
		}

		if (sorted)
			Arrays.sort(res);
		return res;
	}

	public static ListRoll valueOf(String input) {
		try {
			return tryParse(input).left;
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("ErrOffSET: " + e.getErrorOffset());
		}
		return null;

	}

	public static Pair<ListRoll, String> tryParse(String input) throws ParseException {
		input = input.replace(" ", "").replace("\t","");
		
		if (input == null || input.isEmpty())
			throw new IllegalArgumentException("input may not be empty");
		return new ListRollParser(input).parse();
	}

	@Override
	public String toString() {

		if (Arrays.stream(rolls).allMatch(roll -> roll == rolls[0]))
			return rolls.length + "[" + rolls[0] + "]";
		return Arrays.toString(rolls);
	}
	

	@Override
	public String getRollMessage() {
		
		if (name == null)
			return "Rolling " + this + ": " + Arrays.toString(this.roll());
		else
			return "Rolling " + name + " ("+this+"): " + Arrays.toString(this.roll());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ListRoll))
			return false;
		ListRoll other = (ListRoll) o;
		return Arrays.equals(this.rolls, other.rolls);
	}

	public static void main(String[] args) {

		String[] examples = { "6[4d6dl1]", "6[4d6 dl1]", "[d20, d17, 23d6 -7]" };

		for (String exa : examples) {

			System.out.println("Example: " + exa);

			ListRoll l = ListRoll.valueOf(exa);

			System.out.println("Roll:    " + l);

			assert l.equals(ListRoll.valueOf(l.toString()));

			int[] res = l.roll();

			System.out.println("Result:  " + Arrays.toString(res));

		}
	}

	private static class ListRollParser extends AbsParser<ListRoll> {

		public ListRollParser(String chars) {
			super(chars);
		}

		public Pair<ListRoll, String> parse() throws ParseException {

			if (isNextDigit()) {
				// 5[2d20]
				int n = parseInteger().left;
				if (isNext('[')) {
					skip(1);
					Pair<DiceRoll, String> p = DiceRoll.tryParse(getRest().toString());
					if (p.left == null)
						return new Pair<ListRoll, String>(null, getChars());
					DiceRoll[] rs = new DiceRoll[n];
					Arrays.fill(rs, p.left);
					return new Pair<ListRoll, String>(new ListRoll(rs), p.right);
				} else
					return new Pair<ListRoll, String>(null, getChars());
				// throw new ParseException("no valid listroll", getOffset());

			} else if (isNext('[')) {
				// [2d20,8d30]
				skip(1);
				String[] ss = getRest().toString().split(",");
				DiceRoll[] rs = new DiceRoll[ss.length];
				String lastRest = "";
				for (int i = 0; i < rs.length; i++) {
					Pair<DiceRoll, String> p = DiceRoll.tryParse(ss[i]);
					if (p.left == null)
						return new Pair<ListRoll, String>(null, getChars());
					rs[i] = p.left;
					lastRest = p.right;
				}
				// überflüssig
				if (lastRest.startsWith("]"))
					return new Pair<ListRoll, String>(new ListRoll(rs), lastRest.substring(1));

			}
			return new Pair<ListRoll, String>(null, getChars());
			// throw new ParseException("no valid listroll", getOffset());

		}
	}


}
