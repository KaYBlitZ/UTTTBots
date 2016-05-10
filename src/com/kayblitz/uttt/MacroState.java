package com.kayblitz.uttt;

public class MacroState {

	int m00, m10, m20, m01, m11, m21, m02, m12, m22;
	
	public void saveState(int[][] macroboard) {
		m00 = macroboard[0][0];
		m10 = macroboard[1][0];
		m20 = macroboard[2][0];
		m01 = macroboard[0][1];
		m11 = macroboard[1][1];
		m21 = macroboard[2][1];
		m02 = macroboard[0][2];
		m12 = macroboard[1][2];
		m22 = macroboard[2][2];
	}
	
	public void restoreState(int[][] macroboard) {
		macroboard[0][0] = m00;
		macroboard[1][0] = m10;
		macroboard[2][0] = m20;
		macroboard[0][1] = m01;
		macroboard[1][1] = m11;
		macroboard[2][1] = m21;
		macroboard[0][2] = m02;
		macroboard[1][2] = m12;
		macroboard[2][2] = m22;
	}
}
