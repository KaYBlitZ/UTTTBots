package com.kayblitz.uttt;

public abstract class Bot {
	protected int botId, opponentId;
	
	public abstract Move makeMove(Field field, int timebank, int moveNum);
	
	public void setBotId(int botId) {
		this.botId = botId;
	}
	
	public void setOpponentId(int opponentId) {
		this.opponentId = opponentId;
	}
}
