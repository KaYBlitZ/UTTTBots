package com.kayblitz.uttt.bot.mcts;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class UCTTree extends MCTree {
	
	protected static final double EXPLORATION_CONSTANT = 0.5;
	protected UCTNode root;
	
	public UCTTree(Field field, StringBuilder sb, int treeType, int simulationType, int botId, int opponentId) {
		super(field, sb, treeType, simulationType, botId, opponentId);
		root = new UCTNode(-1, -1, botId, -1, null, field.getNumAvailableMoves());
		root.saveState(field);
	}
	
	/** Goes through one iteration of the MCTS algorithm */
	@Override
	public void iterate() {
		// Tree policy: selection and expansion
		UCTNode selected = select();
		UCTNode expanded = selected.isTerminal() ? selected : expand(selected);
		// Simulation
		// Even if the node is terminal we still simulate it because every iteration afterwards
		// will select the same terminal node if we do not simulate, resulting in an infinite loop
		double result = simulate(expanded);
		// Backpropagation
		backpropagate(expanded, result);
	}
	
	/** Selects a child node to expand using UCT = cw/cn + c * sqrt(ln(pn)/cn) where cw is child wins, 
	 * cn is child visits, c is the exploration constant, and pn is parent visits. Returned node may 
	 * be a terminal state.
	 */
	private UCTNode select() {
		UCTNode selected = root;
		while (true) {
			if (selected.isTerminal())
				return selected; // check to see if we have reached a finished state
			
			if (selected.children.size() == selected.numChildren) {
				// children all explored at least once, explore deeper using UCT
				UCTNode selectedChild = null;
				double bestValue = Integer.MIN_VALUE;
				double constant = Math.log(selected.n);
				
				for (UCTNode child : selected.children) {
					// If the next bot's move has a chance to win game, select that move since that bot WILL
					// ALWAYS choose that winning moving instead of selecting any other move
					if (child.winner == selected.nextMoveBotId)
						return child;
					
					// select the child with the highest UCT value
					double value = child.getAverageReward() + EXPLORATION_CONSTANT * Math.sqrt(constant/child.n);
					if (Double.compare(value, bestValue) > 0) {
						bestValue = value;
						selectedChild = child;
					} else if (Double.compare(value, bestValue) == 0 && rand.nextInt(2) == 1) {
						selectedChild = child;
					}
				}
				selected = selectedChild;
			} else {
				// we have unexplored children, select this node
				return selected;
			}
		}
	}
	
	/**
	 * Adds an unexplored child from the selected node and returns the child. The state of the Field will 
	 * be that of the newly created child. The passed in node must not be terminal.
	 */
	protected UCTNode expand(UCTNode selected) {
		selected.restoreState(field); // restore state of node
		Move move = field.getAvailableMoves().get(selected.children.size());
		field.makeMove(move, selected.nextMoveBotId, false); 
		UCTNode child = new UCTNode(move, selected.nextMoveBotId == 1 ? 2 : 1, field.getWinner(), 
				selected, field.getNumAvailableMoves());
		child.saveState(field);
		selected.children.add(child); // add to parent's array of children
		
		return child;
	}
	
	/** 
	 * Simulates a play from the Node to an end state and returns a value, WIN(1), TIE(0.5), LOSS(0).
	 */
	private double simulate(UCTNode expanded) {
		switch (simulationType) {
		case Simulation.RANDOM:
			return Simulation.simulateRandom(field, expanded, botId, opponentId);
		case Simulation.WIN_FIRST_RANDOM:
			return Simulation.simulateWinFirstRandom(field, expanded, botId, opponentId);
		case Simulation.RANDOM_EPT:
			return Simulation.simulateRandomEPT(field, expanded, botId, opponentId);
		case Simulation.WIN_FIRST_RANDOM_EPT:
			return Simulation.simulateWinFirstRandomEPT(field, expanded, botId, opponentId);
		default:
			throw new RuntimeException("Invalid UCT simulation type");
		}
	}
	
	/** Updates visited nodes: nodes from the expanded node to the root node */
	private void backpropagate(UCTNode expanded, double result) {
		while (expanded != null) {
			expanded.update(result, botId, opponentId);
			expanded = expanded.parent;
		}
	}
	
	/**
	 * Attempts to return the move of the max-robust child if exists (both most visited and highest reward) 
	 * else returns the move of the robust child (most visited). Most visited is valued more than highest
	 * reward as the most visited node is the more promising one.
	 */
	@Override
	public Move getBestMove() {		
		UCTNode maxRobustChild = null;
		double maxRobustQ = Integer.MIN_VALUE;
		int maxRobustN = Integer.MIN_VALUE;
		// assume first child is greatest
		UCTNode robustChild = root.children.get(0);
		
		for (UCTNode child : root.children) {
			sb.append(String.format("Root child %d, %d || %.1f / %d\n", child.a.column, child.a.row, child.q, child.n));
			// robust child
			if (child.n > robustChild.n) {
				robustChild = child;
			} else if (child.n == robustChild.n) {
				if (rand.nextInt(2) == 1)
					robustChild = child;
			}
			// max-robust child
			if (child.n == maxRobustN && Double.compare(child.q, maxRobustQ) == 0) {
				// equal, randomly choose
				if (rand.nextInt(2) == 1)
					maxRobustChild = child;
			} else if (child.n >= maxRobustN && Double.compare(child.q, maxRobustQ) >= 0) {
				// we know not equal, therefore either n && q are both greater, or one is greater
				maxRobustChild = child;
			} else if (child.n > maxRobustN || Double.compare(child.q, maxRobustQ) > 0) {
				// not equal, not both greater, but one is greater and the other is smaller
				maxRobustChild = null;
			}
			if (child.n > maxRobustN)
				maxRobustN = child.n;
			if (Double.compare(child.q, maxRobustQ) > 0)
				maxRobustQ = child.q;
		}
		sb.append(String.format("Max %.1f, Robust %d\n", maxRobustQ, maxRobustN));
		
		if (maxRobustChild != null) {
			sb.append(String.format("Max-robust child returned %d, %d\n", maxRobustChild.a.column, maxRobustChild.a.row));
			return maxRobustChild.a;
		} else {
			sb.append(String.format("Robust child returned %d, %d\n", robustChild.a.column, robustChild.a.row));
			return robustChild.a;
		}
	}

	@Override
	public void log() {
		/*if (treeType == MCTree.UCT_HEURISTIC_TREE) {
			sb.append(String.format("Collisions %d, Uses %d, Total Collisions %d, Total Uses %d\n", 
					UCTHeuristicNode.TRANSPOSITION_TABLE.getCurrentCollisions(), UCTHeuristicNode.TRANSPOSITION_TABLE.getCurrentUses(),
					UCTHeuristicNode.TRANSPOSITION_TABLE.getTotalCollisions(), UCTHeuristicNode.TRANSPOSITION_TABLE.getTotalUses()));
			UCTHeuristicNode.TRANSPOSITION_TABLE.clearCurrentLog();
		}*/
	}
}
