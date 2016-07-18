package com.kayblitz.uttt.bot.mcts;

import java.util.ArrayList;
import java.util.Random;

import com.kayblitz.uttt.Field;
import com.kayblitz.uttt.Move;

public class Tree {
	
	private Random rand;
	private Node root;
	private int botId, opponentId;
	private Field field;
	private StringBuffer sb;
	
	public Tree(Field field, StringBuffer sb, int botId, int opponentId) {
		this.field = field;
		this.sb = sb;
		this.botId = botId;
		this.opponentId = opponentId;
		rand = new Random(System.currentTimeMillis());
		root = new Node(-1, -1, botId, false, null);
		root.saveState(field);
	}
	
	/** Goes through one iteration of the MCTS algorithm */
	public void iterate() {
		// tree policy: selection and expansion
		Node selected = select();
		Node expanded = expand(field, selected);
		if (expanded == null) return; // selected node is terminal state, return, nothing else to do
		// simulation
		int result = simulate(field, expanded);
		// backpropagation
		backpropagate(expanded, result);
	}
	
	/** Selects a child node to expand. Returned node may be a terminal state. */
	public Node select() {
		Node current = root;
		// TODO: Selecting randomly, should choose better tree policy
		while (true) {
			if (rand.nextInt(5) == 3 || current.children.size() == 0) break;
			current = current.children.get(rand.nextInt(current.children.size()));
		}
		return current;
	}
	
	/**
	 * Expands the selected node and returns the new node. The state of the Field will be that of  
	 * the newly created node.
	 */
	public Node expand(Field field, Node selected) {
		if (selected.isTerminal) return null;
		
		selected.restoreState(field); // restore state at node
		// make new unique child else choose an existing one
		ArrayList<Move> moves = field.getAvailableMoves();
		if (moves.size() == selected.children.size()) {
			// all children added, just choose one randomly
			return selected.children.get(rand.nextInt(selected.children.size()));
		} else {
			// create new unique child
			Move move = null;
			while (true) {
				move = moves.get(rand.nextInt(moves.size())); // TODO: dont use random
				boolean unique = true;
				for (Node node : selected.children) {
					if (node.a.column == move.column && node.a.row == move.row) {
						unique = false;
						break;
					}
				}
				if (unique) break;
			}
			field.makeMove(move, selected.botId, false); 
			Node child = new Node(move, selected.botId == 1 ? 2 : 1, field.getWinner() >= 0, selected);
			child.saveState(field);
			selected.children.add(child); // add to parent's array of children
			
			return child;
		}
	}
	
	/** 
	 * Simulates a random play from the Node to an end state and returns a value, WIN(1), TIE(0), LOSS(-1).
	 * Field should be in the state of the passed in node.
	 */
	public int simulate(Field field, Node expanded) {
		int winner = field.getWinner();
		int currentId = expanded.botId;
		while (winner < 0) {
			ArrayList<Move> moves = field.getAvailableMoves();
			Move move = moves.get(rand.nextInt(moves.size()));
			field.makeMove(move, currentId, false);
			currentId = (currentId == 1 ? 2 : 1);
			winner = field.getWinner();
		}
		if (winner == botId) {
			return 1;
		} else if (winner == 0) {
			return 0;
		} else {
			return -1;
		}
	}
	
	/** Updates visited nodes: nodes from the expanded node to the root node */
	public void backpropagate(Node expanded, int result) {
		while (expanded != null) {
			expanded.update(result);
			expanded = expanded.parent;
		}
	}
	
	/**
	 * Attempts to return the move of the max-robust child if exists (both most visited and highest reward) 
	 * else returns the move of the max child (highest reward)
	 */
	public Move getBestMove() {
		if (root.children.size() == 0) throw new RuntimeException("MCTS: root node has no children!");
		
		int maxRobustN = Integer.MIN_VALUE;
		int maxRobustQ = Integer.MIN_VALUE;
		int maxChildQ = Integer.MIN_VALUE;
		Node maxRobustChild = null, maxChild = null;
		
		for (Node child : root.children) {
			sb.append(String.format("Root child %d, %d || %d / %d\n", child.a.column, child.a.row, child.q, child.n));
			// max child
			if (child.q > maxChildQ) {
				maxChildQ = child.q;
				maxChild = child;
			} else if (child.q == maxChildQ) {
				if (rand.nextInt(2) == 1) maxChild = child;
			}
			// max-robust child
			boolean n = false, q = false;
			if (child.n >= maxRobustN){
				n = true;
				maxRobustN = child.n;
			}
			if (child.q >= maxRobustQ) {
				q = true;
				maxRobustQ = child.q;
			}
			if (n && q) { // both greater
				// if equal choose randomly
				if (maxRobustChild != null && child.n == maxRobustChild.n && child.q == maxRobustChild.q) {
					if (rand.nextInt(2) == 1) maxRobustChild = child;
				} else { // child is greater than maxRobustChild
					maxRobustChild = child;
				}
			} else if (n || q) { // only ONE greater
				maxRobustChild = null; // not both greater, max-robust does not exist at this point
			}
		}
		sb.append(String.format("Max %d, Robust %d\n", maxRobustQ, maxRobustN));
		
		if (maxRobustChild != null) {
			sb.append(String.format("Max-robust child returned %d, %d\n", maxRobustChild.a.column, maxRobustChild.a.row));
			return maxRobustChild.a;
		} else {
			sb.append(String.format("Max child returned %d, %d\n", maxChild.a.column, maxChild.a.row));
			return maxChild.a;
		}
	}
}
