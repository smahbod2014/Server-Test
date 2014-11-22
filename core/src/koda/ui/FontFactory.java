package koda.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FontFactory {

	private final int FONT_SIZE = 16;
	private TextureRegion[][] sheet;
	private int cols;
	
	public FontFactory(TextureRegion region) {
		sheet = region.split(FONT_SIZE, FONT_SIZE);
		cols = sheet[0].length;
	}
	
	public Sprite[] getFontArray(String s) {
		Sprite[] images = new Sprite[s.length()];
		s = s.toUpperCase();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			Sprite spr = null;
			
			//c between A and Z
			if (c >= 65 && c <= 90) {
				c -= 65;
				spr = new Sprite(sheet[c / cols][c % cols]);
			} else if (c >= 49 && c <= 57) {
				c -= 49;
				c += 39;
				spr = new Sprite(sheet[c / cols][c % cols]);
			} else if (c == '0') {
				spr = new Sprite(sheet[48 / cols][48 % cols]);
			} else if (c == ' ') {
				spr = new Sprite(sheet[31 / cols][31 % cols]);
			} else if (c == '.') {
				spr = new Sprite(sheet[26 / cols][26 % cols]);
			} else if (c == '!') {
				spr = new Sprite(sheet[27 / cols][27 % cols]);
			} else if (c == '?' ){
				spr = new Sprite(sheet[28 / cols][28 % cols]);
			} else if (c == ':') {
				spr = new Sprite(sheet[29 / cols][29 % cols]);
			} else if (c == ',') {
				spr = new Sprite(sheet[30 / cols][30 % cols]);
			} else {
				spr = new Sprite(sheet[28 / cols][28 % cols]);
			}
			
			if (spr != null) {
				images[i] = spr;
			}
		}
		
		return images;
	}
	
	public float width(int length, float scale) {
		return FONT_SIZE * length * scale;
	}
	
	public void render(SpriteBatch sb, Sprite[] sprites, float x, float y, float scale, float alpha) {
		float currx = 0;
		float offx = width(sprites.length, scale) / 2;
		sb.begin();
		for (Sprite spr : sprites) {
			spr.setScale(scale, scale);
			spr.setAlpha(alpha);
			spr.setX(x - offx + width(1, scale) / 4 + currx);
			spr.setY(y - width(1, scale) / 2);
			spr.draw(sb);
			currx += width(1, scale);
		}
		sb.end();
	}
}