package koda.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Entity {

	public float x;
	public float y;
	public boolean[] direction;
	public float speed;
	
	public abstract void update(float dt);
	public abstract void render(SpriteBatch sb);
}
