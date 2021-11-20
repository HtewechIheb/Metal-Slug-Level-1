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
    private final float BODY_RECTANGLE_HEIGHT = 30f * MetalSlug.MAP_SCALE;
    private final float HEAD_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;

    public enum State {
        STANDING,
        RUNNING,
        ATTACKING,
        JUMPING,
        LOOKINGUP,
        TRANSITIONING
    }

    public enum Weapon {
        PISTOL,
        MACHINEGUN,
        FIREGUN
    }

    public enum AttackMode {
        MELEE,
        WEAPON
    }

    private Body body;
    private MissionOneScreen screen;
    private World world;
    private TextureAtlas textureAtlas;
    private Sprite torso;
    private Sprite legs;
    private float bodyWidth;
    private float bodyHeight;
    private Weapon weapon;

    private Animation<TextureRegion> standingTorso;
    private Animation<TextureRegion> runningTorso;
    private Animation<TextureRegion> shootingTorso;
    private Animation<TextureRegion> knifingTorso;
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
    private AttackMode attackMode;
    private AttackMode attackModeBuffer;

    private boolean isStanding = false;
    private boolean isRunning = false;
    private boolean isRunningRight = true;
    private boolean isAttacking = false;
    private boolean isJumping = false;
    private boolean isRunningJumping = false;
    private boolean isStandingJumping = false;
    private boolean isLookingUp = false;
    private boolean isTransitioning = false;

    private boolean runningDisabled = false;
    private boolean attackingDisabled = false;
    private boolean lookingUpDisabled = false;
    private boolean jumpingDisabled = false;

    private boolean lookingUpAnimation = false;
    private boolean reverseLookingUpAnimation = false;

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
        bodyWidth = 0;
        bodyHeight = 0;
        weapon = Weapon.PISTOL;
        attackMode = AttackMode.WEAPON;
        attackModeBuffer = AttackMode.WEAPON;

        defineAnimations();
        defineCharacter();
    }

    private void defineAnimations(){
        byte i;
        Array<TextureRegion> frames = new Array<>();

        for(i = 1; i < 5; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("idle-torso-pistol-%d", i))));
        }
        standingTorso = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for(i = 1; i < 10; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-torso-pistol-%d", i))));
        }
        runningTorso = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();

        for(i = 1; i < 12; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-legs-%d", i))));
        }
        runningLegs = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();

        for(i = 1; i < 11; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("shooting-torso-%d", i))));
        }
        shootingTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("knifing-torso-%d", i))));
        }
        knifingTorso = new Animation<TextureRegion>(0.06f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("standing-jumping-torso-%d", i))));
        }
        standingJumpingTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("standing-jumping-legs-%d", i))));
        }
        standingJumpingLegs = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-jumping-torso-%d", i))));
        }
        runningJumpingTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-jumping-legs-%d", i))));
        }
        runningJumpingLegs = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 11; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("looking-up-shooting-pistol-%d", i))));
        }
        shootingLookingUpTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 5; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("idle-looking-up-pistol-%d", i))));
        }
        idleLookingUpTorso = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for(i = 1; i < 4; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("looking-up-pistol-%d", i))));
        }
        lookingUpTransitionTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.reverse();
        reverseLookingUpTransitionTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        standingLegs = new TextureRegion(textureAtlas.findRegion("idle-legs"));
    }

    private void defineCharacter(){
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        bodyWidth = BODY_RECTANGLE_WIDTH;
        bodyHeight = BODY_RECTANGLE_HEIGHT;

        headShape.setRadius(HEAD_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, bodyHeight / 2));
        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(112 * MetalSlug.MAP_SCALE, 64 * MetalSlug.MAP_SCALE);
        body = world.createBody(bodyDef);

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.PLAYER_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.ENEMY_SENSOR_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.PLAYER_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.ENEMY_SENSOR_BITS;;
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
        if(isJumping && body.getLinearVelocity().y == 0){
            isStandingJumping = false;
            isRunningJumping = false;
        }
        isJumping = body.getLinearVelocity().y != 0;
        isStanding = !isRunning && !isJumping;
        isTransitioning = lookingUpAnimation || reverseLookingUpAnimation;
        if(isRunning && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            body.setLinearVelocity(new Vector2(0, 0));
            isAttacking = false;
            isRunning = false;
        }

        runningDisabled = isLookingUp || isStandingJumping;
        attackingDisabled = isJumping;
        lookingUpDisabled = isRunning || isJumping || isAttacking;
        jumpingDisabled = isLookingUp;

        if(attackingDisabled){
            isAttacking = false;
        }

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

    public void draw(SpriteBatch batch){
        legs.draw(batch);
        torso.draw(batch);
    }

    private TextureRegion getTorsoFrame(){
        TextureRegion region = new TextureRegion();

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
        else if(currentState.contains(State.LOOKINGUP) && currentState.contains(State.ATTACKING)){
            Animation<TextureRegion> animation;

            if(attackMode == AttackMode.WEAPON){
                animation = shootingLookingUpTorso;
                region = shootingLookingUpTorso.getKeyFrame(torsoStateTimer, false);
            }
            else{
                animation = knifingTorso;
                region = knifingTorso.getKeyFrame(torsoStateTimer, false);
            }
            if(animation.isAnimationFinished(torsoStateTimer)){
                isAttacking = false;
            }
            if(!Gdx.input.isKeyPressed(Input.Keys.UP)){
                isAttacking = false;
                reverseLookingUpAnimation = true;
            }
        }
        else if(currentState.contains(State.LOOKINGUP)){
            region = idleLookingUpTorso.getKeyFrame(torsoStateTimer, true);
            if(!Gdx.input.isKeyPressed(Input.Keys.UP)){
                isAttacking = false;
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
        else if(currentState.contains(State.ATTACKING)){
            Animation<TextureRegion> animation;

            if(attackMode == AttackMode.WEAPON){
                animation = shootingTorso;
                region = shootingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else{
                animation = knifingTorso;
                region = knifingTorso.getKeyFrame(torsoStateTimer, false);
            }
            if(animation.isAnimationFinished(torsoStateTimer)){
                isAttacking = false;
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
        else if(currentState.contains(State.ATTACKING)){
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
        if(isAttacking){
            state.add(State.ATTACKING);
        }
        if(isRunning){
            state.add(State.RUNNING);
        }
        if(state.isEmpty()){
            state.add(State.STANDING);
        }

        return state;
    }

    private void resetTransitions(){
        lookingUpAnimation = false;
        reverseLookingUpAnimation = false;
    }

    public void attack(){
        if(!attackingDisabled){
            if(isAttacking && attackMode != AttackMode.MELEE){
                attackMode = attackModeBuffer;
                torsoStateTimer = 0;
            }
            else if(!isAttacking) {
                attackMode = attackModeBuffer;
                isAttacking = true;
            }

            if(attackMode == AttackMode.WEAPON){
                switch (weapon){
                    case PISTOL:
                    default:
                        screen.getWorldCreator().createShot(Shot.ShotType.PISTOL, screen, this);
                }
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

    // Get the end position of the sprite on the X axis
    public float getShotX(){
        TextureRegion straightShootingTexture = new TextureRegion(textureAtlas.findRegion("shooting-torso-3"));

        if(isLookingUp){
            return body.getPosition().x;
        }
        else if(isRunningRight){
            return body.getPosition().x - (bodyWidth / 2) + straightShootingTexture.getRegionWidth() * MetalSlug.MAP_SCALE;
        }
        else {
            return body.getPosition().x + (bodyWidth / 2) - straightShootingTexture.getRegionWidth() * MetalSlug.MAP_SCALE;
        }
    }

    // Get the end position of the sprite on the Y axis
    public float getShotY(){
        TextureRegion upShootingTexture = new TextureRegion(textureAtlas.findRegion("looking-up-shooting-pistol-4"));
        float offsetY = (-1) * MetalSlug.MAP_SCALE;

        if(isLookingUp){
            return torso.getY() + upShootingTexture.getRegionHeight() * MetalSlug.MAP_SCALE;
        }
        else {
            return body.getPosition().y + (bodyWidth / 2) + HEAD_CIRCLE_RADIUS + offsetY;
        }
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

    public void setAttackMode(AttackMode attackMode){
        this.attackModeBuffer = attackMode;
    }
}
