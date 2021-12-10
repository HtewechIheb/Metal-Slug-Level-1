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
import java.util.Stack;
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
        KNIFING,
        FLEEING,
        IDLING,
        SEARCHING,
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
    private Animation<TextureRegion> knifing;
    private Animation<TextureRegion> fleeing;
    private Animation<TextureRegion> idling;
    private Animation<TextureRegion> searching;
    private Animation<TextureRegion> dying;

    private int chattingIndex;

    private State currentState;
    private State previousState;
    private Stack<State> stateStack;
    private float stateTimer;
    private float deathTimer;
    private AttackMode attackMode;

    private boolean isRunningRight = false;
    private boolean isDying = false;
    private boolean collidingWithPlayer = false;

    private Random randomizer;


    public Soldier(MissionOneScreen screen, Vector2 position, State state, boolean isRunningRight){
        super(screen, position);

        textureAtlas = screen.getSoldierTextureAtlas();
        sprite = new Sprite();
        stateTimer = 0;
        deathTimer = 0;
        bodyWidth = 0;
        bodyHeight = 0;
        currentState = state;
        previousState = state;
        stateStack = new Stack<>();
        stateStack.push(currentState);
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

        for(i = 1; i < 14; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("scared-%d", i))));
        }
        scared = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(i = 1; i < 13; i++){
            framesBuffer.add(new TextureRegion(textureAtlas.findRegion(String.format("knifing-%d", i))));
        }
        frames.addAll(framesBuffer);
        framesBuffer.removeIndex(0);
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        knifing = new Animation<TextureRegion>(0.1f, frames);
        framesBuffer.clear();
        frames.clear();

        for(i = 1; i < 13; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("fleeing-%d", i))));
        }
        fleeing = new Animation<TextureRegion>(0.08f, frames);
        frames.clear();

        for(i = 1; i < 5; i++){
            framesBuffer.add(new TextureRegion(textureAtlas.findRegion(String.format("idle-%d", i))));
        }
        frames.addAll(framesBuffer);
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        idling = new Animation<TextureRegion>(0.15f, frames);
        framesBuffer.clear();
        frames.clear();

        for(i = 1; i < 6; i++){
            framesBuffer.add(new TextureRegion(textureAtlas.findRegion(String.format("searching-%d", i))));
        }
        frames.addAll(framesBuffer);
        framesBuffer.reverse();
        frames.addAll(framesBuffer);
        searching = new Animation<TextureRegion>(0.15f, frames);
        framesBuffer.clear();
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
        body.setActive(false);

        headShape.setRadius(BODY_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, bodyHeight / 2));
        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.PLAYER_SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.PLAYER_SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_SENSOR_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.PLAYER_BITS;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    private void setState(State state){
        stateStack.pop();
        stateStack.push(state);
    }

    @Override
    public void update(float delta){
        stateTimer += delta;
        currentState = stateStack.peek();

        if(body.getPosition().x - player.getBody().getPosition().x < 192 * MetalSlug.MAP_SCALE){
            body.setActive(true);
        }

        TextureRegion region;
        float offsetX = 0;
        float offsetY = 0;

        switch(currentState){
            case CHATTING:
                region = chatting.get(chattingIndex).getKeyFrame(stateTimer, false);

                offsetX = sprite.isFlipX() ? 10 * MetalSlug.MAP_SCALE : (-10) * MetalSlug.MAP_SCALE;
                break;
            case SNEAKING:
                region = sneaking.getKeyFrame(stateTimer, true);

                offsetX = sprite.isFlipX() ? 12 * MetalSlug.MAP_SCALE : (-12) * MetalSlug.MAP_SCALE;
                break;
            case SCARED:
                region = scared.getKeyFrame(stateTimer, true);

                offsetX = sprite.isFlipX() ? 12 * MetalSlug.MAP_SCALE : (-12) * MetalSlug.MAP_SCALE;
                break;
            case KNIFING:
                region = knifing.getKeyFrame(stateTimer, false);
                break;
            case FLEEING:
                region = fleeing.getKeyFrame(stateTimer, true);

                offsetX = sprite.isFlipX() ? 2 * MetalSlug.MAP_SCALE : (-2) * MetalSlug.MAP_SCALE;
                break;
            case RUNNING:
                if(!collidingWithPlayer && Math.abs(player.getBody().getPosition().x - body.getPosition().x) < player.getBodyWidth()){
                    region = idling.getKeyFrame(stateTimer, true);
                }
                else {
                    region = running.getKeyFrame(stateTimer, true);
                }

                offsetX = sprite.isFlipX() ? 2 * MetalSlug.MAP_SCALE : (-2) * MetalSlug.MAP_SCALE;
                break;
            case IDLING:
                region = idling.getKeyFrame(stateTimer, true);

                offsetX = sprite.isFlipX() ? 4 * MetalSlug.MAP_SCALE : (-4) * MetalSlug.MAP_SCALE;
                break;
            case SEARCHING:
                region = searching.getKeyFrame(stateTimer, true);

                offsetX = sprite.isFlipX() ? 0 * MetalSlug.MAP_SCALE : (-0) * MetalSlug.MAP_SCALE;
                break;
            case DYING:
            default:
                region = dying.getKeyFrame(stateTimer, false);
                break;
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !region.isFlipX()){
            region.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && region.isFlipX()){
            region.flip(true, false);
        }

        sprite.setRegion(region);
        if (sprite.isFlipX()) {
            sprite.setBounds(body.getPosition().x + (bodyWidth / 2) - sprite.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, body.getPosition().y - (bodyHeight / 2) + offsetY, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);

        } else {
            sprite.setBounds(body.getPosition().x - (bodyWidth / 2) + offsetX, body.getPosition().y - (bodyHeight / 2) + offsetY, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
        }

        switch(currentState){
            case CHATTING:
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
                break;
            case SNEAKING:
                if(!collidingWithPlayer){
                    if(Math.abs(player.getBody().getPosition().x - body.getPosition().x) < player.getBodyWidth()){
                        stop(true, false);
                        resetFrameTimer();
                        stateStack.push(State.SEARCHING);
                    }
                    else if(player.getBody().getPosition().x < body.getPosition().x && body.getLinearVelocity().x >= (-0.2f)){
                        move(new Vector2(-0.1f, 0));
                        isRunningRight = false;
                    }
                    else if(player.getBody().getPosition().x > body.getPosition().x && body.getLinearVelocity().x <= 0.2f){
                        move(new Vector2(0.1f, 0));
                        isRunningRight = true;
                    }
                }
                else if(collidingWithPlayer){
                    stop(true, false);
                    resetFrameTimer();
                    setState(State.KNIFING);
                }
                break;
            case SCARED:
                if(scared.isAnimationFinished(stateTimer)){
                    resetFrameTimer();
                    setState(State.FLEEING);
                    isRunningRight = !isRunningRight;
                }
                break;
            case KNIFING:
                if(knifing.isAnimationFinished(stateTimer)){
                    if(collidingWithPlayer){
                    }
                    else {
                        resetFrameTimer();
                        setState(State.RUNNING);
                    }
                }
                break;
            case FLEEING:
                if(!isRunningRight && body.getLinearVelocity().x >= (-0.8f)){
                    move(new Vector2(-0.3f, 0));
                }
                else if(isRunningRight && body.getLinearVelocity().x <= 0.8f){
                    move(new Vector2(0.3f, 0));
                }
                break;
            case RUNNING:
                if(!collidingWithPlayer){
                    if(Math.abs(player.getBody().getPosition().x - body.getPosition().x) < player.getBodyWidth()){
                        stop(true, false);
                        resetFrameTimer();
                        stateStack.push(State.SEARCHING);
                    }
                    else if(player.getBody().getPosition().x < body.getPosition().x && body.getLinearVelocity().x >= (-0.8f)){
                        move(new Vector2(-0.3f, 0));
                        isRunningRight = false;
                    }
                    else if(player.getBody().getPosition().x > body.getPosition().x && body.getLinearVelocity().x <= 0.8f){
                        move(new Vector2(0.3f, 0));
                        isRunningRight = true;
                    }
                }
                else if(collidingWithPlayer){
                    stop(true, false);
                    resetFrameTimer();
                    setState(State.KNIFING);
                }
                break;
            case IDLING:
                if(Math.abs(player.getBody().getPosition().x - body.getPosition().x) < 100 * MetalSlug.MAP_SCALE){
                    resetFrameTimer();
                    setState(State.SCARED);
                }
                break;
            case SEARCHING:
                if(Math.abs(player.getBody().getPosition().x - body.getPosition().x) >= player.getBodyWidth()){
                    resetFrameTimer();
                    stateStack.pop();
                }
                break;
            case DYING:
            default:
                deathTimer += delta;
                if(deathTimer >= 2f){
                    remove();
                }
                break;
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    private void resetFrameTimer(){
        stateTimer = 0;
    }

    public void kill(){
        Filter filter = new Filter();
        filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.getFixtureList().forEach(fixture -> fixture.setFilterData(filter));
        setState(State.DYING);
        resetFrameTimer();
    }

    public void setAttackMode(AttackMode attackMode){
        this.attackMode = attackMode;
    }

    public void setCollidingWithPlayer(boolean value){
        collidingWithPlayer = value;
    }
}
