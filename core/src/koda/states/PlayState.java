package koda.states;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import koda.MoveAround;
import koda.entities.Player;
import koda.handlers.C;
import koda.server.ClientPackage;
import koda.server.DataPackage;
import koda.server.Server;
import koda.server.ServerPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayState extends State {

	public static ServerPackage server_package = new ServerPackage();
	public static Socket socket;
	public static ObjectOutputStream oos;
	public static ObjectInputStream ois;
	public static int port = 2406;
	public static String ip = "";
	
	private boolean connected = false;
	private int state = Server.RUNNING;
	private int id = -1;
	
	//private ArrayList<DataPackage> othersData = new ArrayList<DataPackage>();
	private ArrayList<Player> otherPlayers = new ArrayList<Player>();
	private Player player;
	
	private Runnable send = new Runnable() {

		@Override
		public void run() {
			
			while (socket != null && connected) {
				try {
					DataPackage dp = new DataPackage();
					dp.x = player.x;
					dp.y = player.y;
					dp.username = player.username;
					
					server_package.dp = dp;
					server_package.state = state;
					server_package.id = id;
					
					oos.writeObject(server_package);
					//oos.flush();
					
					if (state == Server.CLIENT_INITIATED_DISCONNECT) {
						connected = false;
						socket = null;
						JOptionPane.showMessageDialog(null, "Client Disconnected", "Info", JOptionPane.INFORMATION_MESSAGE);
						Gdx.app.exit();
					}
					
					
					Thread.sleep(1);
				} catch (Exception e) {}
			}
		}
	};
	
	private Runnable receive = new Runnable() {

		@Override
		public void run() {
			
			while (connected) {
				try {
					//int receive_state = (Integer) ois.readObject();
					
					ClientPackage packet = (ClientPackage) ois.readObject();
					
					
					switch (packet.state) {
					case Server.RUNNING:
						//do nothing
						break;
					case Server.DISCONNECTED_BY_SERVER:
						connected = false;
						socket = null;
						JOptionPane.showMessageDialog(null, "Disconnected by server", "Info", JOptionPane.INFORMATION_MESSAGE);
						Gdx.app.exit();
						break;
					case Server.SERVER_SHUTTING_DOWN:
						connected = false;
						socket = null;
						JOptionPane.showMessageDialog(null, "Server shutting down", "Info", JOptionPane.INFORMATION_MESSAGE);
						Gdx.app.exit();
						break;
					}
					
					if (packet.new_user != null) {
						//othersData.add(new_user);
						boolean add_user = true;
						for (int i = 0; i < otherPlayers.size(); i++) {
							if (packet.id == otherPlayers.get(i).id) {
								add_user = false;
								break;
							}
						}
						if (add_user)
							otherPlayers.add(new Player(packet.new_user.x, packet.new_user.y, packet.new_user.username, packet.id));
						//System.out.println("id " + id + " says: Added a new player (" + new_user.username + "). id = " + new_user.id);
						//System.out.println("Now there are " + otherPlayers.size() + " players");
					}
					
					if (packet.former_user != null) {
						for (int i = 0; i < otherPlayers.size(); i++) {
							if (packet.id == otherPlayers.get(i).id) {
								System.out.println("id " + id + " says: Removing player (" + packet.former_user.username + "). id = " + packet.id);
								otherPlayers.remove(i);
								i--;
								break;
							}
						}
					}
					
					
					for (int i = 0; i < packet.list_data.length; i++) {
						DataPackage dp = packet.list_data[i];
						if (dp == null) {
							continue;
						}
						
						for (int j = 0; j < otherPlayers.size(); j++) {
							if (packet.id == otherPlayers.get(j).id) {
								otherPlayers.get(j).x = dp.x;
								otherPlayers.get(j).y = dp.y;
							}
						}
					}
					
					Thread.sleep(1);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	};
	
	public PlayState(GSM gsm) {
		super(gsm);
		
		try {
			String local;
			
			try {
				local = InetAddress.getLocalHost().getHostAddress() + ":" + port;
			} catch (UnknownHostException e) {
				local = "Network Error";
			}
			
			ip = (String) JOptionPane.showInputDialog(null, "IP: ", "Info", JOptionPane.INFORMATION_MESSAGE, null, null, local);
		
			port = Integer.parseInt(ip.substring(ip.indexOf(":") + 1));
			ip = ip.substring(0, ip.indexOf(":"));
			
			socket = new Socket(ip, port);
			
			String username = System.getProperty("user.name");
			username = (String) JOptionPane.showInputDialog(null, "Username: ", "Info", JOptionPane.INFORMATION_MESSAGE, null, null, username);
		
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(username);
			
			ois = new ObjectInputStream(socket.getInputStream());
			String response = (String) ois.readObject();

			System.out.println("Got the response! " + response);
			
			
			ClientPackage packet = (ClientPackage) ois.readObject();
			this.id = packet.id;
			
			System.out.println("Got the packet! id is " + id);
			
			response += " (ID: " + id + ")";
			
			for (int i = 0; i < packet.list_data.length; i++) {
				DataPackage dp = packet.list_data[i];
				if (dp == null) {
					continue;
				}
				Player p = new Player(dp.x, dp.y, dp.username, dp.id);
				otherPlayers.add(p);
			}
			
			
			
			
			player = new Player(MoveAround.WIDTH / 2, MoveAround.HEIGHT / 2, username, id);
		
			connected = true;
			state = Server.RUNNING;
			
			new Thread(send).start();
			new Thread(receive).start();
			
			
			JOptionPane.showMessageDialog(null, response, "Message", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			Gdx.app.exit();
		}
		
		
	}

	@Override
	public void handleInput() {
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			player.direction[C.DIRECTION_LEFT] = true;
		} else {
			player.direction[C.DIRECTION_LEFT] = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			player.direction[C.DIRECTION_DOWN] = true;
		} else {
			player.direction[C.DIRECTION_DOWN] = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			player.direction[C.DIRECTION_RIGHT] = true;
		} else {
			player.direction[C.DIRECTION_RIGHT] = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			player.direction[C.DIRECTION_UP] = true;
		} else {
			player.direction[C.DIRECTION_UP] = false;
		}
	}

	@Override
	public void update(float dt) {
		/*while (othersData.size() > otherPlayers.size()) {
			DataPackage dp = othersData.get(othersData.size() - 1);
			otherPlayers.add(new Player(dp.x, dp.y, dp.username, dp.id));
		}
		
		while (othersData.size() < otherPlayers.size()) {
			//find the missing id!!!
			otherPlayers.remove(getDisconnectedPlayer());
		}*/
		
		for (int i = 0; i < otherPlayers.size(); i++) {
			Player p = otherPlayers.get(i);
			if (player.id != p.id) {
				p.update(dt);
			}
		}
		
		if (player != null)
			player.update(dt);
	}

	@Override
	public void render(SpriteBatch sb) {
		for (int i = 0; i < otherPlayers.size(); i++) {
			Player p = otherPlayers.get(i);
			if (player.id != p.id) {
				p.render(sb, new Color(1, 1, 0, 1));
			}
		}
		
		if (player != null)
			player.render(sb, new Color(1, 0, 0, 1));
		
	}
}
