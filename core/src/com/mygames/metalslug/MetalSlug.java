package com.mygames.metalslug;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygames.metalslug.screens.WelcomeScreen;

public class MetalSlug extends Game {
	public static final int V_WIDTH = 304;
	public static final int V_HEIGHT = 224;
	public static final float MAP_SCALE = 1f/100;

	public static final int GROUND_BITS = 2;
	public static final int PLAYER_BITS = 4;
	public static final int PLAYER_SHOT_BITS = 8;
	public static final int ENEMY_BITS = 16;
	public static final int ENEMY_SENSOR_BITS = 32;
	public static final int OBJECT_BITS = 64;
	public static final int HOSTAGE_BITS = 128;
	public static final int HOSTAGE_SENSOR_BITS = 256;
	public static final int HELICOPTER_BOMB_BITS = 512;

	public SpriteBatch batch;
	private AssetManager assetManager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		assetManager.load("audio/music/missionone.mp3", Music.class);
		assetManager.load("audio/sounds/missionone_start.mp3", Sound.class);
		assetManager.load("audio/sounds/mission_complete.mp3", Sound.class);
		assetManager.load("audio/sounds/pistol_shot.mp3", Sound.class);
		assetManager.load("audio/sounds/player_death.mp3", Sound.class);
		assetManager.load("audio/sounds/soldier_death.mp3", Sound.class);
		assetManager.load("audio/sounds/soldier_scared.mp3", Sound.class);
		assetManager.load("audio/sounds/hostage_thankyou.mp3", Sound.class);
		assetManager.load("audio/sounds/bomb_detonation.mp3", Sound.class);
		assetManager.load("audio/sounds/helicopter_explosion.mp3", Sound.class);
		assetManager.load("audio/sounds/button_press.wav", Sound.class);
		assetManager.load("audio/sounds/game_won.mp3", Sound.class);
		assetManager.load("audio/sounds/game_over.mp3", Sound.class);
		assetManager.finishLoading();
		setScreen(new WelcomeScreen(this, assetManager));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assetManager.dispose();
	}
}
