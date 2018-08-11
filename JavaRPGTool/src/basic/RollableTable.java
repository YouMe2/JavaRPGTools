package basic;

import java.text.ParseException;
import java.util.Arrays;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class RollableTable extends Rollable {


	private final DiceRoll tableroll;
	
	private final RollResult[] entries;

	// mutable?
	public RollableTable(DiceRoll tableroll, RollResult[] entries) {
		super(tableroll.getName());
		this.tableroll = tableroll;
		this.entries = entries;
		if (!hasName())
			throw new IllegalArgumentException("name may not be empty");
	}
	
	public RollableTable(DiceRoll tableroll, String[] entries) {
		this(tableroll, Arrays.stream(entries).map(str -> new RollResult.PlainResult(str)).toArray(size -> new RollResult[size]));
	}

	@Override
	public RollResult roll() {
		int res = getTableroll().getRandomRollValue();
		RollResult entry = getEntry(res);
		
		return new RollResult() {
			
			@Override
			public String simple() {
				return getName() + ": " + res + " -> " + entry.toString(SIMPLE);
			}
			
			@Override
			public String plain() {
				return entry.toString(PLAIN);
			}
			
			@Override
			public String detailed() {
				return "Rolling " + getTableroll() +" : "
						+ System.lineSeparator() + res + " -> " + entry.toString(DETAILED);
			}
		};
	}

	public RollResult getEntry(int i) {
		return entries[i - getTableroll().minResult()];
	}

	public DiceRoll getTableroll() {
		return tableroll;
	}

	@Override
	public String toString() {
		//TODO options for inline/simpletable/fancytable
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(tableroll.toString()); // name in roll
		for (int i = 0; i < entries.length; i++) {
			builder.append(System.lineSeparator());
			builder.append(i + 1);
			builder.append("\t");
			builder.append(entries[i]);
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RollableTable))
			return false;
		RollableTable other = (RollableTable) o;
		return this.getName().equals(other.getName()) && this.tableroll.equals(other.tableroll)
				&& Arrays.equals(this.entries, other.entries);
	}

	public static void main(String[] args) {
		System.out.println("TABLE TEST");

		String[] examples = { "<d4 Test Name" + System.lineSeparator() + "1 One" + System.lineSeparator()
				+ "2 Two" + System.lineSeparator() + "3-4 The Rest>", "<d4 Name ; 1 Gold; 2 Nothing; 3-4 Some Shit>",
				"<d4 Name2 ; 1 Gold; 2 Nothing; 3-4 /d20>" };
		for (String example : examples) {
			try {

				System.out.println("Example:\n" + example);

				// Rollable r;
				// r = RollParser.valueOf(example);
				RollableTable t = null;
				// if (r instanceof RollableTable)
				// t = (RollableTable) r;

				t = new RollParser(example).parseRollableTable();

				System.out.println("Roll:\n" + t);
				// System.out.println("Roll2: " + new
				// RollParser(t.toString()).parseRollableTable());

				assert t.equals(Rollable.valueOf(t.toString()));

				System.out.println("Msg: " + System.lineSeparator() + t.roll().simple() + System.lineSeparator()
						+ t.roll().detailed() + System.lineSeparator());

				System.out.println();
				
			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}

	}

}
