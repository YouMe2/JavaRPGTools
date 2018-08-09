package basic;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Rollable {
	
//STATIC
	private static Map<String, Rollable> rollables = new HashMap<>();
	
	public static Rollable getRollable(RollName name) {
		return rollables.get(name.getName());
	}
	
	public static void addRollable(Rollable rollable) throws IllegalArgumentException {
		if (!rollable.hasName())
			throw new IllegalArgumentException("Rollable needs a name to ba added.");
		rollables.put(rollable.getName(), rollable);
		assert hasRollable(rollable.getRollName()) && hasRollable(new RollName(rollable.getName()));
	}
	
	public static boolean hasRollable(RollName name) {
		return rollables.containsKey(name.getName());
	}
	
	public static void removeRollable(RollName name) {
		rollables.remove(name.getName());
	}
	
	public static Collection<Rollable> getRollables() {
		return rollables.values();
	}

	public static Collection<String> getRollNames() {
		return rollables.keySet();
		
	}
	
	public static void clearRollables() {
		rollables.clear();
	}
	
// INSTANCE
	private final String name;
	private final RollName rollname;
	
	public Rollable(String name) {
		this.name = name;
		if (this instanceof RollName)
			rollname = (RollName)this;
		else
			this.rollname = new RollName(name);
//		if(tracked && hasName())
//			try {
//				addRollable(this);
//				System.out.println("Added on creation: "+name);
//			} catch (IllegalAccessException e) {
//				//nothing here, this rollabel just wont be reachable thru a key.
//	//			e.printStackTrace();
//		}
	}
	
	public String getName() {
		return name;
	}
	
	public RollName getRollName() {
		return rollname;
	}
	
	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
	}
	
	public String getRollMessage(int mode) {
		return this.roll().toString(mode);
	}
	
	/**
	 * @param input some String
	 * @return a Rollable, but never null
	 * @throws ParseException if no fitting parse was found
	 */
	public static Rollable valueOf(String input) throws ParseException {

		Pair<Rollable, String> p = RollParser.tryParse(input);

		if (p.left == null)
			throw new ParseException("no parse", 0);

		assert p.left != null;
		return p.left;
	}
	
	public abstract RollResult roll();

	@Override
	public abstract String toString();

}
