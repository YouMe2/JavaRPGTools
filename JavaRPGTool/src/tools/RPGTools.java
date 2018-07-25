package tools;

import java.util.Scanner;

import basic.DiceRoll;
import basic.RollableTable;

public class RPGTools {

	static final RPGTools instance = new RPGTools();

	static final String WELLCOMEMSG = "Wellcome to RPGTools" + System.lineSeparator() + "- by u/YaAlex";
	static final String LINEOPENER = "> ";

	public static void main(String[] args) {
		System.out.println(WELLCOMEMSG);
		Scanner sc = new Scanner(System.in);

		System.out.print(LINEOPENER);
		while (sc.hasNextLine()) {
			RPGTools.getInstance().doRPGCommand(sc.nextLine());
			System.out.print(LINEOPENER);
		}
	}

	private RPGTools() {
	}

	public static RPGTools getInstance() {
		return instance;
	}

	public void doRPGCommand(String input) {
		// System.out.println(input + " wooow!");

		String[] in = input.split(" ", 2);
		String command = in[0];
		String options = in.length>1 ? in[1] : "";

		switch (command) {
		case "roll":
		case "r":	
			DiceRoll dice = DiceRoll.valueOf(options);
			
			System.out.println("rolling " + dice + ": " + dice.roll());
			break;

		case "exit":
		case "quit":
		case "e":
		case "q":
			System.out.println("Goodbye!");
			System.exit(0);
			break;

		default:
			System.out.println("No command found: \"" + input + "\"");
			break;
		}
	}

	
	public RollableTable readTable() {
		return null;
	}
	
	
}
