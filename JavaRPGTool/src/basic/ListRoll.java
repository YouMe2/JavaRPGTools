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
	public static final String NAMESEPERATOR = ":";
	
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
	public ListRoll(DiceRoll lengthroll, Rollable[] listedrolls) {
		super(lengthroll.getName());
		Objects.requireNonNull(lengthroll, "lists need a length");
		Objects.requireNonNull(listedrolls, "no null lists allowed");

		this.rolls = listedrolls;
		this.lenghtroll = lengthroll;
		
		
		if (rolls.length == 0)
			throw new IllegalArgumentException("no emplty lists allowed");
		
		for (Rollable listedroll : listedrolls) {
			if (this.hasName() && this.getName() == listedroll.getName())
				throw new IllegalArgumentException("no recursion in listroll allowed");
		}
		
		if(lengthroll.getMinResult() < 0 || lengthroll.getMaxResult() < 0)
			throw new IllegalArgumentException("lists must allways have a positiv or 0 length");
	}
	
	public boolean hasLengthRoll() {
		return lenghtroll != null;
	}

	public RollResult[] getRandomRollResults() {
		RollResult[] res;
		Rollable[] list;
		if (hasLengthRoll()) {
			res = new RollResult[lenghtroll.getRandomRollValue()];
			list = new Rollable[res.length * rolls.length];
			for (int i = 0; i < list.length; i++) {
				list[i] = rolls[i%rolls.length];
			}
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
	
		String name;
		if (hasLengthRoll())
			name = lenghtroll.toString() + NAMESEPERATOR + " ";
		else if(hasName())
			name = hasName() ? "\"" + getName()+"\""+ NAMESEPERATOR + " " : "";
		else
			name = "";
		
		
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append(name);
		builder.append(rolls[0].toString());
		for (int i = 1; i < rolls.length; i++) {
			builder.append(", ");
			builder.append(rolls[i].toString());
			
		}
		builder.append("]");
		list = builder.toString();
	
		
		
		return list;
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
				&& this.hasLengthRoll() == other.hasLengthRoll()
				&& Arrays.equals(this.rolls, other.rolls);
	}

	public static void main(String[] args) {
		System.out.println("LISTROLL TEST");
		String[] examples = { "[6 \"Ablity Scores\": 4d6dl1] ", 
				"[6: 4d6 dl1] ", 
				"[6 AS: 4d6 dl1] ",
				"[d4 Test: d4, d5]", 
				"[\"Ablity Scores\": 4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha]",
				"[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha]", 
				"[Test: 2d20 dl1, d17 Bla, 23d6 -7]",
				"[ Test : 2d20 dl1 , d17 Bla, 23d6 -7]", 
				"[\"Test\": 2d20 dl1, d17 Bla, 23d6 -7]",
				"[\"MultiList\": [2 \"List\": d4], d4 \"Roll\", <d2 \"Table\";1 bla;2 bla>, A]",
				"[MultiList: [2 List: d4], d4 Roll, <d2 Table;1-2 bla>, A]"};

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
//				+ l.roll().detailedMsg() + System.lineSeparator()
				);

			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}
	}

}
