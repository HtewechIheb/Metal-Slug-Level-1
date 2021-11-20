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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Consumer;

public class Soldier extends Enemy{
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 30f * MetalSlug.MAP_SCALE;
    private final float BODY_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;

    public enum State {
        CHATTING,
        SNEAKING,
        RUNNING,
        SCARED,
        DYING
    }

    public enum AttackMode {
        MELEE,
        WEAPON
    }

    private TextureAtlas textureAtlas;
    private Sprite sprite;
    private float bodyWidth;
    private float bodyHeight;

    private Array<Animation<TextureRegion>> chatting;
    private Animation<TextureRegion> sneaking;
    private Animation<TextureRegion> running;
    private Animation<TextureRegion> scared;
    private Animation<TextureRegion> dying;

    private int chattingIndex;

    private EnumSet<State> currentState;
    private EnumSet<State> previousState;
    private float stateTimer;
    private float deathTimer;
    private AttackMode attackMode;

    private boolean isChatting = false;
    private boolean isSneaking = false;
    private boolean isRunning = false;
    private boolean isRunningRight = false;
    private boolean isScared = false;
    private boolean isDying = false;

    private Random randomizer;



    public Soldier(MissionOneScreen screen, Vector2 position, State state, boolean isRunningRight){
        super(screen, position);

        textureAtlas = screen.getSoldierTextureAtlas();
        sprite = new Sprite();
        stateTimer = 0;
        deathTimer = 0;
        bodyWidth = 0;
        bodyHeight = 0;
        currentState = EnumSet.of(state);
        previousState = EnumSet.of(state);
        isChatting = state == State.CHATTING;
        isSneaking = state == State.SNEAKING;
        isScared = state == State.SCARED;
        isRunning = state == State.RUNNING;
        this.isRunningRight = isRunningRight;
        attackMode = AttackMode.WEAPON;
        randomizer = new Random();

        defineEnemy();
        defineAnimations();
    }

    private void defineAnimations(){
        byte i;
        Array<TextureRegion> frames = new Array<>();
        Array<TextureRegion> framesBuffer = new Array<>();

        for(i = 1; i < 13; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-%d", i))));
        }
        running = new Animation<TextureRegion>(0.06f, frames);
        frames.clear();

        for(i = 1; i < 12; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("death-1-%d", i))));
        }
        dying = new Animation<TextureRegion>(0.06f, frames);
        frames.clear();

        chatting = new Array<>();
        frames.add(new TextureRegion(textureAtlas.findRegion("chatting-1")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("chatting-2")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("chatting-3")));
        frames.addAll(framesBuffer);
        frames.add(new TextureRegion(textureAtlas.findRegion("chatting-4")));
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        chatting.add(new Animation<TextureRegion>(0.12f, frames));
        frames.clear();
        framesBuffer.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("chatting-5")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("chatting-6")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("chatting-7")));
        frames.addAll(framesBuffer);
        frames.add(new TextureRegion(textureAtlas.findRegion("chatting-8")));
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        chatting.add(new Animation<TextureRegion>(0.12f, frames));
        frames.clear();
        framesBuffer.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("chatting-9")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("chatting-10")));
        framesBuffer.add(new TextureRegion(textureAtlas.findRegion("chatting-11")));
        frames.addAll(framesBuffer);
        frames.add(new TextureRegion(textureAtlas.findRegion("chatting-12")));
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        chatting.add(new Animation<TextureRegion>(0.12f, frames));
        frames.clear();
        framesBuffer.clear();

        for(i = 1; i < 13; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("sneaking-%d", i))));
        }
        sneaking = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for(i = 1; i < 12; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("scared-%d", i))));
        }
        scared = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        bodyWidth = BODY_RECTANGLE_WIDTH;
        bodyHeight = BODY_RECTANGLE_HEIGHT;

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (bodyWidth / 2), position.y + (bodyHeight / 2));
        body = world.createBody(bodyDef);

        headShape.setRadius(BODY_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, bodyHeight / 2));
        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_SENSOR_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.PLAYER_BITS;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void update(float delta){
        currentState = getState();
        stateTimer = previousState.equals(currentState) ? stateTimer + delta : 0;

        if(isDying){
            deathTimer += delta;
            if(deathTimer >= 2f){
                remove();
            }
        }

        TextureRegion region = getFrame();
        sprite.setRegion(region);
        sprite.setBounds(0, 0, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
        setSpritePosition();

        previousState = currentState.clone();
    }

    @Override
    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    private TextureRegion getFrame(){
        TextureRegion region;

        if(currentState.contains(State.CHATTING)){
            region = chatting.get(chattingIndex).getKeyFrame(stateTimer, false);
            if(chatting.get(chattingIndex).isAnimationFinished(stateTimer)){
                stateTimer = 0;
                int randomValue = randomizer.nextInt(10);
                if(randomValue == 9){
                    chattingIndex = 2;
                }
                else if(randomValue == 8){
                    chattingIndex = 0;
                }
                else {
                    chattingIndex = 1;
                }
            }
        }
        else if(currentState.contains(State.SNEAKING)){
            region = sneaking.getKeyFrame(stateTimer, true);
        }
        else if(currentState.contains(State.SCARED)){
            region = scared.getKeyFrame(stateTimer, true);
        }
        else if(currentState.contains(State.DYING)){
            region = dying.getKeyFrame(stateTimer, false);
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

        if(currentState.contains(State.CHATTING)){
            offsetX = sprite.isFlipX() ? 10 * MetalSlug.MAP_SCALE : (-10) * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.SNEAKING)){
            offsetX = sprite.isFlipX() ? 12 * MetalSlug.MAP_SCALE : (-12) * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.SCARED)){
            offsetX = sprite.isFlipX() ? 12 * MetalSlug.MAP_SCALE : (-12) * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.RUNNING)){
            offsetX = sprite.isFlipX() ? 2 * MetalSlug.MAP_SCALE : (-2) * MetalSlug.MAP_SCALE;
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

        if(isDying){
            state.add(State.DYING);
        }
        else{
            if(isChatting){
                state.add(State.CHATTING);
            }
            if(isSneaking){
                state.add(State.SNEAKING);
            }
            if(isScared){
                state.add(State.SCARED);
            }
            if(isRunning){
                state.add(State.RUNNING);
            }
        }

        return state;
    }

    public void kill(){
        Filter filter = new Filter();
        filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.getFixtureList().forEach(fixture -> fixture.setFilterData(filter));
        isDying = true;
    }

    private void remove(){
        world.destroyBody(body);
        screen.getWorldCreator().getEnemies().removeValue(this, true);
    }

    public void setAttackMode(AttackMode attackMode){
        this.attackMode = attackMode;
    }
}
