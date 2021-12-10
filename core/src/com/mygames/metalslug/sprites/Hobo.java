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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Stack;

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
        SITTING,
        WAITING,
        SALUTING,
        SHORTING,
        FLEEING
    }

    private TextureAtlas textureAtlas;
    private Sprite sprite;
    private Sprite releaseSprite;

    private Animation<TextureRegion> hanging;
    private Animation<TextureRegion> sitting;
    private Animation<TextureRegion> fleeing;
    private Animation<TextureRegion> hangingReleased;
    private Animation<TextureRegion> sittingReleased;
    private Animation<TextureRegion> waiting;
    private Animation<TextureRegion> shorts;
    private Animation<TextureRegion> salute;

    private State currentState;
    private State previousState;
    private State capturedState;
    private Stack<State> stateStack;
    private float stateTimer;
    private float releaseStateTimer;

    private boolean isRunningRight = false;
    private boolean toBeSaved = false;
    private boolean toBeReleased = false;
    private boolean releaseAnimationPlaying = false;
    private boolean released = false;

    private float bodyWidth;
    private float bodyHeight;

    public Hobo(MissionOneScreen screen, Vector2 position, State state){
        super(screen, position);

        textureAtlas = screen.getHoboTextureAtlas();
        sprite = new Sprite();
        releaseSprite = new Sprite();
        resetFrameTimer();
        releaseStateTimer = 0;
        bodyWidth = 0;
        bodyHeight = 0;
        currentState = state;
        previousState = state;
        capturedState = state;
        stateStack = new Stack<>();
        stateStack.push(currentState);

        defineAnimations();
        defineCapturedHostage();
    }

    private void defineAnimations(){
        byte i;
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

        for(i = 1; i < 9; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("fleeing-%d", i))));
        }
        fleeing = new Animation<TextureRegion>(0.18f, frames);
        frames.clear();

        for(i = 1; i < 11; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("hanging-released-%d", i))));
        }
        hangingReleased = new Animation<TextureRegion>(0.18f, frames);
        frames.clear();

        for(i = 1; i < 11; i++){
            framesBuffer.add(new TextureRegion(textureAtlas.findRegion(String.format("salute-%d", i))));
        }
        frames.addAll(framesBuffer);
        for(i = 0; i < 10; i++){
            frames.add(frames.get(frames.size - 1));
        }
        for(i = 11; i < 15; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("salute-%d", i))));
        }
        salute = new Animation<TextureRegion>(0.08f, frames);
        framesBuffer.clear();
        frames.clear();

        for(i = 1; i < 13; i++){
            framesBuffer.add(new TextureRegion(textureAtlas.findRegion(String.format("shorts-%d", i))));
        }
        frames.addAll(framesBuffer);
        for(i = 0; i < 10; i++){
            frames.add(frames.get(frames.size - 1));
        }
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        shorts = new Animation<TextureRegion>(0.08f, frames);
        framesBuffer.clear();
        frames.clear();

        for(i = 1; i < 5; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("sitting-released-%d", i))));
        }
        sittingReleased = new Animation<TextureRegion>(0.18f, frames);
        frames.clear();

        for(i = 1; i < 13; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("waiting-%d", i))));
        }
        waiting = new Animation<TextureRegion>(0.18f, frames);
        frames.clear();
    }

    protected void defineCapturedHostage() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        if(stateStack.peek() == State.HANGING){
            bodyWidth = HANGING_RECTANGLE_WIDTH;
            bodyHeight = HANGING_RECTANGLE_HEIGHT;
        }
        else if(stateStack.peek() == State.SITTING){
            bodyWidth = SITTING_RECTANGLE_WIDTH;
            bodyHeight = SITTING_RECTANGLE_HEIGHT;
        }

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (bodyWidth / 2), position.y + (bodyHeight / 2));
        body = world.createBody(bodyDef);
        body.setActive(false);

        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.PLAYER_SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
        fixtures = body.getFixtureList();
    }

    private void defineFreeHostage() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        bodyWidth = BODY_RECTANGLE_WIDTH;
        bodyHeight = BODY_RECTANGLE_HEIGHT;

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (bodyWidth / 2), position.y + (bodyHeight / 2));
        body = world.createBody(bodyDef);
        body.setActive(false);

        headShape.setRadius(HEAD_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, bodyHeight / 2));

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_SENSOR_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.PLAYER_BITS;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;
        currentState = stateStack.peek();

        if(body.getPosition().x - player.getBody().getPosition().x < 192 * MetalSlug.MAP_SCALE){
            body.setActive(true);
        }

        if(toBeReleased){
            world.destroyBody(body);
            resetFrameTimer();
            releaseAnimationPlaying = true;
            toBeReleased = false;
            capturedState = currentState;
        }

        if(releaseAnimationPlaying){
            TextureRegion region = getReleaseFrame();
            releaseSprite.setRegion(region);
            setReleaseSpritePosition();
            releaseStateTimer += delta;
        }

        TextureRegion region;
        float offsetX = 0;
        float offsetY = 0;

        switch (currentState){
            case HANGING:
                region = hanging.getKeyFrame(stateTimer, true);
                break;
            case SITTING:
                region = sitting.getKeyFrame(stateTimer, true);

                offsetX = sprite.isFlipX() ? 10 * MetalSlug.MAP_SCALE : (-10) * MetalSlug.MAP_SCALE;
                break;
            case WAITING:
                if(toBeSaved){
                    stop(true, false);
                    resetFrameTimer();
                    setState(State.SALUTING);
                }
                else if(isRunningRight && body.getLinearVelocity().x <= 0.2f){
                    move(new Vector2(0.1f, 0));
                }
                else if(!isRunningRight && body.getLinearVelocity().x >= (-0.2f)){
                    move(new Vector2(-0.1f, 0));
                }

                region = waiting.getKeyFrame(stateTimer, true);
                if(waiting.isAnimationFinished(stateTimer)){
                    isRunningRight = !isRunningRight;
                    resetFrameTimer();
                }

                offsetX = sprite.isFlipX() ? 12 * MetalSlug.MAP_SCALE : (-12) * MetalSlug.MAP_SCALE;
                break;
            case SALUTING:
                region = salute.getKeyFrame(stateTimer, false);
                if(salute.isAnimationFinished(stateTimer)){
                    resetFrameTimer();
                    setState(State.SHORTING);
                }

                offsetX = sprite.isFlipX() ? 10 * MetalSlug.MAP_SCALE : (-10) * MetalSlug.MAP_SCALE;
                break;
            case SHORTING:
                region = shorts.getKeyFrame(stateTimer, false);
                if(shorts.isAnimationFinished(stateTimer)){
                    resetFrameTimer();
                    setState(State.FLEEING);
                }

                offsetX = sprite.isFlipX() ? 4 * MetalSlug.MAP_SCALE : (-4) * MetalSlug.MAP_SCALE;
                break;
            case FLEEING:
            default:
                if(body.getLinearVelocity().x >= (-0.5f)){
                    move(new Vector2(-0.1f, 0));
                }
                if(player.getBody().getPosition().x - body.getPosition().x  > 192 * MetalSlug.MAP_SCALE){
                    remove();
                }

                region = fleeing.getKeyFrame(stateTimer, true);
                offsetX = sprite.isFlipX() ? 12 * MetalSlug.MAP_SCALE : (-12) * MetalSlug.MAP_SCALE;
                break;
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !region.isFlipX()){
            region.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && region.isFlipX()){
            region.flip(true, false);
        }

        sprite.setRegion(region);

        if(sprite.isFlipX()){
            sprite.setBounds(body.getPosition().x + (bodyWidth / 2) - sprite.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, body.getPosition().y - (bodyHeight / 2) + offsetY, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
        }
        else{
            sprite.setBounds(body.getPosition().x - (bodyWidth / 2)  + offsetX, body.getPosition().y - (bodyHeight / 2) + offsetY, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
        }
    }

    private TextureRegion getReleaseFrame(){
        TextureRegion region;

        if(capturedState == State.HANGING){
            region = hangingReleased.getKeyFrame(releaseStateTimer, false);
            if(hangingReleased.getKeyFrameIndex(releaseStateTimer) == 2 && !released){
                defineFreeHostage();
                setState(State.WAITING);
                released = true;
            }
            else if(hangingReleased.isAnimationFinished(releaseStateTimer)){
                releaseAnimationPlaying = false;
                releaseStateTimer = 0;
            }
        }
        else {
            region = sittingReleased.getKeyFrame(releaseStateTimer, false);
            if(sittingReleased.getKeyFrameIndex(releaseStateTimer) == 2 && !released){
                defineFreeHostage();
                setState(State.WAITING);
                released = true;
            }
            else if(sittingReleased.isAnimationFinished(releaseStateTimer)){
                releaseAnimationPlaying = false;
                releaseStateTimer = 0;
            }
        }

        if(!isRunningRight && !region.isFlipX()){
            region.flip(true, false);
        }
        else if(isRunningRight && region.isFlipX()){
            region.flip(true, false);
        }

        return region;
    }

    private void setReleaseSpritePosition(){
        float offsetX;
        float offsetY;

        if(currentState == State.HANGING){
            offsetX = releaseSprite.isFlipX() ? HANGING_RECTANGLE_WIDTH + 9 * MetalSlug.MAP_SCALE : -(9) * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else {
            offsetX = releaseSprite.isFlipX() ? HANGING_RECTANGLE_WIDTH + 11 * MetalSlug.MAP_SCALE : -(HANGING_RECTANGLE_WIDTH + 11) * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }

        if(releaseSprite.isFlipX()){
            releaseSprite.setBounds(position.x - releaseSprite.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, position.y + offsetY, releaseSprite.getRegionWidth() * MetalSlug.MAP_SCALE, releaseSprite.getRegionHeight() * MetalSlug.MAP_SCALE);
        }
        else{
            releaseSprite.setBounds(position.x + offsetX, position.y + offsetY, releaseSprite.getRegionWidth() * MetalSlug.MAP_SCALE, releaseSprite.getRegionHeight() * MetalSlug.MAP_SCALE);
        }
    }

    private void remove(){
        world.destroyBody(body);
        screen.getWorldCreator().getHostages().removeValue(this, true);
    }

    private void move(Vector2 vector){
        body.applyLinearImpulse(vector, body.getWorldCenter(), true);
    }

    private void stop(boolean stopX, boolean stopY){
        body.setLinearVelocity(new Vector2(stopX ? 0 : body.getLinearVelocity().x, stopY ? 0 : body.getLinearVelocity().y));
    }

    private void resetFrameTimer(){
        stateTimer = 0;
    }

    private void setState(State state){
        stateStack.pop();
        stateStack.push(state);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if(releaseAnimationPlaying){
            releaseSprite.draw(batch);
        }
        if(!releaseAnimationPlaying || released){
            sprite.draw(batch);
        }
    }

    public void release(){
        toBeReleased = true;
    }

    public void save(){
        toBeSaved = true;
    }
}
