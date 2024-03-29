package com.mygames.metalslug.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

public class PistolShot extends Shot {
    private final float SHOT_WIDTH = 12f * MetalSlug.MAP_SCALE;
    private final float SHOT_HEIGHT = 6f * MetalSlug.MAP_SCALE;

    private boolean toBeDestoryed = false;

    public PistolShot(MissionOneScreen screen, MarcoRossi player){
        super(screen, player);

        defineShot();

        sprite.setRegion(new TextureRegion(textureAtlas.findRegion("pistol-shot")));
        if(playerLookingUp){
            sprite.setBounds(body.getPosition().x + SHOT_HEIGHT / 2, body.getPosition().y - SHOT_WIDTH / 2, SHOT_WIDTH, SHOT_HEIGHT);
            sprite.rotate(90f);
        }
        else {
            sprite.setBounds(body.getPosition().x - SHOT_WIDTH / 2, body.getPosition().y - SHOT_HEIGHT / 2, SHOT_WIDTH, SHOT_HEIGHT);
        }

        if(playerLookingUp){
            body.setLinearVelocity(new Vector2(0f, 3f));
        }
        else if(playerRunningRight){
            body.setLinearVelocity(new Vector2(3f, 0));
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

        if(playerLookingUp){
            if(playerRunningRight){
                bodyDef.position.set(player.getShotX() + (SHOT_HEIGHT / 4), player.getShotY());
            }
            else {
                bodyDef.position.set(player.getShotX() - (SHOT_HEIGHT / 4), player.getShotY());
            }
        }
        else {
            bodyDef.position.set(player.getShotX(), player.getShotY() - (SHOT_HEIGHT / 2));
        }
        bodyDef.gravityScale = 0f;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(SHOT_WIDTH / 2, SHOT_HEIGHT / 2);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = MetalSlug.PLAYER_SHOT_BITS;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    private void remove(){
        world.destroyBody(body);
        screen.getWorldCreator().getShots().removeValue(this, true);
    }

    @Override
    public void update(float delta){
        if(toBeDestoryed ||
                body.getPosition().x > screen.getCamera().position.x + (screen.getCamera().viewportWidth / 2) ||
                body.getPosition().x + SHOT_WIDTH < screen.getCamera().position.x - (screen.getCamera().viewportWidth / 2) ||
                body.getPosition().y > screen.getCamera().position.y + (screen.getCamera().viewportHeight / 2)){
            remove();
        }
        else {
            if(playerLookingUp){
                sprite.setPosition(body.getPosition().x + SHOT_HEIGHT / 2, body.getPosition().y - SHOT_WIDTH / 2);
            }
            else {
                sprite.setPosition(body.getPosition().x - SHOT_WIDTH / 2, body.getPosition().y - SHOT_HEIGHT / 2);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    @Override
    public void destroy(){
        toBeDestoryed = true;
    }

    @Override
    public void dispose(){

    }
}
