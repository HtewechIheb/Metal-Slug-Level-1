package com.mygames.metalslug.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.screens.MissionOneScreen;

public abstract class Hostage {
    protected MissionOneScreen screen;
    protected World world;
    protected MarcoRossi player;
    protected Vector2 position;
    protected Body body;
    protected Array<Fixture> fixtures;

    protected Hostage(MissionOneScreen screen, Vector2 position){
        this.screen = screen;
        this.world = screen.getWorld();
        this.position = position;
        this.player = screen.getPlayer();
    }

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);
}
