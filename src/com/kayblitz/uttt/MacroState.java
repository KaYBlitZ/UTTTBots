package com.kayblitz.uttt;

public class MacroState {
	
	private int[][] macroState;
	
	public MacroState() {
		macroState = new int[3][3];
	}
	
	public void saveState(int[][] macroField) {
		for (int i = 0; i < 3; i++) {
			System.arraycopy(macroField[i], 0, macroState[i], 0, 3);
		}
	}
	
	public void restoreState(int[][] macroField) {
		for (int i = 0; i < 3; i++) {
			System.arraycopy(macroState[i], 0, macroField[i], 0, 3);
		}
	}
}
