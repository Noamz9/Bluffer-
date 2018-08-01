package main.java.bgu.spl.general;

import java.io.IOException;
import java.net.Socket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.callback.Callback;

import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.StringMessage;

/**
 * the class creates a singletone of a gameboard implements the Client To Server
 * Commands ArrayList<String> NickNames -list of nicks in use.
 * ConcurrentHashMap<String, Room> rooms -save the room with its name.
 * ConcurrentHashMap<protocolCallback<String>, Player> Callbacks-save the
 * players and their callbacks. ConcurrentHashMap<String, Class<? extends TBGP>>
 * supportedGames-save the games that are supported by the server .
 */
public class gameBoard {

	private ArrayList<String> NickNames;
	private ConcurrentHashMap<String, Room> rooms;
	private ConcurrentHashMap<protocolCallback<StringMessage>, Player> Callbacks;
	private ConcurrentHashMap<String, Class<? extends TBGP>> supportedGames;

	/**
	 * construcror
	 */
	protected gameBoard() {
		NickNames = new ArrayList<String>();
		rooms = new ConcurrentHashMap<String, Room>();
		supportedGames = new ConcurrentHashMap<String, Class<? extends TBGP>>();
		Callbacks = new ConcurrentHashMap<protocolCallback<StringMessage>, Player>();
		supportedGames.put("BLUFFER", Bluffer.class);

	}

	// implements as Singleton
	private static class SingeltonHolder {
		private static gameBoard instance = new gameBoard();
	}

	public static gameBoard getInstance() {
		return SingeltonHolder.instance;
	}

	/**
	 * @param nick-the
	 *            nick the player entered
	 * @return if the nick is available
	 */
	public boolean PlayerExist(String nick) {
		return (NickNames.contains(nick));
	}

	/**
	 * @param nick-
	 *            the nick the player entered
	 * @param callback-the
	 *            unique callback of the player
	 * @throws Exception
	 *             if if the nick is available- insert it to the NickName
	 *             arrayList and insert the player and his callback to callbacks
	 *             to the Callback hashMap.
	 */
	public synchronized void addNick(String nick, protocolCallback<StringMessage> callback) throws Exception {
		if (!PlayerExist(nick) && !(Callbacks.containsKey(callback))) {
			NickNames.add(nick);
			Callbacks.put(callback, new Player(nick, callback));
			SYSMSG(" NICK ACCEPTED", callback);
		} else
			SYSMSG(" NICK REJECTED", callback);

	}

	/**
	 * @param roomName-
	 *            the name of the room the player want join to.
	 * @param call-
	 *            the unique callback of the player if the room isn't exist-add
	 *            it to the rooms list check if it available add the player to
	 *            the room
	 */
	public synchronized void join(String roomName, protocolCallback<StringMessage> call) {
		Player temp = Callbacks.get(call);
		rooms.putIfAbsent(roomName, new Room(roomName));
		if (!rooms.get(roomName).isPlaying()) {
			if (temp != null && !temp.playing()) {
				if (temp.getRoom() != null) {
					temp.getRoom().remove(Callbacks.get(call));
					temp.setRoom(rooms.get(roomName));
				}

				rooms.get(roomName).addPlayer(Callbacks.get(call));
				temp.setRoom(rooms.get(roomName));
				SYSMSG(" JOIN ACCEPTED", call);
			} else {
				SYSMSG(" JOIN REJECTED", call);

			}
		} else {
			SYSMSG(" JOIN ROOM IS IN THE MIDDLE OF A GAME", call);
		}
	}

	/**
	 * @param msg-a
	 *            message to be send
	 * @param callback-the
	 *            unique callback of the player Sends the specified message to
	 *            the users current room
	 */
	public synchronized void MSG(String msg,protocolCallback<StringMessage> callback) {
		if (this.Callbacks.containsKey(callback)) {
			Room playerRoom = this.Callbacks.get(callback).getRoom();
			if (playerRoom == null) { // player isn't in a room
				SYSMSG(" MSG REJECTED, not in a room", callback);
			} else {
				playerRoom.MSG(msg,Callbacks.get(callback).getPName() ,callback);
			}
		} else {
			SYSMSG("MSG REJECTED, not in a room", callback);
		}
	}

	/**
	 * @param callback-the
	 *            unique callback of the player Lists the games supported by the
	 *            server.
	 */
	public void LISTGAMES(protocolCallback<StringMessage> callback) {
		String ret = "LISTGAMES ACCEPTED ";
		for (Entry<String, Class<? extends TBGP>> entry : supportedGames.entrySet()) {
			String key = entry.getKey();
			ret += key + " ";
		}
		SYSMSG( ret, callback);

	}

	/**
	 * @param game-
	 *            the name of the game that need to be start
	 * @param callback-callback-the
	 *            unique callback of the player check if the game is supported-
	 *            and call the STARTGAME in Room class
	 */
	public synchronized void STARTGAME(String game, protocolCallback<StringMessage> callback) {
		if (supportedGames.containsKey(game)) {
			if (Callbacks.get(callback).getRoom() != null && !Callbacks.get(callback).getRoom().isPlaying()) {
				SYSMSG(" STARTGAME ACCEPTED", callback);
				try {
					try {
						Callbacks.get(callback).getRoom().STRATGAME(supportedGames.get(game).newInstance());
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else

			{

				SYSMSG(" STARTGAME REJECTED", callback);
			}
		}

	}

	/**
	 * @param ans-
	 *            the false answer of the player
	 * @param callback-the
	 *            unique callback of the player find the player according to the
	 *            callback and call to TXTRESP in Room class
	 */
	public void TXTRESP(String ans, protocolCallback<StringMessage> callback) {
		Player temp = Callbacks.get(callback);
		SYSMSG(" TXTRESP ACCEPTED", callback);
		temp.getRoom().TXTRESP(temp, ans);

	}

	/**
	 * @param ans-the
	 *            answer the player coose
	 * @param callback-the
	 *            unique callback of the player find the player according to the
	 *            callback and call to SELECTRESP in Room class
	 */
	public void SELECTRESP(Integer ans, protocolCallback<StringMessage> callback) {
		Player temp = Callbacks.get(callback);
		temp.getRoom().SELECTRESP(temp, ans);

	}

	public void SYSMSG(String msg, protocolCallback<StringMessage> callback) {
		try {
			callback.sendMessage(new StringMessage("SYSMSG " + msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void GAMEMSG(String msg, protocolCallback<StringMessage> callback) {
		try {
			callback.sendMessage(new StringMessage("GAMEMSG " + msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param callback-the
	 *            unique callback of the player . Closes the connection.
	 */
	public synchronized boolean QUIT(protocolCallback<StringMessage> callback) {
		if (Callbacks.containsKey(callback) && Callbacks.get(callback).getRoom()!=null) {
			String room = Callbacks.get(callback).getRoom().getRoomName();
			if (room != null) {
				if (!rooms.get(room).getPlayers().contains(Callbacks.get(callback)) || rooms.get(room).isPlaying()) {
					SYSMSG("QUIT REJECTED", callback);
					return false;
				}
				NickNames.remove(Callbacks.get(callback).getPName());
				Callbacks.remove(callback);

			} else {
				NickNames.remove(Callbacks.get(callback).getPName());
				Callbacks.remove(callback);
			}
			SYSMSG("QUIT ACCEPTED", callback);
			return true;
		}
		SYSMSG("QUIT ACCEPTED", callback);
		return true;
	}

}
