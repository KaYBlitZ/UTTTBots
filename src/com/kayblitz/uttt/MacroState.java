package com.kayblitz.uttt;

public class MacroState {

	int m00, m10, m20, m01, m11, m21, m02, m12, m22;
	
	public void saveState(int[][] macroField) {
		m00 = macroField[0][0];
		m10 = macroField[1][0];
		m20 = macroField[2][0];
		m01 = macroField[0][1];
		m11 = macroField[1][1];
		m21 = macroField[2][1];
		m02 = macroField[0][2];
		m12 = macroField[1][2];
		m22 = macroField[2][2];
	}
	
	public void restoreState(int[][] macroField) {
		macroField[0][0] = m00;
		macroField[1][0] = m10;
		macroField[2][0] = m20;
		macroField[0][1] = m01;
		macroField[1][1] = m11;
		macroField[2][1] = m21;
		macroField[0][2] = m02;
		macroField[1][2] = m12;
		macroField[2][2] = m22;
	}
}
