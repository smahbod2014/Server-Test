package koda.handlers;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Resources {

	private HashMap<String, TextureRegion> textures;
	
	public Resources() {
		textures = new HashMap<String, TextureRegion>();
	}
	
	public void loadTexture(String key, String path) {
		TextureRegion tr = new TextureRegion(new Texture(Gdx.files.internal(path)));
		textures.put(key, tr);
	}
	
	public void loadPartitionedTexture(String key, String path, int width, int height, int row, int col) {
		TextureRegion tr = new TextureRegion(new Texture(Gdx.files.internal(path)));
		TextureRegion[][] sheet = tr.split(width, height);
		textures.put(key, sheet[row][col]);
	}
	
	public TextureRegion getTexture(String key) {
		return textures.get(key);
	}
}
