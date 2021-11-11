package com.mygames.metalslug.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

public class PistolShot extends Shot {
    private final float SHOT_WIDTH = 12f * MetalSlug.MAP_SCALE;
    private final float SHOT_HEIGHT = 6f * MetalSlug.MAP_SCALE;

    public PistolShot(MissionOneScreen screen, MarcoRossi player){
        super(ShotType.PISTOL, screen, player);

        sprite.setRegion(new TextureRegion(textureAtlas.findRegion("pistol-shot")));
        sprite.setBounds(0, 0, SHOT_WIDTH, SHOT_HEIGHT);

        if(playerLookingUp){
            sprite.rotate(90f);
            body.setLinearVelocity(new Vector2(0f, 3f));
        }
        else if(playerRunningRight){
            body.setLinearVelocity(new Vector2(0f, 0));
        }
        else{
            body.setLinearVelocity(new Vector2(-3f, 0));
        }
    }

    protected void defineShot(){
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        if(playerLookingUp){
            bodyDef.angle = 90 * MathUtils.degreesToRadians;
        }

        bodyDef.position.set(player.getShotX(), player.getShotY());
        bodyDef.gravityScale = 0f;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(SHOT_WIDTH / 2, SHOT_HEIGHT / 2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
    }

    public void update(float delta){
        if(playerLookingUp){
            sprite.setPosition(body.getPosition().x + sprite.getHeight() / 2, body.getPosition().y - sprite.getWidth() / 2);
        }
        else {
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        }
    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }
}
