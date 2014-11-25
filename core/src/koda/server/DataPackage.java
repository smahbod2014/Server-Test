package koda.server;

import java.io.Serializable;

public class DataPackage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6727066495302479750L;
	
	public float x = 0f;
	public float y = 0f;
	public int id = 0;
	
	public String username = "";
	
	public DataPackage(int id) {
		this.id = id;
	}
}
