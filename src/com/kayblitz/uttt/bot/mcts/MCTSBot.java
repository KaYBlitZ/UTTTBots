package com.kayblitz.uttt.bot.mcts;

import com.kayblitz.uttt.Bot;
import com.kayblitz.uttt.BotParser;
import com.kayblitz.uttt.Move;

public class MCTSBot extends Bot {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Tree type and simulation type must be given");
			return;
		}
		int treeType = -1, simulationType = -1;
		try {
			treeType = Integer.parseInt(args[0]);
			simulationType = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid tree or simulation type");
			return;
		}
		new BotParser(new MCTSBot(treeType, simulationType)).run();
	}
	
	private long startTime, limit;
	private int treeType, simulationType;
	
	public MCTSBot(int treeType, int simulationType) {
		this.treeType = treeType;
		this.simulationType = simulationType;
	}
	
	public long getElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public Move makeMove(int timebank) {
		startTime = System.currentTimeMillis();
		if (field.getMoveNum() == 1)
			return new Move(4, 4); // best first move
		int size = field.getNumAvailableMoves();
		if (field.getMoveNum() < 30) {
			if (size < 4) {
				limit = 500L;
			} else if (size < 7) {
				limit = 800L;
			} else if (size < 10) {
				limit = 1100L;
			} else {
				limit = 1700L;
			}
		} else {
			if (size < 4) {
				limit = 400L;
			} else if (size < 7) {
				limit = 700L;
			} else if (size < 10) {
				limit = 1000L;
			} else {
				limit = 1600L;
			}
		}
		// add 5 ms for getBestMove and output
		if (limit + 5 > timebank)
			limit = (long) (0.9f * timebank);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Timebank %d, Limit %d\n", timebank, limit));
		MCTree tree = null;
		if (treeType == MCTree.UCT_TREE) {
			tree = new UCTTree(field, sb, MCTree.UCT_TREE, simulationType, botId, opponentId);
		} else if (treeType == MCTree.UCT_HEURISTIC_TREE) {
			tree = new UCTHeuristicTree(field, sb, MCTree.UCT_HEURISTIC_TREE, simulationType, botId, opponentId);
		} else if (treeType == MCTree.RAVE_TREE) {
			tree = new RAVETree(field, sb, MCTree.RAVE_TREE, simulationType, botId, opponentId);
		} else if (treeType == MCTree.RAVE_HEURISTIC_TREE) {
			tree = new RAVEHeuristicTree(field, sb, MCTree.RAVE_HEURISTIC_TREE, simulationType, botId, opponentId);
		}
		
		int iterations = 0;
		while (getElapsedTime() < limit) {
			tree.iterate();
			iterations++;
		}
		Move bestMove = tree.getBestMove();
		tree.log();
		float elapsedSeconds = getElapsedTime() / 1000f;
		sb.append(String.format("Iterations %d, Elapsed %.3f\n", iterations, elapsedSeconds));
		sb.append("Iterations per second " + (int) (iterations / elapsedSeconds) + '\n');
		System.err.println(sb.toString());
		
		return bestMove;
	}
}
