package com.mygames.metalslug.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;
import com.mygames.metalslug.sprites.Bomb;
import com.mygames.metalslug.sprites.Enemy;
import com.mygames.metalslug.sprites.Helicopter;
import com.mygames.metalslug.sprites.Hobo;
import com.mygames.metalslug.sprites.Hostage;
import com.mygames.metalslug.sprites.MarcoRossi;
import com.mygames.metalslug.sprites.PistolShot;
import com.mygames.metalslug.sprites.Shot;
import com.mygames.metalslug.sprites.Soldier;

import java.util.Properties;

public class MissionOneWorldCreator {
    private MissionOneScreen screen;
    private World world;
    private TiledMap map;

    private Array<Shot> shots;
    private Array<Bomb> bombs;
    private Array<Enemy> enemies;
    private Array<Hostage> hostages;

    public MissionOneWorldCreator(MissionOneScreen screen){
        this.screen = screen;
        world = screen.getWorld();
        map = screen.getMap();

        shots = new Array<>();
        bombs = new Array<>();
        enemies = new Array<>();
        hostages = new Array<>();
    }

    public void createWorld(){
        Body body;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        for(RectangleMapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = object.getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) * MetalSlug.MAP_SCALE, (rectangle.getY() + rectangle.getHeight() / 2) * MetalSlug.MAP_SCALE);
            body = world.createBody(bodyDef);

            shape.setAsBox((rectangle.getWidth() / 2) * MetalSlug.MAP_SCALE, (rectangle.getHeight() / 2) * MetalSlug.MAP_SCALE);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = MetalSlug.GROUND_BITS;
            body.createFixture(fixtureDef);
        }

        for(RectangleMapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = object.getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) * MetalSlug.MAP_SCALE, rectangle.getY() * MetalSlug.MAP_SCALE);
            body = world.createBody(bodyDef);

            shape.setAsBox((rectangle.getWidth() / 2) * MetalSlug.MAP_SCALE, 0);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = MetalSlug.OBJECT_BITS;
            body.createFixture(fixtureDef);
        }

        for(RectangleMapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = object.getRectangle();
            MapProperties properties = object.getProperties();
            Soldier.State state = null;
            boolean isRunningRight = false;

            if(properties.containsKey("state")){
                String stateProp = properties.get("state", String.class);
                switch(stateProp){
                    case "chatting":
                        state = Soldier.State.CHATTING;
                        break;
                    case "sneaking":
                        state = Soldier.State.SNEAKING;
                        break;
                    case "scared":
                        state = Soldier.State.SCARED;
                        break;
                    case "running":
                        state = Soldier.State.RUNNING;
                        break;
                    case "idling":
                    default:
                        state = Soldier.State.IDLING;
                        break;
                }
            }
            if(properties.containsKey("isRunningRight")){
                isRunningRight = properties.get("isRunningRight", boolean.class);
            }

            enemies.add(new Soldier(screen, new Vector2(rectangle.getX() * MetalSlug.MAP_SCALE, rectangle.getY() * MetalSlug.MAP_SCALE), state, isRunningRight));
        }

        for(RectangleMapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = object.getRectangle();
            MapProperties properties = object.getProperties();
            Hobo.State state = null;

            if(properties.containsKey("state")){
                String stateProp = properties.get("state", String.class);
                switch(stateProp){
                    case "hanging":
                        state = Hobo.State.HANGING;
                        break;
                    case "sitting":
                    default:
                        state = Hobo.State.SITTING;
                }
            }

            hostages.add(new Hobo(screen, new Vector2(rectangle.getX() * MetalSlug.MAP_SCALE, rectangle.getY() * MetalSlug.MAP_SCALE), state));
        }

        for(RectangleMapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = object.getRectangle();

            enemies.add(new Helicopter(screen, new Vector2(rectangle.getX() * MetalSlug.MAP_SCALE, rectangle.getY() * MetalSlug.MAP_SCALE)));
        }
    }

    public void createShot(Class<? extends Shot> shotClass, MissionOneScreen screen, MarcoRossi player){
        if(shotClass == PistolShot.class){
            shots.add(new PistolShot(screen, player));
        }
    }

    public void createBomb(MissionOneScreen screen, MarcoRossi player, Helicopter helicopter){
        bombs.add(new Bomb(screen, player, helicopter));
    }

    public Array<Shot> getShots(){
        return shots;
    }
    public Array<Bomb> getBombs(){
        return bombs;
    }
    public Array<Enemy> getEnemies(){
        return enemies;
    }
    public Array<Hostage> getHostages(){
        return hostages;
    }
}
