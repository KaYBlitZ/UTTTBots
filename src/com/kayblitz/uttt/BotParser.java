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

import com.kayblitz.uttt.bot.mcts.RAVEHeuristicNode;
import com.kayblitz.uttt.bot.mcts.RAVETree;
import com.kayblitz.uttt.bot.mcts.Simulation;

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

	public BotParser(Bot bot) {
		this.scanner = new Scanner(System.in);
		this.bot = bot;
	}

	public void run() {
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() == 0)
				continue;
			
			String[] parts = line.split(" ");
			if (parts[0].equals("update")) { /* new game data */
				if (parts[1].equals("game")) {
					bot.field.parseGameData(parts[2], parts[3]);
				} else if (parts[1].equals("rave")) {
					RAVETree.EXPLORATION_CONSTANT = Double.parseDouble(parts[2]);
					RAVETree.RAVE_CONSTANT = Double.parseDouble(parts[3]);
				} else if (parts[1].equals("raveheuristics")) {
					RAVEHeuristicNode.HEURISTIC_MULTIPLIER = Double.parseDouble(parts[2]);
					RAVEHeuristicNode.UCT_CONFIDENCE = Double.parseDouble(parts[3]);
					RAVEHeuristicNode.AMAF_CONFIDENCE = Double.parseDouble(parts[4]);
				} else if (parts[1].equals("simulationconstants")) {
					Simulation.UCT_EPT_MAX_MOVES = Integer.parseInt(parts[2]);
					Simulation.RAVE_EPT_MAX_MOVES = Integer.parseInt(parts[3]);
					Simulation.EPT_WIN_LOSS_THRESHOLD = Double.parseDouble(parts[4]);
				}
			} else if (parts[0].equals("action")) {
				if (parts[1].equals("move")) { /* move requested */
					int timebank = Integer.parseInt(parts[2]);
					Move move = bot.makeMove(timebank);
					System.out.println("place_move " + move.column + " " + move.row);
				}
			} else if (parts[0].equals("settings")) {
				if (parts[1].equals("your_botid")) {
					bot.botId = Integer.parseInt(parts[2]);
					bot.opponentId = bot.botId == 1 ? 2 : 1;
				}
			} else { 
				System.out.println("Unknown command: " + line);
			}
		}
	}
}
