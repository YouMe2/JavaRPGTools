package basic;

import java.util.Arrays;

import roll.RollResult;

public class ListResult extends RollResult {
	
	private final RollResult[] res;
	public ListResult(RollResult[] res, ListRoll roll) {
		super(roll);
		this.res = res;
	}

	@Override
	public String simpleMsg() {
		String n = "";
		String list = "";
		if ( hasName())
			n = getName() + ": ";
//		old
		list = Arrays.toString(Arrays.stream(res).map(r -> r.simpleMsg()).toArray());
		return n + list;
	}
	
	@Override
	public String plainText() {
		return Arrays.toString(Arrays.stream(res).map(r -> r.plainText()).toArray());
	}
	
	@Override
	public String detailedMsg() {
		String n = "";
		String list = "";
		if ( hasName())
			n = getName() + ":" + System.lineSeparator();
		
		
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append(res[0].simpleMsg());
		for (int i = 1; i < res.length; i++) {
			builder.append(',');
			builder.append(System.lineSeparator());
			builder.append(res[i].simpleMsg());
			
		}
		builder.append("]");
		list = builder.toString();
		
		return n + list;
	}

}
