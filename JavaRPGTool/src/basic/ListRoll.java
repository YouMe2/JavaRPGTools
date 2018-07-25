package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

import basic.DiceRoll.DiceRollParser;

public class ListRoll implements Rollable {

	private final DiceRoll[] rolls;
	
	public ListRoll(DiceRoll[] rolls) {
		Objects.requireNonNull(rolls);
		if (rolls.length == 0) throw new IllegalArgumentException("no empty listrolls");
		this.rolls = rolls;
	}

	@Override
	public int[] roll() {
		int[] res = new int[rolls.length];
		
		for (int i = 0; i < res.length; i++) {
			res[i] = rolls[i].roll();
		}
		Arrays.sort(res);
		return res;
	}

	public static ListRoll valueOf(String input){
		if (input == null || input.isEmpty())
			throw new IllegalArgumentException("input may not be empty");
		
	}
	
	private static class ListRollParser {
		private CharSequence chars;
		private int offset; // pointer to next to parse
		
		public ListRollParser(CharSequence chars) {
			this.chars = chars;
			offset = 0;
		}
	}
	
}
