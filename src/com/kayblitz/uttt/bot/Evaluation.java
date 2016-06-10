package com.kayblitz.uttt.bot;

import com.kayblitz.uttt.Field;

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
}
