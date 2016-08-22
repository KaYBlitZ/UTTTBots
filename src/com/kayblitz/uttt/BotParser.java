// Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.kayblitz.uttt;

import java.util.Scanner;

import com.kayblitz.uttt.bot.Evaluation;

/**
 * BotParser class
 * 
 * Main class that will keep reading output from the engine.
 * Will either update the bot state or get actions.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotParser {

	private final Scanner scanner;
	private final Bot bot;
	private Field field;

	public BotParser(Bot bot) {
		this.scanner = new Scanner(System.in);
		this.bot = bot;
	}

	public void run() {
		field = new Field();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(line.length() == 0) continue;
			
			String[] parts = line.split(" ");
			if(parts[0].equals("settings")) {
				if (parts[1].equals("your_botid")) {
					int botId = Integer.parseInt(parts[2]);
					bot.botId = botId;
					bot.opponentId = botId == 1 ? 2 : 1;
					if (bot.botId == 1) {
						Evaluation.TWO_IN_A_ROW_OPTIMIZED = 50.0;
						Evaluation.MIDDLE_OPTIMIZED = 30.0;
						Evaluation.CORNER_OPTIMIZED = 20.0;
						Evaluation.SIDE_OPTIMIZED = 10.0;
						Evaluation.MINI_TWO_IN_A_ROW_OPTIMIZED = 5.0;
						Evaluation.MINI_MIDDLE_OPTIMIZED = 3.0;
						Evaluation.MINI_CORNER_OPTIMIZED = 2.0;
						Evaluation.MINI_SIDE_OPTIMIZED = 1.0;
					} else {
						Evaluation.TWO_IN_A_ROW_OPTIMIZED = 0.6011641903860614;
						Evaluation.MIDDLE_OPTIMIZED = 0.431060854062568;
						Evaluation.CORNER_OPTIMIZED = 1.1628542873013277;
						Evaluation.SIDE_OPTIMIZED = 0.8721021530689863;
						Evaluation.MINI_TWO_IN_A_ROW_OPTIMIZED = 0.19669482972968466;
						Evaluation.MINI_MIDDLE_OPTIMIZED = 0.37401554266349935;
						Evaluation.MINI_CORNER_OPTIMIZED = 0.8584616541090139;
						Evaluation.MINI_SIDE_OPTIMIZED = 1.1813617174231033;
					}
				}
			} else if(parts[0].equals("update") && parts[1].equals("game")) { /* new game data */
			    field.parseGameData(parts[2], parts[3]);
			} else if(parts[0].equals("action")) {
				if (parts[1].equals("move")) { /* move requested */
					int timebank = Integer.parseInt(parts[2]);
					Move move = bot.makeMove(field, timebank, field.getMoveNum());
					System.out.println("place_move " + move.column + " " + move.row);
				}
			} else { 
				System.out.println("Unknown command: " + line);
			}
		}
	}
}
