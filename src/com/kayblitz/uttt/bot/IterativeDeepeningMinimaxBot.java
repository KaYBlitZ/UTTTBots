package com.kayblitz.uttt.bot;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Bot;
import com.kayblitz.uttt.BotParser;
import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class IterativeDeepeningMinimaxBot extends Bot {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Evaluation type must be given");
			return;
		}
		int type = -1;
		try {
			type = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid evaluation type");
			return;
		}
		new BotParser(new IterativeDeepeningMinimaxBot(type)).run();
	}
	
	private static final int MAX_DEPTH = 15;
	private int type;
	private long startTime, limit;
	private boolean timedOut;
	
	public IterativeDeepeningMinimaxBot(int type) {
		this.type = type;
	}
	
	public long getElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public Move makeMove(Field field, int timebank, int moveNum) {
		startTime = System.currentTimeMillis();
		if (moveNum == 1)
			return new Move(4, 4); // best first move
		Random rand = new Random(System.currentTimeMillis());
		// this function acts as the bot's first maximizing node
		ArrayList<Move> moves = field.getAvailableMoves();
		
		if ((type == Evaluation.SIMPLE && moveNum < 20) || 
				((type == Evaluation.CONNECTING || type == Evaluation.ADVANCED || type == Evaluation.ADVANCED_OPTIMIZED) && moveNum < 15)) {
			// heuristics mostly the same (insignificant), dont waste timebank
			limit = 500L;
		} else {
			int size = moves.size();
			if (size < 4) {
				limit = 500L;
			} else if (size < 7) {
				limit = 800L;
			} else if (size < 10) {
				limit = 1100L;
			} else {
				limit = 1700L;
			}
			if (limit > timebank)
				limit = (long) (0.85f * timebank);
		}
		
		// best values from a completely finished depth
		double bestHeuristic = Integer.MIN_VALUE;
		Move bestMove = null;
		// tentative values from the current depth exploration
		double tentativeBestHeuristic = Integer.MIN_VALUE;
		Move tentativeBestMove = null;
		
		timedOut = false;
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Timebank %d, Limit %d\n", timebank, limit));
		
		for (int depth = 1; !timedOut && depth <= MAX_DEPTH; depth++) {
			tentativeBestHeuristic = Integer.MIN_VALUE;
			tentativeBestMove = null;
			sb.append("Depth " + depth + '\n');
			for (Move move : moves) {
				field.makeMove(move, botId, true);
				double heuristic = minimax(field, Integer.MIN_VALUE, Integer.MAX_VALUE, opponentId, depth - 1);
				field.undo();
				if (timedOut)
					break;
				if (Double.compare(heuristic, tentativeBestHeuristic) > 0) {
					tentativeBestHeuristic = heuristic;
					tentativeBestMove = move;
				} else if (Double.compare(heuristic, tentativeBestHeuristic) == 0) {
					// choosing randomly
					if (rand.nextInt(2) == 0)
						tentativeBestMove = move;
				}
				sb.append(String.format("%d,%d : %.2f\n", move.column, move.row, heuristic));
			}
			if (timedOut) {
				sb.append("Timed out\n");
				sb.append("Max depth " + (depth - 1));
			} else {
				// not timed out, so results are valid, update new bests
				bestMove = tentativeBestMove;
				bestHeuristic = tentativeBestHeuristic;
			}
		}
		System.err.println(sb.toString());
		
		if (Double.compare(bestHeuristic, Evaluation.WIN) == 0) { // check to see if we can end the game now
			for (Move move : moves) {
				field.makeMove(move, botId, true);
				if (field.getWinner() > 0) {
					field.undo();
					return move; // win the game
				} else {
					field.undo();
				}
			}
		} else if (Double.compare(bestHeuristic, -Evaluation.WIN) == 0) { // going to lose, delay it
			for (Move move : moves) {
				field.makeMove(move, botId, true);
				ArrayList<Move> opponentMoves = field.getAvailableMoves();
				boolean opponentWins = false;
				for (Move opponentMove : opponentMoves) {
					field.makeMove(opponentMove, opponentId, true);
					if (field.getWinner() > 0) {
						field.undo();
						opponentWins = true;
						break;
					} else {
						field.undo();
					}
				}
				field.undo();
				if (!opponentWins)
					return move;
			}
		}
		
		return bestMove;
	}
	
	public double minimax(Field field, double alpha, double beta, int maximizingPlayer, int depth) {
		if (getElapsedTime() > limit) {
			timedOut = true;
			return 0;
		}
		// the previous move maker won, so if the current maximizingPlayer is us
		// then our opponent made the winning move, so we lost
		int winner = field.getWinner();
		if (winner == 0) return Evaluation.TIE;
		if (winner > 0) return (maximizingPlayer == botId ? -Evaluation.WIN : Evaluation.WIN);
		if (depth == 0) {
			switch (type) {
			case Evaluation.SIMPLE:
				return Evaluation.evaluateFieldSimple(field, botId, opponentId);
			case Evaluation.CONNECTING:
				return Evaluation.evaluateFieldConnecting(field, botId, opponentId);
			case Evaluation.ADVANCED:
				return Evaluation.evaluateFieldAdvanced(field, botId, opponentId);
			case Evaluation.ADVANCED_OPTIMIZED:
				return Evaluation.evaluateFieldAdvancedOptimized(field, botId, opponentId);
			default:
				throw new RuntimeException("Invalid heuristic evaluation function");
			}
		}
		
		ArrayList<Move> moves = field.getAvailableMoves();
		if (maximizingPlayer == botId) {
			for (Move move : moves) {
				field.makeMove(move, maximizingPlayer, true);
				double heuristic = minimax(field, alpha, beta, maximizingPlayer == 1 ? 2 : 1, depth - 1);
				field.undo();
				alpha = Math.max(alpha, heuristic);
				if (Double.compare(beta, alpha) <= 0) return alpha;
			}
		} else { // opponent
			for (Move move : moves) {
				field.makeMove(move, maximizingPlayer, true);
				double heuristic = minimax(field, alpha, beta, maximizingPlayer == 1 ? 2 : 1, depth - 1);
				field.undo();
				beta = Math.min(beta, heuristic);
				if (Double.compare(beta, alpha) <= 0) return beta;
			}
		}
		return maximizingPlayer == botId ? alpha : beta;
	}
}
