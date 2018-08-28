package basic;

import roll.RollResult;

public class TableResult extends RollResult {

	private final InlineResult res;
	private final int n;
	
	public TableResult(InlineResult res, int n, TableRoll roll) {
		super(roll);
		this.res = res;
		this.n = n;
	}

	@Override
	public String plainText() {
		return res.plainText();
	}

	@Override
	public String simpleMsg() {
		return getName() + ": " +  res.simpleMsg();
	}

	@Override
	public String detailedMsg() {
		return getName() + " (" + n + "):" + System.lineSeparator() + res.simpleMsg();
	}

}
