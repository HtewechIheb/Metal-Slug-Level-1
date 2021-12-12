package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.EnumSet;
import java.util.Stack;

public class MarcoRossi implements Disposable {
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 30f * MetalSlug.MAP_SCALE;
    private final float HEAD_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;


    public enum MovementState {
        STANDING,
        RUNNING,
        JUMPING_UP,
        JUMPING_FORWARD,
        LOOKINGUP,
        DYING
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

    public enum DeathType {
        STABBED,
        BOMBED
    }

    private Body body;
    private MissionOneScreen screen;
    private World world;
    private TextureAtlas textureAtlas;
    private Sprite torso;
    private Sprite legs;
    private Sprite fullBody;
    private float bodyWidth;
    private float bodyHeight;
    private Weapon weapon;
    private AssetManager assetManager;

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
    private Animation<TextureRegion> stabbed;
    private Animation<TextureRegion> bombed;

    private MovementState currentMovementState;
    private MovementState previousMovementState;
    private ActionState currentActionState;
    private ActionState previousActionState;
    private Stack<MovementState> movementStateStack;
    private Stack<ActionState> actionStateStack;
    private float torsoStateTimer;
    private float legsStateTimer;
    private float fullBodyStateTimer;
    private AttackMode attackMode;
    private DeathType deathType;
    private boolean useFullBody = false;

    private boolean isRunningRight = true;
    private boolean lookingUpTransition = false;
    private boolean reverseLookingUpTransition = false;

    private Array<Enemy> collidingEnemies;
    private Array<Hostage> collidingHostages;

