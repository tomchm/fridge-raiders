package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Sal on 3/14/2017.
 */
public class WallModel extends GameObject {
    public WallModel(float[] coords, String tag) {
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        //bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        //bodyDef.position.set(x,y);
        bodyDef.angle = 0f;

        PolygonShape shape = new PolygonShape();
        shape.set(coords);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        this.tags = new String[] {tag};
    }
}
