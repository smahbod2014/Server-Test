package koda.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientData {

	public DataPackage data_package;
	public int id;
	public int state;
	public Socket socket;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;
	public String model;
}
