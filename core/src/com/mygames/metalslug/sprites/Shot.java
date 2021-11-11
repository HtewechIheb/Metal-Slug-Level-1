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
    public enum ShotType {
        PISTOL
    }

    protected Body body;

    protected ShotType type;
    protected MarcoRossi player;
    protected MissionOneScreen screen;
    protected World world;
    protected boolean playerLookingUp;
    protected boolean playerRunningRight;

    protected TextureAtlas textureAtlas;
    protected Sprite sprite;

    protected Shot(ShotType type, MissionOneScreen screen, MarcoRossi player){
        this.type = type;
        this.screen = screen;
        this.world = screen.getWorld();
        this.player = player;
        playerLookingUp = player.getIsLookingUp();
        playerRunningRight = player.getIsRunningRight();
        textureAtlas = screen.getTextureAtlas();
        sprite = new Sprite();

        defineShot();
    }

    protected abstract void defineShot();

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);

    public ShotType getType(){
        return type;
    }

    public Body getBody(){
        return body;
    }
}
