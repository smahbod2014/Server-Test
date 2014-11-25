package koda;

import koda.handlers.Resources;
import koda.states.GSM;
import koda.states.MenuState;
import koda.ui.FontFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MoveAround extends ApplicationAdapter {
	
	public static int WIDTH = 720;
	public static int HEIGHT = 480;
	public static Resources res;
	public static FontFactory ff;
	public static ShapeRenderer sr;
	
	private SpriteBatch sb;
	private GSM gsm;
	
	@Override
	public void create () {
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		loadResources();
		ff = new FontFactory(res.getTexture("font"));
		gsm = new GSM();
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.peek().handleInput();
		gsm.peek().update(Gdx.graphics.getDeltaTime());
		gsm.peek().render(sb);
	}
	
	private void loadResources() {
		res = new Resources();
		res.loadTexture("font", "fonts/font1_16.png");
	}
}
