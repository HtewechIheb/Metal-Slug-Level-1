package com.mygames.metalslug.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.sprites.Bomb;
import com.mygames.metalslug.sprites.Enemy;
import com.mygames.metalslug.sprites.Hostage;
import com.mygames.metalslug.sprites.MarcoRossi;
import com.mygames.metalslug.sprites.Shot;
import com.mygames.metalslug.stages.Hud;
import com.mygames.metalslug.tools.MissionOneWorldCreator;
import com.mygames.metalslug.tools.WorldContactListener;

public class MissionOneScreen implements Screen {
    public final float CAMERA_X_LIMIT = 1840f * MetalSlug.MAP_SCALE;
    public final int ENEMY_KILL_SCORE = 100;
    public final int HOSTAGE_SAVE_SCORE = 100;
    public final int HELICOPTER_KILL_SCORE = 1000;

    private final Vector2 GRAVITY = new Vector2(0, -10);
    private int score = 0;

    private MetalSlug game;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private TiledMapRenderer mapRenderer;
    private World world;
    private AssetManager assetManager;
    private Box2DDebugRenderer debugRenderer;
    private MissionOneWorldCreator worldCreator;
    private TextureAtlas playerTextureAtlas;
    private TextureAtlas soldierTextureAtlas;
    private TextureAtlas helicopterTextureAtlas;
    private TextureAtlas explosionsTextureAtlas;
    private TextureAtlas shotsTextureAtlas;
    private TextureAtlas hoboTextureAtlas;

    private Hud hud;
    private MarcoRossi player;
    private Music music;

    public MissionOneScreen(MetalSlug game, AssetManager assetManager){
        this.game = game;
        this.assetManager = assetManager;

        camera = new OrthographicCamera();
        viewport = new FitViewport(MetalSlug.V_WIDTH * MetalSlug.MAP_SCALE, MetalSlug.V_HEIGHT * MetalSlug.MAP_SCALE, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        hud = new Hud(game.batch, this);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("missionone.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, MetalSlug.MAP_SCALE);

        playerTextureAtlas = new TextureAtlas("sprites/player.atlas");
        soldierTextureAtlas = new TextureAtlas("sprites/soldier.atlas");
        helicopterTextureAtlas = new TextureAtlas("sprites/helicopter.atlas");
        explosionsTextureAtlas = new TextureAtlas("sprites/explosions.atlas");
        shotsTextureAtlas = new TextureAtlas("sprites/shots.atlas");
        hoboTextureAtlas = new TextureAtlas("sprites/hobo.atlas");

        world = new World(GRAVITY, true);
        debugRenderer = new Box2DDebugRenderer();

        player = new MarcoRossi(this);

        worldCreator = new MissionOneWorldCreator(this);
        worldCreator.createWorld();
        world.setContactListener(new WorldContactListener());

        music = this.assetManager.get("audio/music/missionone.mp3", Music.class);
        music.setLooping(true);
        music.play();

        this.assetManager.get("audio/sounds/missionone_start.mp3", Sound.class).play();
    }

    public void handleInput(float delta){
    }

    public void update(float delta){
        handleInput(delta);

        world.step(1/60f, 6, 2);

        if(camera.position.x < player.getBody().getPosition().x && (camera.position.x + camera.viewportWidth/2) < CAMERA_X_LIMIT){
            camera.position.x = player.getBody().getPosition().x;
            camera.update();
        }

        for(Shot shot : worldCreator.getShots()){
            shot.update(delta);
        }
        for(Enemy enemy : worldCreator.getEnemies()){
            enemy.update(delta);
        }
        for(Bomb bomb : worldCreator.getBombs()){
            bomb.update(delta);
        }
        for(Hostage hostage : worldCreator.getHostages()){
            hostage.update(delta);
        }
        player.update(delta);
        mapRenderer.setView(camera);

        hud.update(delta);
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
        for(Bomb bomb : worldCreator.getBombs()){
            bomb.draw(game.batch);
        }
        for(Enemy enemy : worldCreator.getEnemies()){
            enemy.draw(game.batch);
        }
        for(Shot shot : worldCreator.getShots()){
            shot.draw(game.batch);
        }
        player.draw(game.batch);
        game.batch.end();
        hud.stage.draw();
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
        map.dispose();
        world.dispose();
        assetManager.dispose();
        debugRenderer.dispose();
        playerTextureAtlas.dispose();
        soldierTextureAtlas.dispose();
        helicopterTextureAtlas.dispose();
        explosionsTextureAtlas.dispose();
        shotsTextureAtlas.dispose();
        hoboTextureAtlas.dispose();
        for(Hostage hostage : worldCreator.getHostages()){
            hostage.dispose();
        }
        for(Bomb bomb : worldCreator.getBombs()){
            bomb.dispose();
        }
        for(Enemy enemy : worldCreator.getEnemies()){
            enemy.dispose();
        }
        for(Shot shot : worldCreator.getShots()){
            shot.dispose();
        }
        player.dispose();
    }

    public void addScore(int value){
        score += value;
    }

    public int getScore(){
        return score;
    }

    public void gameWon(){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                assetManager.get("audio/sounds/mission_complete.mp3", Sound.class).play();

            }
        }, 2f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new GameWonScreen(game, assetManager, score));
            }
        }, 8f);
    }

    public void gameOver(){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                music.stop();
                game.setScreen(new GameOverScreen(game, assetManager));
            }
        }, 5f);
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

    public TextureAtlas getHelicopterTextureAtlas(){
        return helicopterTextureAtlas;
    }

    public TextureAtlas getExplosionsTextureAtlas(){
        return explosionsTextureAtlas;
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

    public AssetManager getAssetManager(){
        return assetManager;
    }
}
