package basic;

import roll.RollResult;

public class InlineResult extends RollResult {

	private final String[] texts;
	private final RollResult[] res;
	private final int length;
	
	public InlineResult(String[] texts, RollResult[] res, InlineRoll roll) {
		super(roll);
		this.texts = texts;
		this.res = res;
		this.length = Math.max(texts.length, res.length);
		
		if (getLength() < 1)
			throw new IllegalArgumentException("no empty inline roll");	
	}

	@Override
	public String simpleMsg() {
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < res.length) {
				builder.append(res[i].simpleMsg());		
			}
		}
		return builder.toString();
	}
	
	@Override
	public String plainText() {
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < res.length) {
				builder.append(res[i].plainText());		
			}
		}
		return builder.toString();
	}
	
	@Override
	public String detailedMsg() {
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < res.length) {
				builder.append(res[i].detailedMsg());		
			}
		}
		return builder.toString();
	}
	
	public int getLength() {
		return length;
	}

}
