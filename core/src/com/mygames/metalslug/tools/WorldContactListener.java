package com.mygames.metalslug.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.sprites.Bomb;
import com.mygames.metalslug.sprites.Enemy;
import com.mygames.metalslug.sprites.Hobo;
import com.mygames.metalslug.sprites.MarcoRossi;
import com.mygames.metalslug.sprites.PistolShot;
import com.mygames.metalslug.sprites.Soldier;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch(collisionDef){
            case MetalSlug.PLAYER_BITS | MetalSlug.ENEMY_SENSOR_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.PLAYER_BITS && fixtureB.getUserData() instanceof Soldier){
                    MarcoRossi player = (MarcoRossi) fixtureA.getUserData();
                    Soldier soldier = (Soldier) fixtureB.getUserData();

                    player.addCollidingEnemy(soldier);
                    soldier.setCollidingWithPlayer(true);
                }
                else if (fixtureB.getFilterData().categoryBits == MetalSlug.PLAYER_BITS && fixtureA.getUserData() instanceof Soldier){
                    MarcoRossi player = (MarcoRossi) fixtureB.getUserData();
                    Soldier soldier = (Soldier) fixtureA.getUserData();

                    player.addCollidingEnemy(soldier);
                    soldier.setCollidingWithPlayer(true);
                }
                break;
            case MetalSlug.PLAYER_SHOT_BITS | MetalSlug.ENEMY_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.PLAYER_SHOT_BITS){
                    ((PistolShot) fixtureA.getUserData()).destroy();
                    ((Enemy) fixtureB.getUserData()).hit();
                }
                else{
                    ((PistolShot) fixtureB.getUserData()).destroy();
                    ((Enemy) fixtureA.getUserData()).hit();
                }
                break;
            case MetalSlug.PLAYER_SHOT_BITS | MetalSlug.HOSTAGE_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.HOSTAGE_BITS){
                    ((Hobo) fixtureA.getUserData()).release();
                    ((PistolShot) fixtureB.getUserData()).destroy();
                }
                else{
                    ((Hobo) fixtureB.getUserData()).release();
                    ((PistolShot) fixtureA.getUserData()).destroy();
                }
                break;
            case MetalSlug.HOSTAGE_SENSOR_BITS | MetalSlug.PLAYER_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.HOSTAGE_SENSOR_BITS){
                    Hobo hobo = (Hobo) fixtureA.getUserData();
                    MarcoRossi player = (MarcoRossi) fixtureB.getUserData();

                    if(hobo.getIsReleased()){
                        hobo.save();
                    }
                    else {
                        player.addCollidingHostage(hobo);
                    }
                }
                else{
                    Hobo hobo = (Hobo) fixtureB.getUserData();
                    MarcoRossi player = (MarcoRossi) fixtureA.getUserData();

                    if(hobo.getIsReleased()){
                        hobo.save();
                    }
                    else {
                        player.addCollidingHostage(hobo);
                    }
                }
                break;
            case MetalSlug.HELICOPTER_BOMB_BITS | MetalSlug.GROUND_BITS:
            case MetalSlug.HELICOPTER_BOMB_BITS | MetalSlug.OBJECT_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.HELICOPTER_BOMB_BITS){
                    ((Bomb) fixtureA.getUserData()).explode();
                }
                else{
                    ((Bomb) fixtureB.getUserData()).explode();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch(collisionDef){
            case MetalSlug.PLAYER_BITS | MetalSlug.ENEMY_SENSOR_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.PLAYER_BITS && fixtureB.getUserData() instanceof Soldier){
                    MarcoRossi player = (MarcoRossi) fixtureA.getUserData();
                    Soldier soldier = (Soldier) fixtureB.getUserData();

                    player.removeCollidingEnemy(soldier);
                    soldier.setCollidingWithPlayer(false);
                }
                else if (fixtureB.getFilterData().categoryBits == MetalSlug.PLAYER_BITS && fixtureA.getUserData() instanceof Soldier){
                    MarcoRossi player = (MarcoRossi) fixtureB.getUserData();
                    Soldier soldier = (Soldier) fixtureA.getUserData();

                    player.removeCollidingEnemy(soldier);
                    soldier.setCollidingWithPlayer(false);
                }
                break;
            case MetalSlug.PLAYER_BITS | MetalSlug.HOSTAGE_SENSOR_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.PLAYER_BITS){
                    MarcoRossi player = (MarcoRossi) fixtureA.getUserData();
                    Hobo hobo = (Hobo) fixtureB.getUserData();

                    if(!hobo.getIsReleased()){
                        player.removeCollidingHostage(hobo);
                    }
                }
                else if (fixtureB.getFilterData().categoryBits == MetalSlug.PLAYER_BITS){
                    MarcoRossi player = (MarcoRossi) fixtureB.getUserData();
                    Hobo hobo = (Hobo) fixtureA.getUserData();

                    if(!hobo.getIsReleased()){
                        player.removeCollidingHostage(hobo);
                    }
                }
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
