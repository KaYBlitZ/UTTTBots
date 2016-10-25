package com.kayblitz.uttt;

public abstract class Bot {
	protected Field field;
	protected int botId, opponentId;
	
	public Bot() {
		field = new Field();
	}
	
	public abstract Move makeMove(int timebank);
}
