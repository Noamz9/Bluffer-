package main.java.bgu.spl.general;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import main.java.bgu.spl.protocol.ServerProtocol;
import main.java.bgu.spl.protocol.protocolCallback;
import main.java.bgu.spl.tokenizer.MessageTokenizer;
import main.java.bgu.spl.tokenizer.StringMessage;

class ConnectionHandler implements Runnable {
	
	private BufferedReader in;
	private PrintWriter out;
	Socket clientSocket;
	ServerProtocol<StringMessage> protocol;
	private protocolCallback<StringMessage> protocolCall;
	
	public ConnectionHandler(Socket acceptedSocket, ServerProtocol p){
		in = null;
		out = null;
		clientSocket = acceptedSocket;
		protocol = p;
		protocolCall=new gameProtocol();
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());
	}
	
	public void run()
	{

		String msg;
		
		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 
		
		System.out.println("Connection closed - bye bye...");
		close();

	}
	
	public void process() throws IOException
	{
		String msg;
		
		while ((msg=in.readLine()) !=null)
		{
			System.out.println("Got "+msg);
			protocol.processMessage(new StringMessage(msg),protocolCall );
			if(protocol.isEnd(new StringMessage(msg))){
				break;
			}
			
		}
		close();
	}
	
	// Starts listening
	public void initialize() throws IOException
	{
		// Initialize I/O
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
		System.out.println("I/O initialized");
	}
	
	// Closes the connection
	public void close()
	{
		try {
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
			
			clientSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
	}
	/**
	 * implements protocolcallback
	 *
	 */
	private class gameProtocol implements protocolCallback<StringMessage>{

	
		/* 
		 * @param String msg- the message to pass
		 * pass the message to the client
		 */
		@Override
		public void sendMessage(StringMessage msg) throws IOException {
			out.println(msg.getMessage());
			
				
		}
		
	}
}
