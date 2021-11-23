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
import java.util.Stack;

public class MarcoRossi {
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 30f * MetalSlug.MAP_SCALE;
    private final float HEAD_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;

    public enum MovementState {
        STANDING,
        RUNNING,
        JUMPING_UP,
        JUMPING_FORWARD,
        LOOKINGUP
    }

    public enum ActionState {
        NEUTRAL,
        SHOOTING,
        KNIFING
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
    private Animation<TextureRegion> jumpingUpTorso;
    private Animation<TextureRegion> jumpingForwardTorso;
    private Animation<TextureRegion> lookingUpTransitionTorso;
    private Animation<TextureRegion> reverseLookingUpTransitionTorso;
    private Animation<TextureRegion> idleLookingUpTorso;
    private Animation<TextureRegion> shootingLookingUpTorso;
    private TextureRegion standingLegs;
    private Animation<TextureRegion> runningLegs;
    private Animation<TextureRegion> jumpingUpLegs;
    private Animation<TextureRegion> jumpingForwardLegs;

    private MovementState currentMovementState;
    private MovementState previousMovementState;
    private ActionState currentActionState;
    private ActionState previousActionState;
    private Stack<MovementState> movementStateStack;
    private Stack<ActionState> actionStateStack;
    private float torsoStateTimer;
    private float legsStateTimer;
    private AttackMode attackMode;

    private boolean isRunningRight = true;
    private boolean lookingUpTransition = false;
    private boolean reverseLookingUpTransition = false;

