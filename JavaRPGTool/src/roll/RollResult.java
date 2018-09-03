package roll;

/**
 * @author YaAlex
 *
 */
public abstract class RollResult extends Rollable{
	
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
		return toPlainText();
	}

//	public String toString(int mode) {
//		switch (mode) {
//		case SIMPLE:	
//			return simpleMsg();
//
//		case DETAILED:
//			return detailedMsg();
//			
//		case PLAIN:
//		default:
//			return plainText();
//
//		}
//	}

	/**
	 * result in plain (inline) format without further eval parsable
	 * @return
	 */
	public abstract String toPlainText();
	/**
	 * result in plain inline format with full eval
	 * @return
	 */
	public abstract String getInLineMsg();
	/**
	 * result is a single line
	 * @return
	 */
	public abstract String getSingleLineMsg();
	/**
	 * result may be multiline
	 * @return
	 */
	public abstract String getMultiLineMsg();
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof RollResult))
			return false;
		RollResult other = (RollResult) obj;
		return other.toPlainText().equals(this.toPlainText());
	}
}
