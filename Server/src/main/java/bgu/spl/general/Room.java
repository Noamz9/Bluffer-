package main.java.bgu.spl.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.StringMessage;

/**
 * the room in which the game occured in
 * String roomName- the name of the room the player enterd
 * ArrayList<Player> players- the list of the players in this room
 * String gameName-  the name of this game
 * boolean isPlaying- indicates if the current game is running of not
 * question[] question- an array of all questions
 * TBGP<Object> game- an instance of the game
 */
public class Room {
	private String roomName;
	private ArrayList<Player> players;
	private String gameName;
	private boolean isPlaying;
	private question[] question;
	private TBGP<Object> game;
	
	/**
	 * Contractor of the class
	 * @param String name- gets the name of the room
	 */
	public Room(String name) {
		roomName = name;
		players = new ArrayList<Player>();
		isPlaying = false;
		gameName = "";
		question = new question[3];

	}
	public String getRoomName(){
		return roomName;
	}
	/**
	 * sets the name of the game in this room
	 * @param String Gname-the updated name
	 */
	public void setGameName(String Gname) {
		gameName = Gname;
	}
	/**
	 * gets the name of the game in this room
	 */
	public String getGameName() {
		return gameName;
	}
	/**
	 * adds a player to this room
	 * @param Player player- the player to add
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}
	/**
	 * removes a player from this room
	 * @param Player player- the player to remove
	 */
	public void remove(Player player) {
		players.remove(player);
	}
	/**
	 * checks if the game is running in this room
	 */
	public boolean isPlaying() {
		return isPlaying;
	}
	/**
	 * return a list of players in this room
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void stopPlaying(){
		isPlaying = false;
	}

	/**
	 * starts the game in the cuurent room
	 * @param TBGP<Object> game- the game to start
	 */
	public void STRATGAME(TBGP<Object> game) {
		this.game = game;
		this.game.init(this);
		isPlaying = true;

		game.runGame();

	}
	/**
	 * get the room name
	 * @param String name- the name
	 */
	public Room getRoom(String name) {
		return this;
	}
	/**
	 * sends message to the players in this room
	 * @param String msg- the message to send
	 * @param protocolCallback<String> callback- unique callback of the palyer
	 */
	public void MSG(String msg,String PlayerName ,protocolCallback<StringMessage> callback) {
		Iterator<Player> playerIt=players.iterator();
		while(playerIt.hasNext()){
			Player p = playerIt.next();
			if(!p.getCallback().equals(callback)){
				try {
					p.getCallback().sendMessage(new StringMessage("USERMSG "+PlayerName+": "+msg));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
			}
			else{
				try {
					callback.sendMessage(new StringMessage("SYSMSG MSG ACCEPTED"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
				
			
		}
					
	}
	/**
	 * @param Player p- the player who answered
	 * @param String ans- the ans by the player
	 * if the player exists than we send to the game (BLUFFER) the player and his ans  
	 * to the setFalseAns function
	 */
	public void TXTRESP(Player p, String ans) {
		if (players.contains(p)) {
			game.setFalseAns(p, ans);

		}
	}
	/**
	 * @param Player temp- 
	 * @param Integer ans
	 * sends to the game (BLUFFER) the player and the ans who was selected
	 */
	public void SELECTRESP(Player temp, Integer ans) {
		game.chooseAns(temp, ans);
	}
	/**
	 * * ends the game by updating the isPlaying field of the room
	 */
	public void EndGame() {
		isPlaying = false;
	}

}
