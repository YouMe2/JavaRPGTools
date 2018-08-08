package basic;

public interface RollableX {

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
	
	public Object roll();
	public String getRollMessage(int mode);
	public String getName();
	public boolean hasName();
}
