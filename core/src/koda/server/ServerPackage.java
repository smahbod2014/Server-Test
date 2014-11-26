package koda.server;

import java.io.Serializable;

public class ServerPackage implements Serializable {

	public DataPackage dp;
	public int state;
	
	public ServerPackage() {
		dp = null;
		state = Server.RUNNING;
	}
}
