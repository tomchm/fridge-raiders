package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;

/**
 * Created by Sal on 3/12/2017.
 */
public class FurnitureModel extends GameObject {
    private float width, height, radius;

    public FurnitureModel(float x, float y, float width, float height, float theta, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f*width, 0.5f*height);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.restitution = 1f;

        this.width = width;
        this.height = height;

        this.tags = tags;
    }

    public FurnitureModel(float x, float y, float radius, float theta, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;

        this.radius = radius;

        this.tags = tags;
    }


    public float getWidth() {return width;}
    public float getHeight() {return height;}
    public float getRadius() {return radius;}
}
