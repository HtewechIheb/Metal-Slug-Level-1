package com.mygames.metalslug.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.sprites.Enemy;
import com.mygames.metalslug.sprites.Hostage;
import com.mygames.metalslug.sprites.MarcoRossi;
import com.mygames.metalslug.sprites.Shot;
import com.mygames.metalslug.tools.MissionOneWorldCreator;
import com.mygames.metalslug.tools.WorldContactListener;

public class MissionOneScreen implements Screen {
    private MetalSlug game;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private TiledMapRenderer mapRenderer;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private MissionOneWorldCreator worldCreator;
    private TextureAtlas playerTextureAtlas;
    private TextureAtlas soldierTextureAtlas;
    private TextureAtlas shotsTextureAtlas;
    private TextureAtlas hoboTextureAtlas;

    private MarcoRossi player;
    private final Vector2 GRAVITY = new Vector2(0, -10);

    public MissionOneScreen(MetalSlug game){
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(MetalSlug.V_WIDTH * MetalSlug.MAP_SCALE, MetalSlug.V_HEIGHT * MetalSlug.MAP_SCALE, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("missionone.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, MetalSlug.MAP_SCALE);

        playerTextureAtlas = new TextureAtlas("sprites/player.atlas");
        soldierTextureAtlas = new TextureAtlas("sprites/soldier.atlas");
        shotsTextureAtlas = new TextureAtlas("sprites/shots.atlas");
        hoboTextureAtlas = new TextureAtlas("sprites/hobo.atlas");

        world = new World(GRAVITY, true);
        debugRenderer = new Box2DDebugRenderer();

        player = new MarcoRossi(this);

        worldCreator = new MissionOneWorldCreator(this);
        worldCreator.createWorld();
        world.setContactListener(new WorldContactListener());
    }

    public void handleInput(float delta){
    }

    public void update(float delta){
        handleInput(delta);

        world.step(1/60f, 6, 2);
        if(camera.position.x < player.getBody().getPosition().x){
            camera.position.x = player.getBody().getPosition().x;
            camera.update();
        }

        for(Shot shot : worldCreator.getShots()){
            shot.update(delta);
        }
        for(Enemy enemy : worldCreator.getEnemies()){
            enemy.update(delta);
        }
        for(Hostage hostage : worldCreator.getHostages()){
            hostage.update(delta);
        }
        player.update(delta);
        mapRenderer.setView(camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        mapRenderer.render();
        debugRenderer.render(world, camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for(Hostage hostage : worldCreator.getHostages()){
            hostage.draw(game.batch);
        }
        for(Enemy enemy : worldCreator.getEnemies()){
            enemy.draw(game.batch);
        }
        for(Shot shot : worldCreator.getShots()){
            shot.draw(game.batch);
        }
        player.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Viewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public World getWorld(){
        return world;
    }

    public TiledMap getMap(){
        return map;
    }

    public MarcoRossi getPlayer(){
        return player;
    }

    public TextureAtlas getPlayerTextureAtlas() {
        return playerTextureAtlas;
    }

    public TextureAtlas getSoldierTextureAtlas() {
        return soldierTextureAtlas;
    }

    public TextureAtlas getShotsTextureAtlas() {
        return shotsTextureAtlas;
    }

    public TextureAtlas getHoboTextureAtlas() {
        return hoboTextureAtlas;
    }

    public MissionOneWorldCreator getWorldCreator(){
        return worldCreator;
    }
}
