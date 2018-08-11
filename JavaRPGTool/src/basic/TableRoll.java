package basic;

import java.text.ParseException;
import java.util.Arrays;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class TableRoll extends Rollable {


	private final DiceRoll tableroll;
	
	private final RollResult[] entries;

	// mutable?
	public TableRoll(DiceRoll tableroll, RollResult[] entries) {
		super(tableroll.getName());
		this.tableroll = tableroll;
		this.entries = entries;
		if (!hasName())
			throw new IllegalArgumentException("name may not be empty");
	}
	
	public TableRoll(DiceRoll tableroll, String[] entries) {
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
				return simple();
//				return "Rolling " + getTableroll() +": "
//						+ System.lineSeparator() + res + " -> " + entry.toString(DETAILED);
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
//			MULTILINE:
//			builder.append(System.lineSeparator());
//			builder.append(i + 1);
//			builder.append("\t");
//			builder.append(entries[i]);
			
//			InLine
			builder.append(';');
			builder.append(i + 1);
			builder.append(' ');
			builder.append(entries[i].toString(RollResult.PLAIN));
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TableRoll))
			return false;
		TableRoll other = (TableRoll) o;
		return this.getName().equals(other.getName())
				&& this.tableroll.equals(other.tableroll)
				&& Arrays.equals(this.entries, other.entries);
	}

	public static void main(String[] args) {
		System.out.println("TABLE TEST");

		Rollable.addRollable(new DiceRoll(1, 2, 0, 0, 0, false, "A"));
		
		String[] examples = {
				"<d4 \"Test Name\"" + System.lineSeparator()
				+ "1 One" + System.lineSeparator()
				+ "2 Two" + System.lineSeparator() 
				+ "3-4 The Rest>", 
				"<d4 Name ; 1 Gold; 2 Nothing; 3-4 Some Shit>",
				"<d4 \"MetaTable1\" ; 1 /d20 Simpleroll; 2-4 Rest>",
				"<d4 \"MetaTable2\" ; 1 /<d2 InlineTable; 1-2 B>; 2-4 Rest>",
				"<d4 \"MetaTable3\" ; 1 /2[d4] List; 2-4 Rest>",
				"<d4 \"MetaTable4\" ; 1 /A; 2-4 Rest>"};
		for (String example : examples) {
			try {

				System.out.println("Example:\n" + example);

				// Rollable r;
				// r = RollParser.valueOf(example);
				TableRoll t = null;
				// if (r instanceof RollableTable)
				// t = (RollableTable) r;

				t = new RollParser(example).parseTableRoll();

				System.out.println("Parse:\n" + t);
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
