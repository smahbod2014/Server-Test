package koda.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private ServerSocket serverSocket;
	private Socket socket;
	
	public static void main(String[] args) throws Exception {
		new Server();
	}
	
	public Server() throws Exception {
		serverSocket = new ServerSocket(7777);
		
	}
}
