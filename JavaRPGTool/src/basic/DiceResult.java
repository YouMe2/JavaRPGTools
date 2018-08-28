package basic;

import roll.RollResult;

public class DiceResult extends RollResult {

	private final int res;
	
	public DiceResult(int res, DiceRoll roll) {
		super(roll);
		this.res = res;
	}

	@Override
	public String simpleMsg() {
		String n = "";
		if (hasName())
			n = getName() + ": ";
		return n + res;
	}

	@Override
	public String plainText() {
		return String.valueOf(res);
	}

	@Override
	public String detailedMsg() {
		
		return getRoll() + ": " + res;
		
	}

}
