package com.mygames.metalslug.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygames.metalslug.screens.MissionOneScreen;

public abstract class Enemy {
    protected MissionOneScreen screen;
    protected World world;
    protected Vector2 position;
    protected Body body;
    protected MarcoRossi player;

    protected Enemy(MissionOneScreen screen, Vector2 position){
        this.screen = screen;
        this.world = screen.getWorld();
        this.position = position;
        this.player = screen.getPlayer();
    }

    protected abstract void defineEnemy();

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);

    public abstract void kill();

    protected void remove(){
        world.destroyBody(body);
        screen.getWorldCreator().getEnemies().removeValue(this, true);
    }

    protected void move(Vector2 vector){
        body.applyLinearImpulse(vector, body.getWorldCenter(), true);
    }

    protected void stop(boolean stopX, boolean stopY){
        body.setLinearVelocity(new Vector2(stopX ? 0 : body.getLinearVelocity().x, stopY ? 0 : body.getLinearVelocity().y));
    }
}
