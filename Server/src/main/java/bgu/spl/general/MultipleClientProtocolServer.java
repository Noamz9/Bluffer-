package main.java.bgu.spl.general;

import java.io.*;
import java.net.*;
//
//interface ServerProtocol {
//	
//	String processMessage(String msg);
//	boolean isEnd(String msg);
//	
//}

import main.java.bgu.spl.protocol.ServerProtocol;
import main.java.bgu.spl.protocol.ServerProtocolFactory;
import main.java.bgu.spl.protocol.ServerProtocolFactoryimpl;
import main.java.bgu.spl.protocol.protocolCallback;


class MultipleClientProtocolServer implements Runnable {
	private ServerSocket serverSocket;
	private int listenPort;
	private ServerProtocolFactoryimpl factory;
	
	
	public MultipleClientProtocolServer(int port, ServerProtocolFactoryimpl p)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (true)
		{
			try {
			ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
            new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
	}
	

	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}

	public static void main(String[] args) throws IOException
	{
		// Get port
		int port = Integer.decode(args[0]).intValue();
		
		MultipleClientProtocolServer server = new MultipleClientProtocolServer(port, new ServerProtocolFactoryimpl());
		Thread serverThread = new Thread(server);
      serverThread.start();
		try {
			serverThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
		
		
				
	}
}
