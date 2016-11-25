package com.kayblitz.uttt.bot;

import com.kayblitz.uttt.Field;

public class Evaluation {
	
	public static final int SIMPLE = 0;
	public static final int CONNECTING = 1;
	public static final int ADVANCED = 2;
	public static final int ADVANCED_OPTIMIZED = 3;
	public static final int COMPREHENSIVE = 4;
	
	public static final int WIN = 999;
	public static final int TIE = 0;
	
	/**
	 * A more positive value indicates that the bot has won more micro fields
	 */
	public static int evaluateFieldSimple(Field field, int botId, int opponentId) {
		int heuristic = 0;
		int[][] microField = field.getMacroField();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (microField[col][row] == botId) {
					heuristic++;
				} else if (microField[col][row] == opponentId) {
					heuristic--;
				}
			}
		}
		return heuristic;
	}
	
	/**
	 * Same as simple, but also gives more points for having two connecting micro field wins.
	 */
	public static int evaluateFieldConnecting(Field field, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
		int[][] macroField = field.getMacroField();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				// points for winning mini TTT field
				if (macroField[col][row] == botId) {
					heuristic += 10;
				} else if (macroField[col][row] == opponentId) {
					heuristic -= 10;
				}
			}
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[0][row] == botId) {
				botConnected++;
			} else if (macroField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[1][row] == botId) {
				botConnected++;
			} else if (macroField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[2][row] == botId) {
				botConnected++;
			} else if (macroField[2][row] == opponentId) {
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
			if (macroField[col][0] == botId) {
				botConnected++;
			} else if (macroField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][1] == botId) {
				botConnected++;
			} else if (macroField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][2] == botId) {
				botConnected++;
			} else if (macroField[col][2] == opponentId) {
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
		if (macroField[0][2] == botId) {
			botConnected++;
		} else if (macroField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][0] == botId) {
			botConnected++;
		} else if (macroField[2][0] == opponentId) {
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
		if (macroField[0][0] == botId) {
			botConnected++;
		} else if (macroField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][2] == botId) {
			botConnected++;
		} else if (macroField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += 20;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= 20;
		}
		// check 2 in a row in mini fields
		int[][] mField = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMiniFieldConnecting(field, mField, i, botId, opponentId);
		}
		return heuristic;
	}
	
	private static int evaluateMiniFieldConnecting(Field field, int[][] microField, int miniIndex, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
		int topLeftColumn = (miniIndex % 3) * 3;
		int topLeftRow = (miniIndex / 3) * 3;
		int[][] mField = field.getField();
		microField[0][0] = mField[topLeftColumn][topLeftRow];
		microField[1][0] = mField[topLeftColumn + 1][topLeftRow];
		microField[2][0] = mField[topLeftColumn + 2][topLeftRow];
		microField[0][1] = mField[topLeftColumn][topLeftRow + 1];
		microField[1][1] = mField[topLeftColumn + 1][topLeftRow + 1];
		microField[2][1] = mField[topLeftColumn + 2][topLeftRow + 1];
		microField[0][2] = mField[topLeftColumn][topLeftRow + 2];
		microField[1][2] = mField[topLeftColumn + 1][topLeftRow + 2];
		microField[2][2] = mField[topLeftColumn + 2][topLeftRow + 2];
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[0][row] == botId) {
				botConnected++;
			} else if (microField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[1][row] == botId) {
				botConnected++;
			} else if (microField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[2][row] == botId) {
				botConnected++;
			} else if (microField[2][row] == opponentId) {
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
			if (microField[col][0] == botId) {
				botConnected++;
			} else if (microField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][1] == botId) {
				botConnected++;
			} else if (microField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][2] == botId) {
				botConnected++;
			} else if (microField[col][2] == opponentId) {
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
		if (microField[0][2] == botId) {
			botConnected++;
		} else if (microField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][0] == botId) {
			botConnected++;
		} else if (microField[2][0] == opponentId) {
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
		if (microField[0][0] == botId) {
			botConnected++;
		} else if (microField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][2] == botId) {
			botConnected++;
		} else if (microField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic++;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic--;
		}
		return heuristic;
	}
	
	private static final int MACRO_TWO_IN_A_ROW = 50;
	private static final int MACRO_MIDDLE = 30;
	private static final int MACRO_CORNER = 20;
	private static final int MACRO_SIDE = 10;
	private static final int MICRO_TWO_IN_A_ROW = 5;
	private static final int MICRO_MIDDLE = 3;
	private static final int MICRO_CORNER = 2;
	private static final int MICRO_SIDE = 1;
	
	/**
	 * Same as simple, but also gives more points for having two in-a-row markers.
	 */
	public static int evaluateFieldAdvanced(Field field, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
		int[][] macroField = field.getMacroField();
		// check field positions
		// middle
		if (macroField[1][1] == botId) {
			heuristic += MACRO_MIDDLE;
		} else if (macroField[1][1] == opponentId) {
			heuristic -= MACRO_MIDDLE;
		}
		// corners
		if (macroField[0][0] == botId) {
			heuristic += MACRO_CORNER;
		} else if (macroField[0][0] == opponentId) {
			heuristic -= MACRO_CORNER;
		}
		if (macroField[2][0] == botId) {
			heuristic += MACRO_CORNER;
		} else if (macroField[2][0] == opponentId) {
			heuristic -= MACRO_CORNER;
		}
		if (macroField[0][2] == botId) {
			heuristic += MACRO_CORNER;
		} else if (macroField[0][2] == opponentId) {
			heuristic -= MACRO_CORNER;
		}
		if (macroField[2][2] == botId) {
			heuristic += MACRO_CORNER;
		} else if (macroField[2][2] == opponentId) {
			heuristic -= MACRO_CORNER;
		}
		// sides
		if (macroField[1][0] == botId) {
			heuristic += MACRO_SIDE;
		} else if (macroField[1][0] == opponentId) {
			heuristic -= MACRO_SIDE;
		}
		if (macroField[0][1] == botId) {
			heuristic += MACRO_SIDE;
		} else if (macroField[0][1] == opponentId) {
			heuristic -= MACRO_SIDE;
		}
		if (macroField[2][1] == botId) {
			heuristic += MACRO_SIDE;
		} else if (macroField[2][1] == opponentId) {
			heuristic -= MACRO_SIDE;
		}
		if (macroField[1][2] == botId) {
			heuristic += MACRO_SIDE;
		} else if (macroField[1][2] == opponentId) {
			heuristic -= MACRO_SIDE;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[0][row] == botId) {
				botConnected++;
			} else if (macroField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[1][row] == botId) {
				botConnected++;
			} else if (macroField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[2][row] == botId) {
				botConnected++;
			} else if (macroField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MACRO_TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MACRO_TWO_IN_A_ROW;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[col][0] == botId) {
				botConnected++;
			} else if (macroField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][1] == botId) {
				botConnected++;
			} else if (macroField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][2] == botId) {
				botConnected++;
			} else if (macroField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MACRO_TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MACRO_TWO_IN_A_ROW;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroField[0][2] == botId) {
			botConnected++;
		} else if (macroField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][0] == botId) {
			botConnected++;
		} else if (macroField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MACRO_TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MACRO_TWO_IN_A_ROW;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroField[0][0] == botId) {
			botConnected++;
		} else if (macroField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][2] == botId) {
			botConnected++;
		} else if (macroField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MACRO_TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MACRO_TWO_IN_A_ROW;
		}
		// check 2 in a row in mini fields
		int[][] micro = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMicroFieldAdvanced(field, micro, i, botId, opponentId);
		}
		return heuristic;
	}
	
	private static int evaluateMicroFieldAdvanced(Field field, int[][] microField, int miniIndex, int botId, int opponentId) {
		int heuristic = 0;
		int botConnected, opponentConnected;
		int topLeftColumn = (miniIndex % 3) * 3;
		int topLeftRow = (miniIndex / 3) * 3;
		int[][] mField = field.getField();
		microField[0][0] = mField[topLeftColumn][topLeftRow];
		microField[1][0] = mField[topLeftColumn + 1][topLeftRow];
		microField[2][0] = mField[topLeftColumn + 2][topLeftRow];
		microField[0][1] = mField[topLeftColumn][topLeftRow + 1];
		microField[1][1] = mField[topLeftColumn + 1][topLeftRow + 1];
		microField[2][1] = mField[topLeftColumn + 2][topLeftRow + 1];
		microField[0][2] = mField[topLeftColumn][topLeftRow + 2];
		microField[1][2] = mField[topLeftColumn + 1][topLeftRow + 2];
		microField[2][2] = mField[topLeftColumn + 2][topLeftRow + 2];
		// check field positions
		// middle
		if (microField[1][1] == botId) {
			heuristic += MICRO_MIDDLE;
		} else if (microField[1][1] == opponentId) {
			heuristic -= MICRO_MIDDLE;
		}
		// corners
		if (microField[0][0] == botId) {
			heuristic += MICRO_CORNER;
		} else if (microField[0][0] == opponentId) {
			heuristic -= MICRO_CORNER;
		}
		if (microField[2][0] == botId) {
			heuristic += MICRO_CORNER;
		} else if (microField[2][0] == opponentId) {
			heuristic -= MICRO_CORNER;
		}
		if (microField[0][2] == botId) {
			heuristic += MICRO_CORNER;
		} else if (microField[0][2] == opponentId) {
			heuristic -= MICRO_CORNER;
		}
		if (microField[2][2] == botId) {
			heuristic += MICRO_CORNER;
		} else if (microField[2][2] == opponentId) {
			heuristic -= MICRO_CORNER;
		}
		// sides
		if (microField[1][0] == botId) {
			heuristic += MICRO_SIDE;
		} else if (microField[1][0] == opponentId) {
			heuristic -= MICRO_SIDE;
		}
		if (microField[0][1] == botId) {
			heuristic += MICRO_SIDE;
		} else if (microField[0][1] == opponentId) {
			heuristic -= MICRO_SIDE;
		}
		if (microField[2][1] == botId) {
			heuristic += MICRO_SIDE;
		} else if (microField[2][1] == opponentId) {
			heuristic -= MICRO_SIDE;
		}
		if (microField[1][2] == botId) {
			heuristic += MICRO_SIDE;
		} else if (microField[1][2] == opponentId) {
			heuristic -= MICRO_SIDE;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[0][row] == botId) {
				botConnected++;
			} else if (microField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[1][row] == botId) {
				botConnected++;
			} else if (microField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[2][row] == botId) {
				botConnected++;
			} else if (microField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[col][0] == botId) {
				botConnected++;
			} else if (microField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][1] == botId) {
				botConnected++;
			} else if (microField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][2] == botId) {
				botConnected++;
			} else if (microField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][2] == botId) {
			botConnected++;
		} else if (microField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][0] == botId) {
			botConnected++;
		} else if (microField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][0] == botId) {
			botConnected++;
		} else if (microField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][2] == botId) {
			botConnected++;
		} else if (microField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW;
		}
		return heuristic;
	}
	
	private static final double MACRO_TWO_IN_A_ROW_OPTIMIZED = 1.481980523838704;
	private static final double MACRO_MIDDLE_OPTIMIZED = 1.140677320506129;
	private static final double MACRO_CORNER_OPTIMIZED = 1.4066889502905648;
	private static final double MACRO_SIDE_OPTIMIZED = 0.9989940865792346;
	private static final double MICRO_TWO_IN_A_ROW_OPTIMIZED = 0.1993119083419216;
	private static final double MICRO_MIDDLE_OPTIMIZED = -0.8167288928897225;
	private static final double MICRO_CORNER_OPTIMIZED = 0.05977267391700242;
	private static final double MICRO_SIDE_OPTIMIZED = 0.4042137326923306;
	/** Max possible value for the optimized evaluation. This value is not obtainable through regular play. **/
	public static final double MAX_HEURISTIC_OPTIMIZED = 53.848553032947635;
	
	/**
	 * Same as simple, but also gives more points for having two in-a-row markers.
	 */
	public static double evaluateFieldAdvancedOptimized(Field field, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int[][] macroField = field.getMacroField();
		// check field positions
		// middle
		if (macroField[1][1] == botId) {
			heuristic += MACRO_MIDDLE_OPTIMIZED;
		} else if (macroField[1][1] == opponentId) {
			heuristic -= MACRO_MIDDLE_OPTIMIZED;
		}
		// corners
		if (macroField[0][0] == botId) {
			heuristic += MACRO_CORNER_OPTIMIZED;
		} else if (macroField[0][0] == opponentId) {
			heuristic -= MACRO_CORNER_OPTIMIZED;
		}
		if (macroField[2][0] == botId) {
			heuristic += MACRO_CORNER_OPTIMIZED;
		} else if (macroField[2][0] == opponentId) {
			heuristic -= MACRO_CORNER_OPTIMIZED;
		}
		if (macroField[0][2] == botId) {
			heuristic += MACRO_CORNER_OPTIMIZED;
		} else if (macroField[0][2] == opponentId) {
			heuristic -= MACRO_CORNER_OPTIMIZED;
		}
		if (macroField[2][2] == botId) {
			heuristic += MACRO_CORNER_OPTIMIZED;
		} else if (macroField[2][2] == opponentId) {
			heuristic -= MACRO_CORNER_OPTIMIZED;
		}
		// sides
		if (macroField[1][0] == botId) {
			heuristic += MACRO_SIDE_OPTIMIZED;
		} else if (macroField[1][0] == opponentId) {
			heuristic -= MACRO_SIDE_OPTIMIZED;
		}
		if (macroField[0][1] == botId) {
			heuristic += MACRO_SIDE_OPTIMIZED;
		} else if (macroField[0][1] == opponentId) {
			heuristic -= MACRO_SIDE_OPTIMIZED;
		}
		if (macroField[2][1] == botId) {
			heuristic += MACRO_SIDE_OPTIMIZED;
		} else if (macroField[2][1] == opponentId) {
			heuristic -= MACRO_SIDE_OPTIMIZED;
		}
		if (macroField[1][2] == botId) {
			heuristic += MACRO_SIDE_OPTIMIZED;
		} else if (macroField[1][2] == opponentId) {
			heuristic -= MACRO_SIDE_OPTIMIZED;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[0][row] == botId) {
				botConnected++;
			} else if (macroField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[1][row] == botId) {
				botConnected++;
			} else if (macroField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[2][row] == botId) {
				botConnected++;
			} else if (macroField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MACRO_TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MACRO_TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[col][0] == botId) {
				botConnected++;
			} else if (macroField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][1] == botId) {
				botConnected++;
			} else if (macroField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][2] == botId) {
				botConnected++;
			} else if (macroField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MACRO_TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MACRO_TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroField[0][2] == botId) {
			botConnected++;
		} else if (macroField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][0] == botId) {
			botConnected++;
		} else if (macroField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MACRO_TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MACRO_TWO_IN_A_ROW_OPTIMIZED;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroField[0][0] == botId) {
			botConnected++;
		} else if (macroField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][2] == botId) {
			botConnected++;
		} else if (macroField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MACRO_TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MACRO_TWO_IN_A_ROW_OPTIMIZED;
		}
		// check 2 in a row in mini fields
		int[][] micro = new int[3][3];
		for (int i = 0; i < 9; i++) {
			heuristic += evaluateMicroFieldAdvancedOptimized(field, micro, i, botId, opponentId);
		}
		return heuristic;
	}
	
	private static double evaluateMicroFieldAdvancedOptimized(Field field, int[][] microField, int miniIndex, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int topLeftColumn = (miniIndex % 3) * 3;
		int topLeftRow = (miniIndex / 3) * 3;
		int[][] mField = field.getField();
		microField[0][0] = mField[topLeftColumn][topLeftRow];
		microField[1][0] = mField[topLeftColumn + 1][topLeftRow];
		microField[2][0] = mField[topLeftColumn + 2][topLeftRow];
		microField[0][1] = mField[topLeftColumn][topLeftRow + 1];
		microField[1][1] = mField[topLeftColumn + 1][topLeftRow + 1];
		microField[2][1] = mField[topLeftColumn + 2][topLeftRow + 1];
		microField[0][2] = mField[topLeftColumn][topLeftRow + 2];
		microField[1][2] = mField[topLeftColumn + 1][topLeftRow + 2];
		microField[2][2] = mField[topLeftColumn + 2][topLeftRow + 2];
		// check field positions
		// middle
		if (microField[1][1] == botId) {
			heuristic += MICRO_MIDDLE_OPTIMIZED;
		} else if (microField[1][1] == opponentId) {
			heuristic -= MICRO_MIDDLE_OPTIMIZED;
		}
		// corners
		if (microField[0][0] == botId) {
			heuristic += MICRO_CORNER_OPTIMIZED;
		} else if (microField[0][0] == opponentId) {
			heuristic -= MICRO_CORNER_OPTIMIZED;
		}
		if (microField[2][0] == botId) {
			heuristic += MICRO_CORNER_OPTIMIZED;
		} else if (microField[2][0] == opponentId) {
			heuristic -= MICRO_CORNER_OPTIMIZED;
		}
		if (microField[0][2] == botId) {
			heuristic += MICRO_CORNER_OPTIMIZED;
		} else if (microField[0][2] == opponentId) {
			heuristic -= MICRO_CORNER_OPTIMIZED;
		}
		if (microField[2][2] == botId) {
			heuristic += MICRO_CORNER_OPTIMIZED;
		} else if (microField[2][2] == opponentId) {
			heuristic -= MICRO_CORNER_OPTIMIZED;
		}
		// sides
		if (microField[1][0] == botId) {
			heuristic += MICRO_SIDE_OPTIMIZED;
		} else if (microField[1][0] == opponentId) {
			heuristic -= MICRO_SIDE_OPTIMIZED;
		}
		if (microField[0][1] == botId) {
			heuristic += MICRO_SIDE_OPTIMIZED;
		} else if (microField[0][1] == opponentId) {
			heuristic -= MICRO_SIDE_OPTIMIZED;
		}
		if (microField[2][1] == botId) {
			heuristic += MICRO_SIDE_OPTIMIZED;
		} else if (microField[2][1] == opponentId) {
			heuristic -= MICRO_SIDE_OPTIMIZED;
		}
		if (microField[1][2] == botId) {
			heuristic += MICRO_SIDE_OPTIMIZED;
		} else if (microField[1][2] == opponentId) {
			heuristic -= MICRO_SIDE_OPTIMIZED;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[0][row] == botId) {
				botConnected++;
			} else if (microField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[1][row] == botId) {
				botConnected++;
			} else if (microField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[2][row] == botId) {
				botConnected++;
			} else if (microField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[col][0] == botId) {
				botConnected++;
			} else if (microField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][1] == botId) {
				botConnected++;
			} else if (microField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][2] == botId) {
				botConnected++;
			} else if (microField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_OPTIMIZED;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_OPTIMIZED;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][2] == botId) {
			botConnected++;
		} else if (microField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][0] == botId) {
			botConnected++;
		} else if (microField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_OPTIMIZED;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][0] == botId) {
			botConnected++;
		} else if (microField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][2] == botId) {
			botConnected++;
		} else if (microField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_OPTIMIZED;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_OPTIMIZED;
		}
		return heuristic;
	}
	
	/**
	 * Comprehensive Evaluation
	 */
	
	private static double MACRO_TWO_IN_A_ROW_COMP = 7.4731143167951;
	private static double MACRO_MIDDLE_COMP = 5.093110039135993;
	private static double MACRO_CORNER_COMP = -2.8228110614735313;
	private static double MACRO_SIDE_COMP = -2.263435727177493;
	
	private static double MICRO_TWO_IN_A_ROW_COMP = 0.1969225685539514;
	
	private static double MICRO_MIDDLE_MIDDLE_COMP = 3.3236142801438406;
	private static double MICRO_MIDDLE_CORNER_COMP = 3.1335267882050735;
	private static double MICRO_MIDDLE_SIDE_COMP = -1.5451956807896643;
	
	private static double MICRO_CORNER_MIDDLE_COMP = -0.8282925953294827;
	private static double MICRO_CORNER_CORNER_COMP = -0.3731218288460024;
	private static double MICRO_CORNER_SIDE_COMP = -1.5242912219260696 ;
	
	private static double MICRO_SIDE_MIDDLE_COMP = 1.5864232256063437;
	private static double MICRO_SIDE_CORNER_COMP = 1.110055969943045;
	private static double MICRO_SIDE_SIDE_COMP = -1.4313661716416888;
	
	/**
	 * Same as simple, but also gives more points for having two in-a-row markers.
	 */
	public static double evaluateFieldComprehensive(Field field, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int[][] macroField = field.getMacroField();
		// check field positions
		// middle
		if (macroField[1][1] == botId) {
			heuristic += MACRO_MIDDLE_COMP;
		} else if (macroField[1][1] == opponentId) {
			heuristic -= MACRO_MIDDLE_COMP;
		}
		// corners
		if (macroField[0][0] == botId) {
			heuristic += MACRO_CORNER_COMP;
		} else if (macroField[0][0] == opponentId) {
			heuristic -= MACRO_CORNER_COMP;
		}
		if (macroField[2][0] == botId) {
			heuristic += MACRO_CORNER_COMP;
		} else if (macroField[2][0] == opponentId) {
			heuristic -= MACRO_CORNER_COMP;
		}
		if (macroField[0][2] == botId) {
			heuristic += MACRO_CORNER_COMP;
		} else if (macroField[0][2] == opponentId) {
			heuristic -= MACRO_CORNER_COMP;
		}
		if (macroField[2][2] == botId) {
			heuristic += MACRO_CORNER_COMP;
		} else if (macroField[2][2] == opponentId) {
			heuristic -= MACRO_CORNER_COMP;
		}
		// sides
		if (macroField[1][0] == botId) {
			heuristic += MACRO_SIDE_COMP;
		} else if (macroField[1][0] == opponentId) {
			heuristic -= MACRO_SIDE_COMP;
		}
		if (macroField[0][1] == botId) {
			heuristic += MACRO_SIDE_COMP;
		} else if (macroField[0][1] == opponentId) {
			heuristic -= MACRO_SIDE_COMP;
		}
		if (macroField[2][1] == botId) {
			heuristic += MACRO_SIDE_COMP;
		} else if (macroField[2][1] == opponentId) {
			heuristic -= MACRO_SIDE_COMP;
		}
		if (macroField[1][2] == botId) {
			heuristic += MACRO_SIDE_COMP;
		} else if (macroField[1][2] == opponentId) {
			heuristic -= MACRO_SIDE_COMP;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[0][row] == botId) {
				botConnected++;
			} else if (macroField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[1][row] == botId) {
				botConnected++;
			} else if (macroField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (macroField[2][row] == botId) {
				botConnected++;
			} else if (macroField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MACRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MACRO_TWO_IN_A_ROW_COMP;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (macroField[col][0] == botId) {
				botConnected++;
			} else if (macroField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][1] == botId) {
				botConnected++;
			} else if (macroField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (macroField[col][2] == botId) {
				botConnected++;
			} else if (macroField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MACRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MACRO_TWO_IN_A_ROW_COMP;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroField[0][2] == botId) {
			botConnected++;
		} else if (macroField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][0] == botId) {
			botConnected++;
		} else if (macroField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MACRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MACRO_TWO_IN_A_ROW_COMP;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (macroField[0][0] == botId) {
			botConnected++;
		} else if (macroField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (macroField[1][1] == botId) {
			botConnected++;
		} else if (macroField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (macroField[2][2] == botId) {
			botConnected++;
		} else if (macroField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MACRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MACRO_TWO_IN_A_ROW_COMP;
		}
		// evaluate micro fields
		int[][] micro = new int[3][3];
		heuristic += evaluateMicroMiddleComprehensive(field, micro, botId, opponentId);
		heuristic += evaluateMicroCornerComprehensive(field, micro, 0, botId, opponentId);
		heuristic += evaluateMicroCornerComprehensive(field, micro, 2, botId, opponentId);
		heuristic += evaluateMicroCornerComprehensive(field, micro, 6, botId, opponentId);
		heuristic += evaluateMicroCornerComprehensive(field, micro, 8, botId, opponentId);
		heuristic += evaluateMicroSideComprehensive(field, micro, 1, botId, opponentId);
		heuristic += evaluateMicroSideComprehensive(field, micro, 3, botId, opponentId);
		heuristic += evaluateMicroSideComprehensive(field, micro, 5, botId, opponentId);
		heuristic += evaluateMicroSideComprehensive(field, micro, 7, botId, opponentId);
		return heuristic;
	}
	
	private static double evaluateMicroMiddleComprehensive(Field field, int[][] microField, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int[][] mField = field.getField();
		microField[0][0] = mField[3][3];
		microField[1][0] = mField[4][3];
		microField[2][0] = mField[5][3];
		microField[0][1] = mField[3][4];
		microField[1][1] = mField[4][4];
		microField[2][1] = mField[5][4];
		microField[0][2] = mField[3][5];
		microField[1][2] = mField[4][5];
		microField[2][2] = mField[5][5];
		// check field positions
		// middle
		if (microField[1][1] == botId) {
			heuristic += MICRO_MIDDLE_MIDDLE_COMP;
		} else if (microField[1][1] == opponentId) {
			heuristic -= MICRO_MIDDLE_MIDDLE_COMP;
		}
		// corners
		if (microField[0][0] == botId) {
			heuristic += MICRO_MIDDLE_CORNER_COMP;
		} else if (microField[0][0] == opponentId) {
			heuristic -= MICRO_MIDDLE_CORNER_COMP;
		}
		if (microField[2][0] == botId) {
			heuristic += MICRO_MIDDLE_CORNER_COMP;
		} else if (microField[2][0] == opponentId) {
			heuristic -= MICRO_MIDDLE_CORNER_COMP;
		}
		if (microField[0][2] == botId) {
			heuristic += MICRO_MIDDLE_CORNER_COMP;
		} else if (microField[0][2] == opponentId) {
			heuristic -= MICRO_MIDDLE_CORNER_COMP;
		}
		if (microField[2][2] == botId) {
			heuristic += MICRO_MIDDLE_CORNER_COMP;
		} else if (microField[2][2] == opponentId) {
			heuristic -= MICRO_MIDDLE_CORNER_COMP;
		}
		// sides
		if (microField[1][0] == botId) {
			heuristic += MICRO_MIDDLE_SIDE_COMP;
		} else if (microField[1][0] == opponentId) {
			heuristic -= MICRO_MIDDLE_SIDE_COMP;
		}
		if (microField[0][1] == botId) {
			heuristic += MICRO_MIDDLE_SIDE_COMP;
		} else if (microField[0][1] == opponentId) {
			heuristic -= MICRO_MIDDLE_SIDE_COMP;
		}
		if (microField[2][1] == botId) {
			heuristic += MICRO_MIDDLE_SIDE_COMP;
		} else if (microField[2][1] == opponentId) {
			heuristic -= MICRO_MIDDLE_SIDE_COMP;
		}
		if (microField[1][2] == botId) {
			heuristic += MICRO_MIDDLE_SIDE_COMP;
		} else if (microField[1][2] == opponentId) {
			heuristic -= MICRO_MIDDLE_SIDE_COMP;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[0][row] == botId) {
				botConnected++;
			} else if (microField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[1][row] == botId) {
				botConnected++;
			} else if (microField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[2][row] == botId) {
				botConnected++;
			} else if (microField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_COMP;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[col][0] == botId) {
				botConnected++;
			} else if (microField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][1] == botId) {
				botConnected++;
			} else if (microField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][2] == botId) {
				botConnected++;
			} else if (microField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_COMP;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][2] == botId) {
			botConnected++;
		} else if (microField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][0] == botId) {
			botConnected++;
		} else if (microField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_COMP;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][0] == botId) {
			botConnected++;
		} else if (microField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][2] == botId) {
			botConnected++;
		} else if (microField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_COMP;
		}
		return heuristic;
	}
	
	private static double evaluateMicroCornerComprehensive(Field field, int[][] microField, int miniIndex, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int topLeftColumn = (miniIndex % 3) * 3;
		int topLeftRow = (miniIndex / 3) * 3;
		int[][] mField = field.getField();
		microField[0][0] = mField[topLeftColumn][topLeftRow];
		microField[1][0] = mField[topLeftColumn + 1][topLeftRow];
		microField[2][0] = mField[topLeftColumn + 2][topLeftRow];
		microField[0][1] = mField[topLeftColumn][topLeftRow + 1];
		microField[1][1] = mField[topLeftColumn + 1][topLeftRow + 1];
		microField[2][1] = mField[topLeftColumn + 2][topLeftRow + 1];
		microField[0][2] = mField[topLeftColumn][topLeftRow + 2];
		microField[1][2] = mField[topLeftColumn + 1][topLeftRow + 2];
		microField[2][2] = mField[topLeftColumn + 2][topLeftRow + 2];
		// check field positions
		// middle
		if (microField[1][1] == botId) {
			heuristic += MICRO_CORNER_MIDDLE_COMP;
		} else if (microField[1][1] == opponentId) {
			heuristic -= MICRO_CORNER_MIDDLE_COMP;
		}
		// corners
		if (microField[0][0] == botId) {
			heuristic += MICRO_CORNER_CORNER_COMP;
		} else if (microField[0][0] == opponentId) {
			heuristic -= MICRO_CORNER_CORNER_COMP;
		}
		if (microField[2][0] == botId) {
			heuristic += MICRO_CORNER_CORNER_COMP;
		} else if (microField[2][0] == opponentId) {
			heuristic -= MICRO_CORNER_CORNER_COMP;
		}
		if (microField[0][2] == botId) {
			heuristic += MICRO_CORNER_CORNER_COMP;
		} else if (microField[0][2] == opponentId) {
			heuristic -= MICRO_CORNER_CORNER_COMP;
		}
		if (microField[2][2] == botId) {
			heuristic += MICRO_CORNER_CORNER_COMP;
		} else if (microField[2][2] == opponentId) {
			heuristic -= MICRO_CORNER_CORNER_COMP;
		}
		// sides
		if (microField[1][0] == botId) {
			heuristic += MICRO_CORNER_SIDE_COMP;
		} else if (microField[1][0] == opponentId) {
			heuristic -= MICRO_CORNER_SIDE_COMP;
		}
		if (microField[0][1] == botId) {
			heuristic += MICRO_CORNER_SIDE_COMP;
		} else if (microField[0][1] == opponentId) {
			heuristic -= MICRO_CORNER_SIDE_COMP;
		}
		if (microField[2][1] == botId) {
			heuristic += MICRO_CORNER_SIDE_COMP;
		} else if (microField[2][1] == opponentId) {
			heuristic -= MICRO_CORNER_SIDE_COMP;
		}
		if (microField[1][2] == botId) {
			heuristic += MICRO_CORNER_SIDE_COMP;
		} else if (microField[1][2] == opponentId) {
			heuristic -= MICRO_CORNER_SIDE_COMP;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[0][row] == botId) {
				botConnected++;
			} else if (microField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[1][row] == botId) {
				botConnected++;
			} else if (microField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[2][row] == botId) {
				botConnected++;
			} else if (microField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_COMP;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[col][0] == botId) {
				botConnected++;
			} else if (microField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][1] == botId) {
				botConnected++;
			} else if (microField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][2] == botId) {
				botConnected++;
			} else if (microField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_COMP;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][2] == botId) {
			botConnected++;
		} else if (microField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][0] == botId) {
			botConnected++;
		} else if (microField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_COMP;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][0] == botId) {
			botConnected++;
		} else if (microField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][2] == botId) {
			botConnected++;
		} else if (microField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_COMP;
		}
		return heuristic;
	}
	
	private static double evaluateMicroSideComprehensive(Field field, int[][] microField, int miniIndex, int botId, int opponentId) {
		double heuristic = 0;
		int botConnected, opponentConnected;
		int topLeftColumn = (miniIndex % 3) * 3;
		int topLeftRow = (miniIndex / 3) * 3;
		int[][] mField = field.getField();
		microField[0][0] = mField[topLeftColumn][topLeftRow];
		microField[1][0] = mField[topLeftColumn + 1][topLeftRow];
		microField[2][0] = mField[topLeftColumn + 2][topLeftRow];
		microField[0][1] = mField[topLeftColumn][topLeftRow + 1];
		microField[1][1] = mField[topLeftColumn + 1][topLeftRow + 1];
		microField[2][1] = mField[topLeftColumn + 2][topLeftRow + 1];
		microField[0][2] = mField[topLeftColumn][topLeftRow + 2];
		microField[1][2] = mField[topLeftColumn + 1][topLeftRow + 2];
		microField[2][2] = mField[topLeftColumn + 2][topLeftRow + 2];
		// check field positions
		// middle
		if (microField[1][1] == botId) {
			heuristic += MICRO_SIDE_MIDDLE_COMP;
		} else if (microField[1][1] == opponentId) {
			heuristic -= MICRO_SIDE_MIDDLE_COMP;
		}
		// corners
		if (microField[0][0] == botId) {
			heuristic += MICRO_SIDE_CORNER_COMP;
		} else if (microField[0][0] == opponentId) {
			heuristic -= MICRO_SIDE_CORNER_COMP;
		}
		if (microField[2][0] == botId) {
			heuristic += MICRO_SIDE_CORNER_COMP;
		} else if (microField[2][0] == opponentId) {
			heuristic -= MICRO_SIDE_CORNER_COMP;
		}
		if (microField[0][2] == botId) {
			heuristic += MICRO_SIDE_CORNER_COMP;
		} else if (microField[0][2] == opponentId) {
			heuristic -= MICRO_SIDE_CORNER_COMP;
		}
		if (microField[2][2] == botId) {
			heuristic += MICRO_SIDE_CORNER_COMP;
		} else if (microField[2][2] == opponentId) {
			heuristic -= MICRO_SIDE_CORNER_COMP;
		}
		// sides
		if (microField[1][0] == botId) {
			heuristic += MICRO_SIDE_SIDE_COMP;
		} else if (microField[1][0] == opponentId) {
			heuristic -= MICRO_SIDE_SIDE_COMP;
		}
		if (microField[0][1] == botId) {
			heuristic += MICRO_SIDE_SIDE_COMP;
		} else if (microField[0][1] == opponentId) {
			heuristic -= MICRO_SIDE_SIDE_COMP;
		}
		if (microField[2][1] == botId) {
			heuristic += MICRO_SIDE_SIDE_COMP;
		} else if (microField[2][1] == opponentId) {
			heuristic -= MICRO_SIDE_SIDE_COMP;
		}
		if (microField[1][2] == botId) {
			heuristic += MICRO_SIDE_SIDE_COMP;
		} else if (microField[1][2] == opponentId) {
			heuristic -= MICRO_SIDE_SIDE_COMP;
		}
		for (int row = 0; row < 3; row++) {
			// check horizontal 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[0][row] == botId) {
				botConnected++;
			} else if (microField[0][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[1][row] == botId) {
				botConnected++;
			} else if (microField[1][row] == opponentId) {
				opponentConnected++;
			}
			if (microField[2][row] == botId) {
				botConnected++;
			} else if (microField[2][row] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_COMP;
			}
		}
		for (int col = 0; col < 3; col++) {
			// check vertical 2 in a row
			botConnected = 0;
			opponentConnected = 0;
			if (microField[col][0] == botId) {
				botConnected++;
			} else if (microField[col][0] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][1] == botId) {
				botConnected++;
			} else if (microField[col][1] == opponentId) {
				opponentConnected++;
			}
			if (microField[col][2] == botId) {
				botConnected++;
			} else if (microField[col][2] == opponentId) {
				opponentConnected++;
			}
			if (botConnected > 1 && opponentConnected == 0) {
				heuristic += MICRO_TWO_IN_A_ROW_COMP;
			} else if (opponentConnected > 1 && botConnected == 0) {
				heuristic -= MICRO_TWO_IN_A_ROW_COMP;
			}
		}
		// check / diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][2] == botId) {
			botConnected++;
		} else if (microField[0][2] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][0] == botId) {
			botConnected++;
		} else if (microField[2][0] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_COMP;
		}
		// check \ diagonal 2 in a row
		botConnected = 0;
		opponentConnected = 0;
		if (microField[0][0] == botId) {
			botConnected++;
		} else if (microField[0][0] == opponentId) {
			opponentConnected++;
		}
		if (microField[1][1] == botId) {
			botConnected++;
		} else if (microField[1][1] == opponentId) {
			opponentConnected++;
		}
		if (microField[2][2] == botId) {
			botConnected++;
		} else if (microField[2][2] == opponentId) {
			opponentConnected++;
		}
		if (botConnected > 1 && opponentConnected == 0) {
			heuristic += MICRO_TWO_IN_A_ROW_COMP;
		} else if (opponentConnected > 1 && botConnected == 0) {
			heuristic -= MICRO_TWO_IN_A_ROW_COMP;
		}
		return heuristic;
	}
}