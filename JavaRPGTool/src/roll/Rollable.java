package roll;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.NameRoll;
import util.Pair;

public abstract class Rollable {
	
//STATIC
	private static Map<String, Rollable> rollables = new HashMap<>();
	
	public static Rollable getRollable(String name) {
		return rollables.get(name);
	}
	
	public static void addRollable(Rollable rollable) throws IllegalArgumentException {
		if (rollable instanceof NameRoll)
			throw new IllegalArgumentException("RollNames may not be added. Please add a real rollable.");
		
		if (!rollable.hasName())
			throw new IllegalArgumentException("Rollable needs a name to ba added.");
		rollables.put(rollable.getName(), rollable);
		assert hasRollable(rollable.getName()) && hasRollable(rollable.getName());
	}
	
	public static void addRollables(List<Rollable> rollables) throws IllegalArgumentException {			
		for (Rollable rollable : rollables) {
			addRollable(rollable);
		}
	}
	
	public static boolean hasRollable(String name) {
		return rollables.containsKey(name);
	}
	
	public static void removeRollable(String name) {
		rollables.remove(name);
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
	
// INSTANCE
	private final String name;
	
	public Rollable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
	}
	
	public abstract RollResult roll();
	
	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object obj);
}
