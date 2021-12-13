package com.mygames.metalslug.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;

public class Bomb implements Disposable {
    private final float SHOT_WIDTH = 14f * MetalSlug.MAP_SCALE;
    private final float SHOT_HEIGHT = 25f  * MetalSlug.MAP_SCALE;
    private final float EXPLOSION_RADIUS = 30f  * MetalSlug.MAP_SCALE;

    private final float offsetX = 4f * MetalSlug.MAP_SCALE;
    private final float offsetY = 20f * MetalSlug.MAP_SCALE;
    private final float explosionOffsetY = 8f * MetalSlug.MAP_SCALE;

    public enum State {
        FALLING,
        EXPLODING
    }

    private Body body;
    private MarcoRossi player;
    private MissionOneScreen screen;
    private World world;
    private AssetManager assetManager;
    private Helicopter helicopter;
    private Vector2 position;

    private Animation<TextureRegion> exploding;
    private TextureRegion falling;
    private float stateTimer;
    private State currentState;

    private TextureAtlas helicopterTextureAtlas;
    private TextureAtlas explosionsTextureAtlas;
    private Sprite sprite;

    private boolean toExplode = false;
    private boolean playerInProximity = false;

    public Bomb(MissionOneScreen screen, MarcoRossi player, Helicopter helicopter){
        this.screen = screen;
        this.world = screen.getWorld();
        this.assetManager = screen.getAssetManager();
        this.player = player;
        this.helicopter = helicopter;
        helicopterTextureAtlas = screen.getHelicopterTextureAtlas();
        explosionsTextureAtlas = screen.getExplosionsTextureAtlas();
        sprite = new Sprite();
        currentState = State.FALLING;
        stateTimer = 0;

        defineAnimations();
        defineShot();
    }

    private void defineAnimations(){
        Array<TextureRegion> frames = new Array<>();

        falling = new TextureRegion(helicopterTextureAtlas.findRegion("bomb-4"));

        for(byte i = 1; i < 27; i++){
            frames.add(new TextureRegion(explosionsTextureAtlas.findRegion(String.format("medium-explosion-%d", i))));
        }
        exploding = new Animation<TextureRegion>(0.05f, frames);
        frames.clear();
    }

    private void defineShot() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(helicopter.getShotX() + offsetX, helicopter.getShotY() - SHOT_HEIGHT/2 + offsetY);
        bodyDef.gravityScale = 0.2f;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(SHOT_WIDTH / 2, SHOT_HEIGHT / 2);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = MetalSlug.HELICOPTER_BOMB_BITS;
        fixtureDef.filter.maskBits = MetalSlug.GROUND_BITS | MetalSlug.OBJECT_BITS;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void update(float delta){
        playerInProximity = Math.abs(player.getBody().getPosition().x - body.getPosition().x) < EXPLOSION_RADIUS;

        if(toExplode){
            position = new Vector2(body.getPosition());

            currentState = State.EXPLODING;
            toExplode = false;
        }

        switch (currentState){
            case EXPLODING:
                stateTimer += delta;
                TextureRegion region = exploding.getKeyFrame(stateTimer, false);

                if(exploding.isAnimationFinished(stateTimer)){
                    remove();
                }

                sprite.setRegion(region);
                sprite.setBounds(position.x - ((float)region.getRegionWidth()/2) * MetalSlug.MAP_SCALE, position.y - ((float)region.getRegionHeight()/2) * MetalSlug.MAP_SCALE + explosionOffsetY, region.getRegionWidth() * MetalSlug.MAP_SCALE, region.getRegionHeight() * MetalSlug.MAP_SCALE);
                break;
            case FALLING:
            default:
                sprite.setRegion(falling);
                sprite.setBounds(body.getPosition().x - SHOT_WIDTH/2, body.getPosition().y - SHOT_HEIGHT/2, SHOT_WIDTH, SHOT_HEIGHT);
                break;
        }
    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void explode(){
        assetManager.get("audio/sounds/bomb_detonation.mp3", Sound.class).play();
        toExplode = true;

        if(playerInProximity && !player.getIsDead()){
            player.kill(MarcoRossi.DeathType.BOMBED);
        }
    }

    private void remove(){
        world.destroyBody(body);
        screen.getWorldCreator().getBombs().removeValue(this, true);
    }

    @Override
    public void dispose() {

    }
}
