package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.EnumSet;

public class MarcoRossi {
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 26f * MetalSlug.MAP_SCALE;
    private final float BODY_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;

    public enum State {
        STANDING,
        RUNNING,
        SHOOTING,
        JUMPING,
        LOOKINGUP,
        TRANSITIONING
    }

    public enum Weapon {
        PISTOL,
        MACHINEGUN,
        FIREGUN
    }

    private Body body;
    private MissionOneScreen screen;
    private World world;
    private TextureAtlas textureAtlas;
    private Sprite torso;
    private Sprite legs;

    private Animation<TextureRegion> standingTorso;
    private Animation<TextureRegion> runningTorso;
    private Animation<TextureRegion> shootingTorso;
    private Animation<TextureRegion> standingJumpingTorso;
    private Animation<TextureRegion> runningJumpingTorso;
    private Animation<TextureRegion> lookingUpTransitionTorso;
    private Animation<TextureRegion> reverseLookingUpTransitionTorso;
    private Animation<TextureRegion> idleLookingUpTorso;
    private Animation<TextureRegion> shootingLookingUpTorso;
    private TextureRegion standingLegs;
    private Animation<TextureRegion> runningLegs;
    private Animation<TextureRegion> standingJumpingLegs;
    private Animation<TextureRegion> runningJumpingLegs;

    private EnumSet<State> currentState;
    private EnumSet<State> previousState;
    private float torsoStateTimer;
    private float legsStateTimer;

    private boolean isStanding;
    private boolean isRunning;
    private boolean isRunningRight;
    private boolean isShooting;
    private boolean isJumping;
    private boolean isRunningJumping;
    private boolean isStandingJumping;
    private boolean isLookingUp;
    private boolean isTransitioning;

    private boolean runningDisabled;
    private boolean shootingDisabled;
    private boolean lookingUpDisabled;
    private boolean jumpingDisabled;

    private boolean lookingUpAnimation;
    private boolean reverseLookingUpAnimation;

    private Weapon weapon;

