package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class ListRoll extends Rollable {
	
	public static final String OPENER = "[";
	public static final String CLOSER = "]";
	public static final String SEPERATOR = ",";
	
	private final Rollable[] rolls;
	private final DiceRoll lenghtroll;

	// immutable
	/**
	 * Constructs a list with a fixed length
	 * 
	 * @param name
	 * @param rolls
	 */
	public ListRoll(String name, Rollable[] rolls) {
		super(name);
		Objects.requireNonNull(rolls, "no null lists allowed");
		Arrays.stream(rolls).map(r -> Objects.requireNonNull(r, "no null rollables in lists allowed"));

		if (rolls.length == 0)
			throw new IllegalArgumentException("no empty listrolls");
		
		this.rolls = rolls;
		this.lenghtroll = null;
		
		if (this.hasName()) //recuresion check
			for (Rollable rollable : rolls) {
				if (rollable.getName() == this.getName())
					throw new IllegalArgumentException("no recursion in listroll allowed");
			}
	}
	
	/**
	 * Constructs a list with rollable length
	 * 
	 * @param name
	 * @param lengthroll
	 * @param listedroll
	 */
	public ListRoll(String name, DiceRoll lengthroll, Rollable listedroll) {
		super(name);
		Objects.requireNonNull(lengthroll, "lists need a length");
		Objects.requireNonNull(listedroll, "no emplty lists allowed");
		
		this.rolls = new Rollable[] {listedroll};
		this.lenghtroll = lengthroll;
		
		if (lengthroll.hasName())
			throw new IllegalArgumentException("lenthroll may not have a name");
		
		if (this.hasName() && this.getName() == listedroll.getName())
			throw new IllegalArgumentException("no recursion in listroll allowed");
		
		if(lengthroll.getMinResult() < 0 || lengthroll.getMaxResult() < 0)
			throw new IllegalArgumentException("lists must allways have a positiv or 0 length");
	}
	
	public boolean isRollableLength() {
		return lenghtroll != null;
	}

	public RollResult[] getRandomRollResults() {
		RollResult[] res;
		Rollable[] list;
		if (isRollableLength()) {
			res = new RollResult[lenghtroll.getRandomRollValue()];
			list = new Rollable[res.length];
			Arrays.fill(list, rolls[0]);
		}		
		else {
			res = new RollResult[rolls.length];
			list = rolls;
		}
		for (int i = 0; i < res.length; i++) {
			res[i] = list[i].roll();
		}
		return res;
	}

	@Override
	public RollResult roll() {
		
		return new ListResult(getRandomRollResults(), this);
	}

	@Override
	public String toString() {
		String list;
		if (isRollableLength())
			list = lenghtroll + "[" + rolls[0].toString() + "]"; //NameRolls werden als name gezeigt
		else {
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			builder.append(rolls[0].toString());
			for (int i = 1; i < rolls.length; i++) {
				builder.append(", ");
				builder.append(rolls[i].toString());
				
			}
			builder.append("]");
			list = builder.toString();
		}
		
		String name = hasName() ? "\"" + getName()+"\" " : "";
		return name + list;
//		return "(" + name + list + ")";
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
				&& this.isRollableLength() == other.isRollableLength()
				&& Arrays.equals(this.rolls, other.rolls);
	}

	public static void main(String[] args) {
		System.out.println("LISTROLL TEST");
		String[] examples = { "\"Ablity Scores\" 6[4d6dl1] ", 
				"AS 6[4d6 dl1] ", 
				"6[4d6 dl1] ", 
				"\"Ablity Scores\" [4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha]",
				"\"Ablity Scores\"[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha]", 
				"Test[2d20 dl1, d17 Bla, 23d6 -7]",
				"Test [2d20 dl1 , d17 Bla, 23d6 -7]", 
				"\"Test\" [2d20 dl1, d17 Bla, 23d6 -7]",
				"MultiList [List 2[d4], d4 Roll, <d2 Table;1-2 bla>, A]"};

		Rollable.addRollable(new DiceRoll("A", new DiceRoll.DieRoll(7, true)));
		
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
				+ l.roll().simpleMsg() + System.lineSeparator()
				+ l.roll().detailedMsg() + System.lineSeparator());

			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}
	}

}
