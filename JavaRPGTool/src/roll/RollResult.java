package roll;

/**
 * @author YaAlex
 *
 */
public abstract class RollResult extends Rollable{
	/**
	 * res
	 */
	public static final int PLAIN = 0;
	/**
	 * Name: res
	 */
	public static final int SIMPLE = 1;
	/**
	 * Rolling "Name": res
	 */
	public static final int DETAILED = 2;
	
	private final Rollable roll;
	public RollResult(Rollable roll) {
		super(roll != null ? roll.getName() : null);
		this.roll = roll;
	}

	public Rollable getRoll() {
		return roll;
	}
	
	@Override
	public RollResult roll() {
		return this;
	}
	
	@Override
	public String toString() {
		return this.toString(PLAIN);
	}

	public String toString(int mode) {
		switch (mode) {
		case SIMPLE:	
			return simpleMsg();

		case DETAILED:
			return detailedMsg();
			
		case PLAIN:
		default:
			return plainText();

		}
	}

	/**
	 * result is parsable
	 * @return
	 */
	public abstract String plainText();
	/**
	 * result is a single line
	 * @return
	 */
	public abstract String simpleMsg();
	/**
	 * result may be multiline
	 * @return
	 */
	public abstract String detailedMsg();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof RollResult))
			return false;
		RollResult other = (RollResult) obj;
		return other.plainText().equals(this.plainText());
	}
}
