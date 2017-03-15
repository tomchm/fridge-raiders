package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.physics.box2d.*;


/**
 * Created by vanyaivan on 3/15/2017.
 */
public class AIModel extends GameObject{
    public AIModel(float x, float y){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);

        Shape shape = new CircleShape();
        shape.setRadius(1.2f);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        tag = "ai";
    }
}
