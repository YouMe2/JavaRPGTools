package basic;

public class RollName extends Rollable {

	
	public RollName(String name) {
		super(name);
	}

	@Override
	public RollResult roll() {
		
		Rollable roll = getRollable(this.getRollName());
		
		if (roll != null)
			return roll.roll();
		else {
			return new RollResult() {
				
				@Override
				public String simple() {
					return plain();
				}
				
				@Override
				public String plain() {
					return "No roll found under this name: " + getName();
				}
				
				@Override
				public String detailed() {
					return plain();
				}
			};	
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if ( !(obj instanceof RollName) )
			return false;
		RollName other = (RollName) obj;
		return this.getName().equals(other.getName());
	}

	@Override
	public String toString() {
		return Rollable.getRollable(this.getRollName()).toString();
	}
}
