package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class RAVEHeuristicTree extends RAVETree {
	
	public RAVEHeuristicTree(Field field, StringBuilder sb, int simulationType, int botId, int opponentId) {
		super(field, sb, simulationType, botId, opponentId);
		root = new RAVEHeuristicNode(-1, -1, botId, -1, null, field, botId, opponentId);
		root.saveState(field);
	}
	
	/**
	 * Adds all unexplored children that will be initialized to values corresponding to the heuristic evaluation function &
	 * heuristic confidence function. The node with the highest UCT value will be returned for simulation.
	 */
	@Override
	protected RAVEHeuristicNode expand(RAVENode selected) {
		if (selected.isTerminal())
			throw new RuntimeException("MCTS expand: node is terminal");
		selected.restoreState(field); // restore state of node
		ArrayList<Move> moves = field.getAvailableMoves();
		
		// add all children and select child with highest UCT value to return
		RAVEHeuristicNode bestChild = null;
		double bestValue = Integer.MIN_VALUE;
		double constant = Math.log(selected.n);
		for (Move move : moves) {
			// add child
			field.makeMove(move, selected.nextMoveBotId, true); 
			RAVEHeuristicNode child = new RAVEHeuristicNode(move, selected.nextMoveBotId == 1 ? 2 : 1, 
					field.getWinner(), (RAVEHeuristicNode) selected, field, botId, opponentId);
			child.saveState(field);
			selected.children.add(child); // add to parent's array of children
			field.undo();
			
			// check UCT value
			double value = (1 - child.beta) * child.getAverageReward() + 
					child.beta * child.getAverageAMAFReward() + 
					EXPLORATION_CONSTANT * Math.sqrt(constant/child.n);
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
