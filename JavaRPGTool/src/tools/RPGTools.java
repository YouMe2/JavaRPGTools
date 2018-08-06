package tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Scanner;

import basic.ListRoll;
import basic.RollParser;
import basic.Rollable;
import basic.RollableTable;

public class RPGTools {

	static final String WELCOMEMSG = "Welcome to RPGTools!" + System.lineSeparator() + "- by u/YaAlex"
			+ System.lineSeparator() + "Try \"?\" or \"help\" for help.";
	static final String LINEOPENER = "> ";

	public static void main(String[] args) {
		RPGTools tool = new RPGTools();
		tool.init();
		tool.start();
	}

	

	private final HashMap<String, Rollable> rollables;
	private final HashMap<String, ToolCommand> commands;

	private final ToolCommand rollCmd;
	private final ToolCommand helpCmd;
	private final ToolCommand addCmd;
	private final ToolCommand loadCmd;
	// TODO saveCmd
	// private final ToolCommand saveCmd;
	private final ToolCommand removeCmd;
	private final ToolCommand listCmd;
	private final ToolCommand pathCmd;
	private final ToolCommand showCmd;
	private final ToolCommand quitCmd;
	private final ToolCommand testCmd;
	private Scanner sc;

	public RPGTools() {
		
		
		
		rollables = new HashMap<>();
		commands = new HashMap<>();
		
		testCmd = new ToolCommand("test", "t", "", "For Testing only.") {
			
			@Override
			public void action(String option) {
				try {
//				Rollable r;
//				r = RollParser.valueOf(example);
				RollableTable t = null;
//				if (r instanceof RollableTable)
//					t = (RollableTable) r;

					t = new RollParser("RollableTable: d5 \"Blubb\"\r\n" + 
							"1,		Stone.\r\n" + 
							"2,		Nothing.\r\n" + 
							"3-5,	Some other shit.").parseRollableTable();
					System.out.println(t);
				} catch (ParseException e) {
					
					e.printStackTrace();
					System.err.println(e.getErrorOffset());
				}
				
			}
		};
		testCmd.addTo(commands);
		
		rollCmd = new ToolCommand("roll", "r", "[roll, list, table, name]",
				"Rolls the specified roll, list of rolls, or on a table." + System.lineSeparator()
						+ "\tExamples: \'roll d20 +3 Dex\' or \'roll 6[4d6 dl1] \"Ability Scores\"\' or \'6d20! dh2 dl2 +5\' or \'roll Name\' where Name is the name of an added roll."
						+ System.lineSeparator() + "\tFor example try: \"roll AbilityScores\""
						+ System.lineSeparator()
						+ "\tRoll syntax: [amount]d[die] optional: ! dh[amount] dl[amount] +/-[modifier] [\"Some Name\"]"
						+ System.lineSeparator()
						+ "\t\t(\"!\": use exploding die, \"dh\": drop highest, \"dl\": drop lowest)"
						+ System.lineSeparator() + "\tList syntax: [amount]\'[\'[roll]\']\' or \'[\'[roll], ... ,[roll]\']\'"
						+ System.lineSeparator() + "\tTable syntax: \'[\'[tableroll with name]; [result], [entie]; ... ;[res], [entie]\']\'") {

			@Override
			public void action(String option) {
				try {
					Rollable r = rollables.containsKey(option) ? rollables.get(option) : RollParser.valueOf(option);
					System.out.println(r.getRollMessage(Rollable.SIMPLE)); // maybe DETAILED?
				} catch (ParseException e) {
					System.out.println("No valid roll parsable and no roll saved under that name: \"" + option + "\"");
				}

			}
		};
		rollCmd.addTo(commands);

		addCmd = new ToolCommand("add", "a", "[roll, list, table]",
				"Adds the specified roll, list of rolls or rollable table. You can roll on it afterwards with the \'roll\' command. Note: A roll or list has to have a name so you can add it.") {

			@Override
			public void action(String option) {
				Rollable r;
				try {
					r = RollParser.valueOf(option);
					if (!r.hasName()) {
						throw new IllegalArgumentException("The rollable needs a name to be added!");
					}
					rollables.put(r.getName(), r);
					assert rollables.containsKey(r.getName());
					// TODO Type recognition and fitting message.
					System.out.println("Added Rollable " + r.getName());
				} catch (ParseException e) {
					System.out.println("Couldn't parse the rollable. Please correct it in the file and try again.");
				} catch (IllegalArgumentException e) {
					System.out.println("Couldn't add rollable. Rollable is missing a name, pleas add one and try again.");
				}
			}

		};
		addCmd.addTo(commands);

		loadCmd = new ToolCommand("load", "l", "[path to file]",
				"Adds any rollable (roll, list, table) found at the specified path, just like the \'add\' command.") {

			@Override
			public void action(String options) {
				String filecontent;
				try {
					filecontent = readFile(options, Charset.defaultCharset());

					Rollable r = RollParser.valueOf(filecontent);
					if (!r.hasName())
						throw new IllegalArgumentException("The rollable needs a name to be added!");
					rollables.put(r.getName(), r);
					// TODO Type recognition and fitting message.
					System.out.println("Added Rollable " + r.getName());

				} catch (IOException e) {
					System.out.println("Couldn't read the file \"" + options + "\". Please try again.");
				} catch (ParseException e) {
					System.out.println("Couldn't parse the rollable. Please correct it in the file and try again.");
				} catch (IllegalArgumentException e) {
					System.out.println("Couldn't add rollable. Rollable is missing a name, pleas add one and try again.");
				}
			}
		};
		loadCmd.addTo(commands);

		removeCmd = new ToolCommand("remove", "rm", "[name of added rollable]", null) {

			@Override
			public void action(String option) {

				if (rollables.containsKey(option)) {
					rollables.remove(option);
					System.out.println("Removed " + option);
				} else
					System.out.println("No rollable found wiht name: " + option);
			}
		};
		removeCmd.addTo(commands);

		showCmd = new ToolCommand("show", "s", "[roll, list, table]",
				"Shows the specified roll, list of rolls, or table.") {

			@Override
			public void action(String option) {
				try {
					Rollable r = rollables.containsKey(option) ? rollables.get(option) : RollParser.valueOf(option);
					System.out.println((r.hasName() ? r.getName() + ":" + System.lineSeparator() : "") + r.toString());
				} catch (ParseException e) {
					System.out.println("No valid roll parsable and no roll saved under that name: \"" + option + "\"");
				}

			}
		};
		showCmd.addTo(commands);

		listCmd = new ToolCommand("list", "ls", "[none]", "Lists all added tables by name.") {

			@Override
			public void action(String option) {
				
				if (rollables.isEmpty()) {
					System.out.println("No rollables have been added yet.");
					return;
				}

				System.out.println("All added rolls, lists, and tables:");
				for (String name : rollables.keySet()) {
					System.out.println("+ " + name);
				}
			}
		};
		listCmd.addTo(commands);

		pathCmd = new ToolCommand("path", "p", "[none]", "Prints the absolute path to the current directory.") {

			@Override
			public void action(String option) {
				System.out.println("user.dir = " + System.getProperty("user.dir"));

			}
		};
		pathCmd.addTo(commands);

		helpCmd = new ToolCommand("help", "?", "[none]", "Shows a list of all commands and their descriptions.") {

			@Override
			public void action(String option) {
				System.out.println("All available commands:");
				for (Object cmd : commands.values().stream().distinct().toArray()) {
					System.out.println(((ToolCommand) cmd).getInfoText());
				}

			}
		};
		helpCmd.addTo(commands);

		quitCmd = new ToolCommand("quit", "q", "[none]", "Quits the Tool, discarding all added tables.") {

			@Override
			public void action(String option) {
				System.out.println("Goodbye!");
				System.exit(0);
			}
		};
		quitCmd.addTo(commands);
	}

	private void init() {
		ListRoll abilitscoreListRoll;
		try {
			abilitscoreListRoll = new RollParser("[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha] \"AbilityScores\"").parseListRoll();
			rollables.put(abilitscoreListRoll.getName(), abilitscoreListRoll);
		
			
		} catch (ParseException e) {
			System.err.println("this should never happen...");
			e.printStackTrace();
		}
	}
	
	private void start() {
		System.out.println(WELCOMEMSG);
		sc = new Scanner(System.in);

		System.out.print(LINEOPENER);
		while (sc.hasNextLine()) {
			doRPGCommand(sc.nextLine());
			System.out.print(LINEOPENER);
		}
		
	}
	
	public void doRPGCommand(String input) {

//System.out.println("before" + rollables.keySet());

		String[] in = input.split(" ", 2);
		String command = in[0];
		String option = in.length > 1 ? in[1] : "";

		if (!commands.containsKey(command))
			System.out.println("No command found: \"" + input + "\"");
		else {

			ToolCommand cmd = commands.get(command);
			cmd.action(option);

//System.out.println("after" + rollables.keySet());
		}

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
