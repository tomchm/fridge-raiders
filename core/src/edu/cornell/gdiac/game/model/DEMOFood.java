package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Created by tomchm on 3/12/17.
 */
public class DEMOFood extends GameObject{
    public DEMOFood(float x, float y){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = false;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);

        Shape shape = new CircleShape();
        shape.setRadius(1.5f);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        tag = "turkey";
    }
}
