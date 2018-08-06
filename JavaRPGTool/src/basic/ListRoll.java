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
	public String getName() {
		return name;
	}
	@Override
	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
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

	@Override
	public String toString() {

		if (Arrays.stream(rolls).allMatch(roll -> roll == rolls[0]))
			return rolls.length + "[" + rolls[0] + "]";
		return Arrays.toString(rolls);
	}

	@Override
	public String getRollMessage(int mode) {
		String n = "";
		switch (mode) {

		case SIMPLE:
			if ( hasName())
				n = getName() + ": ";
			return n + Arrays.toString(Arrays.stream(rolls).map(roll -> roll.getRollMessage(SIMPLE)).toArray());

		case DETAILED:
			if ( hasName())
				n = "Rolling \"" + getName() + "\": ";
			else
				n = "Rolling \"" + this + "\": ";
			return n + Arrays.toString(Arrays.stream(rolls).map(roll -> roll.getRollMessage(SIMPLE)).toArray());

		case PLAIN:
		default:
			return Arrays.toString(roll());
		}
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

		System.out.println("LISTROLL TEST");
		String[] examples = { "6[4d6dl1] \"Ablity Scores\"", 
				"6[4d6 dl1] ", 
				"[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha] \"Ablity Scores\"", 
				"[d20, d17 Bla, 23d6 -7]" };

		for (String exa : examples) {
			try {
				System.out.println("Example: " + exa);

//				Rollable r;
//				r = RollParser.valueOf(exa);
//				ListRoll l = null;
//				if (r instanceof ListRoll)
//					l = (ListRoll) r;

				ListRoll l = new RollParser(exa).parseListRoll();
				
				System.out.println("Roll:    " + l);

				assert l.equals(RollParser.valueOf(l.toString()));


				System.out.println("Msg: " + System.lineSeparator()
				+ l.getRollMessage(SIMPLE) + System.lineSeparator()
				+ l.getRollMessage(DETAILED) + System.lineSeparator());

			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}
	}

}
