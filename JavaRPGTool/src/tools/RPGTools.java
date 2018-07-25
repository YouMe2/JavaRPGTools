package tools;

import java.util.Arrays;
import java.util.Scanner;

import basic.DiceRoll;
import basic.ListRoll;
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
			roll(options);
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

	
	private void exit(String options) {
		System.out.println("Goodbye!");
		System.exit(0);
	}

	private void help(String options) {
		System.out.println("try \"roll 6[4d6dl1]\" or \"r d20\"");
		
	}

	public RollableTable readTable() {
		return null;
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
				System.out.println("No valid roll found: \"" + input + "\"");
			}
		}
					
		
	}
	
	
}
