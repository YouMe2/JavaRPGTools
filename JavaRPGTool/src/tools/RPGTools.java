package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import roll.RollName;
import roll.RollParser;
import roll.RollResult;
import roll.Rollable;
import util.Pair;

public class RPGTools {

	static final String VERSION = "v3.1.1";

	static final Charset STANDARDCHARSET = StandardCharsets.UTF_8;
	static final String WELCOMEMSG = System.lineSeparator() + "Welcome to RPGTools!" + System.lineSeparator()
			+ "Version " + VERSION + System.lineSeparator() + "- by u/YaAlex" + System.lineSeparator()
			+ "Try \"?\" or \"help\" for help." + System.lineSeparator();
	static final String LINEOPENER = "~ ";
	private static final String RPGFILEENDING = ".rpg";

	public static void main(String[] args) {
		RPGTools tool = new RPGTools();
		tool.init();
		tool.start();
	}

	// private final HashMap<String, RollableX> rollables;
	private final HashMap<String, ToolCommand> commands;

	private final ToolCommand rollCmd;
	private final ToolCommand helpCmd;
	private final ToolCommand addCmd;
	private final ToolCommand loadCmd;
	private final ToolCommand addpremadeCmd;
	private final ToolCommand saveCmd;
	private final ToolCommand removeCmd;
	private final ToolCommand removeAllCmd;
	private final ToolCommand listCmd;
	private final ToolCommand pathCmd;
	private final ToolCommand showCmd;
	private final ToolCommand quitCmd;
	private final ToolCommand testCmd;
	private Scanner sc;

