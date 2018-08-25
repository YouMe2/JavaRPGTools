package basic;

import roll.RollResult;

public class TableResult extends RollResult {

	private final InlineResult res;
	
	public TableResult(InlineResult res, TableRoll roll) {
		super(roll);
		this.res = res;
	}

	@Override
	public String plainText() {
		return res.plainText();
	}

	@Override
	public String simpleMsg() {
		return getName() + ": " + res + " -> " + res.simpleMsg();
	}

	@Override
	public String detailedMsg() {
		return simpleMsg();
	}

}
