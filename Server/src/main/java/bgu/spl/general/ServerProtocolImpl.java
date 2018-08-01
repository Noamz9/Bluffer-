package main.java.bgu.spl.general;

import java.io.IOException;

import main.java.bgu.spl.protocol.AsyncServerProtocol;
import main.java.bgu.spl.protocol.ServerProtocol;
import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.StringMessage;

public class ServerProtocolImpl implements ServerProtocol<StringMessage>, AsyncServerProtocol<StringMessage>{
	private boolean _connectionTerminated=false;
	private boolean shouldClose = false;

	public ServerProtocolImpl(){}
	
	
	public void processMessage(StringMessage _msg, protocolCallback callback) {
		StringMessage command;
		StringMessage msg;
		if (_msg.getMessage().contains(" ")) {
			command = new StringMessage(_msg.getMessage().substring(0, _msg.getMessage().indexOf(' ')));
			msg = new StringMessage(_msg.getMessage().substring(_msg.getMessage().indexOf(' ') + 1));
		} else {
			command = _msg;
			msg = _msg;

		}
		if (command.getMessage().startsWith("NICK")) {
			try {
				(gameBoard.getInstance()).addNick(msg.getMessage(), callback);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} // end if NICK
		else if (command.getMessage().startsWith("JOIN")) {
			(gameBoard.getInstance()).join(msg.getMessage(), callback);

		}
		else if (command.getMessage().startsWith("MSG")) {
			(gameBoard.getInstance()).MSG(msg.getMessage(), callback);
		}
		else if (command.getMessage().startsWith("LISTGAMES")) {
			(gameBoard.getInstance()).LISTGAMES(callback);
		}
		else if (command.getMessage().startsWith("STARTGAME")) {
			(gameBoard.getInstance()).STARTGAME(msg.getMessage(), callback);
		}

		else if (command.getMessage().startsWith("TXTRESP")) {
			(gameBoard.getInstance()).TXTRESP(msg.getMessage().toLowerCase(), callback);
		}
		else if (command.getMessage().startsWith("SELECTRESP")) {
			(gameBoard.getInstance()).SELECTRESP(Integer.parseInt(msg.getMessage()), callback);
		}
		else if (command.getMessage().startsWith("QUIT")) {
			boolean ans = (gameBoard.getInstance()).QUIT(callback);
			if (ans == true)
				shouldClose = true;
			else
				shouldClose = false;
		}
		else {
			try {
				callback.sendMessage(new StringMessage("SYSMSG :"+_msg.getMessage()+ " UNIDENTIFIED"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean isEnd(StringMessage msg) {
		if (msg.getMessage().equals("QUIT") && shouldClose)
			return true;
		return false;

	}

	@Override
	public boolean shouldClose() {
		return shouldClose;
	}

	@Override
	public void connectionTerminated() {
		this._connectionTerminated = true;

	}

}