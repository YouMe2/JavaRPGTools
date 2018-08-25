package basic;

import roll.RollResult;
import roll.Rollable;

public class NameRoll extends Rollable {
	
	public NameRoll(String name) {
		super(name);
		if (!hasName())
			throw new IllegalArgumentException("namerolls have to have a name, no empty name allowed");
		
	}

	@Override
	public RollResult roll() {
		
		Rollable roll = getRollable(this.getName());
		
		if (roll != null)
			return roll.roll();
		else {
			return new RollResult(this) {
				
				@Override
				public String simpleMsg() {
					return "No roll found under this name: " + getName();
				}
				
				@Override
				public String plainText() {
					return NameRoll.this.getName();
				}
				
				@Override
				public String detailedMsg() {
					return simpleMsg();
				}
			};	
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if ( !(obj instanceof NameRoll) )
			return false;
		NameRoll other = (NameRoll) obj;
		return this.getName().equals(other.getName());
	}

	@Override
	public String toString() {
		
//		Rollable roll = getRollable(this.getName());
//		
//		if (roll != null)
//			return roll.toString();
//		else
			return getName();
	}
}
