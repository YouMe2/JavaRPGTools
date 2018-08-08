package basic;

public class RollKey extends Rollable {

	final String rollname;
	
	public RollKey(String rollname) {
		super(null); // wont be added to the list of rollabels (danger of recursion!)
		this.rollname = rollname;
	}

	@Override
	public RollResult roll() {
		
		Rollable roll = getRollable(this);
		
		if (roll != null)
			return roll.roll();
		else
			return new RollResult() {
				
				@Override
				public String simple() {
					return plain();
				}
				
				@Override
				public String plain() {
					return "No roll found under this name: " + rollname;
				}
				
				@Override
				public String detailed() {
					return plain();
				}
			};
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if ( !(obj instanceof RollKey) )
			return false;
		RollKey other = (RollKey) obj;
		return this.rollname.equals(other.rollname);
	}
	
	@Override
	public RollKey getRollKey() throws IllegalAccessException {
		return this;
	}

}
