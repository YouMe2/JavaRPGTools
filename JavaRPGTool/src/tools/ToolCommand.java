package tools;

public abstract class ToolCommand {

	private final String commandKey_full;
	private final String commandKey_alt;
	private final String description;
	
	public ToolCommand(String commandKey_full, String commandKey_alt, String desc) {
		this.commandKey_full = commandKey_full;
		this.commandKey_alt = commandKey_alt;
		this.description = desc;
	}

	public String getInfoText() {
		return getCommandKey_full()+", "+getCommandKey_alt()+": "+getDescription();
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getCommandKey_full() {
		return commandKey_full;
	}
	
	public String getCommandKey_alt() {
		return commandKey_alt;
	}
	
	public abstract void action(String options);
	
}
