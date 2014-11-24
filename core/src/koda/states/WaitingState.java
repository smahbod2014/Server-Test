package koda.states;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import koda.MoveAround;
import koda.handlers.C;
import koda.ui.TextButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WaitingState extends State {

	public static Socket socket;
	public static int port = 2406;
	public static String ip = "";
	private TextButton tb;
	
	public WaitingState(GSM gsm) {
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
		
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(username);
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			String response = (String) ois.readObject();
			
			JOptionPane.showMessageDialog(null, response, "Message", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
			Gdx.app.exit();
		}
		
		
		
		
		
		
		
		
		
		
		tb = new TextButton(MoveAround.WIDTH/2, MoveAround.HEIGHT/2, 1, "Waiting for other user...", C.UNDEFINED);
		//gsm.push(new PlayState(gsm));
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float dt) {
		//gsm.set(new PlayState(gsm));
	}

	@Override
	public void render(SpriteBatch sb) {
		MoveAround.ff.render(sb, tb.text, tb.x, tb.y, tb.scale, tb.alpha);
	}
}
