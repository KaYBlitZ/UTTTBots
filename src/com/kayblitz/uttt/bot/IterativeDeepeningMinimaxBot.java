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
		Random rand = new Random(System.currentTimeMillis());
		// this function acts as the bot's first maximizing node
		ArrayList<Move> moves = field.getAvailableMoves();
		
		if ((type == Evaluation.SIMPLE && moveNum < 20) || 
				((type == Evaluation.CONNECTING || type == Evaluation.ADVANCED) && moveNum < 15)) {
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
			if (limit > timebank) limit = (long) (0.75f * timebank);
		}
		
		// best values from a completely finished depth
		int bestHeuristic = Integer.MIN_VALUE;
		Move bestMove = null;
		// tentative values from the current depth exploration
		int tentativeBestHeuristic = Integer.MIN_VALUE;
		Move tentativeBestMove = null;
		
		timedOut = false;
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Timebank: %d, Limit %d\n", timebank, limit));
		
		for (int depth = 1; !timedOut && depth <= MAX_DEPTH; depth++) {
			tentativeBestHeuristic = Integer.MIN_VALUE;
			tentativeBestMove = null;
			sb.append("Depth " + depth + '\n');
			for (Move move : moves) {
				field.makeMove(move, botId);
				int heuristic = alphabeta(field, Integer.MIN_VALUE, Integer.MAX_VALUE, opponentId, depth - 1);
				field.undo();
				if (timedOut) break;
				if (heuristic > tentativeBestHeuristic) {
					tentativeBestHeuristic = heuristic;
					tentativeBestMove = move;
				} else if (heuristic == tentativeBestHeuristic) {
					// choosing randomly
					if (rand.nextInt(2) == 0) {
						tentativeBestMove = move;
					}
				}
				sb.append(String.format("%d,%d : %d\n", move.column, move.row, heuristic));
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
		
		if (bestHeuristic == Evaluation.WIN) { // check to see if we can end the game now
			for (Move move : moves) {
				field.makeMove(move, botId);
				if (field.getWinner() > 0) {
					field.undo();
					return move; // win the game
				} else {
					field.undo();
				}
			}
		} else if (bestHeuristic == -Evaluation.WIN) { // going to lose, delay it
			for (Move move : moves) {
				field.makeMove(move, botId);
				ArrayList<Move> opponentMoves = field.getAvailableMoves();
				boolean opponentWins = false;
				for (Move opponentMove : opponentMoves) {
					field.makeMove(opponentMove, opponentId);
					if (field.getWinner() > 0) {
						field.undo();
						opponentWins = true;
						break;
					} else {
						field.undo();
					}
				}
				field.undo();
				if (!opponentWins) return move;
			}
		}
		
		return bestMove;
	}
	
	public int alphabeta(Field field, int alpha, int beta, int maximizingPlayer, int depth) {
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
				field.makeMove(move, maximizingPlayer);
				int heuristic = alphabeta(field, alpha, beta, maximizingPlayer == 1 ? 2 : 1, depth - 1);
				field.undo();
				alpha = Math.max(alpha, heuristic);
				if (beta <= alpha) return alpha;
			}
		} else { // opponent
			for (Move move : moves) {
				field.makeMove(move, maximizingPlayer);
				int heuristic = alphabeta(field, alpha, beta, maximizingPlayer == 1 ? 2 : 1, depth - 1);
				field.undo();
				beta = Math.min(beta, heuristic);
				if (beta <= alpha) return beta;
			}
		}
		return maximizingPlayer == botId ? alpha : beta;
	}
}
