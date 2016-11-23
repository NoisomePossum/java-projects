package com.damnablevegetable.jackthegiant;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import scenes.Gameplay;

public class GameMain extends Game {

	private SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new Gameplay(this));
	}

	@Override
	public void render () {
		super.render();
	}

//	Allows other classes to access and use the SpriteBatch
//	so that we can avoid creating more than one.
	public SpriteBatch getBatch() {
		return this.batch;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
