package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;
import com.kayblitz.uttt.bot.Evaluation;

/**
 * Includes various functions for simulating plays for MCTS from the expanded node until a terminal state is 
 * reached. Returns WIN(1), TIE(0.5), or LOSS(0).
 * @author Kenneth
 *
 */
public class Simulation {
	
	public static final int UCT_RANDOM = 0;
	public static final int UCT_WIN_FIRST_RANDOM = 1;
	public static final int UCT_RANDOM_EPT = 2;
	public static final int RAVE_RANDOM = 3;
	public static final int RAVE_RANDOM_EPT = 4;
	
	public static int UCT_EPT_MAX_MOVES = 50;
	public static int RAVE_EPT_MAX_MOVES = 50;
	public static double EPT_WIN_LOSS_THRESHOLD = 0.7;
	// values are recommended to be in the range [0,1]
	private static final double WIN = 1;
	private static final double TIE = 0.5;
	private static final double LOSS = 0;
	
	private static final Random rand = new Random(System.currentTimeMillis());
	
	/** Simulates by playing random moves. Field will be left in a terminal state. */
	public static double simulateUCTRandom(Field field, UCTNode expanded, int botId, int opponentId) {
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
	 * The field will be left in a terminal state.
	 */
	public static double simulateUCTWinFirstRandom(Field field, UCTNode expanded, int botId, int opponentId) {
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			// see if there is a winning move, if so play it
			for (Move move : moves) {
				field.makeMove(move, currentId, true);
				winner = field.getWinner();
				if (winner > 0) {
					field.pop();
					return currentId == botId ? WIN : LOSS;
				} else {
					field.undo();
				}
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
	
	public static double simulateRAVERandom(Field field, RAVENode expanded, ArrayList<Move> botMoves,
			ArrayList<Move> opponentMoves, int botId, int opponentId) {
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			addMove(move, currentId, botMoves, opponentMoves, botId, opponentId);
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
	
	private static void addMove(Move move, int currentId, ArrayList<Move> botMoves, ArrayList<Move> opponentMoves,
			int botId, int opponentId) {
		if (currentId == botId) {
			botMoves.add(move);
		} else {
			opponentMoves.add(move);
		}
	}
	
	public static double simulateUCTRandomEPT(Field field, UCTNode expanded, int botId, int opponentId) {
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		int numMoves = 0;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			winner = field.getWinner();
			currentId = currentId == 1 ? 2 : 1;
			if (++numMoves == UCT_EPT_MAX_MOVES) {
				double heuristic = Evaluation.evaluateFieldComprehensive(field, botId, opponentId);
				if (Double.compare(heuristic, EPT_WIN_LOSS_THRESHOLD) > 0) {
					return WIN;
				} else if (Double.compare(heuristic, -EPT_WIN_LOSS_THRESHOLD) < 0) {
					return LOSS;
				} else {
					return TIE;
				}
			}
		}
		if (winner == botId) {
			return WIN;
		} else if (winner == 0) {
			return TIE;
		} else {
			return LOSS;
		}
	}
	
	public static double simulateRAVERandomEPT(Field field, RAVENode expanded, ArrayList<Move> botMoves,
			ArrayList<Move> opponentMoves, int botId, int opponentId) {
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		int numMoves = 0;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			addMove(move, currentId, botMoves, opponentMoves, botId, opponentId);
			if (++numMoves == RAVE_EPT_MAX_MOVES) {
				double heuristic = Evaluation.evaluateFieldComprehensive(field, botId, opponentId);
				if (Double.compare(heuristic, EPT_WIN_LOSS_THRESHOLD) > 0) {
					return WIN;
				} else if (Double.compare(heuristic, -EPT_WIN_LOSS_THRESHOLD) < 0) {
					return LOSS;
				} else {
					return TIE;
				}
			}
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
