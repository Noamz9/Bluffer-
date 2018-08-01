package main.java.bgu.spl.reactor;

import main.java.bgu.spl.protocol.AsyncServerProtocol;
import main.java.bgu.spl.protocol.ServerProtocol;
import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.MessageTokenizer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<T> implements Runnable {

	private final ServerProtocol<T> _protocol;
	private final MessageTokenizer<T> _tokenizer;
	private final ConnectionHandler<T> _handler;
	private final protocolCallback<T> protoCallback;

	public ProtocolTask(final AsyncServerProtocol<T> _protocol2, final MessageTokenizer<T> _tokenizer2,
			final ConnectionHandler<T> connectionHandler) {
		this._protocol = _protocol2;
		this._tokenizer = _tokenizer2;
		this._handler = connectionHandler;
		protoCallback = new gameProtocol();
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
		// go over all complete messages and process them.
		while (_tokenizer.hasMessage()) {
			 T msg = _tokenizer.nextMessage();
	         this._protocol.processMessage(msg,protoCallback);
		}
	}

	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}

	private class gameProtocol implements protocolCallback<T> {

		@Override
		public void sendMessage(T msg) throws IOException {
			_handler.addOutData(_tokenizer.getBytesForMessage(msg));

		}
	}
}

