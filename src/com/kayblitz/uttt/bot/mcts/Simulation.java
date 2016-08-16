package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

/**
 * Includes various functions for simulating plays for MCTS from the expanded node until a terminal state is 
 * reached. Returns WIN(1), TIE(0.5), or LOSS(0).
 * @author Kenneth
 *
 */
public class Simulation {
	
	public static final int RANDOM = 0;
	public static final int WIN_FIRST_RANDOM = 1;
	
	// values are recommended to be in the range [0,1]
	private static final double WIN = 1;
	private static final double TIE = 0.5;
	private static final double LOSS = 0;
	
	/** Simulates by playing random moves. Field will be left in a terminal state. */
	public static double simulateRandom(Field field, Node expanded, int botId, int opponentId) {
		Random rand = new Random(System.currentTimeMillis());
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			winner = field.getWinner();
			currentId = currentId == 1 ? 2 : 1;
		}
		if (winner == botId) {
			return WIN;
		} else if (winner == 0) {
			return TIE;
		} else {
			return LOSS;
		}
	}
	
	/** Simulates by playing random moves, but both sides will play the winning move when possible.
	 * The field will be left in an undefined state.
	 */
	public static double simulateWinFirstRandom(Field field, Node expanded, int botId, int opponentId) {
		Random rand = new Random(System.currentTimeMillis());
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			// see if there is a winning move, if so play it
			for (Move move : moves) {
				field.makeMove(move, currentId, true);
				winner = field.getWinner();
				field.undo();
				if (winner > 0)
					return currentId == botId ? WIN : LOSS;
			}
			// no winning move, just play a random move
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			winner = field.getWinner();
			currentId = currentId == 1 ? 2 : 1;
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
