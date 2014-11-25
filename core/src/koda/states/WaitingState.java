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

	
	private TextButton tb;
	
	public WaitingState(GSM gsm) {
		super(gsm);
		
		
		
		
		
		
		
		
		
		
		
		tb = new TextButton(MoveAround.WIDTH/2, MoveAround.HEIGHT/2, 1, "Waiting for other user...", C.UNDEFINED);
		//gsm.push(new PlayState(gsm));
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float dt) {
		gsm.set(new PlayState(gsm));
	}

	@Override
	public void render(SpriteBatch sb) {
		MoveAround.ff.render(sb, tb.text, tb.x, tb.y, tb.scale, tb.alpha);
	}
}
