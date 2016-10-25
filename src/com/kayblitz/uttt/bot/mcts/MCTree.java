package com.kayblitz.uttt.bot.mcts;

import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public abstract class MCTree {
	public static final int UCT_TREE = 0;
	public static final int UCT_HEURISTIC_TREE = 1;
	public static final int RAVE_TREE = 2;
	public static final int RAVE_HEURISTIC_TREE = 3;
	
	protected Random rand;
	protected int treeType, simulationType, botId, opponentId;
	protected Field field;
	protected StringBuilder sb;

	public MCTree(Field field, StringBuilder sb, int treeType, int simulationType, int botId, int opponentId) {
		this.field = field;
		this.sb = sb;
		this.treeType = treeType;
		this.simulationType = simulationType;
		this.botId = botId;
		this.opponentId = opponentId;
		rand = new Random(System.currentTimeMillis());
	}
	
	public abstract void iterate();
	public abstract Move getBestMove();
	/** Logs data to the StringBuilder **/
	public abstract void log();
}
