package com.kayblitz.uttt.bot;

import com.kayblitz.uttt.Field;

public class Evaluation {
	
	public static final int SIMPLE = 0;
	public static final int CONNECTING = 1;
	public static final int ADVANCED = 2;
	public static final int ADVANCED_OPTIMIZED = 3;
	
	public static final int WIN = 999;
	public static final int TIE = 0;
	
	/**
	 * A more positive value indicates that the bot has won more macro fields
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
	 * Same as simple, but also gives more points for having two in-a-row markers.
	 */
	public static int evaluateFieldConnecting(Field field, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
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
			botConnected = 0;
			opponentConnected = 0;
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
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= 20;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= 20;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroBoard[0][2] == botId) {
			botConnected++;
		} else if (macroBoard[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[1][1] == botId) {
			botConnected++;
		} else if (macroBoard[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[2][0] == botId) {
			botConnected++;
		} else if (macroBoard[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += 20;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= 20;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroBoard[0][0] == botId) {
			botConnected++;
		} else if (macroBoard[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[1][1] == botId) {
			botConnected++;
		} else if (macroBoard[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[2][2] == botId) {
			botConnected++;
		} else if (macroBoard[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += 20;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= 20;
		}
		// check 2 in a row in mini fields
		int[][] board = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMiniFieldConnecting(field, board, i, botId, opponentId);
		}
		return heuristic;
	}
	
	private static int evaluateMiniFieldConnecting(Field field, int[][] board, int miniIndex, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
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
			botConnected = 0;
			opponentConnected = 0;
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
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic--;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic--;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (board[0][2] == botId) {
			botConnected++;
		} else if (board[0][2] == opponentId) {
			opponentConnected++;
		}
		if (board[1][1] == botId) {
			botConnected++;
		} else if (board[1][1] == opponentId) {
			opponentConnected++;
		}
		if (board[2][0] == botId) {
			botConnected++;
		} else if (board[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic++;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic--;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (board[0][0] == botId) {
			botConnected++;
		} else if (board[0][0] == opponentId) {
			opponentConnected++;
		}
		if (board[1][1] == botId) {
			botConnected++;
		} else if (board[1][1] == opponentId) {
			opponentConnected++;
		}
		if (board[2][2] == botId) {
			botConnected++;
		} else if (board[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic++;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic--;
		}
		return heuristic;
	}
	
	private static final int TWO_IN_A_ROW = 50;
	private static final int MIDDLE = 30;
	private static final int CORNER = 20;
	private static final int SIDE = 10;
	private static final int MINI_TWO_IN_A_ROW = 5;
	private static final int MINI_MIDDLE = 3;
	private static final int MINI_CORNER = 2;
	private static final int MINI_SIDE = 1;
	
	/**
	 * Same as simple, but also gives more points for having two in-a-row markers.
	 */
	public static int evaluateFieldAdvanced(Field field, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
		int[][] macroBoard = field.getMacroboard();
		// check board positions
		// middle
		if (macroBoard[1][1] == botId) {
			heuristic += MIDDLE;
		} else if (macroBoard[1][1] == opponentId) {
			heuristic -= MIDDLE;
		}
		// corners
		if (macroBoard[0][0] == botId) {
			heuristic += CORNER;
		} else if (macroBoard[0][0] == opponentId) {
			heuristic -= CORNER;
		}
		if (macroBoard[2][0] == botId) {
			heuristic += CORNER;
		} else if (macroBoard[2][0] == opponentId) {
			heuristic -= CORNER;
		}
		if (macroBoard[0][2] == botId) {
			heuristic += CORNER;
		} else if (macroBoard[0][2] == opponentId) {
			heuristic -= CORNER;
		}
		if (macroBoard[2][2] == botId) {
			heuristic += CORNER;
		} else if (macroBoard[2][2] == opponentId) {
			heuristic -= CORNER;
		}
		// sides
		if (macroBoard[1][0] == botId) {
			heuristic += SIDE;
		} else if (macroBoard[1][0] == opponentId) {
			heuristic -= SIDE;
		}
		if (macroBoard[0][1] == botId) {
			heuristic += SIDE;
		} else if (macroBoard[0][1] == opponentId) {
			heuristic -= SIDE;
		}
		if (macroBoard[2][1] == botId) {
			heuristic += SIDE;
		} else if (macroBoard[2][1] == opponentId) {
			heuristic -= SIDE;
		}
		if (macroBoard[1][2] == botId) {
			heuristic += SIDE;
		} else if (macroBoard[1][2] == opponentId) {
			heuristic -= SIDE;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= TWO_IN_A_ROW;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= TWO_IN_A_ROW;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroBoard[0][2] == botId) {
			botConnected++;
		} else if (macroBoard[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[1][1] == botId) {
			botConnected++;
		} else if (macroBoard[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[2][0] == botId) {
			botConnected++;
		} else if (macroBoard[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= TWO_IN_A_ROW;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroBoard[0][0] == botId) {
			botConnected++;
		} else if (macroBoard[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[1][1] == botId) {
			botConnected++;
		} else if (macroBoard[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[2][2] == botId) {
			botConnected++;
		} else if (macroBoard[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= TWO_IN_A_ROW;
		}
		// check 2 in a row in mini fields
		int[][] board = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMiniFieldAdvanced(field, board, i, botId, opponentId);
		}
		return heuristic;
	}
	
	private static int evaluateMiniFieldAdvanced(Field field, int[][] board, int miniIndex, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
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
		// check board positions
		// middle
		if (board[1][1] == botId) {
			heuristic += MINI_MIDDLE;
		} else if (board[1][1] == opponentId) {
			heuristic -= MINI_MIDDLE;
		}
		// corners
		if (board[0][0] == botId) {
			heuristic += MINI_CORNER;
		} else if (board[0][0] == opponentId) {
			heuristic -= MINI_CORNER;
		}
		if (board[2][0] == botId) {
			heuristic += MINI_CORNER;
		} else if (board[2][0] == opponentId) {
			heuristic -= MINI_CORNER;
		}
		if (board[0][2] == botId) {
			heuristic += MINI_CORNER;
		} else if (board[0][2] == opponentId) {
			heuristic -= MINI_CORNER;
		}
		if (board[2][2] == botId) {
			heuristic += MINI_CORNER;
		} else if (board[2][2] == opponentId) {
			heuristic -= MINI_CORNER;
		}
		// sides
		if (board[1][0] == botId) {
			heuristic += MINI_SIDE;
		} else if (board[1][0] == opponentId) {
			heuristic -= MINI_SIDE;
		}
		if (board[0][1] == botId) {
			heuristic += MINI_SIDE;
		} else if (board[0][1] == opponentId) {
			heuristic -= MINI_SIDE;
		}
		if (board[2][1] == botId) {
			heuristic += MINI_SIDE;
		} else if (board[2][1] == opponentId) {
			heuristic -= MINI_SIDE;
		}
		if (board[1][2] == botId) {
			heuristic += MINI_SIDE;
		} else if (board[1][2] == opponentId) {
			heuristic -= MINI_SIDE;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += MINI_TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MINI_TWO_IN_A_ROW;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += MINI_TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MINI_TWO_IN_A_ROW;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (board[0][2] == botId) {
			botConnected++;
		} else if (board[0][2] == opponentId) {
			opponentConnected++;
		}
		if (board[1][1] == botId) {
			botConnected++;
		} else if (board[1][1] == opponentId) {
			opponentConnected++;
		}
		if (board[2][0] == botId) {
			botConnected++;
		} else if (board[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MINI_TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MINI_TWO_IN_A_ROW;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (board[0][0] == botId) {
			botConnected++;
		} else if (board[0][0] == opponentId) {
			opponentConnected++;
		}
		if (board[1][1] == botId) {
			botConnected++;
		} else if (board[1][1] == opponentId) {
			opponentConnected++;
		}
		if (board[2][2] == botId) {
			botConnected++;
		} else if (board[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MINI_TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MINI_TWO_IN_A_ROW;
		}
		return heuristic;
	}
	
	public static double TWO_IN_A_ROW_OPTIMIZED;
	public static double MIDDLE_OPTIMIZED;
	public static double CORNER_OPTIMIZED;
	public static double SIDE_OPTIMIZED;
	public static double MINI_TWO_IN_A_ROW_OPTIMIZED;
	public static double MINI_MIDDLE_OPTIMIZED;
	public static double MINI_CORNER_OPTIMIZED;
	public static double MINI_SIDE_OPTIMIZED;
	
	/**
	 * Same as simple, but also gives more points for having two in-a-row markers.
	 */
	public static double evaluateFieldAdvancedOptimized(Field field, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int[][] macroBoard = field.getMacroboard();
		// check board positions
		// middle
		if (macroBoard[1][1] == botId) {
			heuristic += MIDDLE_OPTIMIZED;
		} else if (macroBoard[1][1] == opponentId) {
			heuristic -= MIDDLE_OPTIMIZED;
		}
		// corners
		if (macroBoard[0][0] == botId) {
			heuristic += CORNER_OPTIMIZED;
		} else if (macroBoard[0][0] == opponentId) {
			heuristic -= CORNER_OPTIMIZED;
		}
		if (macroBoard[2][0] == botId) {
			heuristic += CORNER_OPTIMIZED;
		} else if (macroBoard[2][0] == opponentId) {
			heuristic -= CORNER_OPTIMIZED;
		}
		if (macroBoard[0][2] == botId) {
			heuristic += CORNER_OPTIMIZED;
		} else if (macroBoard[0][2] == opponentId) {
			heuristic -= CORNER_OPTIMIZED;
		}
		if (macroBoard[2][2] == botId) {
			heuristic += CORNER_OPTIMIZED;
		} else if (macroBoard[2][2] == opponentId) {
			heuristic -= CORNER_OPTIMIZED;
		}
		// sides
		if (macroBoard[1][0] == botId) {
			heuristic += SIDE_OPTIMIZED;
		} else if (macroBoard[1][0] == opponentId) {
			heuristic -= SIDE_OPTIMIZED;
		}
		if (macroBoard[0][1] == botId) {
			heuristic += SIDE_OPTIMIZED;
		} else if (macroBoard[0][1] == opponentId) {
			heuristic -= SIDE_OPTIMIZED;
		}
		if (macroBoard[2][1] == botId) {
			heuristic += SIDE_OPTIMIZED;
		} else if (macroBoard[2][1] == opponentId) {
			heuristic -= SIDE_OPTIMIZED;
		}
		if (macroBoard[1][2] == botId) {
			heuristic += SIDE_OPTIMIZED;
		} else if (macroBoard[1][2] == opponentId) {
			heuristic -= SIDE_OPTIMIZED;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroBoard[0][2] == botId) {
			botConnected++;
		} else if (macroBoard[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[1][1] == botId) {
			botConnected++;
		} else if (macroBoard[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[2][0] == botId) {
			botConnected++;
		} else if (macroBoard[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= TWO_IN_A_ROW_OPTIMIZED;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroBoard[0][0] == botId) {
			botConnected++;
		} else if (macroBoard[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[1][1] == botId) {
			botConnected++;
		} else if (macroBoard[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroBoard[2][2] == botId) {
			botConnected++;
		} else if (macroBoard[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= TWO_IN_A_ROW_OPTIMIZED;
		}
		// check 2 in a row in mini fields
		int[][] board = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMiniFieldAdvancedOptimized(field, board, i, botId, opponentId);
		}
		return heuristic;
	}
	
	private static double evaluateMiniFieldAdvancedOptimized(Field field, int[][] board, int miniIndex, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
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
		// check board positions
		// middle
		if (board[1][1] == botId) {
			heuristic += MINI_MIDDLE_OPTIMIZED;
		} else if (board[1][1] == opponentId) {
			heuristic -= MINI_MIDDLE_OPTIMIZED;
		}
		// corners
		if (board[0][0] == botId) {
			heuristic += MINI_CORNER_OPTIMIZED;
		} else if (board[0][0] == opponentId) {
			heuristic -= MINI_CORNER_OPTIMIZED;
		}
		if (board[2][0] == botId) {
			heuristic += MINI_CORNER_OPTIMIZED;
		} else if (board[2][0] == opponentId) {
			heuristic -= MINI_CORNER_OPTIMIZED;
		}
		if (board[0][2] == botId) {
			heuristic += MINI_CORNER_OPTIMIZED;
		} else if (board[0][2] == opponentId) {
			heuristic -= MINI_CORNER_OPTIMIZED;
		}
		if (board[2][2] == botId) {
			heuristic += MINI_CORNER_OPTIMIZED;
		} else if (board[2][2] == opponentId) {
			heuristic -= MINI_CORNER_OPTIMIZED;
		}
		// sides
		if (board[1][0] == botId) {
			heuristic += MINI_SIDE_OPTIMIZED;
		} else if (board[1][0] == opponentId) {
			heuristic -= MINI_SIDE_OPTIMIZED;
		}
		if (board[0][1] == botId) {
			heuristic += MINI_SIDE_OPTIMIZED;
		} else if (board[0][1] == opponentId) {
			heuristic -= MINI_SIDE_OPTIMIZED;
		}
		if (board[2][1] == botId) {
			heuristic += MINI_SIDE_OPTIMIZED;
		} else if (board[2][1] == opponentId) {
			heuristic -= MINI_SIDE_OPTIMIZED;
		}
		if (board[1][2] == botId) {
			heuristic += MINI_SIDE_OPTIMIZED;
		} else if (board[1][2] == opponentId) {
			heuristic -= MINI_SIDE_OPTIMIZED;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += MINI_TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MINI_TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
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
				heuristic += MINI_TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MINI_TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (board[0][2] == botId) {
			botConnected++;
		} else if (board[0][2] == opponentId) {
			opponentConnected++;
		}
		if (board[1][1] == botId) {
			botConnected++;
		} else if (board[1][1] == opponentId) {
			opponentConnected++;
		}
		if (board[2][0] == botId) {
			botConnected++;
		} else if (board[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MINI_TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MINI_TWO_IN_A_ROW_OPTIMIZED;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (board[0][0] == botId) {
			botConnected++;
		} else if (board[0][0] == opponentId) {
			opponentConnected++;
		}
		if (board[1][1] == botId) {
			botConnected++;
		} else if (board[1][1] == opponentId) {
			opponentConnected++;
		}
		if (board[2][2] == botId) {
			botConnected++;
		} else if (board[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MINI_TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MINI_TWO_IN_A_ROW_OPTIMIZED;
		}
		return heuristic;
	}
}