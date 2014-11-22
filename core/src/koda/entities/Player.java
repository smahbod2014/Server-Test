package koda.entities;

import koda.handlers.C;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Player extends Entity {

	public Player(float x, float y) {
		this.x = x;
		this.y = y;
		speed = 100;
		sr = new ShapeRenderer();
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
	}
	
	@Override
	public void update(float dt) {
		updateMovement(dt);
	}

	@Override
	public void render(SpriteBatch sb) {
		sr.begin(ShapeType.Filled);
		int radius = 10;
		sr.circle(x - radius, y - radius, radius);
		sr.end();
	}
}
