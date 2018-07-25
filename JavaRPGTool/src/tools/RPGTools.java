package tools;

import java.util.Scanner;

public class RPGTools {
	
	static final RPGTools instance = new RPGTools();
	
	static final String WELLCOMEMSG = "Wellcome to RPGTools" + System.lineSeparator() + "- by u/YaAlex";
	static final String LINEOPENER = "> ";
	
	public static void main(String[] args) {	
		System.out.println(WELLCOMEMSG);
		Scanner sc = new Scanner(System.in);


		System.out.print(LINEOPENER);
		while(sc.hasNext()){
			RPGTools.getInstance().doRPGCommand(sc.nextLine());
		}
	}

	private RPGTools() {
	}
	
	public static RPGTools getInstance() {
		return instance;
	}
	
	public void doRPGCommand(String input) {
		System.out.println(input + " wooow!");
	}

}
