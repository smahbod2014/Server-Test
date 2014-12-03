package koda.server;

import java.io.Serializable;

public class ClientPackage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//public ArrayList<DataPackage> list_data;
	public int state;
	public int id;
	//public DataPackage new_user;
	//public DataPackage former_user;
	public DataPackage[] list_data;
	public int huh;
	
	
	public ClientPackage() {}
}
