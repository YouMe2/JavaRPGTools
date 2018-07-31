package basic;

import java.text.ParseException;
import java.util.Arrays;

public class RollableTable implements Rollable{

	private String name;
	private DiceRoll tableroll;
	private String[] table;
	
	//mutable?
	public RollableTable(String name, DiceRoll tableroll, String[] table) {
		this.name = name;
		this.tableroll = tableroll;
		this.table = table;
	}

	@Override
	public String roll() {
		return getEntry(getTableroll().roll());
	}
	
	public String getEntry(int i){
		return table[i-1];
	}

	public String getName() {
		return name;
	}
	
	public DiceRoll getTableroll() {
		return tableroll;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(tableroll.toString());
		builder.append(",\t");
		builder.append(name);
		builder.append(System.lineSeparator());
		for (int i = 0; i < table.length; i++) {
			builder.append(i+1);
			builder.append(",\t");
			builder.append(table[i]);
			builder.append(System.lineSeparator());
		}
		
		return builder.toString();
	}
	
	public static RollableTable valueOf(String input){
		try {
			return tryParse(input).left;
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("ErrOffSET: " + e.getErrorOffset());
		}
		return null;
	}
	
	public static Pair<RollableTable, String> tryParse(String input) throws ParseException{
		input = input.replace(" ", "").replace("\t","");
		
		if (input == null || input.isEmpty())
			throw new IllegalArgumentException("input may not be empty");
		
		String[] lines = input.split(System.lineSeparator());
		String header[] = lines[0].split(",", 2);
 		

		// tableroll
		final DiceRoll roll = DiceRoll.tryParse(header[0]).left;
		if(roll == null) 
			return new Pair<RollableTable, String>(null, input);
			//throw new ParseException("no valid table roll found!",0);
		if (roll.isExploding())
			return new Pair<RollableTable, String>(null, input);
			//throw new ParseException("no exploding table rolles allowed!",0);
		
		// table name
		if (header.length != 2)
			return new Pair<RollableTable, String>(null, input);
			//throw new ParseException("no unnamed tables allowed!",0);
		final String name = header[1];
		
		// table entries
		final String[] entries = new String[roll.maxResult()];
		
		for (int i = 1; i < lines.length; i++) {
			String tabs[] = lines[i].split(",", 2);
			if (tabs.length != 2)
				return new Pair<RollableTable, String>(null, input);
				//throw new ParseException("missing entry in tabel at line: "+i,0);
			
			int[] nums = new RollableTableLineParser(tabs[0]).parse().left;
			if (nums == null)
				return new Pair<RollableTable, String>(null, input);
				//throw new ParseException("no valid roll intable at line: "+i,0);
		
			for (int n : nums) {
				if(n-1 >= entries.length)
					return new Pair<RollableTable, String>(null, input);
//					throw new ParseException("rollable range and values in table aren't matching up!", 0);
				entries[n-1] = tabs[1];
			}
		}
		//abfragen das alle werte belegt sind.
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] == null || entries[i].isEmpty())
				return new Pair<RollableTable, String>(null, input);
//				throw new IllegalArgumentException("missing entry in output table for value: "+i);
		}
		
		return new Pair<RollableTable, String>( new RollableTable(name, roll, entries), "");
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RollableTable))
			return false;
		RollableTable other = (RollableTable) o;
		return this.name.equals(other.name)
				&& this.tableroll.equals(other.tableroll)
				&& Arrays.equals(this.table, other.table);
	}
	
	public static void main(String[] args) {
		
		String example = 
				"d10, Name" + System.lineSeparator()
				+ "1, eins"	+ System.lineSeparator()
				+ "2, zwei"	+ System.lineSeparator()
				+ "3-10, rest";		
		
		System.out.println("Example: "+example);
		
		RollableTable t = RollableTable.valueOf(example);
		
		System.out.println("Roll:    "+t);
		
		assert t.equals(RollableTable.valueOf(t.toString()));
		
		String res = t.roll();
		
		System.out.println("Result:  " +res);
		
	}
	
	public static class RollableTableLineParser extends AbsParser<int[]> {

		
		public RollableTableLineParser(String chars) {
			super(chars);
		}

		@Override
		public Pair<int[], String> parse() throws ParseException {
			//6 - 10 -> [6,7,8,9,10]
			//10-6 = 4
			
			if (!isNextDigit())
				return new Pair<int[], String>(null, getRest());
			int n = parseInteger().left;
			if (isNext('-')) {
				skip(1);

				if (!isNextDigit())
					return new Pair<int[], String>(null, getRest());
				
				int m = parseInteger().left;
				if (n >= m)
					return new Pair<int[], String>(null, getRest());
					//throw new ParseException("no valid value range", getOffset());
				int[] res = new int[m - n + 1];
				for (int i = 0; i < res.length; i++) {
					res[i] = n + i;
				}
				return new Pair<int[], String>(res, "");
			}
			else if (!hasNext())
				return new Pair<int[], String>(new int[] {n}, "");
			else
				return new Pair<int[], String>(null, getRest());
				//throw new ParseException("no valid value", getOffset());
				
		}
		
	}
	
}
