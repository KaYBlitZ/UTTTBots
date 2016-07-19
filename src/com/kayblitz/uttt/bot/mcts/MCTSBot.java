package com.kayblitz.uttt.bot.mcts;

import com.kayblitz.uttt.Bot;
import com.kayblitz.uttt.BotParser;
import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class MCTSBot extends Bot {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Simulation type must be given");
			return;
		}
		int type = -1;
		try {
			type = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid simulation type");
			return;
		}
		new BotParser(new MCTSBot(type)).run();
	}
	
	private long startTime, limit;
	private int type;
	
	public MCTSBot(int type) {
		this.type = type;
	}
	
	public long getElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public Move makeMove(Field field, int timebank, int moveNum) {
		startTime = System.currentTimeMillis();
		if (moveNum < 15) {
			// heuristics mostly the same (insignificant), dont waste timebank
			limit = 500L;
		} else {
			int size = field.getAvailableMoves().size();
			if (size < 4) {
				limit = 500L;
			} else if (size < 7) {
				limit = 800L;
			} else if (size < 10) {
				limit = 1100L;
			} else {
				limit = 1700L;
			}
			if (limit > timebank) limit = (long) (0.85f * timebank);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Timebank %d, Limit %d\n", timebank, limit));
		Tree tree = new Tree(field, sb, type, botId, opponentId);
		
		while (getElapsedTime() < limit) {
			tree.iterate();
		}
		Move bestMove = tree.getBestMove();
		System.err.println(sb.toString());
		
		return bestMove;
	}
}
