package com.mygames.metalslug.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygames.metalslug.MetalSlug;
import com.mygames.metalslug.sprites.Enemy;
import com.mygames.metalslug.sprites.PistolShot;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch(collisionDef){
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

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
