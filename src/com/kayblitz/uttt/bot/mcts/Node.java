package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.FieldState;
import com.kayblitz.uttt.MacroState;
import com.kayblitz.uttt.Move;

public class Node {
	public Node parent;
	public ArrayList<Node> children;
	public boolean isTerminal; // terminal state (finished) or not
	public int botId; // the id of the bot to make the next move from this state
	public Move a; // incoming action, move leading to this state
	public int n, q; // num of visits and total reward
	
	private FieldState fieldState;
	private MacroState macroState;
	
	public Node(Move move, int botId, boolean isTerminal, Node parent) {
		this(move.column, move.row, botId, isTerminal, parent);
	}
	public Node(int x, int y, int botId, boolean isTerminal, Node parent) {
		this.botId = botId;
		this.isTerminal = isTerminal;
		this.parent = parent;
		fieldState = new FieldState();
		macroState = new MacroState();
		a = new Move(x, y);
		children = new ArrayList<Node>(9);
	}
	
	/** Called during backpropagation, value is either WIN(1), TIE(0), LOSS(-1) */
	public void update(int result) {
		n++;
		q += result;
	}
	
	public void saveState(Field field) {
		fieldState.saveState(field.getBoard());
		macroState.saveState(field.getMacroboard());
	}
	
	public void restoreState(Field field) {
		fieldState.restoreState(field.getBoard());
		macroState.restoreState(field.getMacroboard());
	}
	
	public float getHeuristic() {
		return (float) q/n;
	}
}
