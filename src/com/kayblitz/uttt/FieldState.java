package com.kayblitz.uttt;

public class FieldState {

	private int[][] fieldState;
	
	public FieldState() {
		fieldState = new int[9][9];
	}
	
	public void saveState(int[][] field) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				fieldState[i][j] = field[i][j];
			}
		}
	}
	
	public void restoreState(int[][] field) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				field[i][j] = fieldState[i][j];
			}
		}
	}
}
