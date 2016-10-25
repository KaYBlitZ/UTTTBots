package com.kayblitz.uttt;

import java.util.HashMap;
import java.util.Random;

public class TranspositionTable {
	
	private long[][][] values;
	private HashMap<Long, Double> table;
	int collisions, uses, totalCollisions, totalUses;
	
	public TranspositionTable() {
		Random rand = new Random(System.currentTimeMillis());
		// each configuration is board location and player id (1 or 2)
		values = new long[Field.COLS][Field.ROWS][2];
		for (int col = 0; col < Field.COLS; col++) {
			for (int row = 0; row < Field.ROWS; row++) {
				for (int i = 0; i < 2; i++) {
					values[col][row][i] = rand.nextLong();
				}
			}
		}
		table = new HashMap<Long, Double>();
	}
	
	/** Calculates the zobrist key to store the heuristic **/
	public void storeHeuristic(int[][] field, double heuristic) {
		long zobristKey = calcZobristKey(field);
		if (table.containsKey(zobristKey)) {
			collisions++;
			totalCollisions++;
		}
		table.put(zobristKey, heuristic);
	}
	
	/** Stores the heuristic with the zobrist key **/
	public void storeHeuristic(long zobristKey, double heuristic) {
		if (table.containsKey(zobristKey)) {
			collisions++;
			totalCollisions++;
		}
		table.put(zobristKey, heuristic);
	}
	
	/** Calculates the zobrist key to retrieve the heuristic **/
	public Double retrieveHeuristic(int[][] field) {
		long zobristKey = calcZobristKey(field);
		if (table.containsKey(zobristKey)) {
			uses++;
			totalUses++;
		}
		return table.get(zobristKey);
	}
	
	/** Retrieves the heuristic using the passed in zobrist key **/
	public Double retrieveHeuristic(long zobristKey) {
		if (table.containsKey(zobristKey)) {
			uses++;
			totalUses++;
		}
		return table.get(zobristKey);
	}
	
	public long calcZobristKey(int[][] field) {
		long zobristKey = 0;
		for (int col = 0; col < Field.COLS; col++) {
			for (int row = 0; row < Field.ROWS; row++) {
				int id = field[col][row];
				if (id != 0) {
					zobristKey ^= values[col][row][id - 1];
				}
			}
		}
		return zobristKey;
	}
	
	/** Returns the number of collisions that occurred in the transposition table **/
	public int getCurrentCollisions() {
		return collisions;
	}
	
	/** Returns the number of collisions that occurred in the transposition table **/
	public int getTotalCollisions() {
		return totalCollisions;
	}
	
	/** Returns the number of successful retrievals from table **/
	public int getCurrentUses() {
		return uses;
	}
	
	/** Returns the number of successful retrievals from table **/
	public int getTotalUses() {
		return totalUses;
	}
	
	public void clearCurrentLog() {
		collisions = 0;
		uses = 0;
	}
	
	public void clearTotalLog() {
		totalCollisions = 0;
		totalUses = 0;
	}
}
