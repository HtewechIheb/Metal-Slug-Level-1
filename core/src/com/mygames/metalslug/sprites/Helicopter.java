package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.EnumSet;
import java.util.Stack;

public class Helicopter extends Enemy {
    private final float BODY_RECTANGLE_WIDTH = 89f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 66f * MetalSlug.MAP_SCALE;
    private final float HOVERING_DISTANCE = 2f * MetalSlug.MAP_SCALE;

    public enum State {
        FLYING_RIGHT,
        FLYING_LEFT,
        HOVERING,
        EXPLODING
    }

    private TextureAtlas helicopterTextureAtlas;
    private TextureAtlas explosionsTextureAtlas;
    private Sprite bodySprite;
    private Sprite bladesSprite;
    private Sprite fullBodySprite;
    private float bodyWidth;
    private float bodyHeight;

    private Animation<TextureRegion> hovering;
    private Animation<TextureRegion> flyingLeft;
    private Animation<TextureRegion> flyingRight;
    private Animation<TextureRegion> exploding;
    private Array<TextureRegion> bodyFrames;
    private Array<Animation<TextureRegion>> hoveringBlades;

    private boolean flyingRightToHovering = false;
    private boolean flyingLeftToHovering = false;

    private State currentState;
    private Stack<State> stateStack;
    private float stateTimer;
    private float bombTimer;
    private boolean singleSprite = false;

    private boolean bombingActive = false;
    private int healthPoints;

    public Helicopter(MissionOneScreen screen, Vector2 position){
        super(screen, position);
        helicopterTextureAtlas = screen.getHelicopterTextureAtlas();
        explosionsTextureAtlas = screen.getExplosionsTextureAtlas();
        bodySprite = new Sprite();
        bladesSprite = new Sprite();
        fullBodySprite = new Sprite();
        currentState = State.HOVERING;
        stateStack = new Stack<>();
        stateStack.push(currentState);
        stateTimer = 0;
        bombTimer = 0;
        healthPoints = 20;

        defineEnemy();
        defineAnimations();
    }

