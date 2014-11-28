package koda.server;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientPackage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9060263190949024623L;
	//public ArrayList<DataPackage> list_data;
	public DataPackage[] list_data;
	public int state;
	public int id;
	public DataPackage new_user;
	public DataPackage former_user;
	
	public ClientPackage(final int capacity) {
		//list_data = new ArrayList<DataPackage>();
		list_data = new DataPackage[capacity];
		state = Server.RUNNING;
		new_user = null;
		former_user = null;
		this.id = -1;
	}
}
