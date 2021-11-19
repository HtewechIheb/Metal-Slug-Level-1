package com.mygames.metalslug.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

public class Hobo extends Hostage {
    private final float HEAD_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 26f * MetalSlug.MAP_SCALE;
    private final float HANGING_RECTANGLE_WIDTH = 34f * MetalSlug.MAP_SCALE;
    private final float HANGING_RECTANGLE_HEIGHT = 45f * MetalSlug.MAP_SCALE;
    private final float SITTING_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float SITTING_RECTANGLE_HEIGHT = 26f * MetalSlug.MAP_SCALE;

    public enum State {
        HANGING,
        SITTING
    }

    private TextureAtlas textureAtlas;
    private Sprite sprite;

    private Animation<TextureRegion> hanging;
    private Animation<TextureRegion> sitting;

    private EnumSet<State> currentState;
    private EnumSet<State> previousState;
    private float stateTimer;

    private boolean isRunning = false;
    private boolean isRunningRight = false;
    private boolean isHanging = false;
    private boolean isSitting = false;

    private boolean toBeDestroyed = false;

    private float bodyWidth;
    private float bodyHeight;

    public Hobo(MissionOneScreen screen, Vector2 position, State state){
        super(screen, position);

        textureAtlas = screen.getHoboTextureAtlas();
        sprite = new Sprite();
        stateTimer = 0;
        bodyWidth = 0;
        bodyHeight = 0;
        currentState = EnumSet.of(state);
        previousState = EnumSet.of(state);
        isHanging = state == State.HANGING;
        isSitting = state == State.SITTING;

        defineAnimations();
        defineHostage();
    }

    private void defineAnimations(){
        Array<TextureRegion> frames = new Array<>();
        Array<TextureRegion> framesBuffer = new Array<>();

        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-1")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-2")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-3")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-4")));
        frames.addAll(framesBuffer);
        frames.addAll(framesBuffer);
        frames.addAll(framesBuffer);
        frames.addAll(framesBuffer);

        framesBuffer.clear();
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-5")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-6")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-7")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("hanging-8")));

        frames.addAll(framesBuffer);
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-9")));
        framesBuffer.reverse();
        frames.addAll(framesBuffer);

        hanging = new Animation<TextureRegion>(0.18f, frames);
        framesBuffer.clear();
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("sitting-1")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("sitting-2")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("sitting-3")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("sitting-4")));

        frames.addAll(framesBuffer);
        frames.add(new TextureRegion(textureAtlas.findRegion("sitting-5")));
        framesBuffer.reverse();
        frames.addAll(framesBuffer);

        sitting = new Animation<TextureRegion>(0.18f, frames);
        framesBuffer.clear();
        frames.clear();
    }

    @Override
    protected void defineHostage() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        if(isHanging){
            bodyWidth = HANGING_RECTANGLE_WIDTH;
            bodyHeight = HANGING_RECTANGLE_HEIGHT;
        }
        else if(isSitting){
            bodyWidth = SITTING_RECTANGLE_WIDTH;
            bodyHeight = SITTING_RECTANGLE_HEIGHT;
        }
        else {
            bodyWidth = BODY_RECTANGLE_WIDTH;
            bodyHeight = BODY_RECTANGLE_HEIGHT;
        }

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (bodyWidth / 2), position.y + (bodyHeight / 2));
        body = world.createBody(bodyDef);

        /*headShape.setRadius(HEAD_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, bodyHeight / 2));

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);*/

        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
        fixtures = body.getFixtureList();
    }

    @Override
    public void update(float delta) {
        currentState = getState();
        stateTimer = previousState.equals(currentState) ? stateTimer + delta : 0;

        TextureRegion region = getFrame();
        sprite.setRegion(region);
        sprite.setBounds(0, 0, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
        setSpritePosition();

        previousState = currentState.clone();
    }

    private TextureRegion getFrame(){
        TextureRegion region;

        if(currentState.contains(State.HANGING)){
            region = hanging.getKeyFrame(stateTimer, true);
        }
        else if(currentState.contains(State.SITTING)){
            region = sitting.getKeyFrame(stateTimer, true);
        }
        else {
            region = sitting.getKeyFrame(stateTimer, true);
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

        if(currentState.contains(State.HANGING)){
            offsetX = sprite.isFlipX() ? 0 * MetalSlug.MAP_SCALE : 0 * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.SITTING)){
            offsetX = sprite.isFlipX() ? 10 * MetalSlug.MAP_SCALE : (-10) * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else{
            offsetX = sprite.isFlipX() ? 0 * MetalSlug.MAP_SCALE : 0 * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }

        if(sprite.isFlipX()){
            sprite.setPosition(body.getPosition().x + (bodyWidth / 2) - sprite.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, body.getPosition().y - (bodyHeight / 2) + offsetY);
        }
        else{
            sprite.setPosition(body.getPosition().x - (bodyWidth / 2)  + offsetX, body.getPosition().y - (bodyHeight / 2) + offsetY);
        }
    }

    private EnumSet<State> getState(){
        EnumSet<State> state = EnumSet.noneOf(State.class);

        if(isHanging){
            state.add(State.HANGING);
        }
        else if(isSitting){
            state.add(State.SITTING);
        }

        return state;
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
