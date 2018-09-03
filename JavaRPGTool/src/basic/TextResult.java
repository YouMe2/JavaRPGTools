package basic;

import org.junit.jupiter.api.Test;

import roll.RollResult;

public class TextResult extends RollResult {

	private final String[] texts;
	private final RollResult[] res;
	private final int maxlength;
	
	public TextResult(String[] texts, RollResult[] res, TextRoll roll) {
		super(roll);
		this.texts = texts;
		this.res = res;
		this.maxlength = Math.max(texts.length, res.length);
		
		if (getLength() < 1)
			throw new IllegalArgumentException("no empty inline roll");	
	}

	@Override
	public String getSingleLineMsg() {
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < res.length) {
				builder.append(res[i].getInLineMsg());		
			}
		}
		return builder.toString();
	}
	
	@Override
	public String toPlainText() {
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < res.length) {
				builder.append(res[i].toPlainText());		
			}
		}
		return builder.toString();
	}
	
	@Override
	public String getMultiLineMsg() {
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < res.length) {
				builder.append(res[i].getSingleLineMsg());		
			}
		}
		return builder.toString();
	}
	
	@Override
	public String getInLineMsg() {
		return getSingleLineMsg();
	}
	
	private int getLength() {
		return maxlength;
	}
	


	
}
