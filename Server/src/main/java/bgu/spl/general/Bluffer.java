package main.java.bgu.spl.general;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.StringMessage;

/**
 * A BLUFFER instance of a game implements the general interface of all
 * games(TBGP) created when the game starts in the room class
 * ConcurrentHashMap<String, Player> playersAns- a hash map of the answers
 * (string) and the player who entered it int numOfPlayers- the number of player
 * in cuurent BLUFFER game String[] totalAnswers- all the answers
 * ConcurrentHashMap<Player, Integer> playersScore- a hash map of the players
 * and their score int iter- counts the round of the game ArrayList
 * <Player> ListOfPlayers- list of all the players in this game Room room- the
 * room in which the game is occurring
 */

public class Bluffer implements TBGP<Object> {

	private int numOfPlayers;
	private ConcurrentHashMap<String, Player> playersAns;
	private String[] totalAnswers;
	private question[] ques;
	private ConcurrentHashMap<Player, Integer> playersScore;
	private int iter;
	private ArrayList<Player> ListOfPlayers;
	private Room room;
	private int counter;

	/**
	 * constractor
	 */
	public Bluffer() {
		this.playersAns = new ConcurrentHashMap<String, Player>();
		this.numOfPlayers = 0;
		this.totalAnswers = new String[0];
		this.playersScore = new ConcurrentHashMap<Player, Integer>();
		ListOfPlayers = new ArrayList<Player>();
		iter = 0;
		counter = 0;
	}

	/*
	 * initialing the questions from json initialize the fileds of the class
	 */
	public void init(Object curRoom) {
		iter = 0;
		this.room = (Room) curRoom;
		this.numOfPlayers = room.getPlayers().size();
		this.ListOfPlayers = room.getPlayers();
		this.totalAnswers = new String[numOfPlayers + 1];
		ListOfPlayers = room.getPlayers();
		Iterator<Player> it = ListOfPlayers.iterator();
		while (it.hasNext()) {
			playersScore.put((Player) it.next(), 0);

		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("Bluffer.json"));
		} catch (FileNotFoundException e) {// no file
			e.printStackTrace();
		}
		Gson gson = new Gson();
		initialData readJson = gson.fromJson(br, initialData.class);

