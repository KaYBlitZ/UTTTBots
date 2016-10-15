package com.kayblitz.uttt.bot.mcts;

import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public abstract class MCTree {
	public static final int UCT_TREE = 0;
	public static final int RAVE_TREE = 1;
	public static final int RAVE_HEURISTIC_TREE = 2;
	
	protected Random rand;
	protected int simulationType, botId, opponentId;
	protected Field field;
	protected StringBuilder sb;

	public MCTree(Field field, StringBuilder sb, int simulationType, int botId, int opponentId) {
		this.field = field;
		this.sb = sb;
		this.simulationType = simulationType;
		this.botId = botId;
		this.opponentId = opponentId;
		rand = new Random(System.currentTimeMillis());
	}
	
	public abstract void iterate();
	public abstract Move getBestMove();
}
