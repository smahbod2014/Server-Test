package koda.states;

import koda.MoveAround;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class State {

	protected GSM gsm;
	protected Vector3 mouse;
	public static OrthographicCamera camera;
	
	public State(GSM gsm) {
		this.gsm = gsm;
		this.mouse = new Vector3();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, MoveAround.WIDTH, MoveAround.HEIGHT);
		camera.update();
	}
	
	public abstract void handleInput();
	public abstract void update(float dt);
	public abstract void render(SpriteBatch sb);
}
