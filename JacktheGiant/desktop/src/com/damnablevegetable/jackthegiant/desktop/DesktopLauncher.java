package com.damnablevegetable.jackthegiant.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.damnablevegetable.jackthegiant.GameMain;

import helpers.GameInfo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = (int)GameInfo.WIDTH;
		config.height = (int)GameInfo.HEIGHT;

		new LwjglApplication(new GameMain(), config);
	}
}
