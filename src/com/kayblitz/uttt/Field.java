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
    private int mRoundNr;
    private int mMoveNr;
	private int[][] mBoard;
	private int[][] mMacroboard;

	private final int COLS = 9, ROWS = 9;
	private String mLastError = "";
	private ArrayList<Move> moves;
	private ArrayList<MacroState> macroStates;
	
	public Field() {
		mBoard = new int[COLS][ROWS];
		mMacroboard = new int[COLS / 3][ROWS / 3];
		moves = new ArrayList<Move>(81);
		macroStates = new ArrayList<MacroState>(81);
		clearBoard();
	}
	
	/**
	 * Parse data about the game given by the engine
	 * @param key : type of data given
	 * @param value : value
	 */
	public void parseGameData(String key, String value) {
	    if (key.equals("round")) {
	        mRoundNr = Integer.parseInt(value);
	    } else if (key.equals("move")) {
	        mMoveNr = Integer.parseInt(value);
	        System.err.println("Move " + mMoveNr);
	    } else if (key.equals("field")) {
            parseFromString(value); /* Parse Field with data */
        } else if (key.equals("macroboard")) {
            parseMacroboardFromString(value); /* Parse macroboard with data */
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
				mBoard[x][y] = Integer.parseInt(r[counter]); 
				counter++;
			}
		}
	}
	
	/**
	 * Initialise macroboard from comma separated String
	 * @param String : 
	 */
	public void parseMacroboardFromString(String s) {
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				mMacroboard[x][y] = Integer.parseInt(r[counter]);
				counter++;
			}
		}
	}
	
	public void clearBoard() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				mBoard[x][y] = 0;
			}
		}
	}

	public ArrayList<Move> getAvailableMoves() {
	    ArrayList<Move> moves = new ArrayList<Move>();
	    for (int x = 0; x < COLS; x++) {
	    	for (int y = 0; y < ROWS; y++) {
                if (isInActiveMacroboard(x, y) && mBoard[x][y] == 0) {
                    moves.add(new Move(x, y));
                }
            }
        }
		return moves;
	}
	
	public boolean isInActiveMacroboard(int x, int y) {
	    return mMacroboard[x/3][y/3] == -1;
	}
	
	/**
	 * Returns reason why addMove returns false
	 * @param args : 
	 * @return : reason why addMove returns false
	 */
	public String getLastError() {
		return mLastError;
	}

	
	@Override
	/**
	 * Creates comma separated String with player ids for the microboards.
	 * 
	 * @param args
	 *            :
	 * @return : String with player names for every cell, or 0 when cell is
	 *         empty.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				if (counter > 0) sb.append(',');
				sb.append(mBoard[x][y]);
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
				if (counter > 0) sb.append(',');
				sb.append(mMacroboard[x][y]);
				counter++;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Checks whether the field is full
	 * @param args : 
	 * @return : Returns true when field is full, otherwise returns false.
	 */
	public boolean isFull() {
		for (int x = 0; x < COLS; x++)
		  for (int y = 0; y < ROWS; y++)
		    if (mBoard[x][y] == 0)
		      return false; // At least one cell is not filled
		// All cells are filled
		return true;
	}
	
	public int getNrColumns() {
		return COLS;
	}
	
	public int getNrRows() {
		return ROWS;
	}
	
	public int getMoveNum() {
		return mMoveNr;
	}

	public boolean isEmpty() {
		for (int x = 0; x < COLS; x++) {
			  for (int y = 0; y < ROWS; y++) {
				  if (mBoard[x][y] > 0) {
					  return false;
				  }
			  }
		}
		return true;
	}
	
	/**
	 * Returns the player id on given column and row
	 * @param args : int column, int row
	 * @return : int
	 */
	public int getPlayerId(int column, int row) {
		return mBoard[column][row];
	}
	
	/**
	 * Returns -1 if game is not over, 0 if tie, else returns the winner
	 * @return
	 */
	public int getWinner() {
		if (mMacroboard[0][0] > 0 && mMacroboard[0][0] == mMacroboard[1][1] &&
				mMacroboard[1][1] == mMacroboard[2][2]) { // \ diagonal
			return mMacroboard[0][0];
		} else if (mMacroboard[0][2] > 0 && mMacroboard[0][2] == mMacroboard[1][1] &&
				mMacroboard[1][1] == mMacroboard[2][0]) { // / diagonal
			return mMacroboard[0][2];
		}
		
		// check vertical
		for (int x = 0; x < 3; x++) {
			if (mMacroboard[x][0] > 0 && mMacroboard[x][0] == mMacroboard[x][1] &&
					mMacroboard[x][1] == mMacroboard[x][2]) {
				return mMacroboard[x][0];
			}
		}
		
		// check horizontal
		for (int y = 0; y < 3; y++) {
			if (mMacroboard[0][y] > 0 && mMacroboard[0][y] == mMacroboard[1][y] &&
					mMacroboard[1][y] == mMacroboard[2][y]) {
				return mMacroboard[0][y];
			}
		}
		
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				// game still playable
				if (mMacroboard[col][row] == -1) return -1;
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
	
	/**
	 * Makes the specified move
	 * @param column
	 * @param row
	 * @param id
	 * @param addToStack - add to stack to undo later
	 * @return
	 */
	public boolean makeMove(int column, int row, int id, boolean addToStack) {
		if (mBoard[column][row] > 0) throw new RuntimeException("Invalid move: space not empty");
		
		mBoard[column][row] = id; // do move
		if (addToStack) {
			moves.add(new Move(column, row)); // save move
			// save original macro board state
			MacroState state = new MacroState();
			state.saveState(mMacroboard);
			macroStates.add(state);
		}
		// update macro board state
		updateMacro(column, row);
		return true;
	}
	
	public void updateMacro(int column, int row) {
		// ie: 3,2 move -> 0,2 macro -> 0,6 top-left box coords
		// convert move coords into next macro board coords 
		while (column > 2) column -= 3;
		while (row > 2) row -= 3;
		// convert macro board coords into top-left box coords
		int bCol = column * 3;
		int bRow = row * 3;

		// whether the next board is finished or not
		boolean nextMiniOpen = getMiniWinner(bCol, bRow) == -1;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int winner = getMiniWinner(x * 3, y * 3);
				if (winner >= 0) { // winner, tie
					mMacroboard[x][y] = winner;
				} else { // open mini TTT, need to check if playable or not
					if (!nextMiniOpen || (x == column && y == row)) {
						// if next board is finished all open boards are playable
						// else set playable only if its the nextMacroIndex
						mMacroboard[x][y] = -1;
					} else {
						mMacroboard[x][y] = 0;
					}
				}
			}
		}
	}
	
	public void undo() {
		// undo move
		Move lastMove = moves.remove(moves.size() - 1);
		mBoard[lastMove.column][lastMove.row] = 0;
		// restore original macro state
		MacroState lastMacro = macroStates.remove(macroStates.size() - 1);
		lastMacro.restoreState(mMacroboard);
	}
	
	public int[][] getMacroboard() {
		return mMacroboard;
	}
	
	public int[][] getBoard() {
		return mBoard;
	}
	
	private int getMiniWinner(int topLeftColumn, int topLeftRow) {
		int m00 = mBoard[topLeftColumn][topLeftRow];
		int m10 = mBoard[topLeftColumn + 1][topLeftRow];
		int m20 = mBoard[topLeftColumn + 2][topLeftRow];
		int m01 = mBoard[topLeftColumn][topLeftRow + 1];
		int m11 = mBoard[topLeftColumn + 1][topLeftRow + 1];
		int m21 = mBoard[topLeftColumn + 2][topLeftRow + 1];
		int m02 = mBoard[topLeftColumn][topLeftRow + 2];
		int m12 = mBoard[topLeftColumn + 1][topLeftRow + 2];
		int m22 = mBoard[topLeftColumn + 2][topLeftRow + 2];
    	
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
		
		// check if the board is full
		if (m00 == 0 || m10 == 0 || m20 == 0 ||
				m01 == 0 || m11 == 0 || m21 == 0 ||
				m02 == 0 || m12 == 0 || m22 == 0) {
			return -1;
		}
		
		// must be a tie
        return 0;
	}
}