package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class RAVETree extends MCTree {
	
	protected RAVENode root;
	public static double EXPLORATION_CONSTANT = 0.5;
	public static double RAVE_CONSTANT = 0.1;
	
	public RAVETree(Field field, StringBuilder sb, int treeType, int simulationType, int botId, int opponentId) {
		super(field, sb, treeType, simulationType, botId, opponentId);
		root = new RAVENode(-1, -1, botId, -1, null, field.getNumAvailableMoves());
		root.saveState(field);
	}
	
	/** Goes through one iteration of the MCTS algorithm */
	@Override
	public void iterate() {
		// Tree policy: selection and expansion
		RAVENode selected = select();
		RAVENode expanded = selected.isTerminal() ? selected : expand(selected);
		// Simulation
		// Even if the node is terminal we still simulate it because every iteration afterwards
		// will select the same terminal node if we do not simulate, resulting in an infinite loop
		ArrayList<Move> botMoves = new ArrayList<Move>(35);
		ArrayList<Move> opponentMoves = new ArrayList<Move>(35);
		double result = simulate(expanded, botMoves, opponentMoves);
		// Backpropagation
		backpropagate(expanded, result, botMoves, opponentMoves);
	}
	
	/** Selects a child node to expand using UCT = cw/cn + c * sqrt(ln(pn)/cn) where cw is child wins, 
	 * cn is child visits, c is the exploration constant, and pn is parent visits. Returned node may 
	 * be a terminal state.
	 */
	protected RAVENode select() {
		RAVENode selected = root;
		while (true) {
			if (selected.isTerminal())
				return selected; // check to see if we have reached a finished state
			
			if (selected.children.size() == selected.numChildren) {
				// children all explored at least once, explore deeper using UCT-RAVE
				RAVENode selectedChild = null;
				double bestValue = Integer.MIN_VALUE;
				double constant = Math.log(selected.n);
				
				for (RAVENode child : selected.children) {
					// If the next bot's move has a chance to win game, select that move since that bot WILL
					// ALWAYS choose that winning moving instead of selecting any other move
					if (child.winner == selected.nextMoveBotId)
						return child;
					
					// select the child with the highest UCT value
					double value = (1 - child.beta) * child.getAverageReward() + 
							child.beta * child.getAverageAMAFReward() + 
							EXPLORATION_CONSTANT * Math.sqrt(constant/child.n);
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
	protected RAVENode expand(RAVENode selected) {
		selected.restoreState(field); // restore state of node
		// get next unexplored move
		Move move = field.getAvailableMoves().get(selected.children.size());
		field.makeMove(move, selected.nextMoveBotId, false); 
		RAVENode child = new RAVENode(move, selected.nextMoveBotId == 1 ? 2 : 1, field.getWinner(), 
				selected, field.getNumAvailableMoves());
		child.saveState(field);
		selected.children.add(child); // add to parent's array of children
		
		return child;
	}
	
	/** 
	 * Simulates a play from the Node to an end state and returns a value, WIN(1), TIE(0.5), LOSS(0).
	 */
	protected double simulate(RAVENode expanded, ArrayList<Move> botMoves, ArrayList<Move> opponentMoves) {
		switch (simulationType) {
		case Simulation.WIN_FIRST_RANDOM_RAVE:
			return Simulation.simulateWinFirstRandomRAVE(field, expanded, botMoves, opponentMoves, botId, opponentId);
		case Simulation.RANDOM_EPT_RAVE:
			return Simulation.simulateRandomEPTRAVE(field, expanded, botMoves, opponentMoves, botId, opponentId);
		default:
			throw new RuntimeException("Invalid RAVE simulation type");
		}
	}
	
	/** Updates visited nodes: nodes from the expanded node to the root node */
	protected void backpropagate(RAVENode expanded, double result, ArrayList<Move> botMoves, ArrayList<Move> opponentMoves) {
		while (expanded != null) {
			// standard UCT update
			expanded.updateUCT(result, botId, opponentId);
			// AMAF update
			// update playout node
			expanded.updateAMAF(result, botId, opponentId);
			updateBeta(expanded);
			// update siblings
			if (expanded.parent != null) {
				for (RAVENode sibling : expanded.parent.children) {
					if (sibling != expanded) { // check for self
						if (sibling.nextMoveBotId == opponentId) { // our bot made this node
							for (Move move : botMoves) { // therefore check in bot moves
								if (move.row == sibling.a.row && move.column == sibling.a.column) {
									// matches one of our bot's moves
									sibling.updateAMAF(result, botId, opponentId);
									updateBeta(sibling);
									break;
								}
							}
						} else { // opponent made this node
							for (Move move : opponentMoves) { // therefore check in opponent moves
								if (move.row == sibling.a.row && move.column == sibling.a.column) {
									// matches one of our opponent's moves
									sibling.updateAMAF(result, botId, opponentId);
									updateBeta(sibling);
									break;
								}
							}
						}
					}
				}
			}
			expanded = expanded.parent;
		}
	}
	
	/** Updates beta according to mimimum MSE schedule **/
	protected void updateBeta(RAVENode node) {
		node.beta = node.amafN / (node.n + node.amafN + RAVE_CONSTANT * node.n * node.amafN);
	}
	
	/**
	 * Attempts to return the move of the max-robust child if exists (both most visited and highest reward) 
	 * else returns the move of the robust child (most visited). Most visited is valued more than highest
	 * reward as the most visited node is the more promising one.
	 */
	@Override
	public Move getBestMove() {		
		RAVENode maxRobustChild = null;
		double maxRobustQ = Integer.MIN_VALUE;
		double maxRobustN = Integer.MIN_VALUE;
		// assume first child is greatest
		RAVENode robustChild = root.children.get(0);
		
		for (RAVENode child : root.children) {
			double adjustedQ = (1 - child.beta) * child.q + child.beta * child.amafQ;
			double adjustedN = (1 - child.beta) * child.n + child.beta * child.amafN;
			sb.append(String.format("Root child %d, %d || %.1f / %.1f\n", child.a.column, child.a.row, adjustedQ, adjustedN));
			// robust child
			double adjRobustChildN = (1 - robustChild.beta) * robustChild.n + robustChild.beta * robustChild.amafN;
			if (Double.compare(adjustedN, adjRobustChildN) > 0) {
				robustChild = child;
			} else if (Double.compare(adjustedN, adjRobustChildN) == 0) {
				if (rand.nextInt(2) == 1)
					robustChild = child;
			}
			// max-robust child
			if (Double.compare(adjustedN, maxRobustN) == 0 && Double.compare(adjustedQ, maxRobustQ) == 0) {
				// equal, randomly choose
				if (rand.nextInt(2) == 1)
					maxRobustChild = child;
			} else if (Double.compare(adjustedN, maxRobustN) >= 0 && Double.compare(adjustedQ, maxRobustQ) >= 0) {
				// we know not equal, therefore either n && q are both greater, or one is greater
				maxRobustChild = child;
			} else if (Double.compare(adjustedN, maxRobustN) > 0 || Double.compare(adjustedQ, maxRobustQ) > 0) {
				// not equal, not both greater, but one is greater and the other is smaller
				maxRobustChild = null;
			}
			if (Double.compare(adjustedN, maxRobustN) > 0)
				maxRobustN = adjustedN;
			if (Double.compare(adjustedQ, maxRobustQ) > 0)
				maxRobustQ = adjustedQ;
		}
		sb.append(String.format("Max %.1f, Robust %.1f\n", maxRobustQ, maxRobustN));
		
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
	}
}
