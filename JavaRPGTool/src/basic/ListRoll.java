package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

public class ListRoll extends Rollable {

	private final Rollable[] rolls;

	// immutable
	public ListRoll(Rollable[] rolls, String name) {
		super(name);
		Objects.requireNonNull(rolls);
		if (rolls.length == 0)
			throw new IllegalArgumentException("no empty listrolls");
		this.rolls = rolls;
	}

	//unused
//	public RollResult[] getRandomRollResults() {
//		RollResult[] res = new RollResult[rolls.length];
//
//		for (int i = 0; i < res.length; i++) {
//			res[i] = rolls[i].roll();
//		}
//		return res;
//	}

	@Override
	public RollResult roll() {
		
		return new RollResult() {
			
			@Override
			public String simple() {
				String n = "";
				if ( hasName())
					n = getName() + ": ";
				return n + Arrays.toString(Arrays.stream(rolls).map(roll -> roll.roll().toString(SIMPLE)).toArray());

			}
			
			@Override
			public String plain() {
				return Arrays.toString(Arrays.stream(rolls).map(roll -> roll.roll().toString(PLAIN)).toArray());
			}
			
			@Override
			public String detailed() {
				String n = "";
				if ( hasName())
					n = "Rolling \"" + getName() + "\": ";
				else
					n = "Rolling \"" + ListRoll.this + "\": ";
				return n + Arrays.toString(Arrays.stream(rolls).map(roll -> /*System.lineSeparator() + */ roll.roll().toString(SIMPLE)).toArray());

			}
		};
	}

	@Override
	public String toString() {
		
		String list;	
		if (Arrays.stream(rolls).allMatch(roll -> roll == rolls[0]))
			list = rolls.length + "[" + rolls[0].toString() + "]";
		else
			list = Arrays.toString(rolls);
		return list + ((getName() == null || getName().isEmpty()) ? "" : " \"" + getName()+"\"");
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
				"[d20, d17 Bla, 23d6 -7] Test" };

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

				assert l.equals(Rollable.valueOf(l.toString()));


				System.out.println("Msg: " + System.lineSeparator()
				+ l.roll().simple() + System.lineSeparator()
				+ l.roll().detailed() + System.lineSeparator());

			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}
	}

}
