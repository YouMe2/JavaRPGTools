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
		return table[tableroll.roll()-1];
	}

	public String getName() {
		return name;
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
			return tryParse(input);
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("ErrOffSET: " + e.getErrorOffset());
		}
		return null;
	}
	
	public static RollableTable tryParse(String input) throws ParseException{
		if (input == null || input.isEmpty())
			throw new IllegalArgumentException("input may not be empty");
		
		input = input.replace(" ", "").replace("\t","");
		String[] lines = input.split(System.lineSeparator());
		String header[] = lines[0].split(",", 2);
 		
		
		final String name = header[1];
		final DiceRoll roll = DiceRoll.tryParse(header[0]);
		if(roll.isExploding())
			throw new IllegalArgumentException("no exploding tables!");
		
		final String[] table = new String[roll.maxResult()];
		
		for (int i = 1; i < lines.length; i++) {
			String tabs[] = lines[i].split(",", 2);
			if (tabs.length < 2)
				throw new IllegalArgumentException("missing entry in input tabel at line: "+i);
			
			int[] nums = new RollableTableLineParser(tabs[0]).parse();
			for (int n : nums) {
				table[n-1] = tabs[1];
			}
		}
		//abfragen das alle werte belegt sind.
		for (int i = 0; i < table.length; i++) {
			if (table[i] == null || table[i].isEmpty())
				throw new IllegalArgumentException("missing entry in output table for value: "+i);
		}
		
		return new RollableTable(name, roll, table);
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

		
		public RollableTableLineParser(CharSequence chars) {
			super(chars);
		}

		@Override
		public int[] parse() throws ParseException {
			//6-10 -> [6,7,8,9,10]
			//10-6 = 4
			
			int n = parseInteger();
			if (isNext('-')) {
				skip(1);
				int m = parseInteger();
				if (n >= m)
					throw new ParseException("no valid value range", getOffset());
				int[] res = new int[m - n + 1];
				for (int i = 0; i < res.length; i++) {
					res[i] = n + i;
				}
				return res;
			}
			else if (!hasNext())
				return new int[] {n};
			else
				throw new ParseException("no valid value", getOffset());
				
		}
		
	}
	
}
