package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
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

public class MarcoRossi {
    private final float BODY_RECTANGLE_WIDTH = 18;
    private final float BODY_RECTANGLE_HEIGHT = 26;
    private final float BODY_CIRCLE_RADIUS = 9;

    public Body body;
    public enum State {
        STANDING,
        RUNNING,
        SHOOTING
    }

    private MissionOneScreen screen;
    private World world;
    private TextureAtlas textureAtlas;
    private Sprite torso;
    private Sprite legs;
    private Animation<TextureRegion> standingTorso;
    private Animation<TextureRegion> runningTorso;
    private Animation<TextureRegion> shootingTorso;
    private TextureRegion standingLegs;
    private Animation<TextureRegion> runningLegs;

    private State currentState;
    private State previousState;
    private float stateTimer;

    public MarcoRossi(MissionOneScreen screen){
        this.screen = screen;
        world = screen.getWorld();
        torso = new Sprite();
        legs = new Sprite();
        textureAtlas = new TextureAtlas("sprites/MarcoRossi/marcorossi.atlas");
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

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

        standingLegs = new TextureRegion(textureAtlas.findRegion("idle-legs"));

        defineCharacter();
        defineSprites();
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

        headShape.setRadius(BODY_CIRCLE_RADIUS * MetalSlug.MAP_SCALE);
        headShape.setPosition(new Vector2(0, (BODY_RECTANGLE_HEIGHT / 2) * MetalSlug.MAP_SCALE));
        bodyShape.setAsBox((BODY_RECTANGLE_WIDTH / 2) * MetalSlug.MAP_SCALE, (BODY_RECTANGLE_HEIGHT / 2) * MetalSlug.MAP_SCALE);

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(112 * MetalSlug.MAP_SCALE, 150 * MetalSlug.MAP_SCALE);
        body = world.createBody(bodyDef);

        fixtureDef.shape = headShape;
        body.createFixture(fixtureDef);

        fixtureDef.shape = bodyShape;
        body.createFixture(fixtureDef);
    }

    private void defineSprites(){

    }

    public void update(float delta){
        currentState = getState();
        Gdx.app.log("velocity", body.getLinearVelocity().x + "");

        TextureRegion torsoRegion = getTorsoFrame();
        TextureRegion legsRegion = getLegsFrame();
        torso.setRegion(torsoRegion);
        torso.setBounds(0, 0, torsoRegion.getRegionWidth() * MetalSlug.MAP_SCALE, torsoRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
        legs.setRegion(legsRegion);
        legs.setBounds(0, 0, legsRegion.getRegionWidth() * MetalSlug.MAP_SCALE, legsRegion.getRegionHeight() * MetalSlug.MAP_SCALE);
        legs.setPosition(body.getPosition().x - legs.getWidth() / 2, body.getPosition().y - (BODY_RECTANGLE_HEIGHT / 2) * MetalSlug.MAP_SCALE);
        if(torsoRegion.isFlipX()){
            torso.setPosition(body.getPosition().x + (BODY_RECTANGLE_WIDTH / 2) * MetalSlug.MAP_SCALE - torsoRegion.getRegionWidth() * MetalSlug.MAP_SCALE + 1 * MetalSlug.MAP_SCALE, legs.getY() + legs.getHeight() - 7 * MetalSlug.MAP_SCALE);
        }
        else{
            torso.setPosition(body.getPosition().x - (BODY_RECTANGLE_WIDTH / 2) * MetalSlug.MAP_SCALE - 1 * MetalSlug.MAP_SCALE, legs.getY() + legs.getHeight() - 7 * MetalSlug.MAP_SCALE);
        }

        stateTimer = previousState == currentState ? stateTimer + delta : 0;
        previousState = currentState;
    }

    private TextureRegion getTorsoFrame(){
        TextureRegion region;

        switch (currentState){
            case RUNNING:
                region = runningTorso.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = standingTorso.getKeyFrame(stateTimer, true);
                break;
        }

        if(body.getLinearVelocity().x < 0 && !region.isFlipX()){
            region.flip(true, false);
        }
        else if(body.getLinearVelocity().x > 0 && region.isFlipX()){
            region.flip(true, false);
        }

        return region;
    }

    private TextureRegion getLegsFrame(){
        TextureRegion region;

        switch (currentState){
            case RUNNING:
                region = runningLegs.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = standingLegs;
                break;
        }

        if(body.getLinearVelocity().x < 0 && !region.isFlipX()){
            region.flip(true, false);
        }
        else if(body.getLinearVelocity().x > 0 && region.isFlipX()){
            region.flip(true, false);
        }

        return region;
    }

    private State getState(){
        if(body.getLinearVelocity().x == 0){
            return State.STANDING;
        }
        else{
            return State.RUNNING;
        }
    }
}
