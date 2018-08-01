package main.java.bgu.spl.general;
/**
 * TBGP interface that has all the methods that each game  should perform 
 */
public interface TBGP <T>{
	/**
	 * set false answer from the players (client)
	 * @param T p- receives the player (client) who insert this answer
	 * @param T answer- the answer of the player
	 */
	public void setFalseAns(T p, T ans);
	/**
	 * check the ans that the client chose
	 * @param T temp the client
	 * @param T ans that he choose
	 */
	public void chooseAns(T temp,T ans);
	/**
	 * init the fields to the game
	 * @param T board- the borad in which the game is preformed on
	 */
	public void init(T board);
	/**
	 * runs the game
	 */
	public void runGame();
}