    private void defineAnimations(){
        byte i;
        Array<TextureRegion> frames = new Array<>();

        bodyFrames = new Array<>();
        for(i = 1; i < 8; i++){
            frames.add(new TextureRegion(helicopterTextureAtlas.findRegion(String.format("flying-%d", i))));
        }
        bodyFrames.addAll(frames);
        frames.clear();
        frames.addAll(bodyFrames.get(2), bodyFrames.get(1), bodyFrames.get(0));
        flyingRight = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();
        frames.addAll(bodyFrames.get(3));
        hovering = new Animation<TextureRegion>(0.4f, frames);
        frames.clear();
        frames.addAll(bodyFrames.get(4), bodyFrames.get(5), bodyFrames.get(6));
        flyingLeft = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for(i = 1; i < 28; i++){
            frames.add(new TextureRegion(explosionsTextureAtlas.findRegion(String.format("big-explosion-%d", i))));
        }
        exploding = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();

        hoveringBlades = new Array<>();
        for(i = 1; i < 8; i++){
            for(byte j = 1; j < 6; j++){
                frames.add(new TextureRegion(helicopterTextureAtlas.findRegion(String.format("blades-%d-%d", i, j))));
            }
            hoveringBlades.add(new Animation<TextureRegion>(0.12f, frames));
            frames.clear();
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();

        bodyWidth = BODY_RECTANGLE_WIDTH;
        bodyHeight = BODY_RECTANGLE_HEIGHT;

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (bodyWidth / 2), position.y + (bodyHeight / 2));
        bodyDef.gravityScale = 0.0f;
        body = world.createBody(bodyDef);
        body.setActive(false);

        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.PLAYER_SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;
        bombTimer += delta;
        currentState = stateStack.peek();

        if(body.getPosition().x - player.getBody().getPosition().x < 192 * MetalSlug.MAP_SCALE){
            body.setActive(true);
            bombingActive = true;
        }

        TextureRegion bodyRegion = new TextureRegion();
        TextureRegion bladesRegion = new TextureRegion();
        TextureRegion fullBodyRegion = new TextureRegion();
        float bodyOffsetX = 0;
        float bodyOffsetY = 0;
        float bladesOffsetX = 0;
        float bladesOffsetY = 0;
        float fullBodyOffsetX = 0;
        float fullBodyOffsetY = 0;
        int index;

        if(flyingRightToHovering){
            bodyRegion = flyingRight.getKeyFrame(stateTimer, false);

            index = bodyFrames.indexOf(bodyRegion, false);
            bladesRegion = hoveringBlades.get(index).getKeyFrame(stateTimer, true);

            switch (index){
                case 0:
                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-9f) * MetalSlug.MAP_SCALE;
                    break;
                case 1:
                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-6f) * MetalSlug.MAP_SCALE;
                    break;
                case 2:
                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-7f) * MetalSlug.MAP_SCALE;
                    break;
            }

            if(flyingRight.isAnimationFinished(stateTimer)){
                resetFrameTimer();
                flyingRightToHovering = false;
                flyingRight.setPlayMode(Animation.PlayMode.NORMAL);
            }
        }
        else if(flyingLeftToHovering){
            bodyRegion = flyingLeft.getKeyFrame(stateTimer, false);

            index = bodyFrames.indexOf(bodyRegion, false);
            bladesRegion = hoveringBlades.get(index).getKeyFrame(stateTimer, true);

            switch (index){
                case 4:
                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-9f) * MetalSlug.MAP_SCALE;
                    break;
                case 5:
                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-9f) * MetalSlug.MAP_SCALE;
                    break;
                case 6:
                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-15f) * MetalSlug.MAP_SCALE;
                    break;
            }

            if(flyingLeft.isAnimationFinished(stateTimer)){
                resetFrameTimer();
                flyingLeftToHovering = false;
                flyingLeft.setPlayMode(Animation.PlayMode.NORMAL);
            }
        }
        else {
            switch (currentState){
                case FLYING_RIGHT:
                    bodyRegion = flyingRight.getKeyFrame(stateTimer, false);

                    index = bodyFrames.indexOf(bodyRegion, false);
                    bladesRegion = hoveringBlades.get(index).getKeyFrame(stateTimer, true);

                    switch (index){
                        case 0:
                            bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                            bladesOffsetY = (-9f) * MetalSlug.MAP_SCALE;
                            break;
                        case 1:
                            bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                            bladesOffsetY = (-6f) * MetalSlug.MAP_SCALE;
                            break;
                        case 2:
                            bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                            bladesOffsetY = (-7f) * MetalSlug.MAP_SCALE;
                            break;
                    }
                break;
                case FLYING_LEFT:
                    bodyRegion = flyingLeft.getKeyFrame(stateTimer, false);

                    index = bodyFrames.indexOf(bodyRegion, false);
                    bladesRegion = hoveringBlades.get(index).getKeyFrame(stateTimer, true);

                    switch (index){
                        case 4:
                            bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                            bladesOffsetY = (-9f) * MetalSlug.MAP_SCALE;
                            break;
                        case 5:
                            bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                            bladesOffsetY = (-9f) * MetalSlug.MAP_SCALE;
                            break;
                        case 6:
                            bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                            bladesOffsetY = (-15f) * MetalSlug.MAP_SCALE;
                            break;
                    }
                break;
                case HOVERING:
                    bodyRegion = hovering.getKeyFrame(stateTimer, true);

                    index = bodyFrames.indexOf(bodyRegion, false);
                    bladesRegion = hoveringBlades.get(index).getKeyFrame(stateTimer, true);

                    bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                    bladesOffsetY = (-6f) * MetalSlug.MAP_SCALE;
                break;
                case EXPLODING:
                default:
                    singleSprite = true;

                    fullBodyRegion = exploding.getKeyFrame(stateTimer, false);

                    break;
            }
        }

        if(!singleSprite){
            bodySprite.setRegion(bodyRegion);
            bladesSprite.setRegion(bladesRegion);

            if (bodySprite.isFlipX()) {
                bodySprite.setBounds(body.getPosition().x + (bodyWidth / 2) - bodySprite.getRegionWidth() * MetalSlug.MAP_SCALE + bodyOffsetX, body.getPosition().y - (bodyHeight / 2) + bodyOffsetY, bodyRegion.getRegionWidth() * MetalSlug.MAP_SCALE, bodyRegion.getRegionHeight() * MetalSlug.MAP_SCALE);

            } else {
                bodySprite.setBounds(body.getPosition().x - (bodyWidth / 2) + bodyOffsetX, body.getPosition().y - (bodyHeight / 2) + bodyOffsetY, bodyRegion.getRegionWidth() * MetalSlug.MAP_SCALE, bodyRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }

            if (bladesSprite.isFlipX()) {
                bladesSprite.setBounds(body.getPosition().x + (bodyWidth / 2) - bladesSprite.getRegionWidth() * MetalSlug.MAP_SCALE + bladesOffsetX, bodySprite.getY() + bodySprite.getRegionHeight() * MetalSlug.MAP_SCALE + bladesOffsetY, bladesRegion.getRegionWidth() * MetalSlug.MAP_SCALE, bladesRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            } else {
                bladesSprite.setBounds(body.getPosition().x + (bodyWidth / 2) - bladesSprite.getRegionWidth() * MetalSlug.MAP_SCALE + bladesOffsetX, bodySprite.getY() + bodySprite.getRegionHeight() * MetalSlug.MAP_SCALE + bladesOffsetY, bladesRegion.getRegionWidth() * MetalSlug.MAP_SCALE, bladesRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
        }
        else {
            fullBodySprite.setRegion(fullBodyRegion);

            if (fullBodySprite.isFlipX()) {
                fullBodySprite.setBounds(body.getPosition().x + (bodyWidth / 2) - fullBodySprite.getRegionWidth() * MetalSlug.MAP_SCALE + fullBodyOffsetX, body.getPosition().y - (bodyHeight / 2) + fullBodyOffsetY, fullBodyRegion.getRegionWidth() * MetalSlug.MAP_SCALE, fullBodyRegion.getRegionHeight() * MetalSlug.MAP_SCALE);

            } else {
                fullBodySprite.setBounds(body.getPosition().x - (bodyWidth / 2) + fullBodyOffsetX, body.getPosition().y - (bodyHeight / 2) + fullBodyOffsetY, fullBodyRegion.getRegionWidth() * MetalSlug.MAP_SCALE, fullBodyRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
            }
        }

        switch (currentState){
            case FLYING_RIGHT:
                if(Math.abs(player.getBody().getPosition().x - body.getPosition().x) < HOVERING_DISTANCE){
                    resetFrameTimer();
                    flyingRightToHovering = true;
                    flyingRight.setPlayMode(Animation.PlayMode.REVERSED);
                    stop(true, false);
                    setState(State.HOVERING);
                }
                else if(player.getBody().getPosition().x - body.getPosition().x >= HOVERING_DISTANCE && body.getLinearVelocity().x <= 0.6f){
                    move(new Vector2(0.2f, 0));
                }
                break;
            case FLYING_LEFT:
                if(Math.abs(player.getBody().getPosition().x - body.getPosition().x) < HOVERING_DISTANCE){
                    resetFrameTimer();
                    flyingLeftToHovering = true;
                    flyingLeft.setPlayMode(Animation.PlayMode.REVERSED);
                    stop(true, false);
                    setState(State.HOVERING);
                }
                else if(body.getPosition().x - player.getBody().getPosition().x >= HOVERING_DISTANCE && body.getLinearVelocity().x >= -0.6f){
                    move(new Vector2(-0.2f, 0));
                }
                break;
            case HOVERING:
                if(body.getLinearVelocity().x != 0){
                    stop(true, false);
                }
                if(player.getBody().getPosition().x - body.getPosition().x >= HOVERING_DISTANCE && body.getLinearVelocity().x <= 0.6f){
                    resetFrameTimer();
                    setState(State.FLYING_RIGHT);
                }
                else if(body.getPosition().x - player.getBody().getPosition().x >= HOVERING_DISTANCE && body.getLinearVelocity().x >= -0.6f){
                    resetFrameTimer();
                    setState(State.FLYING_LEFT);
                }
                break;
            case EXPLODING:
                if(exploding.isAnimationFinished(stateTimer)){
                    remove();
                }

                break;
        }

        if(bombingActive && bombTimer >= 2f && !player.getIsDead() && currentState != State.EXPLODING){
            Helicopter helicopter = this;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    screen.getWorldCreator().createBomb(screen, player, helicopter);
                }
            }, 0, 0.15f, 2);
            resetBombTimer();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if(!singleSprite){
            bodySprite.draw(batch);
            bladesSprite.draw(batch);
        }
        else {
            fullBodySprite.draw(batch);
        }
    }

    private void setState(State state){
        stateStack.pop();
        stateStack.push(state);
    }

    private void resetFrameTimer(){
        stateTimer = 0;
    }

    private void resetBombTimer(){
        bombTimer = 0;
    }

    @Override
    public void hit(){
        if(currentState != State.EXPLODING){
            healthPoints--;
            if(healthPoints == 0){
                assetManager.get("audio/sounds/helicopter_explosion.mp3", Sound.class).play();
                screen.addScore(screen.HELICOPTER_KILL_SCORE);
                stop(true, true);
                flyingRightToHovering = false;
                flyingLeftToHovering = false;
                setState(State.EXPLODING);
                resetFrameTimer();
                screen.gameWon();
            }
        }
    }

    @Override
    public void dispose(){

    }

    public float getShotX(){
        return body.getPosition().x;
    }

    public float getShotY(){
        return body.getPosition().y - bodyHeight/2;
    }
}
