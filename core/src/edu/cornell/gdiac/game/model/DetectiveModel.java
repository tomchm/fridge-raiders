package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;

/**
 * Created by tomchm on 3/9/17.
 */
public class DetectiveModel extends GameObject{

    public DetectiveModel(float x, float y){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = false;
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

        tag = "detective";
    }

    public void update(float delta){
        System.out.println(body.getPosition().y);
    }
}
