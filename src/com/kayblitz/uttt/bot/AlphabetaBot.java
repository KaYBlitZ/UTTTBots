package com.kayblitz.uttt.bot;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Bot;
import com.kayblitz.uttt.BotParser;
import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class AlphabetaBot extends Bot {

	public static void main(String[] args) {
		new BotParser(new AlphabetaBot()).run();
	}
	
	private static final int DEPTH = 7;
	private static final int WIN = 999;
	private static final int TIE = 0;
	private int botId, opponentId;

	@Override
	public Move makeMove(Field field, int timebank, int botId) {
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
			int heuristic = alphabeta(field, Integer.MIN_VALUE, Integer.MAX_VALUE, opponentId, DEPTH - 1);
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
		if (depth == 0) return evaluateField(field);
		
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
	
	public int evaluateField(Field field) {
		int heuristic = 0;
		
		int[][] macroBoard = field.getMacroboard();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (macroBoard[col][row] == botId) {
					heuristic++;
				} else if (macroBoard[col][row] == opponentId) {
					heuristic--;
				}
			}
		}
		
		return heuristic;
	}
}