    public MarcoRossi(MissionOneScreen screen){
        this.screen = screen;
        world = screen.getWorld();
        torso = new Sprite();
        legs = new Sprite();
        textureAtlas = screen.getPlayerTextureAtlas();
        currentState = EnumSet.of(State.STANDING);
        previousState = EnumSet.of(State.STANDING);
        torsoStateTimer = 0;
        legsStateTimer = 0;
        isStanding = false;
        isRunningRight = true;
        isShooting = false;
        isJumping = false;
        isRunningJumping = false;
        isStandingJumping = false;
        isLookingUp = false;
        runningDisabled = false;
        shootingDisabled = false;
        lookingUpDisabled = false;
        jumpingDisabled = false;
        lookingUpAnimation = false;
        reverseLookingUpAnimation = false;
        weapon = Weapon.PISTOL;

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-torso-pistol-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-torso-pistol-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-torso-pistol-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-torso-pistol-4")));
        standingTorso = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-torso-pistol-9")));
        runningTorso = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-10")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-legs-11")));
        runningLegs = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("shooting-torso-10")));
        shootingTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-torso-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-torso-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-torso-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-torso-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-torso-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-torso-6")));
        standingJumpingTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-legs-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-legs-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-legs-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-legs-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-legs-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("standing-jumping-legs-6")));
        standingJumpingLegs = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-torso-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-torso-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-torso-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-torso-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-torso-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-torso-6")));
        runningJumpingTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-legs-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-legs-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-legs-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-legs-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-legs-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("running-jumping-legs-6")));
        runningJumpingLegs = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-10")));
        shootingLookingUpTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("idle-looking-up-pistol-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-looking-up-pistol-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-looking-up-pistol-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("idle-looking-up-pistol-4")));
        idleLookingUpTorso = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-pistol-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-pistol-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("looking-up-pistol-3")));
        lookingUpTransitionTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.reverse();
        reverseLookingUpTransitionTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        standingLegs = new TextureRegion(textureAtlas.findRegion("idle-legs"));

        defineCharacter();
    }

    public void draw(SpriteBatch batch){
        legs.draw(batch);
        torso.draw(batch);
    }

    private void defineCharacter(){
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        headShape.setRadius(BODY_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, BODY_RECTANGLE_HEIGHT / 2));
        bodyShape.setAsBox(BODY_RECTANGLE_WIDTH / 2, BODY_RECTANGLE_HEIGHT / 2);

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(112 * MetalSlug.MAP_SCALE, 64 * MetalSlug.MAP_SCALE);
        body = world.createBody(bodyDef);

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.PLAYER_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;

        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.PLAYER_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void update(float delta){
        isRunning = body.getLinearVelocity().x != 0;
        if(body.getLinearVelocity().x > 0){
            isRunningRight = true;
        }
        else if(body.getLinearVelocity().x < 0){
            isRunningRight = false;
        }
        isJumping = body.getLinearVelocity().y != 0;
        isStanding = !isRunning && !isJumping;
        isTransitioning = lookingUpAnimation || reverseLookingUpAnimation;

        runningDisabled = isLookingUp;
        shootingDisabled = isJumping;
        lookingUpDisabled = isRunning || isJumping;
        jumpingDisabled = isLookingUp;

        currentState = getState();
        torsoStateTimer = previousState.equals(currentState) ? torsoStateTimer + delta : 0;
        legsStateTimer = previousState.equals(currentState) ? legsStateTimer + delta : 0;

        TextureRegion torsoRegion = getTorsoFrame();
        TextureRegion legsRegion = getLegsFrame();
        torso.setRegion(torsoRegion);
        torso.setBounds(0, 0, torsoRegion.getRegionWidth() * MetalSlug.MAP_SCALE, torsoRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
        legs.setRegion(legsRegion);
        legs.setBounds(0, 0, legsRegion.getRegionWidth() * MetalSlug.MAP_SCALE, legsRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
        setLegsPosition();
        setTorsoPosition();

        previousState = currentState.clone();
    }

    private TextureRegion getTorsoFrame(){
        TextureRegion region = new TextureRegion();

        /*if(currentState.contains(State.SHOOTING) || currentState.contains(State.RUNNING) || currentState.contains(State.JUMPING)){
            resetTransitions();
        }*/

        if(currentState.contains(State.TRANSITIONING)){
            if(lookingUpAnimation){
                region = lookingUpTransitionTorso.getKeyFrame(torsoStateTimer, false);
                if(lookingUpTransitionTorso.isAnimationFinished(torsoStateTimer)){
                    lookingUpAnimation = false;
                    isLookingUp = true;
                }
            }
            else if(reverseLookingUpAnimation){
                region = reverseLookingUpTransitionTorso.getKeyFrame(torsoStateTimer, false);
                if(reverseLookingUpTransitionTorso.isAnimationFinished(torsoStateTimer)){
                    reverseLookingUpAnimation = false;
                    isLookingUp = false;
                }
            }
        }
        else if(currentState.contains(State.LOOKINGUP) && currentState.contains(State.SHOOTING)){
            region = shootingLookingUpTorso.getKeyFrame(torsoStateTimer, false);
            if(shootingLookingUpTorso.isAnimationFinished(torsoStateTimer)){
                isShooting = false;
            }
        }
        else if(currentState.contains(State.LOOKINGUP)){
            region = idleLookingUpTorso.getKeyFrame(torsoStateTimer, true);
            if(!Gdx.input.isKeyPressed(Input.Keys.UP)){
                reverseLookingUpAnimation = true;
            }
        }
        else if(currentState.contains(State.JUMPING)){
            if(isStandingJumping){
                region = standingJumpingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else if(isRunningJumping){
                region = runningJumpingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else {
                region = standingJumpingTorso.getKeyFrame(torsoStateTimer, false);
            }
        }
        else if(currentState.contains(State.SHOOTING)){
            region = shootingTorso.getKeyFrame(torsoStateTimer, false);
            if(shootingTorso.isAnimationFinished(torsoStateTimer)){
                isShooting = false;
            }
        }
        else if(currentState.contains(State.RUNNING)){
            region = runningTorso.getKeyFrame(torsoStateTimer, true);
        }
        else {
            region = standingTorso.getKeyFrame(torsoStateTimer, true);
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !region.isFlipX()){
            region.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && region.isFlipX()){
            region.flip(true, false);
        }

        return region;
    }

    private TextureRegion getLegsFrame(){
        TextureRegion region;

        if(currentState.contains(State.JUMPING)){
            if(isStandingJumping){
                region = standingJumpingLegs.getKeyFrame(legsStateTimer, false);
            }
            else if(isRunningJumping){
                region = runningJumpingLegs.getKeyFrame(legsStateTimer, false);
            }
            else {
                region = standingJumpingLegs.getKeyFrame(legsStateTimer, false);
            }
        }
        else if(currentState.contains(State.RUNNING)){
            region = runningLegs.getKeyFrame(legsStateTimer, true);
        }
        else {
            region = standingLegs;
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !region.isFlipX()){
            region.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && region.isFlipX()){
            region.flip(true, false);
        }

        return region;
    }

    private void setTorsoPosition(){
        float offsetX;
        float offsetY;

        offsetY = (-7) * MetalSlug.MAP_SCALE;

        if(currentState.contains(State.TRANSITIONING)){
            if(lookingUpAnimation || reverseLookingUpAnimation){
                offsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                offsetY = (-2) * MetalSlug.MAP_SCALE;
            }
            else {
                offsetX = 0;
                offsetY = 0;
            }
        }
        else if(currentState.contains(State.LOOKINGUP)){
            offsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
            offsetY = (-2) * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.JUMPING) && currentState.contains(State.RUNNING)){
            offsetX = torso.isFlipX() ? 9 * MetalSlug.MAP_SCALE : (-9) * MetalSlug.MAP_SCALE;
            offsetY = (-14) * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.JUMPING)){
            offsetX = torso.isFlipX() ? 9 * MetalSlug.MAP_SCALE : (-9) * MetalSlug.MAP_SCALE;
            offsetY = (-3) * MetalSlug.MAP_SCALE;
        }
        else if(currentState.contains(State.SHOOTING)){
            offsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
            offsetY = (-6) * MetalSlug.MAP_SCALE;
        }
        else{
            offsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
            offsetY = (-7) * MetalSlug.MAP_SCALE;
        }

        if(torso.isFlipX()){
            torso.setPosition(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - torso.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, legs.getY() + legs.getHeight() + offsetY);
        }
        else{
            torso.setPosition(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + offsetX, legs.getY() + legs.getHeight() + offsetY);
        }
    }

    private void setLegsPosition(){
        float offsetX;
        float offsetY;

        offsetY = (-7) * MetalSlug.MAP_SCALE;

        if(currentState.contains(State.JUMPING) && currentState.contains(State.RUNNING)){
            offsetX = legs.isFlipX() ? 7 * MetalSlug.MAP_SCALE : (-7) * MetalSlug.MAP_SCALE;
            offsetY = 0;
        }
       else if(currentState.contains(State.RUNNING)){
            offsetX = legs.isFlipX() ? 6 * MetalSlug.MAP_SCALE : (-6) * MetalSlug.MAP_SCALE;
            offsetY = 0;
        }
       else{
           offsetX = legs.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
           offsetY = 0;
       }

        if(legs.isFlipX()){
            legs.setPosition(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - legs.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + offsetY);
        }
        else{
            legs.setPosition(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + offsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + offsetY);
        }
    }

    public void shoot(){
        if(!shootingDisabled){
            if(isShooting){
                torsoStateTimer = 0;
            }
            else {
                isShooting = true;
            }

            switch (weapon){
                case PISTOL:
                default:
                    screen.getWorldCreator().createShot(Shot.ShotType.PISTOL, screen, this);
            }
        }
    }

    public void move(Vector2 vector){
        if(!runningDisabled){
            body.applyLinearImpulse(vector, body.getWorldCenter(), true);
        }
    }

    public void jump(Vector2 vector){
        if(!jumpingDisabled && !isJumping){
            isRunningJumping = false;
            isStandingJumping = false;

            if(isRunning){
                isRunningJumping = true;
            }
            else{
                isStandingJumping = true;
            }

            body.applyLinearImpulse(vector, body.getWorldCenter(), true);
        }
    }

    public void stop(boolean stopX, boolean stopY){
        body.setLinearVelocity(new Vector2(stopX ? 0 : body.getLinearVelocity().x, stopY ? 0 : body.getLinearVelocity().y));
    }

    public void lookup(){
        if(!lookingUpDisabled && !lookingUpAnimation && !isLookingUp){
            lookingUpAnimation = true;
        }
    }

    private EnumSet<State> getState(){
        EnumSet<State> state = EnumSet.noneOf(State.class);

        if(isTransitioning){
            state.add(State.TRANSITIONING);
        }
        if(isLookingUp){
            state.add(State.LOOKINGUP);
        }
        if(isJumping){
            state.add(State.JUMPING);
        }
        if(isShooting){
            state.add(State.SHOOTING);
        }
        if(isRunning){
            state.add(State.RUNNING);
        }
        if(state.isEmpty()){
            state.add(State.STANDING);
        }

        return state;
    }

    // Get the end position of the sprite on the X axis
    public float getShotX(){
        TextureRegion straightShootingTexture = new TextureRegion(textureAtlas.findRegion("shooting-torso-3"));

        if(isLookingUp){
            return body.getPosition().x;
        }
        else if(isRunningRight){
            return body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2) + straightShootingTexture.getRegionWidth() * MetalSlug.MAP_SCALE;
        }
        else {
            return body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - straightShootingTexture.getRegionWidth() * MetalSlug.MAP_SCALE;
        }
    }

    // Get the end position of the sprite on the Y axis
    public float getShotY(){
        float offsetY = (-1) * MetalSlug.MAP_SCALE;

        TextureRegion upShootingTexture = new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-4"));

        if(isLookingUp){
            return torso.getY() + upShootingTexture.getRegionHeight() * MetalSlug.MAP_SCALE;
        }
        else {
            return body.getPosition().y + (BODY_RECTANGLE_WIDTH / 2) + BODY_CIRCLE_RADIUS + offsetY;
        }
        //TextureRegion shootingTexture = new TextureRegion(textureAtlas.findRegion("shooting-torso-3"));
    }

    private void resetTransitions(){
        lookingUpAnimation = false;
        reverseLookingUpAnimation = false;
    }

    public Body getBody(){
        return body;
    }

    public boolean getIsRunningRight(){
        return isRunningRight;
    }

    public boolean getIsLookingUp(){
        return isLookingUp;
    }
}
