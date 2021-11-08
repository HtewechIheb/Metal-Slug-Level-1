package com.mygames.metalslug.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.screens.MissionOneScreen;
import com.mygames.metalslug.sprites.MarcoRossi;
import com.mygames.metalslug.sprites.PistolShot;
import com.mygames.metalslug.sprites.Shot;

public class MissionOneWorldCreator {
    private MissionOneScreen screen;
    private World world;
    private TiledMap map;

    private Array<Shot> shots;

    public MissionOneWorldCreator(MissionOneScreen screen){
        this.screen = screen;
        world = screen.getWorld();
        map = screen.getMap();

        shots = new Array<>();
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
            body.createFixture(fixtureDef);
        }

        for(RectangleMapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = object.getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) * MetalSlug.MAP_SCALE, rectangle.getY() * MetalSlug.MAP_SCALE);
            body = world.createBody(bodyDef);

            shape.setAsBox((rectangle.getWidth() / 2) * MetalSlug.MAP_SCALE, 0);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }
    }

    public void createShot(Shot.ShotType type, MissionOneScreen screen, MarcoRossi player){
        switch(type){
            case PISTOL:
            default:
                shots.add(new PistolShot(screen, player));
        }
    }

    public Array<Shot> getShots(){
        return shots;
    }
}
