package tools;

import java.io.File;
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
	static final String WELCOMEMSG = "Welcome to RPGTools" + NEWLINE + "- by u/YaAlex";
	static final String LINEOPENER = "> ";

	public static void main(String[] args) {
		System.out.println(WELCOMEMSG);
		Scanner sc = new Scanner(System.in);

		System.out.print(LINEOPENER);
		while (sc.hasNextLine()) {
			RPGTools.getInstance().doRPGCommand(sc.nextLine());
			System.out.print(LINEOPENER);
		}
	}

	private HashMap<String, RollableTable> tabels;

	private RPGTools() {
		tabels = new HashMap<>();
	}

	public static RPGTools getInstance() {
		return instance;
	}

	public void doRPGCommand(String input) {
		// System.out.println(input + " wooow!");

		String[] in = input.split(" ", 2);
		String command = in[0];
		String options = in.length > 1 ? in[1] : "";

		switch (command) {
		case "path":
		case "p":
			path(options);
			break;
			
		case "roll":
		case "r":
			roll(options);
			break;

		case "add":
		case "a":
			addTable(options);
			break;

		case "show":
		case "s":
			show(options);
			break;
			
		case "help":
		case "h":
			help(options);
			break;

		case "exit":
		case "quit":
		case "e":
		case "q":
			exit(options);

			break;

		default:
			System.out.println("No command found: \"" + input + "\"");
			break;
		}
	}

	private void show(String input) {
		RollableTable t = tabels.get(input);
		if (t != null) {
			System.out.println(t.toString());
		} else
			System.out.println("No valid roll found: \"" + input + "\"");
	}

	private void exit(String options) {
		System.out.println("Goodbye!");
		System.exit(0);
	}

	private void help(String options) {
		
		System.out.println("Try one of the following commands:" + NEWLINE);
		
		System.out.println("try \"roll 6[4d6dl1]\" or \"r d20\"");
		System.out.println("or try \"add [table].cvs\"");

	}
	
	private void path(String options) {
		System.out.println("user.dir = " + System.getProperty("user.dir"));
	}

	public void addTable(String input) {
		String filecontent;
		try {
			filecontent = readFile(input, Charset.defaultCharset());
			RollableTable t = RollableTable.tryParse(filecontent);
			tabels.put(t.getName(), t);
			System.out.println("Added Table " + t.getName());

		} catch (IOException e) {
			System.out.println("Couldn't read the file. Try again.");
		} catch (ParseException e) {
			System.out.println("Couldn't parse the table. Please correct it and try again.");
		}

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public void roll(String input) {
		try {
			DiceRoll dice = DiceRoll.tryParse(input);
			System.out.println("Rolling " + dice + ": " + dice.roll());
		} catch (Exception e) {
			try {
				ListRoll list = ListRoll.tryParse(input);
				System.out.println("Rolling " + list + ": " + Arrays.toString(list.roll()));
			} catch (Exception e2) {
				RollableTable t = tabels.get(input);
				if (t != null) {
					System.out.println("Rolling on Table " + t.getName() + ":");
					int res = t.getTableroll().roll();
					System.out.println(res+" -> " + t.getEntry(res));
				} else
					System.out.println("No valid roll found: \"" + input + "\"");
			}
		}

	}

}