	public RPGTools() {

		// TODO fix command helptexts

		commands = new HashMap<>();

		testCmd = new ToolCommand("test", "t", "", "For Testing only.") {

			@Override
			public void action(String option) {
				try {
					System.out.println(new RollParser(option).parseRollName());
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		};
		// testCmd.addTo(commands);

		rollCmd = new ToolCommand("roll", "r", "[dice, list, table, name]",
				"Rolls the specified dice, list of rolls, on a table, or on some added rollable with the given name."
						+ System.lineSeparator()
						+ "\tExamples: \'roll d20 +3 Dex\', \'r 6[4d6 dl1] \"Ability Scores\"\', \'r 6d20! dh2 dl2 +5\', \'r Name\'"
						+ System.lineSeparator()
						+ "\tDiceRoll syntax: [amount]d[die] optional: ! dh[amount] dl[amount] +/-[modifier] [name]"
						+ System.lineSeparator()
						+ "\t\t(\"!\": use exploding die, \"dh\": drop highest, \"dl\": drop lowest)"
						+ System.lineSeparator()
						+ "\tListRoll syntax: [amount]\'[\'[roll]\'] [name]\' or \'[\'[roll], ... ,[roll]\'] [name]\'"
						+ System.lineSeparator()
						+ "\tTableRoll syntax: \'<\'[tableroll with name]; [result(s)] [entie]; ... ;[result(s)] [entie]\'>\'"
						+ System.lineSeparator()
						+ "\t\tTable Entries may include inline rollables after a \'/\'. Example entrie: \"You age by /d4 Days\"") {

			@Override
			public void action(String option) {
				try {
					Rollable roll = parseRollable(option);
					System.out.println(roll.roll().toString(RollResult.DETAILED)); // maybe DETAILED?
				} catch (ParseException e) {
					System.out.println("No roll saved under the name \"" + option
							+ "\" and couldn't parse a rollable. Please correct it and try again.");
				}

			}
		};
		rollCmd.addTo(commands);

		addCmd = new ToolCommand("add", "a", "[roll, list, table]",
				"Adds the specified roll, list of rolls or rollable table. You can roll on it afterwards with the \'roll\' command."
						+ System.lineSeparator() + "\tNote: A roll or list has to have a name so you can add it.") {

			@Override
			public void action(String option) {
				try {
					addRollable(Rollable.valueOf(option));
				} catch (ParseException e) {
					System.out.println("Couldn't parse the rollable. Please correct it and try again.");
				}
			}

		};
		addCmd.addTo(commands);

		loadCmd = new ToolCommand("load", "l", "[path to file]",
				"Adds any rollable (roll, list, table) found at the specified path, just like the \'add\' command.") {

			@Override
			public void action(String option) {
				String filepath = option + (option.endsWith(RPGFILEENDING) ? "" : RPGFILEENDING);
				try {
					loadFromFile(filepath);
				} catch (IOException e) {
					System.out.println("Couldn't read the file \"" + filepath + "\". Please try again.");
				}
			}
		};
		loadCmd.addTo(commands);

		addpremadeCmd = new ToolCommand("addpre", "ap", "[name of the premade set]",
				"Adds a premade set rollables (rolls, lists, tables) that might be usefull to you."
						+ System.lineSeparator() + "\tPremade sets: " + Arrays.toString(PremadeRollables.values())) {

			@Override
			public void action(String option) {
				String restcontent = null;
				try {
					restcontent = PremadeRollables.valueOf(option).getContent();
					Pair<Rollable, String> parse;

					do {
						parse = RollParser.tryParse(restcontent);
						restcontent = parse.right;
						Rollable rollable = parse.left;
						if (rollable != null)
							addRollable(rollable);
						else
							throw new ParseException("no parse2", 0);
					} while (restcontent != null && !restcontent.isEmpty());

				} catch (ParseException e) {
					System.out.println("Couldn't parse the rollable"
							+ (restcontent != null ? " at \"" + restcontent.substring(0, 10) + "... \"" : "")
							+ ". Please correct the file and try again.");
					// System.err.println("ParseError at offset " + e.getErrorOffset() +" : " +
					// e.getMessage());
				} catch (IllegalArgumentException e) {
					System.out.println("No premade set found: " + option);
				}
			}
		};
//		addpremadeCmd.addTo(commands);

		saveCmd = new ToolCommand("save", "sa", "[filename]",
				"Saves all your added rollables to a a file with the given name with the\".rpg\" fileending.") {

			@Override
			public void action(String option) {

				saveToFile(option);

			}
		};
		saveCmd.addTo(commands);

		removeCmd = new ToolCommand("remove", "rm", "[name of added rollable]",
				"Removes the specified rollable from your list.") {

			@Override
			public void action(String option) {

				RollName key = new RollName(option);
				if (Rollable.hasRollable(key)) {
					Rollable.removeRollable(key);
					System.out.println("Removed " + key.getName());
				} else
					System.out.println("No rollable found wiht name: " + option);
			}
		};
		removeCmd.addTo(commands);

		removeAllCmd = new ToolCommand("removeall", "rma", "[none]", "Removes all rollables from your list.") {

			@Override
			public void action(String option) {

				Rollable.clearRollables();
				System.out.println("Removed all rollables.");
			}
		};
		removeAllCmd.addTo(commands);

		showCmd = new ToolCommand("show", "s", "[roll, list, table]",
				"Shows the specified roll, list of rolls, or table.") {

			@Override
			public void action(String option) {
				try {
					Rollable r = parseRollable(option);
					System.out.println(r.toString());
				} catch (ParseException e) {
					System.out.println("No valid roll parsable and no roll saved under that name: \"" + option + "\"");
				}

			}
		};
		showCmd.addTo(commands);

		listCmd = new ToolCommand("list", "ls", "[none]", "Lists all added tables by name.") {

			@Override
			public void action(String option) {
				list();
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

		helpCmd = new ToolCommand("help", "?", "[none, command]",
				"Shows a list of all commands and their help texts or a single help text.") {

			@Override
			public void action(String option) {

				showHelptext(option);
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

	public void init() {

		try {
			loadFromFile("init.rpg");
		} catch (IOException e) {
			System.out.println("No \"init.rpg\" file was found. starting blanc.");
		}

		// ListRoll abilitscoreListRoll;
		// try {
		// abilitscoreListRoll = new RollParser("[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con,
		// 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha] AbilityScores").parseListRoll();
		//// addRollable(abilitscoreListRoll);
		// Rollable.addRollable(abilitscoreListRoll);
		// } catch (ParseException e) {
		// System.err.println("this should never happen...");
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// System.err.println("this should also never happen");
		// e.printStackTrace();
		// }
	}

	public void start() {
		System.out.println(WELCOMEMSG);
		sc = new Scanner(System.in);

		System.out.print(LINEOPENER);
		while (sc.hasNextLine()) {
			doRPGCommand(sc.nextLine());
			System.out.print(System.lineSeparator() + LINEOPENER);
		}

	}

	public void loadFromFile(String option) throws IOException {

		String restcontent = null;
		try {
			restcontent = readRPGFile(option);
			Pair<Rollable, String> parse;

			do {
				parse = RollParser.tryParse(restcontent);
				restcontent = parse.right;
				Rollable rollable = parse.left;
				if (rollable != null)
					addRollable(rollable);
				else
					throw new ParseException("no parse2", 0);
			} while (restcontent != null && !restcontent.isEmpty());

		} catch (ParseException e) {
			System.out.println("Couldn't parse the rollable"
					+ (restcontent != null ? " at \"" + restcontent.substring(0, 10) + "... \"" : "")
					+ ". Please correct the file and try again.");
		}
	}

	public void saveToFile(String option) {
		StringBuilder builder = new StringBuilder();
		for (Rollable rollable : Rollable.getRollables()) {
			builder.append(rollable.toString());
			builder.append(System.lineSeparator());
			builder.append(System.lineSeparator());
		}
		try {
			writeRPGFile(option, builder.toString().trim());
			System.out.println("Saved all rollables to file: " + option + ".rpg");
		} catch (IOException e) {
			System.out.println("Couldn't write to file \"" + option + "\". Please try again.");
		}
	}

	private void showHelptext(String option) {

		if (option == null || option.isEmpty()) {
			System.out.println("All available commands:");
			for (Object cmd : commands.values().stream().distinct().toArray()) {
				System.out.println(((ToolCommand) cmd).getInfoText() + System.lineSeparator());
			}
		} else {

			ToolCommand cmd = commands.get(option);
			if (cmd == null) {
				System.out.println("No command found: " + option);
				return;
			}
			System.out.println("Helptext for: " + option);
			System.out.println(cmd.getInfoText() + System.lineSeparator());

		}

	}

	private void list() {
		if (Rollable.getRollNames().isEmpty()) {
			System.out.println("No rollables have been added yet.");
			return;
		}

		System.out.println("All added rollables:");
		for (String name : Rollable.getRollNames()) {
			System.out.println("+ " + name);
		}
	}

	public Rollable parseRollable(String option) throws ParseException {

		return Rollable.valueOf(option);

	}

	public void addRollable(Rollable rollable) {
				
		
		try {
			Rollable.addRollable(rollable);
			System.out.println("Added " + rollable.getClass().getSimpleName() + " \"" + rollable.getName() + "\".");

		} catch (IllegalArgumentException e) {
			System.out.println("Couldn't add rollable: "+e.getMessage());
		}

	}

	public void doRPGCommand(String input) {

		// System.out.println("before" + rollables.keySet());

		String[] in = input.split(" ", 2);
		String command = in[0];
		String option = in.length > 1 ? in[1] : "";

		if (!commands.containsKey(command))
			System.out.println("No command found: \"" + input + "\"");
		else {

			ToolCommand cmd = commands.get(command);
			cmd.action(option);

			// System.out.println("after" + rollables.keySet());
		}

	}

	public static String readRPGFile(String filename) throws IOException {
		if (!filename.endsWith(RPGFILEENDING))
			throw new IOException("wron file ending");
		byte[] encoded = Files.readAllBytes(Paths.get(filename));
		return new String(encoded, STANDARDCHARSET);
	}

	public static void writeRPGFile(String filename, String input) throws IOException {

		Path p = Paths.get(filename + ".rpg");

		try (final BufferedWriter writer = Files.newBufferedWriter(p, STANDARDCHARSET,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);) {
			writer.write(input);
			writer.flush();
		}
	}

}
