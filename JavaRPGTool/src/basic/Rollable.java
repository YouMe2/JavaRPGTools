package basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Rollable {
	
//STATIC
	private static Map<RollKey, Rollable> rollables = new HashMap<>();
	
	public static Rollable getRollable(RollKey key) {
		return rollables.get(key);
	}
	
	public static boolean hasRollable(RollKey key) {
		return rollables.containsKey(key);
	}
	
	public static void removeRollable(RollKey key) {
		rollables.remove(key);
	}
	
	public static Collection<Rollable> getRollables() {
		return rollables.values();
	}

	public static Collection<RollKey> getRollKeys() {
		return rollables.keySet();
	}
	
// INSTANCE
	private final String name;
	private RollKey rollkey;
	
	public Rollable(String name) {
		this.name = name;	
		
		try {
			rollables.put(getRollKey(), this);
		} catch (IllegalAccessException e) {
			//nothing here, this rollabel just wont be reachable thru a key.
//			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
	}
	
	public RollKey getRollKey() throws IllegalAccessException {
		if (!hasName())
			throw new IllegalAccessException("This rollable has no name and therefore has no key.");
		return rollkey == null ? rollkey = new RollKey(getName()) : rollkey;
	}
	
//	public String getRollMessage(int mode) {
//		return this.roll().toString(mode);
//	}
	
	public abstract RollResult roll();

}
