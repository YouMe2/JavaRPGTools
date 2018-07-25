package basic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Random;

public class DiceRoll implements Rollable {

	public static final boolean ROLLTYPE_NORMAL = false;
	public static final boolean ROLLTYPE_EXPLODING = true;

	private final Random rng;

	private final int n, die, droplowest, drophighest, mod;
	private final boolean exploding;

	// immutable
	public DiceRoll(int n, int die, int dl, int dh, int mod, boolean exploding) {
		if (n < 1 || die < 1 || dl < 0 || dh < 0 || dh + dl >= n)
			throw new IllegalArgumentException();
		this.n = n;
		this.die = die;
		this.droplowest = dl;
		this.drophighest = dh;
		this.mod = mod;
		this.exploding = exploding;
		rng = new Random();
	}

	public DiceRoll(int n, int die, int mod) {
		this(n, die, 0, 0, mod, ROLLTYPE_NORMAL);
	}

	public DiceRoll(int n, int die) {
		this(n, die, 0);
	}

	@Override
	public Integer roll() {
		int[] rolls = new int[n];

		for (int i = 0; i < rolls.length; i++) {
			int r;
			do {
				r = 1 + rng.nextInt(die); // do a roll
				rolls[i] += r;

			} while (exploding == ROLLTYPE_EXPLODING && r == die);

		}

		// Arrays.stream(rolls).min().getAsInt();
		Arrays.sort(rolls);
		rolls = Arrays.copyOfRange(rolls, droplowest, n - drophighest);

		int res = Arrays.stream(rolls).sum();
		return res;
	}

	public Integer roll(int mod) {
		return roll() + mod;
	}

	public Integer rollAdvantage() {
		return Math.max(roll(), roll());
	}

	public Integer rollDisadvantage() {
		return Math.min(roll(), roll());
	}

	public int getN() {
		return n;
	}

	public int getDie() {
		return die;
	}

	public int getDroplowest() {
		return droplowest;
	}

	public int getDrophighest() {
		return drophighest;
	}

	public int getMod() {
		return mod;
	}

	public int minResult() {
		return (n -drophighest -droplowest) + mod;
	}
	
	public int maxResult() {
		return exploding ? Integer.MAX_VALUE : (n -drophighest -droplowest)*die + mod;
	}
	
	public boolean isExploding() {
		return exploding;
	}

	@Override
	public String toString() {

		return (n==1 ? "" : n) + "d" + die + (drophighest != 0 ? "dh" + drophighest : "") + (droplowest != 0 ? "dl" + droplowest : "")
				+ (exploding == ROLLTYPE_EXPLODING ? "!" : "") + (mod < 0 ? mod : (mod > 0 ? "+" + mod : ""));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DiceRoll))
			return false;
		DiceRoll other = (DiceRoll) o;
		return this.getN() == other.getN() && this.getDie() == other.getDie()
				&& this.getDrophighest() == other.getDrophighest() && this.getDroplowest() == other.getDroplowest()
				&& this.getMod() == other.getMod() && this.isExploding() == other.isExploding();
	}

	public static DiceRoll valueOf(String input){
		if (input == null || input.isEmpty())
			throw new IllegalArgumentException("input may not be empty");
		
		try {
			return new DiceRollParser(input.replace(" ", "")).parse();
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("ErrOffSET: " + e.getErrorOffset());
		}
		return new DiceRoll(1, 1);
	}
	
	public static void main(String[] args) {
		
		String[] examples
		= {"d6", "4d8 +5", "d20-8", "10d6-2", "d20+7", "d8", "1d8+0", "10d20dh2dl2!+5", "4d20 dl2 ! -5"};
		
		for (String exa : examples) {
			
			System.out.println("Example: "+exa);
			
			DiceRoll d = DiceRoll.valueOf(exa);
			
			System.out.println("Roll:    "+d.toString());
			
			assert d.equals(DiceRoll.valueOf(d.toString()));
			
			int res = d.roll();
			
			System.out.println("roll: " +res);
			assert res >= d.minResult() && res <= d.maxResult();
			
		}
		
		
	}
	
	private static class DiceRollParser {
		private CharSequence chars;
		private int offset; // pointer to next to parse

		public DiceRollParser(CharSequence chars) {
			this.chars = chars;
			offset = 0;
		}

		public DiceRoll parse() throws ParseException {

			final int n, die, dh, dl, mod;
			final boolean exploding;

			// 10d20dh2dl2!+5

			n = isNextDigit() ? parseInteger() : 1;
			if (!isNextAnyOf('d', 'D'))
				throw new ParseException("expected d or D", offset);
			skip(1);	
			die = parseInteger();

			if (isNextSeq("dh")) {
				skip(2);
				dh = parseInteger();
			} else
				dh = 0;
			
			if (isNextSeq("dl")) {
				skip(2);
				dl = parseInteger();
			} else
				dl = 0;
			
			if (isNextAnyOf('!')) {
				skip(1);
				exploding = DiceRoll.ROLLTYPE_EXPLODING;
			} else
				exploding = DiceRoll.ROLLTYPE_NORMAL;
			
			if (isNextAnyOf('+', '-')) {
				mod = parseInteger();
			} else
				mod = 0;
			
			return new DiceRoll(n, die, dl, dh, mod, exploding);
		}

		public CharSequence getRest() {
			return chars.subSequence(offset, chars.length());
		}
		
		private int parseInteger() throws ParseException {
			if (!isNextDigit() && !isNextAnyOf('-', '+'))
				throw new ParseException("expecting digit", offset);
			StringBuilder builder = new StringBuilder();
			do {
				builder.append(next());
			} while (isNextDigit());
			return Integer.valueOf(builder.toString());
		}

		private boolean isNextSeq(CharSequence seq) {
			for (int i = 0; i < seq.length(); i++) {
				if (offset+i >= chars.length() || chars.charAt(offset + i) != seq.charAt(i))
					return false;
			}
			return true;
		}

		private boolean isNextDigit() {
			return hasNext() && Character.isDigit(chars.charAt(offset));
		}

		private boolean isNextAnyOf(char... characters) {
			if (!hasNext())
				return false;
			for (char character : characters)
				if (chars.charAt(offset) == character)
					return true;
			return false;
		}

		private char next() throws ParseException {
			if (!hasNext())
				throw new ParseException("unexpected end", offset);
			return chars.charAt(offset++);
		}

		private void skip(int n) throws ParseException {
			if (n < 1)
				return;
			if (!hasNext())
				throw new ParseException("unexpected end", offset);
			offset++;
			skip(n - 1);
		}

		private boolean hasNext() {
			return offset < chars.length();
		}

	}
}
