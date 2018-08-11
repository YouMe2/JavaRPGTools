package roll;

public abstract class RollResult {
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
	
	public RollResult() {
		//TODO rework to feature a list of strings and rollables to intercalate...
	}

	@Override
	public String toString() {
		return this.toString(PLAIN);
	}

	public String toString(int mode) {
		switch (mode) {
		case SIMPLE:	
			return simple();

		case DETAILED:
			return detailed();
			
		case PLAIN:
		default:
			return plain();

		}
	}

	public abstract String plain();
	public abstract String simple();
	public abstract String detailed();
	
	public static class PlainResult extends RollResult{

		private final String msg;
		public PlainResult(String msg) {
			this.msg = msg;
		}
		
		@Override
		public String plain() {
			return msg;
		}

		@Override
		public String simple() {
			return plain();
		}

		@Override
		public String detailed() {
			return plain();
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof RollResult))
			return false;
		RollResult other = (RollResult) obj;
		return other.plain().equals(this.plain());
	}
}
