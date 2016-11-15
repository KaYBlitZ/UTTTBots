// // Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.kayblitz.uttt;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Field class
 * 
 * Handles everything that has to do with the field, such 
 * as storing the current state and performing calculations
 * on the field.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Field {
	/** Field refers to the entire field
	 * MacroField refers to the large TTT field
	 * MicroField refers to any single "mini" TTT field
	 */
	public static final int COLS = 9, ROWS = 9;
	public static final Move[][] MOVES = new Move[COLS][ROWS];
	
	static {
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS; row++) {
				MOVES[col][row] = new Move(col, row);
			}
		}
	}
	
    private int roundNum;
    private int moveNum;
	private int[][] field;
	private int[][] macroField;
	private Stack<Move> moves;
	private Stack<MacroState> macroStates;
	
	public Field() {
		field = new int[COLS][ROWS];
		macroField = new int[COLS / 3][ROWS / 3];
		moves = new Stack<Move>();
		macroStates = new Stack<MacroState>();
		clearFields();
	}
	
	/**
	 * Parse data about the game given by the engine
	 * @param key : type of data given
	 * @param value : value
	 */
	public void parseGameData(String key, String value) {
	    if (key.equals("round")) {
	        roundNum = Integer.parseInt(value);
	    } else if (key.equals("move")) {
	        moveNum = Integer.parseInt(value);
	        System.err.println("Move " + moveNum);
	    } else if (key.equals("field")) {
            parseFromString(value); /* Parse Field with data */
        } else if (key.equals("macroboard")) {
            parseMacroFieldFromString(value); /* Parse macro field with data */
        }
	}
	
	/**
	 * Initialise field from comma separated String
	 * @param String : 
	 */
	public void parseFromString(String s) {
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				field[x][y] = Integer.parseInt(r[counter]); 
				counter++;
			}
		}
	}
	
	public void parseMacroFieldFromString(String s) {
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				macroField[x][y] = Integer.parseInt(r[counter]);
				counter++;
			}
		}
	}
	
	public void clearFields() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				field[x][y] = 0;
			}
		}
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				macroField[x][y] = -1;
			}
		}
	}

	public ArrayList<Move> getAvailableMoves() {
	    ArrayList<Move> moves = new ArrayList<Move>();
	    for (int col = 0; col < COLS; col++) {
	    	for (int row = 0; row < ROWS; row++) {
	    		// check if in allowed macro field and if field is empty
                if (macroField[col/3][row/3] == -1 && field[col][row] == 0) {
                    moves.add(MOVES[col][row]);
                }
            }
        }
		return moves;
	}
	
	public int getNumAvailableMoves() {
		int n = 0;
		for (int x = 0; x < COLS; x++) {
	    	for (int y = 0; y < ROWS; y++) {
	    		// check if in allowed macro field and if field is empty
                if (macroField[x/3][y/3] == -1 && field[x][y] == 0) {
                    n++;
                }
            }
        }
		return n;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				if (counter > 0)
					sb.append(',');
				sb.append(field[x][y]);
				counter++;
			}
		}
		return sb.toString();
	}
	
	public String toStringMacro() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (counter > 0)
					sb.append(',');
				sb.append(macroField[x][y]);
				counter++;
			}
		}
		return sb.toString();
	}
	
	public int getNumColumns() {
		return COLS;
	}
	
	public int getNumRows() {
		return ROWS;
	}
	
	public int getMoveNum() {
		return moveNum;
	}
	
	/**
	 * Returns the player id on given column and row
	 * @param args : int column, int row
	 * @return : int
	 */
	public int getPlayerId(int column, int row) {
		return field[column][row];
	}
	
	/**
	 * Returns -1 if game is not over, 0 if tie, else returns the winner
	 * @return
	 */
	public int getWinner() {
		if (macroField[0][0] > 0 && macroField[0][0] == macroField[1][1] &&
				macroField[1][1] == macroField[2][2]) { // \ diagonal
			return macroField[0][0];
		} else if (macroField[0][2] > 0 && macroField[0][2] == macroField[1][1] &&
				macroField[1][1] == macroField[2][0]) { // / diagonal
			return macroField[0][2];
		}
		
		// check vertical
		for (int x = 0; x < 3; x++) {
			if (macroField[x][0] > 0 && macroField[x][0] == macroField[x][1] &&
					macroField[x][1] == macroField[x][2]) {
				return macroField[x][0];
			}
		}
		
		// check horizontal
		for (int y = 0; y < 3; y++) {
			if (macroField[0][y] > 0 && macroField[0][y] == macroField[1][y] &&
					macroField[1][y] == macroField[2][y]) {
				return macroField[0][y];
			}
		}
		
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				// game still playable
				if (macroField[col][row] == -1)
					return -1;
			}
		}
		
		return 0;
	}
	
	/**
	 * Makes the specified move
	 * @param move
	 * @param id
	 * @param addToStack - add to stack to undo later
	 * @return success
	 */
	public boolean makeMove(Move move, int id, boolean addToStack) {
		return makeMove(move.column, move.row, id, addToStack);
	}
	
	public boolean makeMove(int column, int row, int id, boolean addToStack) {
		if (field[column][row] > 0)
			throw new RuntimeException("Invalid move: space not empty");
		
		field[column][row] = id;
		if (addToStack) {
			moves.push(new Move(column, row)); // save move
			// save original micro field state
			MacroState state = new MacroState();
			state.saveState(macroField);
			macroStates.push(state);
		}
		// update macro field state
		updateMacroWinner(column / 3, row / 3);
		updateMacroField(column, row);
		return true;
	}
	
	public void updateMacroWinner(int macroColumn, int macroRow) {
		macroField[macroColumn][macroRow] = getMicroWinner(macroColumn * 3, macroRow * 3);
	}
	
	public void updateMacroField(int column, int row) {
		// ie: 3,2 move -> 0,2 next micro -> 0,6 top-left next micro box coords
		// ie: 5,4 -> 2,1 -> 6,3
		// convert move coords into next micro field coords 
		while (column > 2)
			column -= 3;
		while (row > 2)
			row -= 3;

		// whether the next micro field is finished or not
		boolean nextMicroPlayable = getMicroWinner(column * 3, row * 3) == -1;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (macroField[x][y] < 0 || (macroField[x][y] == 0 && !isMicroFull(x * 3, y * 3))) {
					if (!nextMicroPlayable || (x == column && y == row)) {
						// next micro field finished, set all open micro fields to be playable
						// else set playable only if its the nextMicroIndex
						macroField[x][y] = -1;
					} else {
						macroField[x][y] = 0;
					}
				}
			}
		}
	}
	
	private boolean isMicroFull(int topLeftColumn, int topLeftRow) {
		if (field[topLeftColumn][topLeftRow] == 0 || 
			field[topLeftColumn + 1][topLeftRow] == 0 || 
			field[topLeftColumn + 2][topLeftRow] == 0 ||
			field[topLeftColumn][topLeftRow + 1] == 0 || 
			field[topLeftColumn + 1][topLeftRow + 1] == 0 || 
			field[topLeftColumn + 2][topLeftRow + 1] == 0 ||
			field[topLeftColumn][topLeftRow + 2] == 0 || 
			field[topLeftColumn + 1][topLeftRow + 2] == 0 || 
			field[topLeftColumn + 2][topLeftRow + 2] == 0) {
			return false;
		}
		return true;
	}
	
	private int getMicroWinner(int topLeftColumn, int topLeftRow) {
		int m00 = field[topLeftColumn][topLeftRow];
		int m10 = field[topLeftColumn + 1][topLeftRow];
		int m20 = field[topLeftColumn + 2][topLeftRow];
		int m01 = field[topLeftColumn][topLeftRow + 1];
		int m11 = field[topLeftColumn + 1][topLeftRow + 1];
		int m21 = field[topLeftColumn + 2][topLeftRow + 1];
		int m02 = field[topLeftColumn][topLeftRow + 2];
		int m12 = field[topLeftColumn + 1][topLeftRow + 2];
		int m22 = field[topLeftColumn + 2][topLeftRow + 2];
    	
		/* Check for vertical wins */
        if (m00 > 0 && m00 == m01 && m01 == m02) return m00;
        if (m10 > 0 && m10 == m11 && m11 == m12) return m10;
        if (m20 > 0 && m20 == m21 && m21 == m22) return m20;
        
		/* Check for horizontal wins */
        if (m00 > 0 && m00 == m10 && m10 == m20) return m00;
        if (m01 > 0 && m01 == m11 && m11 == m21) return m01;
        if (m02 > 0 && m02 == m12 && m12 == m22) return m02;
        
		/* Check for forward diagonal wins - / */
		if (m02 > 0 && m02 == m11 && m11 == m20) return m02;
		
		/* Check for backward diagonal wins - \ */
		if (m00 > 0 && m00 == m11 && m11 == m22) return m00;
		
		// check if the field is open
		if (m00 == 0 || m10 == 0 || m20 == 0 ||
				m01 == 0 || m11 == 0 || m21 == 0 ||
				m02 == 0 || m12 == 0 || m22 == 0) {
			return -1;
		}
		
		// must be a tie
        return 0;
	}
	
	public void undo() {
		// undo move
		Move lastMove = moves.pop();
		field[lastMove.column][lastMove.row] = 0;
		// restore original micro state
		MacroState lastMacro = macroStates.pop();
		lastMacro.restoreState(macroField);
	}
	
	/** Removes last saved move. Does NOT undo the move **/
	public void pop() {
		moves.pop();
		macroStates.pop();
	}
	
	public int[][] getMacroField() {
		return macroField;
	}
	
	public int[][] getField() {
		return field;
	}
}