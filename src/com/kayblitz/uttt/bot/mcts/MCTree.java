package com.kayblitz.uttt.bot.mcts;

import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public abstract class MCTree {
	public static final int UCT_TREE = 0;
	public static final int RAVE_TREE = 1;
	
	protected MCTSNode root;
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
		root = new MCTSNode(-1, -1, botId, -1, null);
		root.saveState(field);
	}
	
	public abstract void iterate();
	protected abstract MCTSNode select();
	protected abstract MCTSNode expand(MCTSNode selected);
	protected abstract double simulate(MCTSNode expanded);
	protected abstract void backpropagate(MCTSNode expanded, double result);
	public abstract Move getBestMove();
}
