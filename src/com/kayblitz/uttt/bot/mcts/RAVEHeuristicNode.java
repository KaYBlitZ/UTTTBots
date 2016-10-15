package com.kayblitz.uttt.bot.mcts;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;
import com.kayblitz.uttt.bot.Evaluation;

public class RAVEHeuristicNode extends RAVENode {
	
	private static final double HEURISTIC_MULTIPLIER = 1.0;
	private static final double UCT_CONFIDENCE_CONSTANT = 2.0;
	private static final double AMAF_CONFIDENCE_CONSTANT = 2.0;
	
	/** The value of the state this node is in according to the evaluation function **/
	public double heuristic;
	
	public RAVEHeuristicNode(Move move, int nextMoveBotId, int winner, RAVEHeuristicNode parent, Field field, int botId, int opponentId) {
		this(move.column, move.row, nextMoveBotId, winner, parent, field, botId, opponentId);
	}
	
	public RAVEHeuristicNode(int x, int y, int nextMoveBotId, int winner, RAVEHeuristicNode parent, Field field, int botId, int opponentId) {
		super(x, y, nextMoveBotId, winner, parent);
		// update initial values with heuristic evaluation function and heuristic confidence function
		// We are going to offset the heuristic value so it is always positive by adding the max possible value
		heuristic = Evaluation.evaluateFieldAdvancedOptimized(field, botId, opponentId) + Evaluation.MAX_HEURISTIC_OPTIMIZED;;
		heuristic *= HEURISTIC_MULTIPLIER;
		q = heuristic;
		n = (int) (q * UCT_CONFIDENCE_CONSTANT);
		amafQ = heuristic;
		amafN = (int) (amafQ * AMAF_CONFIDENCE_CONSTANT);
	}
}