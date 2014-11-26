package koda.server;

import java.net.Socket;

public class ClientData {

	public DataPackage data_package;
	public int state;
	public Socket socket;
	
	public ClientData(DataPackage data_package, int state, Socket socket) {
		this.data_package = data_package;
		this.state = state;
		this.socket = socket;
	}
}
