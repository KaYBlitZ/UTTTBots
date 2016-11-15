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
	private Random rand;
	
	public MinimaxBot(int depth, int type) {
		this.depth = depth;
		this.type = type;
		rand = new Random(System.currentTimeMillis());
	}

	@Override
	public Move makeMove(int timebank) {		
		System.err.println("Timebank: " + timebank);
		if (field.getMoveNum() == 1)
			return new Move(4, 4); // best first move
		double bestHeuristic = Integer.MIN_VALUE;
		Move bestMove = null;
		
		// this function acts as the bot's first maximizing node
		ArrayList<Move> moves = field.getAvailableMoves();
		StringBuffer sb = new StringBuffer();
		for (Move move : moves) {
			field.makeMove(move, botId, true);
			double heuristic = minimax(field, Integer.MIN_VALUE, Integer.MAX_VALUE, opponentId, depth - 1);
			field.undo();
			if (Double.compare(heuristic, bestHeuristic) > 0) {
				bestHeuristic = heuristic;
				bestMove = move;
			} else if (Double.compare(heuristic, bestHeuristic) == 0) {
				// choosing randomly
				if (rand.nextInt(2) == 0)
					bestMove = move;
			}
			sb.append(String.format("%d,%d : %.2f\n", move.column, move.row, heuristic));
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
			case Evaluation.COMPREHENSIVE:
				return Evaluation.evaluateFieldComprehensive(field, botId, opponentId);
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
