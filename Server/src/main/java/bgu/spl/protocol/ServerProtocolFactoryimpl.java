package main.java.bgu.spl.protocol;

import main.java.bgu.spl.general.ServerProtocolImpl;
import main.java.bgu.spl.protocol.ServerProtocolFactory;
import main.java.bgu.spl.tokenizer.StringMessage;

public class ServerProtocolFactoryimpl <StringMessage> implements ServerProtocolFactory<StringMessage>{
	public ServerProtocol<StringMessage> create(){
		return (ServerProtocol<StringMessage>)new ServerProtocolImpl();
	}
}
/*public class GameProtocolFactory<StringMessage> implements ServerProtocolFactory<StringMessage> {

	@Override
	public ServerProtocol<StringMessage> create() {
		return (ServerProtocol<StringMessage>) new TBGProtocol();
	}

}
*/