package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class ListRoll extends Rollable {

	private final Rollable[] rolls;

	// immutable
	public ListRoll(Rollable[] rolls, String name) {
		super(name);
		Objects.requireNonNull(rolls);
		if (rolls.length == 0)
			throw new IllegalArgumentException("no empty listrolls");
		this.rolls = rolls;
		
		for (Rollable rollable : rolls) {
			if (rollable.getRollName() == this.getRollName())
				throw new IllegalArgumentException("recursion in list roll!");
		}
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
				String list = "";
				if ( hasName())
					n = getName() + ": ";
//				old
				list = Arrays.toString(Arrays.stream(rolls).map(roll -> roll.roll().toString(SIMPLE)).toArray());
				return n + list;
			}
			
			@Override
			public String plain() {
				return Arrays.toString(Arrays.stream(rolls).map(roll -> roll.roll().toString(PLAIN)).toArray());
			}
			
			@Override
			public String detailed() {
				String n = "";
				String list = "";
				if ( hasName())
					n = getName() + ":" + System.lineSeparator();
				
				
				StringBuilder builder = new StringBuilder();
				builder.append('[');
				builder.append(rolls[0].getRollMessage(SIMPLE));
				for (int i = 1; i < rolls.length; i++) {
					builder.append(',');
					builder.append(System.lineSeparator());
					builder.append(rolls[i].getRollMessage(SIMPLE));
					
				}
				builder.append("]");
				list = builder.toString();
				
				return n + list;
			}
		};
	}

	@Override
	public String toString() {
		
		String list;	
		if (Arrays.stream(rolls).allMatch(roll -> roll.equals(rolls[0])))
			list = rolls.length + "[" + rolls[0].getInlineToString() + "]";
		else {
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			builder.append(rolls[0].getInlineToString());
			for (int i = 1; i < rolls.length; i++) {
				builder.append(", ");
				builder.append(rolls[i].getInlineToString());
				
			}
			builder.append("]");
			list = builder.toString();
		}
		return list + ((getName() == null || getName().isEmpty()) ? "" : " \"" + getName()+"\"");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ListRoll))
			return false;
		ListRoll other = (ListRoll) o;
		return this.hasName() == other.hasName()
				&& (this.hasName() ? this.getName().equals(other.getName()) : true)
				&& Arrays.equals(this.rolls, other.rolls);
	}

	public static void main(String[] args) {

		System.out.println("LISTROLL TEST");
		String[] examples = { "6[4d6dl1] \"Ablity Scores\"", 
				"6[4d6 dl1] AS", 
				"[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha] \"Ablity Scores\"", 
				"[d20, d17 Bla, 23d6 -7] Test",
				"[2[d4] List, d4 Roll, <d2 Table;1-2 bla>, A] MultiList"};

		Rollable.addRollable(new DiceRoll(1, 2, 0, 0, 0, false, "A"));
		
		for (String exa : examples) {
			try {
				System.out.println("Example: " + exa);

//				Rollable r;
//				r = RollParser.valueOf(exa);
//				ListRoll l = null;
//				if (r instanceof ListRoll)
//					l = (ListRoll) r;

				ListRoll l = new RollParser(exa).parseListRoll();

				System.out.println("Parse:   " + l);

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
