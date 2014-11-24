package koda.states;

import koda.entities.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class PlayState extends State {

	private Array<Player> players;
	
	public PlayState(GSM gsm) {
		super(gsm);
		players = new Array<Player>();
		System.out.println("Entered play state");
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float dt) {
		if (players == null) return;
		for (Player p : players) {
			p.update(dt);
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		if (players == null) {
			return;
		}
		if (players.get(0) != null) {
			players.get(0).sr.setColor(1, 0, 0, 1);
			players.get(0).render(sb);
		}
		
		if (players.size > 1 && players.get(1) != null) {
			players.get(1).sr.setColor(0, 0, 1, 1);
			players.get(1).render(sb);
		}
	}
}
