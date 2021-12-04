package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
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
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.Stack;

public class Helicopter extends Enemy {
    private final float BODY_RECTANGLE_WIDTH = 89f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 66f * MetalSlug.MAP_SCALE;

    public enum State {
        FLYING_DOWN,
        HOVERING
    }

    private TextureAtlas textureAtlas;
    private Sprite bodySprite;
    private Sprite bladesSprite;
    private float bodyWidth;
    private float bodyHeight;

    private Animation<TextureRegion> hovering;
    private Array<TextureRegion> hoveringFrames;
    private Array<Animation<TextureRegion>> hoveringBlades1;
    private Animation<TextureRegion> hoveringBlades2;
    private Animation<TextureRegion> hoveringBlades3;
    private Animation<TextureRegion> hoveringBlades4;
    private Animation<TextureRegion> hoveringBlades5;
    private Animation<TextureRegion> hoveringBlades6;
    private Animation<TextureRegion> hoveringBlades7;

    private State currentState;
    private Stack<State> stateStack;
    private float stateTimer;

    private boolean isRunningRight = false;

    public Helicopter(MissionOneScreen screen, Vector2 position){
        super(screen, position);
        textureAtlas = screen.getHelicopterTextureAtlas();
        bodySprite = new Sprite();
        bladesSprite = new Sprite();
        currentState = State.FLYING_DOWN;
        stateStack = new Stack<>();
        stateStack.push(currentState);
        stateTimer = 0;

        defineEnemy();
        defineAnimations();
    }

    private void defineAnimations(){
        byte i;
        Array<TextureRegion> frames = new Array<>();

        hoveringFrames = new Array<>();
        for(i = 1; i < 8; i++){
            frames.add(new TextureRegion(textureAtlas.findRegion(String.format("flying-%d", i))));
        }
        hoveringFrames.addAll(frames);
        hovering = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        hoveringBlades1 = new Array<>();
        for(i = 1; i < 8; i++){
            for(byte j = 1; j < 6; j++){
                frames.add(new TextureRegion(textureAtlas.findRegion(String.format("blades-%d-%d", i, j))));
            }
            hoveringBlades1.add(new Animation<TextureRegion>(0.1f, frames));
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

        bodyShape.setAsBox(bodyWidth / 2, bodyHeight / 2);
        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.ENEMY_BITS;
        fixtureDef.filter.maskBits = MetalSlug.SHOT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;
        currentState = stateStack.peek();

        TextureRegion bodyRegion;
        TextureRegion bladesRegion;
        float bodyOffsetX = 0;
        float bodyOffsetY = 0;
        float bladesOffsetX = 0;
        float bladesOffsetY = 0;

        switch (currentState){
            case FLYING_DOWN:
            case HOVERING:
            default:
                bodyRegion = hovering.getKeyFrame(stateTimer, true);

                int index = hoveringFrames.indexOf(bodyRegion, false);
                bladesRegion = hoveringBlades1.get(index).getKeyFrame(stateTimer, true);

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
                    case 3:
                        bladesOffsetX = (-13f) * MetalSlug.MAP_SCALE;
                        bladesOffsetY = (-6f) * MetalSlug.MAP_SCALE;
                        break;
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
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !bodyRegion.isFlipX()){
            bodyRegion.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && bodyRegion.isFlipX()){
            bodyRegion.flip(true, false);
        }

        if((body.getLinearVelocity().x < 0 || !isRunningRight) && !bladesRegion.isFlipX()){
            bladesRegion.flip(true, false);
        }
        else if((body.getLinearVelocity().x > 0 || isRunningRight)  && bladesRegion.isFlipX()){
            bladesRegion.flip(true, false);
        }

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

        switch (currentState){
            case FLYING_DOWN:
                break;
            case HOVERING:
                break;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        bodySprite.draw(batch);
        bladesSprite.draw(batch);
    }

    private void setState(State state){
        stateStack.pop();
        stateStack.push(state);
    }

    private void resetFrameTimer(){
        stateTimer = 0;
    }

    @Override
    public void kill() {

    }
}
