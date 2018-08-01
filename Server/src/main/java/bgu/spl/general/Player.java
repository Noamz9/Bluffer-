package main.java.bgu.spl.general;

import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.StringMessage;

/**
 * object of a player in the game
 *String name-nick of player
 *Room room- the current room the player is in
 *protocolCallback<String> call- the unique callback of the player 
 *int score-the score of the player
 */
public class Player {
	private String name;
	private Room room;
	private protocolCallback<StringMessage> call;
	private int score;
	private int roundScore;
	private boolean roundAns;

	/**
	 * Constructor
	 */
	public Player(String name,protocolCallback<StringMessage> call) {
		this.call = call;
		this.name = name;
		room = null;
		score=0;
		roundScore=0;
		roundAns=false;
	}
	
	public String getPName(){
		return name;
	}
	public boolean getRoundAns(){
		return roundAns;
	}
	public void setRoundAns(boolean ans){
		roundAns=ans;
	}
	

	/**
	 * @return if the player is in middle of game
	 */
	public boolean playing() {
		if (room != null) {
			return (room.isPlaying());
		}
		return false;
	}


	/**
	 * @return the callback of the player
	 */
	public protocolCallback<StringMessage> getCallback() {
		return call;
	}

	/**
	 * @return the room the player is in
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * @param room- change the room the player is in.
	 */
	public void setRoom(Room room) {
		this.room = room;
	}
	public int getRoundScore(){
		return roundScore;
	}
	public void addRoundScore(int score){
		roundScore=roundScore+score;
	}
	
	public void setRoundScore(){
		roundScore=0;
	}

}
