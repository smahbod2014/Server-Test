package koda.states;

import koda.MoveAround;
import koda.handlers.C;
import koda.ui.TextButton;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WaitingState extends State {

	private TextButton tb;
	
	public WaitingState(GSM gsm) {
		super(gsm);
		tb = new TextButton(MoveAround.WIDTH/2, MoveAround.HEIGHT/2, 1, "Waiting for other user...", C.UNDEFINED);
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(SpriteBatch sb) {
		MoveAround.ff.render(sb, tb.text, tb.x, tb.y, tb.scale, tb.alpha);
	}
}
