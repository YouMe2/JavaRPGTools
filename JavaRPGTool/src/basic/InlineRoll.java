package basic;

import java.util.Arrays;

import roll.RollResult;
import roll.Rollable;

public class InlineRoll extends Rollable {

	public static final String OPENER = "$(";
	public static final String CLOSER = ")";
	
	private final String[] texts;
	private final Rollable[] rolls;
	private final int length;
	
	public InlineRoll(String[] texts, Rollable[] rolls) {
		super(null);
		this.texts = texts;
		this.rolls = rolls;
		this.length = Math.max(texts.length, rolls.length);
		
		if (getLength() < 1)
			throw new IllegalArgumentException("no empty inline roll");	
	}

	public RollResult[] getRandomRollResults() {
		RollResult[] res = new RollResult[rolls.length];

		for (int i = 0; i < res.length; i++) {
			res[i] = rolls[i].roll();
		}
		return res;
	}
	
	@Override
	public InlineResult roll() {
		
		return new InlineResult(texts, getRandomRollResults(), this);
	}
	
	public int getLength() {
		return length;
	}

	@Override
	public String toString() {
		//text $rollable text ...
		
		StringBuilder builder = new StringBuilder();		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < rolls.length) {
				builder.append(OPENER);
				builder.append(rolls[i]); //rolls dont add () on their own
				builder.append(CLOSER);			
			}
		}
		return builder.toString();
		
	}
	
	

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof InlineRoll))
			return false;
		InlineRoll other = (InlineRoll) o;
		return this.hasName() == other.hasName()
				&& (this.hasName() ? this.getName().equals(other.getName()) : true)
				&& Arrays.equals(this.texts, other.texts)
				&& Arrays.equals(this.rolls, other.rolls);
	}

}
