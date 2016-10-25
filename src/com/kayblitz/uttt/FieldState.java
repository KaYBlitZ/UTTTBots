package com.kayblitz.uttt;

public class FieldState {

	private int[][] fieldState;
	
	public FieldState() {
		fieldState = new int[9][9];
	}
	
	public void saveState(int[][] field) {
		for (int i = 0; i < 9; i++) {
			System.arraycopy(field[i], 0, fieldState[i], 0, 9);
		}
	}
	
	public void restoreState(int[][] field) {
		for (int i = 0; i < 9; i++) {
			System.arraycopy(fieldState[i], 0, field[i], 0, 9);
		}
	}
}
