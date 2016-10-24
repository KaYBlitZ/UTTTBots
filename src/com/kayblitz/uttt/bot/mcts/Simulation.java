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
	
	public static final int RANDOM = 0;
	public static final int WIN_FIRST_RANDOM = 1;
	public static final int WIN_FIRST_RANDOM_RAVE = 2;
	public static final int WIN_FIRST_HEURISTIC_RAVE = 3;
	
	public static int RAVE_HEURISTIC_SIMULATION = 50;
	// values are recommended to be in the range [0,1]
	private static final double WIN = 1;
	private static final double TIE = 0.5;
	private static final double LOSS = 0;
	
	/** Simulates by playing random moves. Field will be left in a terminal state. */
	public static double simulateRandom(Field field, UCTNode expanded, int botId, int opponentId) {
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
	 * The field will be left in a terminal state.
	 */
	public static double simulateWinFirstRandom(Field field, UCTNode expanded, int botId, int opponentId) {
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
	
	/** Simulates by playing random moves, but both sides will play the winning move when possible.
	 * The field will be left in a terminal state. The moves made during simulation will be stored
	 * in botMoves and opponentMoves. These saved moves can later be used to implement RAVE functionality.
	 */
	public static double simulateWinFirstRandomRAVE(Field field, RAVENode expanded, ArrayList<Move> botMoves,
			ArrayList<Move> opponentMoves, int botId, int opponentId) {
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
				if (winner > 0) {
					addMove(move, currentId, botMoves, opponentMoves, botId, opponentId);
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
			addMove(move, currentId, botMoves, opponentMoves, botId, opponentId);
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
	
	/** Simulates by playing the best heuristic move, but both sides will play the winning move when possible. 
	 * The field will be left in a terminal state. The moves made during simulation will be stored in botMoves 
	 * and opponentMoves. These saved moves can later be used to implement RAVE functionality.
	 */
	public static double simulateWinFirstHeuristicRAVE(Field field, RAVENode expanded, ArrayList<Move> botMoves,
			ArrayList<Move> opponentMoves, int botId, int opponentId) {
		expanded.restoreState(field);
		int winner = expanded.winner;
		int currentId = expanded.nextMoveBotId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			// check for winning move and the best move
			Move bestMove = null;
			double bestHeuristic = currentId == botId ? Integer.MIN_VALUE : Integer.MAX_VALUE;
			for (Move move : moves) {
				field.makeMove(move, currentId, true);
				winner = field.getWinner();
				double heuristic = Evaluation.evaluateFieldAdvancedOptimized(field, botId, opponentId);
				if (winner > 0) {
					addMove(move, currentId, botMoves, opponentMoves, botId, opponentId);
					field.pop();
					return currentId == botId ? WIN : LOSS;
				} else {
					field.undo();
				}
				if (currentId == botId) { // bot plays highest heuristic move
					if (Double.compare(heuristic, bestHeuristic) > 0) {
						bestMove = move;
						bestHeuristic = heuristic;
					}
				} else { // opponent plays lowest heuristic move
					if (Double.compare(heuristic, bestHeuristic) < 0) {
						bestMove = move;
						bestHeuristic = heuristic;
					}
				}
			}
			field.makeMove(bestMove, currentId, false);
			winner = field.getWinner();
			addMove(bestMove, currentId, botMoves, opponentMoves, botId, opponentId);
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
}
