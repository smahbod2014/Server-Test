package koda.states;

import koda.MoveAround;
import koda.handlers.C;
import koda.ui.TextButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class MenuState extends State {

	private Array<TextButton> buttons;
	
	public MenuState(GSM gsm) {
		super(gsm);
		buttons = new Array<TextButton>();
		buttons.add(new TextButton(MoveAround.WIDTH/2, MoveAround.HEIGHT/2, 2, "Play", C.BUTTON_PLAY));
		buttons.add(new TextButton(MoveAround.WIDTH/2, MoveAround.HEIGHT/2 - 50, 2, "Quit", C.BUTTON_QUIT));
	}

	@Override
	public void handleInput() {
		mouse.x = Gdx.input.getX();
		mouse.y = Gdx.input.getY();
		camera.unproject(mouse);
		
		for (TextButton tb : buttons) {
			if(tb.contains(mouse.x, mouse.y)) {
				tb.setColor(1, 0, 0, 1);
			} else {
				tb.setColor(1, 1, 1, 1);
			}
		}
		
		if(Gdx.input.justTouched()) {
			for (TextButton tb : buttons) {
				if(tb.contains(mouse.x, mouse.y)) {
					if (tb.id == C.BUTTON_PLAY) {
						//gsm.set(new WaitingState(gsm));
						gsm.set(new PlayState(gsm));
					} else if (tb.id == C.BUTTON_QUIT) {
						Gdx.app.exit();
					}
				}
			}
		}
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(camera.combined);
		
		for (TextButton tb : buttons) {
			MoveAround.ff.render(sb, tb.text, tb.x, tb.y, tb.scale, tb.alpha);
		}
	}
}
