package basic;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;

import roll.RollParser;
import roll.RollResult;
import roll.Rollable;

public class DiceRoll extends Rollable {

	public static final String EXPLODING = "!";
	public static final String DROPLOWEST = "dl";
	public static final String DROPHIGHEST = "dh";
	public static final char[] DIE = new char[] {'d', 'D'};
	
	public static final int MAXEXPLOSIONRESULT = 10000; 
	
	private final DieRoll[] dice;
	
	private final boolean exploding;
	private final int max, min;
	
	// immutable
	public DiceRoll(String name, DieRoll... dice) {
		super(name);
		this.dice = dice;
		
		exploding = Arrays.stream(dice).anyMatch(d -> d.isExploding());
		
		int mint = 0, maxt = 0;
		for (DieRoll dieRoll : dice) {
			mint += dieRoll.getMinResult();
			maxt += dieRoll.getMaxResult();
		}
		min = mint;
		max = maxt;
		assert this.getMinResult() <= this.getMaxResult();
	}

	@Override
	public RollResult roll() {
		int res = getRandomRollValue();

		return new DiceResult(res, this);
	}

	public int getRandomRollValue() {
		
		int res = 0;
		
		for (DieRoll dieRoll : dice) {			
			res += dieRoll.getRandomRollValue();
		}
		return res;
	}

