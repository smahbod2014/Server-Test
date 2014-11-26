package koda.server;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientPackage implements Serializable {

	public ArrayList<DataPackage> list_data;
	public int state;
	public int id;
	public DataPackage new_user;
	public DataPackage former_user;
	
	public ClientPackage() {
		list_data = new ArrayList<DataPackage>();
		state = Server.RUNNING;
		new_user = null;
		former_user = null;
		this.id = -1;
	}
}