    public MarcoRossi(MissionOneScreen screen){
        this.screen = screen;
        this.assetManager = screen.getAssetManager();
        world = screen.getWorld();
        torso = new Sprite();
        legs = new Sprite();
        fullBody = new Sprite();
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
        fullBodyStateTimer = 0;
        bodyWidth = 0;
        bodyHeight = 0;
        weapon = Weapon.PISTOL;
        attackMode = AttackMode.WEAPON;
        deathType = DeathType.STABBED;
        collidingEnemies = new Array<>();
        collidingHostages = new Array<>();

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

        for(i = 1; i < 20; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("dying-1-%d", i))));
        }
        stabbed = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(i = 1; i < 30; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("dying-2-%d", i))));
        }
        bombed = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();
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
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.ENEMY_SENSOR_BITS | MetalSlug.HOSTAGE_SENSOR_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.PLAYER_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS | MetalSlug.ENEMY_SENSOR_BITS | MetalSlug.HOSTAGE_SENSOR_BITS;;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void update(float delta){
        torsoStateTimer += delta;
        legsStateTimer += delta;
        fullBodyStateTimer += delta;
        currentMovementState = movementStateStack.peek();
        currentActionState = actionStateStack.peek();

        if(collidingEnemies.size == 0 && collidingHostages.size == 0){
            attackMode = AttackMode.WEAPON;
        }
        else {
            attackMode = AttackMode.MELEE;
        }

        TextureRegion torsoRegion = new TextureRegion();
        TextureRegion legsRegion = new TextureRegion();
        TextureRegion fullBodyRegion = new TextureRegion();
        float torsoOffsetX = 0;
        float torsoOffsetY = 0;
        float legsOffsetX = 0;
        float legsOffsetY = 0;
        float fullBodyOffsetX = 0;
        float fullBodyOffsetY = 0;

        switch(currentMovementState){
            case STANDING:
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && (body.getPosition().x + bodyWidth) < screen.CAMERA_X_LIMIT){
                    move(new Vector2(0.3f, 0));

                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.RUNNING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && (body.getPosition().x - 13 * MetalSlug.MAP_SCALE) > (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2)){
                    move(new Vector2(-0.3f, 0));

                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.RUNNING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.C)){
                    move(new Vector2(0f, 3.5f));

                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.JUMPING_UP);
                    setActionState(ActionState.NEUTRAL);
                }
                else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    attack();

                    resetFrameTimers(true, false, false);
                    if(attackMode == AttackMode.MELEE){
                        setActionState(ActionState.KNIFING);
                    }
                    else {
                        setActionState(ActionState.SHOOTING);
                    }
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
                    resetFrameTimers(true, false, false);
                    lookingUpTransition = true;
                    setMovementState(MovementState.LOOKINGUP);
                    setActionState(ActionState.NEUTRAL);
                }

                if(currentActionState == ActionState.SHOOTING) {
                    torsoRegion = shootingTorso.getKeyFrame(torsoStateTimer, false);
                }
                else if(currentActionState == ActionState.KNIFING){
                    torsoRegion = knifingTorso.getKeyFrame(torsoStateTimer, false);
                }
                else{
                    torsoRegion = standingTorso.getKeyFrame(torsoStateTimer, true);
                }
                legsRegion = standingLegs;

                torsoOffsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                torsoOffsetY = (-7) * MetalSlug.MAP_SCALE;

                legsOffsetX = legs.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                break;
            case RUNNING:
                if((body.getPosition().x - 13 * MetalSlug.MAP_SCALE) <= (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2) || (body.getPosition().x + bodyWidth) >= screen.CAMERA_X_LIMIT){
                    stop(true, false);

                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.STANDING);
                }
                if(!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                    stop(true, false);

                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.STANDING);
                }
                else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && body.getLinearVelocity().x <= 1.5f && (body.getPosition().x + bodyWidth) < screen.CAMERA_X_LIMIT){
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

                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.JUMPING_FORWARD);
                    setActionState(ActionState.NEUTRAL);
                }
                else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && currentActionState != ActionState.KNIFING){
                    attack();

                    resetFrameTimers(true, false, false);
                    if(attackMode == AttackMode.MELEE){
                        setActionState(ActionState.KNIFING);
                    }
                    else {
                        setActionState(ActionState.SHOOTING);
                    }
                }

                if(currentActionState == ActionState.SHOOTING) {
                    torsoRegion = shootingTorso.getKeyFrame(torsoStateTimer, false);
                }
                else if(currentActionState == ActionState.KNIFING){
                    torsoRegion = knifingTorso.getKeyFrame(torsoStateTimer, false);
                }
                else{
                    torsoRegion = runningTorso.getKeyFrame(torsoStateTimer, true);
                }
                legsRegion = runningLegs.getKeyFrame(legsStateTimer, true);

                torsoOffsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                torsoOffsetY = (-7) * MetalSlug.MAP_SCALE;

                legsOffsetX = legs.isFlipX() ? 6 * MetalSlug.MAP_SCALE : (-6) * MetalSlug.MAP_SCALE;
                break;
            case JUMPING_UP:
                if(body.getLinearVelocity().y == 0){
                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.STANDING);
                }

                torsoRegion = jumpingUpTorso.getKeyFrame(torsoStateTimer, false);
                legsRegion = jumpingUpLegs.getKeyFrame(legsStateTimer, false);

                torsoOffsetX = torso.isFlipX() ? 9 * MetalSlug.MAP_SCALE : (-9) * MetalSlug.MAP_SCALE;
                torsoOffsetY = (-3) * MetalSlug.MAP_SCALE;

                legsOffsetX = legs.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                break;
            case JUMPING_FORWARD:
                if(body.getLinearVelocity().y == 0){
                    resetFrameTimers(true, true, false);
                    setMovementState(MovementState.STANDING);
                }
                if((body.getPosition().x - 13 * MetalSlug.MAP_SCALE) <= (screen.getCamera().position.x - screen.getViewport().getWorldWidth() / 2)){
                    stop(true, false);
                }

                torsoRegion = jumpingForwardTorso.getKeyFrame(torsoStateTimer, false);
                legsRegion = jumpingForwardLegs.getKeyFrame(legsStateTimer, false);

                torsoOffsetX = torso.isFlipX() ? 9 * MetalSlug.MAP_SCALE : (-9) * MetalSlug.MAP_SCALE;
                torsoOffsetY = (-14) * MetalSlug.MAP_SCALE;

                legsOffsetX = legs.isFlipX() ? 7 * MetalSlug.MAP_SCALE : (-7) * MetalSlug.MAP_SCALE;
                break;
            case LOOKINGUP:
                if(lookingUpTransition && lookingUpTransitionTorso.isAnimationFinished(torsoStateTimer)){
                    lookingUpTransition = false;
                    resetFrameTimers(true, false, false);
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
                        resetFrameTimers(true, false, false);
                    }
                    else if(reverseLookingUpTransition && reverseLookingUpTransitionTorso.isAnimationFinished(torsoStateTimer)){
                        reverseLookingUpTransition = false;
                        resetFrameTimers(true, false, false);
                        setMovementState(MovementState.STANDING);
                        setActionState(ActionState.NEUTRAL);
                    }
                }
                else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    if(lookingUpTransition){
                        lookingUpTransition = false;
                        resetFrameTimers(true, false, false);
                    }
                    attack();

                    resetFrameTimers(true, false, false);
                    setActionState(ActionState.SHOOTING);
                }

                if(lookingUpTransition){
                    torsoRegion = lookingUpTransitionTorso.getKeyFrame(torsoStateTimer, false);
                }
                else if(reverseLookingUpTransition){
                    torsoRegion = reverseLookingUpTransitionTorso.getKeyFrame(torsoStateTimer, false);

                }
                else if(currentActionState == ActionState.SHOOTING) {
                    torsoRegion = shootingLookingUpTorso.getKeyFrame(torsoStateTimer, false);
                }
                else {
                    torsoRegion = idleLookingUpTorso.getKeyFrame(torsoStateTimer, true);
                }
                legsRegion = standingLegs;

                torsoOffsetX = torso.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                torsoOffsetY = (-2) * MetalSlug.MAP_SCALE;

                legsOffsetX = legs.isFlipX() ? 1 * MetalSlug.MAP_SCALE : (-1) * MetalSlug.MAP_SCALE;
                break;
            case DYING:
            default:
                useFullBody = true;

                if(deathType == DeathType.STABBED){
                    fullBodyRegion = stabbed.getKeyFrame(fullBodyStateTimer, false);

                    fullBodyOffsetX = fullBody.isFlipX() ? 10 * MetalSlug.MAP_SCALE : (-10) * MetalSlug.MAP_SCALE;
                }
                else {
                    fullBodyRegion = bombed.getKeyFrame(fullBodyStateTimer, false);

                    fullBodyOffsetX = fullBody.isFlipX() ? 20 * MetalSlug.MAP_SCALE : (-20) * MetalSlug.MAP_SCALE;
                    fullBodyOffsetY = (-5) * MetalSlug.MAP_SCALE;
                }

                break;
        }
        switch (currentActionState){
            case SHOOTING:
                if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    resetFrameTimers(true, false, false);
                    attack();
                }
                if(shootingTorso.isAnimationFinished(torsoStateTimer)){
                    resetFrameTimers(true, true, false);
                    setActionState(ActionState.NEUTRAL);
                }
                break;
            case KNIFING:
                if(knifingTorso.isAnimationFinished(torsoStateTimer)){
                    Array.ArrayIterator<Enemy> enemyIterator = collidingEnemies.iterator();
                    Array.ArrayIterator<Hostage> hostageIterator = collidingHostages.iterator();
                    Enemy enemy;
                    Hostage hostage;

                    while(enemyIterator.hasNext()){
                        enemy = enemyIterator.next();
                        enemy.hit();
                        enemyIterator.remove();
                    }
                    while(hostageIterator.hasNext()){
                        hostage = hostageIterator.next();
                        hostage.release();
                        hostageIterator.remove();
                    }

                    resetFrameTimers(true, true, false);
                    setActionState(ActionState.NEUTRAL);
                }
                break;
        }

        if(!useFullBody){
            if((body.getLinearVelocity().x < 0 || !isRunningRight) && !legsRegion.isFlipX()){
                legsRegion.flip(true, false);
            }
            else if((body.getLinearVelocity().x > 0 || isRunningRight)  && legsRegion.isFlipX()){
                legsRegion.flip(true, false);
            }

            if((body.getLinearVelocity().x < 0 || !isRunningRight) && !torsoRegion.isFlipX()){
                torsoRegion.flip(true, false);
            }
            else if((body.getLinearVelocity().x > 0 || isRunningRight)  && torsoRegion.isFlipX()){
                torsoRegion.flip(true, false);
            }

            legs.setRegion(legsRegion);
            torso.setRegion(torsoRegion);

            if(legs.isFlipX()){
                legs.setBounds(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - legs.getRegionWidth() * MetalSlug.MAP_SCALE + legsOffsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + legsOffsetY, legsRegion.getRegionWidth() * MetalSlug.MAP_SCALE, legsRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
            else{
                legs.setBounds(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + legsOffsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + legsOffsetY, legsRegion.getRegionWidth() * MetalSlug.MAP_SCALE, legsRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }

            if(torso.isFlipX()){
                torso.setBounds(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - torso.getRegionWidth() * MetalSlug.MAP_SCALE + torsoOffsetX, legs.getY() + legs.getHeight() + torsoOffsetY, torsoRegion.getRegionWidth() * MetalSlug.MAP_SCALE, torsoRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
            else{
                torso.setBounds(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + torsoOffsetX, legs.getY() + legs.getHeight() + torsoOffsetY, torsoRegion.getRegionWidth() * MetalSlug.MAP_SCALE, torsoRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
        }
        else {
            if((body.getLinearVelocity().x < 0 || !isRunningRight) && !fullBodyRegion.isFlipX()){
                fullBodyRegion.flip(true, false);
            }
            else if((body.getLinearVelocity().x > 0 || isRunningRight)  && fullBodyRegion.isFlipX()){
                fullBodyRegion.flip(true, false);
            }

            fullBody.setRegion(fullBodyRegion);

            if(fullBody.isFlipX()){
                fullBody.setBounds(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - fullBody.getRegionWidth() * MetalSlug.MAP_SCALE + fullBodyOffsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + fullBodyOffsetY, fullBodyRegion.getRegionWidth() * MetalSlug.MAP_SCALE, fullBodyRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
            else{
                fullBody.setBounds(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + fullBodyOffsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + fullBodyOffsetY, fullBodyRegion.getRegionWidth() * MetalSlug.MAP_SCALE, fullBodyRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
        }
    }

    public void draw(SpriteBatch batch){
        if(!useFullBody){
            legs.draw(batch);
            torso.draw(batch);
        }
        else {
            fullBody.draw(batch);
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

    private void resetFrameTimers(boolean torso, boolean legs, boolean fullBody){
        if(torso){
            torsoStateTimer = 0;
        }
        if(legs){
            legsStateTimer = 0;
        }
        if(fullBody){
            fullBodyStateTimer = 0;
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
                    assetManager.get("audio/sounds/pistol_shot.mp3", Sound.class).play();
                    screen.getWorldCreator().createShot(PistolShot.class, screen, this);
                    break;
            }
        }
    }

    public void kill(DeathType deathType){
        assetManager.get("audio/sounds/player_death.mp3", Sound.class).play();
        Filter filter = new Filter();
        filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.getFixtureList().forEach(fixture -> fixture.setFilterData(filter));

        this.deathType = deathType;
        setMovementState(MovementState.DYING);
        setActionState(ActionState.NEUTRAL);
        resetFrameTimers(true, true, true);
        screen.gameOver();
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

    public void addCollidingEnemy(Enemy enemy){
        if(!collidingEnemies.contains(enemy, true)){
            collidingEnemies.add(enemy);
        }
    }

    public void removeCollidingEnemy(Enemy enemy){
        collidingEnemies.removeValue(enemy, true);
    }

    public void addCollidingHostage(Hostage hostage){
        if(!collidingHostages.contains(hostage, true)){
            collidingHostages.add(hostage);
        }
    }

    public void removeCollidingHostage(Hostage hostage){
        collidingHostages.removeValue(hostage, true);
    }

    public void dispose(){

    }

    public Body getBody(){
        return body;
    }

    public float getBodyWidth(){
        return bodyWidth;
    }

    public float getBodyHeight(){
        return bodyHeight;
    }

    public boolean getIsRunningRight(){
        return isRunningRight;
    }

    public boolean getIsLookingUp(){
        return currentMovementState == MovementState.LOOKINGUP;
    }

    public boolean getIsDead(){
        return currentMovementState == MovementState.DYING;
    }
}
