package com.kayblitz.uttt.bot;

import com.kayblitz.uttt.Field;

/**
 * Simple evaluation = 0
 * Connecting evaluation = 1
 * @author Kenneth
 *
 */
public class Evaluation {
	
	/**
	 * A more positive value indicates that the bot has won more macro fields
	 * @param field
	 * @param botId
	 * @param opponentId
	 * @return heuristic value
	 */
	public static int evaluateFieldSimple(Field field, int botId, int opponentId) {
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
	
	/**
	 * Same as simple, but also gives more points for having two-in-a-row markers.
	 * @param field
	 * @param botId
	 * @param opponentId
	 * @return heuristic value
	 */
	public static int evaluateFieldConnecting(Field field, int botId, int opponentId) {
		int heuristic = 0;
		int[][] macroBoard = field.getMacroboard();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				// points for winning mini TTT field
				if (macroBoard[col][row] == botId) {
					heuristic += 10;
				} else if (macroBoard[col][row] == opponentId) {
					heuristic -= 10;
				}
			}
			// check horizontal 2 in a row
			int botConnected = 0, opponentConnected = 0;
			if (macroBoard[0][row] == botId) {
				botConnected++;
			} else if (macroBoard[0][row] == opponentId) {
				opponentConnected++;
			}
			if (macroBoard[1][row] == botId) {
				botConnected++;
			} else if (macroBoard[1][row] == opponentId) {
				opponentConnected++;
			}
			if (macroBoard[2][row] == botId) {
				botConnected++;
			} else if (macroBoard[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += 20;
			}
			if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= 20;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			int botConnected = 0, opponentConnected = 0;
			if (macroBoard[col][0] == botId) {
				botConnected++;
			} else if (macroBoard[col][0] == opponentId) {
				opponentConnected++;
			}
			if (macroBoard[col][1] == botId) {
				botConnected++;
			} else if (macroBoard[col][1] == opponentId) {
				opponentConnected++;
			}
			if (macroBoard[col][2] == botId) {
				botConnected++;
			} else if (macroBoard[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += 20;
			}
			if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= 20;
			}
		}
		// check 2 in a row in mini fields
		int[][] board = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMiniFieldConnecting(field, board, i, botId, opponentId);
		}
		return heuristic;
	}
	
	/**
	 * 
	 * @param field - the field
	 * @param board - an array to store markers
	 * @param miniIndex - index of mini field to evaluate
	 * @return
	 */
	private static final int evaluateMiniFieldConnecting(Field field, int[][] board, int miniIndex, int botId, int opponentId) {
		int heuristic = 0;
		int topLeftColumn = (miniIndex % 3) * 3;
		int topLeftRow = (miniIndex / 3) * 3;
		int[][] mBoard = field.getBoard();
		board[0][0] = mBoard[topLeftColumn][topLeftRow];
		board[1][0] = mBoard[topLeftColumn + 1][topLeftRow];
		board[2][0] = mBoard[topLeftColumn + 2][topLeftRow];
		board[0][1] = mBoard[topLeftColumn][topLeftRow + 1];
		board[1][1] = mBoard[topLeftColumn + 1][topLeftRow + 1];
		board[2][1] = mBoard[topLeftColumn + 2][topLeftRow + 1];
		board[0][2] = mBoard[topLeftColumn][topLeftRow + 2];
		board[1][2] = mBoard[topLeftColumn + 1][topLeftRow + 2];
		board[2][2] = mBoard[topLeftColumn + 2][topLeftRow + 2];
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			int botConnected = 0, opponentConnected = 0;
			if (board[0][row] == botId) {
				botConnected++;
			} else if (board[0][row] == opponentId) {
				opponentConnected++;
			}
			if (board[1][row] == botId) {
				botConnected++;
			} else if (board[1][row] == opponentId) {
				opponentConnected++;
			}
			if (board[2][row] == botId) {
				botConnected++;
			} else if (board[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic++;
			}
			if (opponentConnected > 1 && botConnected == 0) {
				heuristic--;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			int botConnected = 0, opponentConnected = 0;
			if (board[col][0] == botId) {
				botConnected++;
			} else if (board[col][0] == opponentId) {
				opponentConnected++;
			}
			if (board[col][1] == botId) {
				botConnected++;
			} else if (board[col][1] == opponentId) {
				opponentConnected++;
			}
			if (board[col][2] == botId) {
				botConnected++;
			} else if (board[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic++;
			}
			if (opponentConnected > 1 && botConnected == 0) {
				heuristic--;
			}
		}
		return heuristic;
	}
}