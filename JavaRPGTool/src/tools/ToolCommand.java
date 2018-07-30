package tools;

import java.util.Map;

public abstract class ToolCommand {

	private final String commandKey_full;
	private final String commandKey_alt;
	private final String options;
	private final String description;
	
	public ToolCommand(String commandKey_full, String commandKey_alt, String options, String desc) {
		this.commandKey_full = commandKey_full;
		this.commandKey_alt = commandKey_alt;
		this.options = options;
		this.description = desc;
	}

	public String getInfoText() {
		return getCommandKey_full()+", "+getCommandKey_alt()+" "+getOptions()+System.lineSeparator()+getDescription();
	}
	
	public String getOptions() {
		return options;
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
	
	public void addTo(Map<String, ToolCommand> map) {
		map.put(getCommandKey_full(), this);
		map.put(getCommandKey_alt(), this);
	}
	
	public abstract void action(String option);
	
}
