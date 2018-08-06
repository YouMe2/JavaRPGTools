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

	private final String name;

	// immutable
	public DiceRoll(int n, int die, int dl, int dh, int mod, boolean exploding, String name) {
		if (n < 1 || die < 1 || dl < 0 || dh < 0 || dh + dl >= n)
			throw new IllegalArgumentException();
		this.n = n;
		this.die = die;
		this.droplowest = dl;
		this.drophighest = dh;
		this.mod = mod;
		this.exploding = exploding;
		this.name = name;
		rng = new Random();
	}

	public DiceRoll(int n, int die, int dl, int dh, int mod, boolean exploding) {
		this(n, die, dl, dh, mod, exploding, null);
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
		return res + mod;
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

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
	}
	
	public int minResult() {
		return (n - drophighest - droplowest) + mod;
	}

	public int maxResult() {
		return exploding ? Integer.MAX_VALUE : (n - drophighest - droplowest) * die + mod;
	}

	public boolean isExploding() {
		return exploding;
	}

	@Override
	public String toString() {

		return (n == 1 ? "" : n) 
				+ "d" 
				+ die
				+ (exploding == ROLLTYPE_EXPLODING ? "!" : "")
				+ (drophighest != 0 ? " dh" + drophighest : "")
				+ (droplowest != 0 ? " dl" + droplowest : "") 
				+ (mod < 0 ? " " + mod : (mod > 0 ? " +" + mod : ""))
//				+ ((getName() == null || getName().isEmpty()) ? "" : " " + getName())
				;
	}

	@Override
	public String getRollMessage(int mode) {
		
		Integer roll = roll();
		String n = "";
		
		switch (mode) {
		case SIMPLE:
			
			if (hasName())	
				n = getName() + ": ";
			return n + roll;

		case DETAILED:
			if (hasName())
				n = "Rolling \"" + getName() +  "\": ";
			else
				n = "Rolling \"" + this + "\": ";
			return n + roll;
		case PLAIN:
		default:
			return roll.toString();

		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DiceRoll))
			return false;
		DiceRoll other = (DiceRoll) o;
		return this.getN() == other.getN()
				&& this.getDie() == other.getDie()
				&& this.getDrophighest() == other.getDrophighest()
				&& this.getDroplowest() == other.getDroplowest()
				&& this.getMod() == other.getMod()
				&& this.isExploding() == other.isExploding()
//				&& this.getName().equals(other.getName())
				;
	}

	public static void main(String[] args) {

		System.out.println("DICEROLL TEST");
		String[] examples = { "d6", "4d8 +5", "d20-8", "10d6-2", "d20 ! +7", "d8", "1d8+0", "10d20!dh2dl2+5",
				"4d20! dl2 -5", "d20 +8 Skillcheck", "d10 Test Name", "d10 \"Test Name\"" };

		for (String exa : examples) {
			try {
				System.out.println("Example: " + exa);

				DiceRoll d = null;
				Rollable r;

				r = RollParser.valueOf(exa);

				if (r instanceof DiceRoll)
					d = (DiceRoll) r;

				System.out.println("Roll:    " + d.toString());
				assert d.equals(RollParser.valueOf(d.toString()));

				Integer res = d.roll();

				System.out.println("Msg:" + System.lineSeparator()
					+ d.getRollMessage(SIMPLE) + System.lineSeparator()
					+ d.getRollMessage(DETAILED) + System.lineSeparator());
				
				assert res >= d.minResult();
				assert res <= d.maxResult();
			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}

	}

}
