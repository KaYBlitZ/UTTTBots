package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class Simulation {
	
	public static final int RANDOM = 0;
	// values are recommended to be in the range [0,1]
	private static final double WIN = 1;
	private static final double TIE = 0.5;
	private static final double LOSS = 0;
	
	/** Simulates by playing random moves until a terminal state is reached */
	public static double simulateRandom(Field field, Node expanded, int botId, int opponentId) {
		Random rand = new Random(System.currentTimeMillis());
		expanded.restoreState(field);
		int winner = expanded.isTerminal ? field.getWinner() : -1;
		int currentId = expanded.nextMoveBotId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			currentId = currentId == 1 ? 2 : 1;
			winner = field.getWinner();
		}
		if (winner == botId) {
			return WIN;
		} else if (winner == 0) {
			return TIE;
		} else {
			return LOSS;
		}
	}
}
