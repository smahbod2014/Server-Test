package koda.entities;

import koda.MoveAround;
import koda.handlers.C;
import koda.ui.TextButton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Player extends Entity {

	public String username;
	public int id;
	private TextButton name;
	
	public Player(float x, float y, String username, int id) {
		this.x = x;
		this.y = y;
		speed = 200;
		direction = new boolean[4];
		this.username = username;
		this.id = id;
		name = new TextButton(x - 20, y + 15, .85f, username, C.UNDEFINED);
	}
	
	private void updateMovement(float dt) {
		if (direction[C.DIRECTION_LEFT]) {
			x -= dt * speed;
		}
		
		if (direction[C.DIRECTION_DOWN]) {
			y -= dt * speed;
		}
		
		if (direction[C.DIRECTION_RIGHT]) {
			x += dt * speed;
		}
		
		if (direction[C.DIRECTION_UP]) {
			y += dt * speed;
		}
		
		name.x = x - 20;
		name.y = y + 15;
	}
	
	@Override
	public void update(float dt) {
		updateMovement(dt);
	}

	@Override
	public void render(SpriteBatch sb) {
		render(sb, new Color(1, 0, 0, 1));
	}
	
	public void render(SpriteBatch sb, Color c) {
		MoveAround.sr.begin(ShapeType.Filled);
		MoveAround.sr.setColor(c);
		int radius = 20;
		MoveAround.sr.circle(x - radius, y - radius, radius);
		MoveAround.sr.end();
		
		MoveAround.ff.render(sb, name.text, name.x, name.y, name.scale, name.alpha);
	}
}
