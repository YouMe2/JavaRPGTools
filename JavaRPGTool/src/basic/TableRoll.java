package basic;

import java.text.ParseException;
import java.util.Arrays;

import roll.RollParser;
import roll.Rollable;

public class TableRoll extends Rollable {

	public static final String OPENER = "<";
	public static final String CLOSER = ">";
	public static final String SEPERATOR = ";";
	
	private final DiceRoll tableroll;
	private final TextRoll[] entries;

	// immutable
	public TableRoll(DiceRoll tableroll, TextRoll[] entries) {
		super(tableroll.getName());
		this.tableroll = tableroll;
		this.entries = entries;

		if (tableroll.isExploding())
			throw new IllegalArgumentException("tablerolls my not use exploding die");
		
		if (tableroll.getMaxResult() - tableroll.getMinResult() + 1 != entries.length)
			throw new IllegalArgumentException("table length unfit for tableroll");
	
		if (!hasName())
			throw new IllegalArgumentException("name may not be empty");
	}
	

	@Override
	public TableResult roll() {
		int n = getTableroll().getRandomRollValue();
		TextRoll resLine= getEntry(n);	
		return new TableResult(resLine.roll(), n, this);
	}

	public TextRoll getEntry(int i) {
		return entries[i - getTableroll().getMinResult()];
	}

	public DiceRoll getTableroll() {
		return tableroll;
	}

	@Override
	public String toString() {
		//TODO options for inline/simpletable/fancytable
		
		//<diceroll with name; optional lineseperator!
		//1-2 inlineroll;
		//3	inlineroll>
		
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(tableroll.toString()); // with name in roll
		for (int i = getTableroll().getMinResult(); i <= getTableroll().getMaxResult(); i++) {
			
//			SingleLine
			builder.append(';');
			builder.append(i);
			builder.append(' ');
			builder.append(getEntry(i).toString());
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

		Rollable.addRollable(new DiceRoll("A", new DiceRoll.DieRoll(8, true)));
		
		String[] examples = {
				"<d4 \"Test Name\"" + System.lineSeparator()
				+ "1 One" + System.lineSeparator()
				+ "2 Two" + System.lineSeparator() 
				+ "3-4 The Rest>", 
				"<d4 Name ; 1 Gold; 2 Nothing; 3-4 Some Shit>",
				"<1d4 \"Name\" ; 1 Gold; 2 Nothing; 3-4 Some Shit>",
				"<d4 \"MetaTable1\" ; 1-3 $( d20 Simpleroll ); 4 Rest>",
				"<d4 \"MetaTable2\" ; 1-3 $( <d2 InlineTable; 1-2 B> ); 4 Rest>",
				"<d4 \"MetaTable3\" ; 1-3 $( [2 List: d4] ); 4 Rest>",
				"<d4 \"MetaTable4\" ; 1-3 $( A ); 4 Rest>"};
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

				System.out.println("Msg: " + System.lineSeparator() 
					+ t.roll().getSingleLineMsg() + System.lineSeparator()
					+ t.roll().getMultiLineMsg() + System.lineSeparator()
					);

				System.out.println();
				
			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}

	}

}
