package koda.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import koda.MoveAround;

public class MoveAroundDesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = MoveAround.WIDTH;
		config.height = MoveAround.HEIGHT;
		new LwjglApplication(new MoveAround(), config);
	}
}
