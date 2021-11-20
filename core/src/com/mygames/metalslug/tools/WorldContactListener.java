package com.mygames.metalslug.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.sprites.Enemy;
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
                    ((MarcoRossi) fixtureA.getUserData()).setAttackMode(MarcoRossi.AttackMode.MELEE);
                    ((Soldier) fixtureB.getUserData()).setAttackMode(Soldier.AttackMode.MELEE);
                }
                else if (fixtureB.getFilterData().categoryBits == MetalSlug.PLAYER_BITS && fixtureA.getUserData() instanceof Soldier){
                    ((MarcoRossi) fixtureB.getUserData()).setAttackMode(MarcoRossi.AttackMode.MELEE);
                    ((Soldier) fixtureA.getUserData()).setAttackMode(Soldier.AttackMode.MELEE);
                }
                break;
            case MetalSlug.SHOT_BITS | MetalSlug.ENEMY_BITS:
                if(fixtureA.getFilterData().categoryBits == MetalSlug.SHOT_BITS){
                    ((PistolShot) fixtureA.getUserData()).destroy();
                    ((Enemy) fixtureB.getUserData()).kill();
                }
                else{
                    ((PistolShot) fixtureB.getUserData()).destroy();
                    ((Enemy) fixtureA.getUserData()).kill();
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
                    ((MarcoRossi) fixtureA.getUserData()).setAttackMode(MarcoRossi.AttackMode.WEAPON);
                    ((Soldier) fixtureB.getUserData()).setAttackMode(Soldier.AttackMode.WEAPON);
                }
                else if (fixtureB.getFilterData().categoryBits == MetalSlug.PLAYER_BITS && fixtureA.getUserData() instanceof Soldier){
                    ((MarcoRossi) fixtureB.getUserData()).setAttackMode(MarcoRossi.AttackMode.WEAPON);
                    ((Soldier) fixtureA.getUserData()).setAttackMode(Soldier.AttackMode.WEAPON);
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
