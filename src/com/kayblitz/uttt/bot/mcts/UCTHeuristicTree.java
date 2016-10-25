package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class UCTHeuristicTree extends UCTTree {
	
	public UCTHeuristicTree(Field field, StringBuilder sb, int treeType, int simulationType, int botId, int opponentId) {
		super(field, sb, treeType, simulationType, botId, opponentId);
		root = new UCTHeuristicNode(-1, -1, botId, -1, null, field, botId, opponentId, field.getNumAvailableMoves());
		root.saveState(field);
	}
	
	/**
	 * Adds all unexplored children that will be initialized to values corresponding to the heuristic evaluation function &
	 * heuristic confidence function. The node with the highest UCT value will be returned for simulation.
	 */
	@Override
	protected UCTHeuristicNode expand(UCTNode selected) {
		selected.restoreState(field); // restore state of node
		ArrayList<Move> moves = field.getAvailableMoves();
		
		// add all children and select child with highest UCT value to return
		UCTHeuristicNode bestChild = null;
		double bestValue = Integer.MIN_VALUE;
		double constant = Math.log(selected.n);
		for (Move move : moves) {
			// add child
			field.makeMove(move, selected.nextMoveBotId, true); 
			UCTHeuristicNode child = new UCTHeuristicNode(move, selected.nextMoveBotId == 1 ? 2 : 1, 
					field.getWinner(), (UCTHeuristicNode) selected, field, botId, opponentId, field.getNumAvailableMoves());
			child.saveState(field);
			selected.children.add(child); // add to parent's array of children
			field.undo();
			
			// check UCT value
			double value = child.getAverageReward() + EXPLORATION_CONSTANT * Math.sqrt(constant/child.n);
			if (Double.compare(value, bestValue) > 0) {
				bestValue = value;
				bestChild = child;
			} else if (Double.compare(value, bestValue) == 0 && rand.nextInt(2) == 1) {
				bestChild = child;
			}
		}
		return bestChild;
	}
}
