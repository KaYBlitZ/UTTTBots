package com.kayblitz.uttt.bot.mcts;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;
import com.kayblitz.uttt.bot.Evaluation;

public class UCTHeuristicNode extends UCTNode {
	
	public static double HEURISTIC_MULTIPLIER = 0.1;
	public static double UCT_CONFIDENCE = 2.0;
	//public static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable();
	
	/** The value of the state this node is in according to the evaluation function **/
	public double heuristic;
	
	public UCTHeuristicNode(Move move, int nextMoveBotId, int winner, UCTHeuristicNode parent, 
			Field field, int botId, int opponentId, int numChildren) {
		this(move.column, move.row, nextMoveBotId, winner, parent, field, botId, opponentId, numChildren);
	}
	
	public UCTHeuristicNode(int x, int y, int nextMoveBotId, int winner, UCTHeuristicNode parent, Field field, int botId, int opponentId, int numChildren) {
		super(x, y, nextMoveBotId, winner, parent, numChildren);
		// update initial values with heuristic evaluation function and heuristic confidence function
		// We are going to offset the heuristic value so it is always positive by adding the max possible value
		
		// Transposition table decreases the # of iterations. Probably because the evaluation is light and therefore much faster.
		// In the future, if the evaluation becomes heavy, the table will be faster.
		/*long zobristKey = TRANSPOSITION_TABLE.calcZobristKey(field.getField());
		Double heuristic = TRANSPOSITION_TABLE.retrieveHeuristic(zobristKey);
		if (heuristic == null) {
			heuristic = Evaluation.evaluateFieldAdvancedOptimized(field, botId, opponentId) + Evaluation.MAX_HEURISTIC_OPTIMIZED;
			TRANSPOSITION_TABLE.storeHeuristic(zobristKey, heuristic);
		}*/
		heuristic = Evaluation.evaluateFieldAdvancedOptimized(field, botId, opponentId) + Evaluation.MAX_HEURISTIC_OPTIMIZED;
		heuristic *= HEURISTIC_MULTIPLIER;
		q = heuristic;
		n = (int) (q * UCT_CONFIDENCE);
	}
}
