package tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import basic.DiceRoll;
import basic.ListRoll;
import basic.RollableTable;

public class RPGTools {

	static final RPGTools instance = new RPGTools();

	static final String NEWLINE = System.lineSeparator();
	static final String WELCOMEMSG = "Welcome to RPGTools!" + NEWLINE + "- by u/YaAlex" + NEWLINE + "Try \"?\" for help.";
	static final String LINEOPENER = ": ";

	public static void main(String[] args) {
		System.out.println(WELCOMEMSG);
		Scanner sc = new Scanner(System.in);

		System.out.print(LINEOPENER);
		while (sc.hasNextLine()) {
			RPGTools.getInstance().doRPGCommand(sc.nextLine());
			System.out.print(LINEOPENER);
		}
	}

	private final HashMap<String, RollableTable> tables;
	
	private final HashMap<String, ToolCommand> commands;
	
	private final ToolCommand rollCmd;
	private final ToolCommand helpCmd;
	private final ToolCommand addCmd;
	private final ToolCommand listCmd;
	private final ToolCommand pathCmd;
	private final ToolCommand showCmd;
	private final ToolCommand quitCmd;


	private RPGTools() {
		tables = new HashMap<>();
		commands = new HashMap<>();
		
		addCmd = new ToolCommand(
				"add", 
				"a", 
				"[path_to_table]", 
				"Addes the specified table. You can roll on it afterwards.") {
			
			@Override
			public void action(String options) {
				String filecontent;
				try {
					filecontent = readFile(options, Charset.defaultCharset());
					RollableTable t = RollableTable.tryParse(filecontent);
					tables.put(t.getName(), t);
					System.out.println("Added Table " + t.getName());

				} catch (IOException e) {
					System.out.println("Couldn't read the file. Try again.");
				} catch (ParseException e) {
					System.out.println("Couldn't parse the table. Please correct it and try again.");
				}
				
			}
		};
		addCmd.addTo(commands);
		
		pathCmd = new ToolCommand("path", "p", "[none]", "Prints the absolute path to the current directory.") {
			
			@Override
			public void action(String option) {
				System.out.println("user.dir = " + System.getProperty("user.dir"));
				
			}
		};
		pathCmd.addTo(commands);
		
		quitCmd = new ToolCommand("quit", "q", "[none]", "Quits the Tool, discarding all added tables.") {
			
			@Override
			public void action(String option) {
				System.out.println("Goodbye!");
				System.exit(0);
			}
		};
		quitCmd.addTo(commands);
		
		rollCmd = new ToolCommand("roll", "r", "[roll, list, table]", "Rolls the specified roll, list of rolls, or on a table." + System.lineSeparator()
				+ "\tExamples: \"roll d20 +3\" or \"roll 6[4d6 dl1]\" or \"6d20 dh2 dl2 ! +5\" or \"roll tableName\" where tableName is the name of an added table" + System.lineSeparator()
				+ "\tRoll syntax: [amount]d[die] optional: dh[amount] dl[amount] ! +/-[modifier]" + System.lineSeparator()
				+ "\t(\"dh\": drop highest, \"dl\": drop lowest, \"!\": use exploding die)" + System.lineSeparator()
				+ "\tList syntax: [amount][[roll]] or [[roll], ... ,[roll]]") {
			
			@Override
			public void action(String option) {
				try {
					DiceRoll dice = DiceRoll.tryParse(option);
					System.out.println("Rolling " + dice + ": " + dice.roll());
				} catch (Exception e) {
					try {
						ListRoll list = ListRoll.tryParse(option);
						System.out.println("Rolling " + list + ": " + Arrays.toString(list.roll()));
					} catch (Exception e2) {
						RollableTable t = tables.get(option);
						if (t != null) {
							System.out.println("Rolling on Table " + t.getName() + ":");
							int res = t.getTableroll().roll();
							System.out.println(res+" -> " + t.getEntry(res));
						} else
							System.out.println("No valid roll found: \"" + option + "\"");
					}
				}	
			}
		};
		rollCmd.addTo(commands);
		
		showCmd = new ToolCommand("show", "s", "[roll, list, table]", "Shows the specified roll, list of rolls, or table.") {
			
			@Override
			public void action(String option) {
				
				try {
					DiceRoll dice = DiceRoll.tryParse(option);
					System.out.println(dice);
				} catch (Exception e) {
					try {
						ListRoll list = ListRoll.tryParse(option);
						System.out.println(list);
					} catch (Exception e2) {
						RollableTable t = tables.get(option);
						if (t != null) {
							System.out.println(t);

						} else
							System.out.println("No valid roll, list or table found: \"" + option + "\"");
					}
				}	
				
			}
		};
		showCmd.addTo(commands);
		
		listCmd = new ToolCommand("list", "ls", "[none]", "Lists all added tables by name.") {
			
			@Override
			public void action(String option) {
				if(tables.isEmpty()) {
					System.out.println("No tables have been added yet.");
					return;
				}
				
				System.out.println("All added tables:");
				for (String name : tables.keySet()) {
					System.out.println(name);
				}
			}
		};
		listCmd.addTo(commands);
		
		helpCmd = new ToolCommand("help", "?", "[none]", "Shows a list of all commands and their descriptions.") {
			
			@Override
			public void action(String option) {
				for (Object cmd : commands.values().stream().distinct().toArray()) {
					System.out.println(((ToolCommand)cmd).getInfoText());
				}
				
			}
		};
		helpCmd.addTo(commands);
	}

	public static RPGTools getInstance() {
		return instance;
	}

	public void doRPGCommand(String input) {
		// System.out.println(input + " wooow!");

		String[] in = input.split(" ", 2);
		String command = in[0];
		String option = in.length > 1 ? in[1] : "";

		if (!commands.containsKey(command))
			System.out.println("No command found: \"" + input + "\"");
		else {
			
			ToolCommand cmd = commands.get(command);
			cmd.action(option);
		}
			
		
		
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}


}
