package basic;

import java.text.ParseException;
import java.util.Arrays;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class TextRoll extends Rollable {


	public static final String TEXTOPENER = "\"";
	public static final String TEXTCLOSER = "\"";
	
	public static final String INLINEOPENER = "$(";
	public static final String INLINECLOSER = ")";
	
	private final String[] texts;
	private final Rollable[] rolls;
	private final int length;
	
	public TextRoll(String[] texts, Rollable[] rolls) {
		super(null);
		this.texts = texts!=null ? texts : new String[0];
		this.rolls = rolls!=null ? rolls : new Rollable[0];
		assert this.texts != null;
		assert this.rolls != null;
		this.length = Math.max(this.texts.length, this.rolls.length);
		
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
	public TextResult roll() {
		
		return new TextResult(texts, getRandomRollResults(), this);
	}
	
	public int getLength() {
		return length;
	}

	@Override
	public String toString() {
		//text $rollable text ...
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(TEXTOPENER);
		
		for (int i = 0; i < getLength(); i++) {
			if (i < texts.length)
				builder.append(texts[i]);
			if (i < rolls.length) {
				builder.append(INLINEOPENER);
				builder.append(rolls[i]); //rolls dont add () on their own
				builder.append(INLINECLOSER);			
			}
		}
		

		builder.append(TEXTCLOSER);
		
		return builder.toString();
		
	}
	
	

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TextRoll))
			return false;
		TextRoll other = (TextRoll) o;
		return this.hasName() == other.hasName()
				&& (this.hasName() ? this.getName().equals(other.getName()) : true)
				&& Arrays.equals(this.texts, other.texts)
				&& Arrays.equals(this.rolls, other.rolls);
	}

	
	public static void main(String[] args) {
		System.out.println("TEXTROLL TEST");
		Object[][] examples = { 
				{"text", new TextRoll(new String[] {"text"}, null)},
				{"$(d6)", new TextRoll(null, new Rollable[]{new DiceRoll("", new DiceRoll.DieRoll(1, 6, 0, 0, false, true))})},
				{"textI$([3: d4])textII", new TextRoll(new String[] {"textI", "textII"}, new Rollable[]{new ListRoll(new DiceRoll("", new DiceRoll.DieRoll(3, true)), new Rollable[] {new DiceRoll("", new DiceRoll.DieRoll(1, 4, 0, 0, false, true))})})},
				};

		for (Object[] exa : examples) {
			
			String str = (String) exa[0];
			TextRoll tr = (TextRoll) exa[1];
			
			System.out.println("Example:  " + str);
			System.out.println("Real:     " + tr);
			
			RollParser parser = new RollParser(str);
			
			TextRoll trparse;
			try {
				trparse = parser.parseTextRoll();

				assert tr.equals(trparse);
				
				System.out.println("Parse:    " + trparse);
				System.out.println("Msg:      " + trparse.roll().getMultiLineMsg());
				
			} catch (ParseException e) {
				e.printStackTrace();
//				fail();
			}
			
			
			
		}
	}
	
}
