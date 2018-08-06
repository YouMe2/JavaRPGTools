package basic;

import java.text.ParseException;
import java.util.Arrays;

public class RollableTable implements Rollable {

	public static final String PREFIX = "Rollable Table" + System.lineSeparator();
	
	private final String name;
	private DiceRoll tableroll;
	private String[] entries;

	// mutable?
	public RollableTable(String name, DiceRoll tableroll, String[] entries) {
		this.name = name;
		this.tableroll = tableroll;
		this.entries = entries;
		if (!hasName())
			throw new IllegalArgumentException("name may not be empty");
	}

	@Override
	public String roll() {
		return getEntry(getTableroll().roll());
	}

	public String getEntry(int i) {
		return entries[i - 1];
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
	}

	public DiceRoll getTableroll() {
		return tableroll;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(PREFIX);
		builder.append(tableroll.toString() + " \"" + getName() + "\"");
		builder.append(System.lineSeparator());
		for (int i = 0; i < entries.length; i++) {
			builder.append(i + 1);
			builder.append(",\t");
			builder.append(entries[i]);
			builder.append(System.lineSeparator());
		}

		return builder.toString();
	}

	@Override
	public String getRollMessage(int mode) {
		int res = this.getTableroll().roll();

		switch (mode) {

		case SIMPLE:
			return getName() + ": " + res + " -> " + this.getEntry(res);

		case DETAILED:
			return "Rolling on " + this.getName() + " (" + this.getTableroll() + "): " + System.lineSeparator()
			+ res + " -> " + this.getEntry(res);

		case PLAIN:
		default:
			return this.getEntry(res);
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RollableTable))
			return false;
		RollableTable other = (RollableTable) o;
		return this.getName().equals(other.getName())
				&& this.tableroll.equals(other.tableroll)
				&& Arrays.equals(this.entries, other.entries);
	}

	public static void main(String[] args) {
		System.out.println("TABLE TEST");
		
		try {
			String example = 
			PREFIX 
			+ "d10 \"Test Name\"" + System.lineSeparator() 
			+ "1, eins" + System.lineSeparator() 
			+ "2, zwei"	+ System.lineSeparator()
			+ "3-10, rest";

			System.out.println("Example: " + example);

//			Rollable r;
//			r = RollParser.valueOf(example);
			RollableTable t = null;
//			if (r instanceof RollableTable)
//				t = (RollableTable) r;
			
			t = new RollParser(example).parseRollableTable();
			
			
			System.out.println("Roll:    " + t);
//			System.out.println("Roll2:    " + new RollParser(t.toString()).parseRollableTable());

			assert t.equals(RollParser.valueOf(t.toString()));

			
			System.out.println("Msg: " + System.lineSeparator()
			+ t.getRollMessage(SIMPLE) + System.lineSeparator()
			+ t.getRollMessage(DETAILED) + System.lineSeparator());
			
			
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println(e.getErrorOffset());
		}
	}

}
