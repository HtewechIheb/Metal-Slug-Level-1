package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

public abstract class Shot {
    protected Body body;
    protected MarcoRossi player;
    protected MissionOneScreen screen;
    protected World world;

    protected boolean playerLookingUp;
    protected boolean playerRunningRight;

    protected TextureAtlas textureAtlas;
    protected Sprite sprite;

    protected Shot(MissionOneScreen screen, MarcoRossi player){
        this.screen = screen;
        this.world = screen.getWorld();
        this.player = player;
        playerLookingUp = player.getIsLookingUp();
        playerRunningRight = player.getIsRunningRight();
        textureAtlas = screen.getShotsTextureAtlas();
        sprite = new Sprite();
    }

    protected abstract void defineShot();

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);

    public abstract void destroy();
}