	@Override
	public String toString() {

		// "Some Name" dieroll + ... + dieroll
		String name = ((getName() == null || getName().isEmpty()) ? "" : " \"" + getName() + "\"");
		
		
		StringBuilder builder = new StringBuilder();
		
//		builder.append('(');
		
		builder.append(dice[0].toString());
		
		
		for (int i = 1; i < dice.length; i++) {
			builder.append((dice[i].isPositiv() ? " +" : " "));
			builder.append(dice[i].toString());
		}
		
//		for (DieRoll dieRoll : dice) {
//			builder.append(dieRoll.toString());
//			builder.append(' ');
//		}
		builder.append(name);
//		builder.append(')');
		
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DiceRoll))
			return false;
		DiceRoll other = (DiceRoll) o;
		return Arrays.equals(this.getDice(), other.getDice());
	}

	private DieRoll[] getDice() {
		return dice;
	}
	
	public int getMinResult() {
		return min;
	}

	public int getMaxResult() {
		return max;
	}

	public boolean isExploding() {
		return exploding;
	}
	
	public static class DieRoll extends Rollable {

		private final int n, die, droplowest, drophighest, maxflat, minflat, min, max;
		private final boolean exploding, positiv, constant;
		private final SecureRandom rng = new SecureRandom();;
		
		/**
		 * @param n > 0
		 * @param die > 1
		 * @param dl > -1
		 * @param dh > -1
		 * @param exploding
		 * @param positiv
		 */
		public DieRoll(int n, int die, int dl, int dh, boolean exploding, boolean positiv) {
			super(null);
			if (n < 1 || die < 2 || dl < 0 || dh < 0 || dh + dl >= n)
				throw new IllegalArgumentException();		
			this.n = n;
			this.die = die;
			this.drophighest = dh;
			this.droplowest = dl;
			this.exploding = exploding;
			this.positiv = positiv;
			this.constant = false;
			
			maxflat = (isExploding() ?
					MAXEXPLOSIONRESULT 
					: (getN() - getDrophighest() - getDroplowest()) * getDie());
			minflat = (getN() - getDrophighest() - getDroplowest());
			
			min = isPositiv() ? minflat : getMod()*maxflat;
			max = isPositiv() ? maxflat : getMod()*minflat;
			
			assert getMinResult() < getMaxResult();
		}

		/**
		 * Constructs a constant. 
		 * 
		 * @param n > 0
		 * @param positiv
		 */
		public DieRoll(int n, boolean positiv) {
			super(null);
			if (n < 0)
				throw new IllegalArgumentException();	
			this.n = n;
			this.die = 0;
			this.drophighest = 0;
			this.droplowest = 0;
			this.exploding = false;
			this.positiv = positiv;
			this.constant = true;
			
			maxflat = minflat = n;
			max = min = getMod() * n;
			assert getMinResult() == getMaxResult();
			
		}

		@Override
		public RollResult roll() {
			//should not be done on its own
			assert false;
			return null;
		}

		@Override
		public String toString() {

			//Syntax:
			//+ 1d20! dh1 dl1
			//- 12

			if (isConstant())
				return (isPositiv()?"": "-") + String.valueOf(n);

			return (isPositiv() ? "": "-") 
					+ (getN() == 1 ? "" : getN())
					+ DIE[0] 
					+ getDie()
					+ (isExploding() ? EXPLODING : "")
					+ (getDrophighest() != 0 ? " "+DROPHIGHEST + getDrophighest() : "") 
					+ (getDroplowest() != 0 ? " "+DROPLOWEST + getDroplowest() : "");
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof DieRoll))
				return false;
			DieRoll other = (DieRoll) o;
			return this.getN() == other.getN() 
					&& this.getDie() == other.getDie()
					&& this.getDrophighest() == other.getDrophighest()
					&& this.getDroplowest() == other.getDroplowest()
					&& this.isExploding() == other.isExploding()
					&& this.isPositiv() == other.isPositiv()
					&& this.isConstant() == other.isConstant();
		}

		public int getRandomRollValue() {
			if (isConstant())
				return getMod() * getN();
			
			
			int[] rolls = new int[getN()];

			for (int i = 0; i < rolls.length; i++) {
				int r;
				do {
					r = 1 + rng.nextInt(getDie()); // do a roll
					rolls[i] += r;
					if (rolls[i] > MAXEXPLOSIONRESULT)
						rolls[i] = MAXEXPLOSIONRESULT;

				} while (isExploding()&& r == getDie() && rolls[i] < MAXEXPLOSIONRESULT );

			}

			Arrays.sort(rolls);
			rolls = Arrays.copyOfRange(rolls, getDroplowest(), getN() - getDrophighest());
			int res = getMod() *Arrays.stream(rolls).sum();
//			System.out.println(res);
			assert res <= getMaxResult() && res >= getMinResult();
			return res;
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

		public int getMinResult() {
			return min;
		}

		public int getMaxResult() {
			return max;
		}

		public boolean isExploding() {
			return exploding;
		}
		
		public boolean isConstant() {
			return constant;
		}
		
		public boolean isPositiv() {
			return positiv;
		}
		
		public int getMod() {
			return isPositiv() ? 1 : -1;
		}

	}

	// TESTING
	public static void main(String[] args) {

		System.out.println("DICEROLL TEST");
		String[] examples = { "d6", "4d8 +5", "d20-8", "10d6 !-2", "d20 ! +1", "d8", "1d8+0", "10d20!dh2dl2+5",
				"4d20! dl2 -5", "d20 +8 Skillcheck", "d10 \"Test Name\"", "d2 d2 - d2 0", "4d20!dl1dh1 + 4d20dh1dl1" };

		for (String exa : examples) {
			try {
				System.out.println("Example: " + exa);

				DiceRoll d = new RollParser(exa).parseDiceRoll();

				System.out.println("Roll:    " + d.toString());
				assert d.equals(Rollable.valueOf(d.toString()));

				System.out.println("Res:" + System.lineSeparator()
					+ d.roll().toString(RollResult.SIMPLE) + System.lineSeparator()
					+ d.roll().toString(RollResult.DETAILED) + System.lineSeparator());

				int res = d.getRandomRollValue();
				assert res >= d.getMinResult();
				assert res <= d.getMaxResult();
			} catch (ParseException e) {
				e.printStackTrace();
				System.err.println(e.getErrorOffset());
			}
		}

	}

}
