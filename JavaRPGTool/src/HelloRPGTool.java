import java.util.Scanner;

import basic.DiceRoll;

public class HelloRPGTool {

	public static void main(String[] args) {
		System.out.println("Hello RPG");
		Scanner sc = new Scanner(System.in);

	    while(sc.hasNext()) {
	    	String in = sc.nextLine();
	    	System.out.println("Damn yo! " + in);
	    	System.out.println(in.matches("'.*'"));
	    }

	}

}