    public MarcoRossi(MissionOneScreen screen){
        this.screen = screen;
        world = screen.getWorld();
        torso = new Sprite();
        legs = new Sprite();
        textureAtlas = screen.getPlayerTextureAtlas();
        currentMovementState = MovementState.STANDING;
        previousMovementState = MovementState.STANDING;
        currentActionState = ActionState.NEUTRAL;
        previousActionState = ActionState.NEUTRAL;
        movementStateStack = new Stack<>();
        actionStateStack = new Stack<>();
        movementStateStack.push(currentMovementState);
        actionStateStack.push(currentActionState);
        torsoStateTimer = 0;
        legsStateTimer = 0;
        bodyWidth = 0;
        bodyHeight = 0;
        weapon = Weapon.PISTOL;
        attackMode = AttackMode.WEAPON;

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
        jumpingUpTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("standing-jumping-legs-%d", i))));
        }
        jumpingUpLegs = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-jumping-torso-%d", i))));
        }
        jumpingForwardTorso = new Animation<TextureRegion>(0.04f, frames);
        frames.clear();

        for(i = 1; i < 7; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("running-jumping-legs-%d", i))));
        }
        jumpingForwardLegs = new Animation<TextureRegion>(0.04f, frames);
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

    public void handleInput(float delta){
        switch(previousMovementState){
            case STANDING:
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                    move(new Vector2(0.3f, 0));

                    resetFrameTimers(true, true);
                    setMovementState(MovementState.RUNNING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && (body.getPosition().x - 13 * MetalSlug.MAP_SCALE) > (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2)){
                    move(new Vector2(-0.3f, 0));

                    resetFrameTimers(true, true);
                    setMovementState(MovementState.RUNNING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.C)){
                    move(new Vector2(0f, 3.5f));

                    resetFrameTimers(true, true);
                    setMovementState(MovementState.JUMPING_UP);
                    setActionState(ActionState.NEUTRAL);
                }
                else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    attack();

                    resetFrameTimers(true, false);
                    if(attackMode == AttackMode.MELEE){
                        setActionState(ActionState.KNIFING);
                    }
                    else {
                        setActionState(ActionState.SHOOTING);
                    }
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
                    resetFrameTimers(true, false);
                    lookingUpTransition = true;
                    setMovementState(MovementState.LOOKINGUP);
                    setActionState(ActionState.NEUTRAL);
                }
                break;
            case RUNNING:
                if((body.getPosition().x - 13 * MetalSlug.MAP_SCALE) <= (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2)){
                    stop(true, false);

                    resetFrameTimers(true, true);
                    setMovementState(MovementState.STANDING);
                }
                if(!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                    stop(true, false);

                    resetFrameTimers(true, true);
                    setMovementState(MovementState.STANDING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && body.getLinearVelocity().x <= 1.5f){
                    move(new Vector2(0.3f, 0));

                    if(!isRunningRight){
                        setActionState(ActionState.NEUTRAL);
                    }
                    isRunningRight = true;
                    setMovementState(MovementState.RUNNING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && body.getLinearVelocity().x >= -1.5f && (body.getPosition().x - 13 * MetalSlug.MAP_SCALE) > (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2)){
                    move(new Vector2(-0.3f, 0));

                    if(isRunningRight){
                        setActionState(ActionState.NEUTRAL);
                    }
                    isRunningRight = false;
                    setMovementState(MovementState.RUNNING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.C)){
                    move(new Vector2(0f, 3.5f));

                    resetFrameTimers(true, true);
                    setMovementState(MovementState.JUMPING_FORWARD);
                    setActionState(ActionState.NEUTRAL);
                }
                else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && currentActionState != ActionState.KNIFING){
                    attack();

                    resetFrameTimers(true, false);
                    if(attackMode == AttackMode.MELEE){
                        setActionState(ActionState.KNIFING);
                    }
                    else {
                        setActionState(ActionState.SHOOTING);
                    }
                }
                break;
            case JUMPING_UP:
                if(body.getLinearVelocity().y == 0){
                    resetFrameTimers(true, true);
                    setMovementState(MovementState.STANDING);
                }
                break;
            case JUMPING_FORWARD:
                if(body.getLinearVelocity().y == 0){
                    resetFrameTimers(true, true);
                    setMovementState(MovementState.STANDING);
                }
                if((body.getPosition().x - 13 * MetalSlug.MAP_SCALE) <= (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2)){
                    stop(true, false);
                }
                break;
            case LOOKINGUP:
                if(lookingUpTransition && lookingUpTransitionTorso.isAnimationFinished(torsoStateTimer)){
                    lookingUpTransition = false;
                    resetFrameTimers(true, false);
                }
                else if(reverseLookingUpTransition && Gdx.input.isKeyPressed(Input.Keys.UP)){
                    reverseLookingUpTransition = false;
                    lookingUpTransition = true;
                }
                else if(!Gdx.input.isKeyPressed(Input.Keys.UP) && currentActionState != ActionState.SHOOTING){
                    if(lookingUpTransition){
                        reverseLookingUpTransition = true;
                        lookingUpTransition = false;
                    }
                    else if(!reverseLookingUpTransition){
                        reverseLookingUpTransition = true;
                        resetFrameTimers(true, false);
                    }
                    else if(reverseLookingUpTransition && reverseLookingUpTransitionTorso.isAnimationFinished(torsoStateTimer)){
                        reverseLookingUpTransition = false;
                        resetFrameTimers(true, false);
                        setMovementState(MovementState.STANDING);
                        setActionState(ActionState.NEUTRAL);
                    }
                }
                else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    if(lookingUpTransition){
                        lookingUpTransition = false;
                        resetFrameTimers(true, false);
                    }
                    attack();

                    resetFrameTimers(true, false);
                    setActionState(ActionState.SHOOTING);
                }
                break;
        }
        switch (previousActionState){
            case SHOOTING:
                if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    resetFrameTimers(true, false);
                    attack();
                }
                if(shootingTorso.isAnimationFinished(torsoStateTimer)){
                    resetFrameTimers(true, true);
                    setActionState(ActionState.NEUTRAL);
                }
                break;
            case KNIFING:
                if(knifingTorso.isAnimationFinished(torsoStateTimer)){
                    resetFrameTimers(true, true);
                    setActionState(ActionState.NEUTRAL);
                }
                break;
        }
    }

    public void update(float delta){
        torsoStateTimer += delta;
        legsStateTimer += delta;

        previousMovementState = movementStateStack.peek();
        previousActionState = actionStateStack.peek();
        handleInput(delta);
        currentMovementState = movementStateStack.peek();
        currentActionState = actionStateStack.peek();

        TextureRegion torsoRegion = getTorsoFrame();
        TextureRegion legsRegion = getLegsFrame();
        torso.setRegion(torsoRegion);
        torso.setBounds(0, 0, torsoRegion.getRegionWidth() * MetalSlug.MAP_SCALE, torsoRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
        legs.setRegion(legsRegion);
        legs.setBounds(0, 0, legsRegion.getRegionWidth() * MetalSlug.MAP_SCALE, legsRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
        setLegsPosition();
        setTorsoPosition();
    }

    public void draw(SpriteBatch batch){
        legs.draw(batch);
        torso.draw(batch);
    }

    private TextureRegion getTorsoFrame(){
        TextureRegion region = new TextureRegion();

        if(currentMovementState == MovementState.LOOKINGUP){
            if(lookingUpTransition){
                region = lookingUpTransitionTorso.getKeyFrame(torsoStateTimer, false);
            }
            else if(reverseLookingUpTransition){
                region = reverseLookingUpTransitionTorso.getKeyFrame(torsoStateTimer, false);

            }
            else if(currentActionState == ActionState.SHOOTING) {
                region = shootingLookingUpTorso.getKeyFrame(torsoStateTimer, false);
            }
            else {
                region = idleLookingUpTorso.getKeyFrame(torsoStateTimer, true);
            }
        }
        else if(currentMovementState == MovementState.JUMPING_UP){
            region = jumpingUpTorso.getKeyFrame(torsoStateTimer, false);
        }
        else if(currentMovementState == MovementState.JUMPING_FORWARD){
            region = jumpingForwardTorso.getKeyFrame(torsoStateTimer, false);
        }
        else if(currentMovementState == MovementState.RUNNING){
            if(currentActionState == ActionState.SHOOTING) {
                region = shootingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else if(currentActionState == ActionState.KNIFING){
                region = knifingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else{
                region = runningTorso.getKeyFrame(torsoStateTimer, true);
            }
        }
        else if(currentMovementState == MovementState.STANDING){
            if(currentActionState == ActionState.SHOOTING) {
                region = shootingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else if(currentActionState == ActionState.KNIFING){
                region = knifingTorso.getKeyFrame(torsoStateTimer, false);
            }
            else{
                region = standingTorso.getKeyFrame(torsoStateTimer, true);
            }
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

        if(currentMovementState == MovementState.JUMPING_UP){
            region = jumpingUpLegs.getKeyFrame(legsStateTimer, false);
        }
        else if(currentMovementState == MovementState.JUMPING_FORWARD){
            region = jumpingForwardLegs.getKeyFrame(legsStateTimer, false);
        }
        else if(currentMovementState == MovementState.RUNNING){
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

        if(currentMovementState == MovementState.LOOKINGUP){
            offsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
            offsetY = (-2) * MetalSlug.MAP_SCALE;
        }
        else if(currentMovementState == MovementState.JUMPING_FORWARD){
            offsetX = torso.isFlipX() ? 9 * MetalSlug.MAP_SCALE : (-9) * MetalSlug.MAP_SCALE;
            offsetY = (-14) * MetalSlug.MAP_SCALE;
        }
        else if(currentMovementState == MovementState.JUMPING_UP){
            offsetX = torso.isFlipX() ? 9 * MetalSlug.MAP_SCALE : (-9) * MetalSlug.MAP_SCALE;
            offsetY = (-3) * MetalSlug.MAP_SCALE;
        }
        else {
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

        if(currentMovementState == MovementState.JUMPING_UP){
            offsetX = legs.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
            offsetY = 0;
        }
        else if(currentMovementState == MovementState.JUMPING_FORWARD){
            offsetX = legs.isFlipX() ? 7 * MetalSlug.MAP_SCALE : (-7) * MetalSlug.MAP_SCALE;
            offsetY = 0;
        }
        else if(currentMovementState == MovementState.RUNNING){
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

    public void setMovementState(MovementState state){
        movementStateStack.pop();
        movementStateStack.push(state);
    }

    public void setActionState(ActionState state){
        actionStateStack.pop();
        actionStateStack.push(state);
    }

    private void resetFrameTimers(boolean torso, boolean legs){
        if(torso){
            torsoStateTimer = 0;

        }
        if(legs){
            legsStateTimer = 0;
        }
    }

    private void move(Vector2 vector){
        body.applyLinearImpulse(vector, body.getWorldCenter(), true);
    }

    private void stop(boolean stopX, boolean stopY){
        body.setLinearVelocity(new Vector2(stopX ? 0 : body.getLinearVelocity().x, stopY ? 0 : body.getLinearVelocity().y));
    }

    private void attack(){
        if(attackMode == AttackMode.WEAPON){
            switch (weapon){
                case PISTOL:
                default:
                    screen.getWorldCreator().createShot(Shot.ShotType.PISTOL, screen, this);
            }
        }
    }

    // Get the end position of the sprite on the X axis
    public float getShotX(){
        TextureRegion straightShootingTexture = new TextureRegion(textureAtlas.findRegion("shooting-torso-3"));

        if(currentMovementState == MovementState.LOOKINGUP){
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

        if(currentMovementState == MovementState.LOOKINGUP){
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
        return currentMovementState == MovementState.LOOKINGUP;
    }

    public void setAttackMode(AttackMode attackMode){
        this.attackMode = attackMode;
    }
}
