package com.kayblitz.uttt.bot;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Bot;
import com.kayblitz.uttt.BotParser;
import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class MinimaxBot extends Bot {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Depth and evaluation type must be given");
			return;
		}
		int depth = -1;
		int type = -1;
		try {
			depth = Integer.parseInt(args[0]);
			type = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid depth or evaluation type");
			return;
		}
		new BotParser(new MinimaxBot(depth, type)).run();
	}
	
	private int depth, type;
	
	public MinimaxBot(int depth, int type) {
		this.depth = depth;
		this.type = type;
	}

	@Override
	public Move makeMove(Field field, int timebank, int moveNum) {		
		System.err.println("Timebank: " + timebank);
		Random rand = new Random(System.currentTimeMillis());
		int bestHeuristic = Integer.MIN_VALUE;
		Move bestMove = null;
		
		// this function acts as the bot's first maximizing node
		ArrayList<Move> moves = field.getAvailableMoves();
		StringBuffer sb = new StringBuffer();
		for (Move move : moves) {
			field.makeMove(move, botId, true);
			int heuristic = minimax(field, Integer.MIN_VALUE, Integer.MAX_VALUE, opponentId, depth - 1);
			field.undo();
			if (heuristic > bestHeuristic) {
				bestHeuristic = heuristic;
				bestMove = move;
			} else if (heuristic == bestHeuristic) {
				// choosing randomly
				if (rand.nextInt(2) == 0) {
					bestMove = move;
				}
			}
			sb.append(String.format("%d,%d : %d\n", move.column, move.row, heuristic));
		}
		System.err.println(sb.toString());
		
		if (bestHeuristic == Evaluation.WIN) { // check to see if we can end the game now
			for (Move move : moves) {
				field.makeMove(move, botId, true);
				if (field.getWinner() > 0) {
					field.undo();
					return move; // win the game
				} else {
					field.undo();
				}
			}
		} else if (bestHeuristic == -Evaluation.WIN) { // going to lose, delay it
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
				if (!opponentWins) return move;
			}
		}
		
		return bestMove;
	}
	
	public int minimax(Field field, int alpha, int beta, int maximizingPlayer, int depth) {
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
				int heuristic = minimax(field, alpha, beta, maximizingPlayer == 1 ? 2 : 1, depth - 1);
				field.undo();
				alpha = Math.max(alpha, heuristic);
				if (beta <= alpha) return alpha;
			}
		} else { // opponent
			for (Move move : moves) {
				field.makeMove(move, maximizingPlayer, true);
				int heuristic = minimax(field, alpha, beta, maximizingPlayer == 1 ? 2 : 1, depth - 1);
				field.undo();
				beta = Math.min(beta, heuristic);
				if (beta <= alpha) return beta;
			}
		}
		return maximizingPlayer == botId ? alpha : beta;
	}
}
