package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.EnumSet;

public class Soldier extends Enemy{
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 26f * MetalSlug.MAP_SCALE;
    private final float BODY_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;

    public enum State {
        RUNNING,
        DYING
    }

    private TextureAtlas textureAtlas;
    private Sprite sprite;

    private Animation<TextureRegion> running;
    private Animation<TextureRegion> dying;

    private EnumSet<State> currentState;
    private EnumSet<State> previousState;
    private float stateTimer;
    private float deathTimer;

    private boolean isRunning;
    private boolean isRunningRight;

    private boolean toBeDestroyed;
    private boolean isDying;
    private boolean dead;

    public Soldier(MissionOneScreen screen, Vector2 position){
        super(screen, position);

        textureAtlas = screen.getSoldierTextureAtlas();
        sprite = new Sprite();
        stateTimer = 0;
        deathTimer = 0;
        currentState = EnumSet.of(State.RUNNING);
        previousState = EnumSet.of(State.RUNNING);
        //isRunning = false;
        isRunningRight = false;
        toBeDestroyed = false;
        isDying = false;
        dead = false;

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(textureAtlas.findRegion("running-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-10")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-11")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-12")));
        running = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-10")));
        frames.add(new TextureRegion(textureAtlas.findRegion("death-1-11")));
        dying = new Animation<TextureRegion>(0.06f, frames);
        frames.clear();
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (BODY_RECTANGLE_WIDTH / 2), position.y + (BODY_RECTANGLE_HEIGHT / 2));
        body = world.createBody(bodyDef);

        headShape.setRadius(BODY_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, BODY_RECTANGLE_HEIGHT / 2));
        bodyShape.setAsBox(BODY_RECTANGLE_WIDTH / 2, BODY_RECTANGLE_HEIGHT / 2);

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void update(float delta){
        if(dead){
            deathTimer += delta;
            if(deathTimer >= 1f){
                remove();
            }
        }
        else{
            currentState = getState();
            stateTimer = previousState.equals(currentState) ? stateTimer + delta : 0;

            if(!toBeDestroyed){
                isRunning = true;
            }

            TextureRegion region = getFrame();
            sprite.setRegion(region);
            sprite.setBounds(0, 0, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
            setSpritePosition();

            previousState = currentState.clone();
        }
    }

    private TextureRegion getFrame(){
        TextureRegion region;

        if(currentState.contains(State.DYING)){
            region = dying.getKeyFrame(stateTimer, false);
            if(dying.isAnimationFinished(stateTimer)){
                dead = true;
            }
        }
        else if(currentState.contains(State.RUNNING)){
            region = running.getKeyFrame(stateTimer, true);
        }
        else {
            region = running.getKeyFrame(stateTimer, true);
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !region.isFlipX()){
            region.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && region.isFlipX()){
            region.flip(true, false);
        }

        return region;
    }

    private void setSpritePosition(){
        float offsetX;
        float offsetY;

        if(currentState.contains(State.RUNNING)){
            offsetX = sprite.isFlipX() ? 0 * MetalSlug.MAP_SCALE : 0 * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else{
            offsetX = sprite.isFlipX() ? 0 * MetalSlug.MAP_SCALE : 0 * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }

        if(sprite.isFlipX()){
            sprite.setPosition(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - sprite.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + offsetY);
        }
        else{
            sprite.setPosition(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + offsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + offsetY);
        }
    }

    private EnumSet<State> getState(){
        EnumSet<State> state = EnumSet.noneOf(State.class);

        if(isDying){
            state.add(State.DYING);
        }
        else{
            if(isRunning){
                state.add(State.RUNNING);
            }
        }

        return state;
    }

    @Override
    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void kill(){
        isDying = true;
    }

    private void remove(){
        world.destroyBody(body);
        screen.getWorldCreator().getEnemies().removeValue(this, true);
    }
}
