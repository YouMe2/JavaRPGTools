package basic;

import roll.RollResult;

public class DiceResult extends RollResult {

	private final int res;
	
	public DiceResult(int res, DiceRoll roll) {
		super(roll);
		this.res = res;
	}

	@Override
	public String getSingleLineMsg() {
		String n = "";
		if (hasName())
			n = getName() + ": ";
		return n + res;
	}

	@Override
	public String toPlainText() {
		return String.valueOf(res);
	}

	@Override
	public String getMultiLineMsg() {
		
		return getRoll().toString() + ": " + res;
		
	}

	@Override
	public String getInLineMsg() {
		return String.valueOf(res);
	}

}
