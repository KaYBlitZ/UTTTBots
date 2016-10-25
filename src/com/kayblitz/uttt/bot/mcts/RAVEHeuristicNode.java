package com.kayblitz.uttt.bot.mcts;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;
import com.kayblitz.uttt.bot.Evaluation;

public class RAVEHeuristicNode extends RAVENode {
	
	public static double HEURISTIC_MULTIPLIER = 0.1;
	public static double UCT_CONFIDENCE = 2.0;
	public static double AMAF_CONFIDENCE = 2.0;
	
	/** The value of the state this node is in according to the evaluation function **/
	public double heuristic;
	
	public RAVEHeuristicNode(Move move, int nextMoveBotId, int winner, RAVEHeuristicNode parent, 
			Field field, int botId, int opponentId, int numChildren) {
		this(move.column, move.row, nextMoveBotId, winner, parent, field, botId, opponentId, numChildren);
	}
	
	public RAVEHeuristicNode(int x, int y, int nextMoveBotId, int winner, RAVEHeuristicNode parent, Field field, int botId, int opponentId, int numChildren) {
		super(x, y, nextMoveBotId, winner, parent, numChildren);
		// update initial values with heuristic evaluation function and heuristic confidence function
		// We are going to offset the heuristic value so it is always positive by adding the max possible value
		heuristic = Evaluation.evaluateFieldAdvancedOptimized(field, botId, opponentId) + Evaluation.MAX_HEURISTIC_OPTIMIZED;;
		heuristic *= HEURISTIC_MULTIPLIER;
		q = heuristic;
		n = (int) (q * UCT_CONFIDENCE);
		amafQ = heuristic;
		amafN = (int) (amafQ * AMAF_CONFIDENCE);
	}
}
