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
	public String getSingleLineMsg() {
		String n = "";
		String list = "";
		if ( hasName())
			n = getName() + ": ";
//		old
		list = Arrays.toString(Arrays.stream(res).map(r -> r.getSingleLineMsg()).toArray());
		return n + list;
	}
	
	@Override
	public String toPlainText() {
		return Arrays.toString(Arrays.stream(res).map(r -> r.toPlainText()).toArray());
	}
	
	@Override
	public String getMultiLineMsg() {
		String n = "";
		String list = "";
		if ( hasName())
			n = getName() + ":" + System.lineSeparator();
		
		
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append(res[0].getSingleLineMsg());
		for (int i = 1; i < res.length; i++) {
			builder.append(',');
			builder.append(System.lineSeparator());
			builder.append(res[i].getSingleLineMsg());
			
		}
		builder.append("]");
		list = builder.toString();
		
		return n + list;
	}

	@Override
	public String getInLineMsg() {
		return Arrays.toString(Arrays.stream(res).map(r -> r.getInLineMsg()).toArray());
	}

}
