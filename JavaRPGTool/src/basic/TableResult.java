package basic;

import roll.RollResult;

public class TableResult extends RollResult {

	private final TextResult res;
	private final int n;
	
	public TableResult(TextResult res, int n, TableRoll roll) {
		super(roll);
		this.res = res;
		this.n = n;
	}

	@Override
	public String toPlainText() {
		return res.toPlainText();
	}

	@Override
	public String getSingleLineMsg() {
		return getName() + ": " +  res.getSingleLineMsg();
	}

	@Override
	public String getMultiLineMsg() {
		return getName() + " (" + n + "):" + System.lineSeparator() + res.getSingleLineMsg();
	}

	@Override
	public String getInLineMsg() {
		return res.getInLineMsg();
	}

}
