package com.mygames.metalslug;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygames.metalslug.screens.MissionOneScreen;

public class MetalSlug extends Game {
	public static final int V_WIDTH = 304;
	public static final int V_HEIGHT = 224;
	public static final float MAP_SCALE = 1f/100;

	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new MissionOneScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
