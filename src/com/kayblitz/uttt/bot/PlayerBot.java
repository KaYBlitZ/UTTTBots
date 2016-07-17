package com.kayblitz.uttt.bot;

import java.util.Scanner;

public class PlayerBot {

	public static void main(String[] args) {
		new PlayerBot().run();
	}

	private Scanner scanner;

	public PlayerBot() {
		scanner = new Scanner(System.in);
	}

	public void run() {
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] parts = line.split(" ");
			if (parts[0].equals("move")) { // player clicked on GUI, repeat move to game
				System.out.println("place_move " + Integer.parseInt(parts[1]) + " " + Integer.parseInt(parts[2]));
			}
		}
	}
}
