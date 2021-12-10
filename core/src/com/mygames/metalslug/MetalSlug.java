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

	public static final int GROUND_BITS = 2;
	public static final int PLAYER_BITS = 4;
	public static final int PLAYER_SHOT_BITS = 8;
	public static final int ENEMY_BITS = 16;
	public static final int ENEMY_SHOT_BITS = 32;
	public static final int ENEMY_SENSOR_BITS = 64;
	public static final int OBJECT_BITS = 128;
	public static final int HOSTAGE_BITS = 256;
	public static final int HOSTAGE_SENSOR_BITS = 512;
	public static final int HELICOPTER_BOMB_BITS = 1024;

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
