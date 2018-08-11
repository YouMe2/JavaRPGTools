package basic;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class DiceRoll extends Rollable {

	public static final boolean ROLLTYPE_NORMAL = false;
	public static final boolean ROLLTYPE_EXPLODING = true;

	private final SecureRandom rng;

	private final int n, die, droplowest, drophighest, mod;
	private final boolean exploding;


	// immutable
	public DiceRoll(int n, int die, int dl, int dh, int mod, boolean exploding, String name) {
		super(name);
		if (n < 1 || die < 1 || dl < 0 || dh < 0 || dh + dl >= n)
			throw new IllegalArgumentException();
		this.n = n;
		this.die = die;
		this.droplowest = dl;
		this.drophighest = dh;
		this.mod = mod;
		this.exploding = exploding;
		rng = new SecureRandom();
	}

	@Override
	public RollResult roll() {
		int res = getRandomRollValue();	
		
		return new RollResult() {
			
			@Override
			public String simple() {
				String n = "";
				if (hasName())	
					n = getName() + ": ";
				return n + res;
			}
			
			@Override
			public String plain() {
				return String.valueOf(res);
			}
			
			@Override
			public String detailed() {
				String n = "";
				if (hasName())
					n = "Rolling \"" + getName() +  "\": ";
				else
					n = "Rolling \"" + DiceRoll.this + "\": ";
				return n + res;
			}
		};
		
		
	}
	
	public int getRandomRollValue() {
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

		return Arrays.stream(rolls).sum() + mod;
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
				+ ((getName() == null || getName().isEmpty()) ? "" : " \"" + getName()+"\"");
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
				&& (this.hasName() == other.hasName())
				&& (this.hasName() ? this.getName().equals(other.getName()) : true);
	}

	public static void main(String[] args) {

		System.out.println("DICEROLL TEST");
		String[] examples = { "d6", "4d8 +5", "d20-8", "10d6-2", "d20 ! +7", "d8", "1d8+0", "10d20!dh2dl2+5",
				"4d20! dl2 -5", "d20 +8 Skillcheck", "d10 Test Name", "d10 \"Test Name\"" };

		for (String exa : examples) {
			try {
				System.out.println("Example: " + exa);

				DiceRoll d = new RollParser(exa).parseDiceRoll();
				
				System.out.println("Roll:    " + d.toString());
				assert d.equals(Rollable.valueOf(d.toString()));


				System.out.println("Msg:" + System.lineSeparator()
					+ d.roll().toString(RollResult.SIMPLE) + System.lineSeparator()
					+ d.roll().toString(RollResult.DETAILED) + System.lineSeparator());
				
				int res = d.getRandomRollValue();
				assert res >= d.minResult();
				assert res <= d.maxResult();
			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}

	}

}
