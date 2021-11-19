package com.mygames.metalslug.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

import java.util.EnumSet;

public class Hobo extends Hostage {
    private final float BODY_RECTANGLE_WIDTH = 18f * MetalSlug.MAP_SCALE;
    private final float BODY_RECTANGLE_HEIGHT = 26f * MetalSlug.MAP_SCALE;
    private final float BODY_CIRCLE_RADIUS = 9f * MetalSlug.MAP_SCALE;

    public enum State {
        HANGING,
        FREE
    }

    private TextureAtlas textureAtlas;
    private Sprite sprite;

    private Animation<TextureRegion> hanging;
    private Animation<TextureRegion> free;

    private EnumSet<State> currentState;
    private EnumSet<State> previousState;
    private float stateTimer;

    private boolean isRunning;
    private boolean isRunningRight;
    private boolean isHanging;

    private boolean toBeDestroyed;

    public Hobo(MissionOneScreen screen, Vector2 position){
        super(screen, position);

        textureAtlas = screen.getHoboTextureAtlas();
        sprite = new Sprite();
        stateTimer = 0;
        currentState = EnumSet.of(State.HANGING);
        previousState = EnumSet.of(State.HANGING);
        isRunning = false;
        isRunningRight = false;
        toBeDestroyed = false;

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-1")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-2")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-3")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-4")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-5")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-6")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-9")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-8")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-7")));
        frames.add(new TextureRegion(textureAtlas.findRegion("hanging-6")));

        hanging = new Animation<TextureRegion>(0.18f, frames);
    }

    @Override
    protected void defineHostage() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape headShape = new CircleShape();
        PolygonShape bodyShape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + (BODY_RECTANGLE_WIDTH / 2), position.y + (BODY_RECTANGLE_HEIGHT / 2));
        body = world.createBody(bodyDef);

        headShape.setRadius(BODY_CIRCLE_RADIUS);
        headShape.setPosition(new Vector2(0, BODY_RECTANGLE_HEIGHT / 2));
        bodyShape.setAsBox(BODY_RECTANGLE_WIDTH / 2, BODY_RECTANGLE_HEIGHT / 2);

        fixtureDef.shape = headShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);

        fixtureDef.shape = bodyShape;
        fixtureDef.filter.categoryBits = MetalSlug.HOSTAGE_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
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
        else {
            region = hanging.getKeyFrame(stateTimer, true);
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
        else{
            offsetX = sprite.isFlipX() ? 0 * MetalSlug.MAP_SCALE : 0 * MetalSlug.MAP_SCALE;
            offsetY = 0 * MetalSlug.MAP_SCALE;
        }

        if(sprite.isFlipX()){
            sprite.setPosition(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) - sprite.getRegionWidth() * MetalSlug.MAP_SCALE + offsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + offsetY);
        }
        else{
            sprite.setPosition(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2)  + offsetX, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) + offsetY);
        }
    }

    private EnumSet<State> getState(){
        EnumSet<State> state = EnumSet.noneOf(State.class);

        if(isHanging){
            state.add(State.HANGING);
        }

        return state;
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void free() {

    }
}