	//	ques = new question[readJson.questions.length];
		ques = readJson.questions;
	//	for (int i = 0; i < ques.length; i++) {
	//		question question = new question(readJson.questions[i].getQuestion(), readJson.questions[i].getRealAns());
	//		ques[i] = question;
	//	}
		shuffleArray(ques);

	}

	/**
	 * set false answer from the players
	 * 
	 * @param Player
	 *            player- recieves the player that inserted the false ans
	 * @param String
	 *            ans- the false ans of the player
	 */
	public void setFalseAns(Object player, Object ans) {
		if (!playersAns.containsValue(player)) {
			playersAns.put((String) ans, (Player) player);
			if (playersAns.size() == numOfPlayers) {
				setAns(ques[iter]);
				ASKCHOICES(ques[iter].getQuestion(), totalAnswers);
			}
		}
	}

	/**
	 * init the array with all the answer
	 * 
	 * @param question
	 *            currQuestion- the question in the current round
	 */
	public void setAns(question currQuestion) {

		int i = 1;
		for (Entry<String, Player> entry : playersAns.entrySet()) {
			String key = entry.getKey();
			totalAnswers[i - 1] = key;
			i++;
		}
		totalAnswers[totalAnswers.length - 1] = currQuestion.getRealAns();
		shuffleArray(totalAnswers);
	}

	/**
	 * shuffle the values in the array
	 * 
	 * @param String[]
	 *            totalAnswers- the array of total answer (real and fake)
	 */
	public void shuffleArray(Object[] totalAnswers) {
		Random rgen = new Random(); // Random number generator

		for (int i = 0; i < totalAnswers.length; i++) {
			int randomPosition = rgen.nextInt(totalAnswers.length);
			Object temp = totalAnswers[i];
			totalAnswers[i] = totalAnswers[randomPosition];
			totalAnswers[randomPosition] = temp;
		}

	}
	/*
	 * Random rnd = ThreadLocalRandom.current(); for (int i =
	 * totalAnswers.length - 1; i >= 0; i--) { int index = rnd.nextInt(i + 1);
	 * // Simple swaplayersAns.put((String) ans, (Player) player); String a =
	 * totalAnswers[index]; totalAnswers[index] = totalAnswers[i];
	 * totalAnswers[i] = a; }
	 */

	/**
	 * asks all the players the current question.excpects to receive a false
	 * answer from TXTRESP. called by runGame
	 * 
	 * @param String
	 *            msg-the current question
	 */
	public void ASKTXT(String msg) {
		Iterator<Player> i = ListOfPlayers.iterator();
		while (i.hasNext()) {
			Player p = i.next();
			try {
				p.getCallback().sendMessage(new StringMessage("ASKTXT "+msg));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * gives the player all the options (answers) to the current question
	 * 
	 * @param String
	 *            Question- the question in the current round
	 * @param String[]
	 *            totalAnswers- all the answers called by setFalseAns after all
	 *            the players entered their false ans
	 */
	public void ASKCHOICES(String Question, String[] totalAnswers) {
		Iterator<Player> i = ListOfPlayers.iterator();
		while (i.hasNext()) {
			Player p = i.next();
			String choices = "";
			for (int j = 0; j < totalAnswers.length; j++) {
				choices = choices + j + "." + totalAnswers[j] + " ";
			}
			try {
				p.getCallback().sendMessage(new StringMessage("ASKCHOICES "+choices));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * run the game- each round asks a question, waits for a false ans and at
	 * the end prints the score for each player
	 */
	public void runGame() {
		if (iter < 3) {
			ASKTXT(ques[iter].getQuestion()); // sends the question to the
												// clients

		} else {
			String summary = " ";
			for (Entry<Player, Integer> entry : playersScore.entrySet()) {
				int key = entry.getValue();
				Player p = entry.getKey();
				summary = summary + p.getPName() + " " + key + "pts";
			}
			for (Entry<Player, Integer> entry : playersScore.entrySet()) {
				Player p = entry.getKey();
				(gameBoard.getInstance()).GAMEMSG("SUMMARY: " + summary, p.getCallback());
			}
			room.stopPlaying();

		}

	}

	/*
	 * checks if the ans that the player choose is the right one and update the
	 * score accordingly at the end clear the array of answers to the next round
	 * 
	 * @param Object player- the player that needs to choose the ans
	 * 
	 * @param Object ans- the ans that the player choose called by SELECTRESP in
	 * class Room
	 */
	public void chooseAns(Object player, Object ans) {
		if ((int) ans < totalAnswers.length && (int) ans >= 0) {
			(gameBoard.getInstance()).GAMEMSG("SELECTRESP ACCEPTED", ((Player) player).getCallback());
			counter++;
			String s = totalAnswers[(int) ans];
			if (s.equals(ques[iter].getRealAns())) {
				((Player) player).setRoundAns(true);
				int tmpScore = playersScore.get(player);
				tmpScore = tmpScore + 10;
				((Player) player).addRoundScore(10);
				playersScore.put((Player) player, (int) tmpScore);

			}
			// check what is the chosen ans and update score to the player who
			// owns
			// it
			else {
				Player p = playersAns.get(s); // the player who owns this ans
				playersScore.forEach((k, j) -> {
					if (k.getPName().equals(p.getPName())) { // updates score to
																// every
																// owner of the
																// ans
						j = j + 5;
						playersScore.replace(k, j);
						k.addRoundScore(5);
					}

					// String score=String.valueOf(k.getRoundScore());

				});

			}

			if (counter == numOfPlayers) {
				counter = 0;
				iter++;
				this.playersAns.clear();
				this.totalAnswers = new String[numOfPlayers + 1];
				Iterator<Player> playerIt = ListOfPlayers.iterator();
				while (playerIt.hasNext()) {
					Player p = playerIt.next();
					if (p.getRoundAns()) {
						String score = String.valueOf(p.getRoundScore());
						(gameBoard.getInstance()).GAMEMSG(" correct! +" + score + "pts", p.getCallback());
					} else {
						String score = String.valueOf(p.getRoundScore());
						(gameBoard.getInstance()).GAMEMSG(" wrong!+ " + score + " pts", p.getCallback());
					}

					p.setRoundAns(false);
					p.setRoundScore();
				}
				runGame();
			}
		} else {
			(gameBoard.getInstance()).GAMEMSG("SELECTRESP REJECTED", ((Player) player).getCallback());
		}
	}
}
