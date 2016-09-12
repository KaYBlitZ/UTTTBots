package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.FieldState;
import com.kayblitz.uttt.MacroState;
import com.kayblitz.uttt.Move;

// We don't extend UCTNode to prevent casting overhead due to UCTNode parent & children or extra memory usage in
// member variable hiding
public class RAVENode {
	public RAVENode parent;
	public ArrayList<RAVENode> children;
	public int winner; // -1 if not terminal, 0 if tie, else bot id of winner
	/** The id of the bot to make the next move from this state **/
	public int nextMoveBotId;
	/** Incoming action, move leading to this state **/
	public Move a;
	public int n, amafN; // num of visits
	public double q, amafQ; // total reward
	public double beta;
	
	private FieldState fieldState;
	private MacroState macroState;
	
	public RAVENode(Move move, int nextMoveBotId, int winner, RAVENode parent) {
		this(move.column, move.row, nextMoveBotId, winner, parent);
	}
	public RAVENode(int x, int y, int nextMoveBotId, int winner, RAVENode parent) {
		a = new Move(x, y);
		this.nextMoveBotId = nextMoveBotId;
		this.winner = winner;
		this.parent = parent;
		fieldState = new FieldState();
		macroState = new MacroState();
		children = new ArrayList<RAVENode>(9);
		beta = 1.0;
	}
	
	/** Called during backpropagation, updates UCT values, result is either WIN(1), TIE(0.5), LOSS(0) */
	public void update(double result, int botId, int opponentId) {
		n++;
		// We need to update the win from the perspective of the player that made the move that
		// created this node. This will cause MCTS to converge to minimax given enough time and
		// improve the node selection process.
		if (nextMoveBotId == opponentId) { // the player that made this node is our bot
			q += result;
		} else { // the player that made this node is our opponent
			// our bot win is his loss, a tie is still a tie, and our bot loss is his win
			q -= result - 1;
		}
	}
	
	/** Called during backpropagation, updates AMAF values, result is either WIN(1), TIE(0.5), LOSS(0) */
	public void updateAMAF(double result, int botId, int opponentId) {
		amafN++;
		// We need to update the win from the perspective of the player that made the move that
		// created this node. This will cause MCTS to converge to minimax given enough time and
		// improve the node selection process.
		if (nextMoveBotId == opponentId) { // the player that made this node is our bot
			amafQ += result;
		} else { // the player that made this node is our opponent
			// our bot win is his loss, a tie is still a tie, and our bot loss is his win
			amafQ -= result - 1;
		}
	}
	
	public void saveState(Field field) {
		fieldState.saveState(field.getBoard());
		macroState.saveState(field.getMacroboard());
	}
	
	public void restoreState(Field field) {
		fieldState.restoreState(field.getBoard());
		macroState.restoreState(field.getMacroboard());
	}
	
	/** Returns the ratio reward/visits for UCT */
	public double getAverageReward() {
		return q / n;
	}
	
	/** Returns the ratio reward/visits for AMAF */
	public double getAverageAMAFReward() {
		return amafQ / amafN;
	}
	
	public boolean isTerminal() {
		return winner > -1;
	}
}
