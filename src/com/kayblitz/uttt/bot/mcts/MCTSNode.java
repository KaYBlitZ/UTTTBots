package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.FieldState;
import com.kayblitz.uttt.MacroState;
import com.kayblitz.uttt.Move;

public class MCTSNode {	
	public MCTSNode parent;
	public ArrayList<MCTSNode> children;
	public int winner; // -1 if not terminal, 0 if tie, else bot id of winner
	public int nextMoveBotId; // the id of the bot to make the next move from this state
	public Move a; // incoming action, move leading to this state
	public int n; // num of visits
	public double q; // total reward
	
	private FieldState fieldState;
	private MacroState macroState;
	
	public MCTSNode(Move move, int nextMoveBotId, int winner, MCTSNode parent) {
		this(move.column, move.row, nextMoveBotId, winner, parent);
	}
	public MCTSNode(int x, int y, int nextMoveBotId, int winner, MCTSNode parent) {
		a = new Move(x, y);
		this.nextMoveBotId = nextMoveBotId;
		this.winner = winner;
		this.parent = parent;
		fieldState = new FieldState();
		macroState = new MacroState();
		children = new ArrayList<MCTSNode>(9);
	}
	
	/** Called during backpropagation, value is either WIN(1), TIE(0.5), LOSS(0) */
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
	
	public void saveState(Field field) {
		fieldState.saveState(field.getBoard());
		macroState.saveState(field.getMacroboard());
	}
	
	public void restoreState(Field field) {
		fieldState.restoreState(field.getBoard());
		macroState.restoreState(field.getMacroboard());
	}
	
	/** Returns the ratio reward/visits */
	public double getAverageReward() {
		return q / n;
	}
	
	public boolean isTerminal() {
		return winner > -1;
	}
}
