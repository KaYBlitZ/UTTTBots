package com.kayblitz.uttt.bot;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Bot;
import com.kayblitz.uttt.BotParser;
import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class AlphabetaBot extends Bot {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Alphabeta depth must be given");
			return;
		}
		int depth = -1;
		try {
			depth = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid depth");
			return;
		}
		new BotParser(new AlphabetaBot(depth)).run();
	}
	
	private static final int WIN = 999;
	private static final int TIE = 0;
	private int botId, opponentId, depth;
	
	public AlphabetaBot(int depth) {
		this.depth = depth;
	}

	@Override
	public Move makeMove(Field field, int timebank, int botId, int moveNum) {
		this.botId = botId;
		opponentId = (botId == 1 ? 2 : 1);
		
		System.err.println("Timebank: " + timebank);
		Random rand = new Random(System.currentTimeMillis());
		int bestHeuristic = Integer.MIN_VALUE;
		Move bestMove = null;
		
		// this function acts as the bot's first maximizing node
		ArrayList<Move> moves = field.getAvailableMoves();
		StringBuffer sb = new StringBuffer();
		for (Move move : moves) {
			field.makeMove(move, botId);
			int heuristic = alphabeta(field, Integer.MIN_VALUE, Integer.MAX_VALUE, opponentId, depth - 1);
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
		
		return bestMove;
	}
	
	public int alphabeta(Field field, int alpha, int beta, int maximizingPlayer, int depth) {
		// the previous move maker won, so if the current maximizingPlayer is us
		// then our opponent made the winning move, so we lost
		int winner = field.getWinner();
		if (winner == 0) return TIE;
		if (winner > 0) return (maximizingPlayer == botId ? -WIN : WIN);
		if (depth == 0) return Evaluation.evaluateFieldSimple(field, botId, opponentId);
		
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
