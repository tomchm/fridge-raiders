package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Sal on 3/12/2017.
 */
public class FoodModel extends GameObject {
    private boolean isDessert;
    private float radius;
    private int amount;

    public FoodModel(float x, float y, float radius, float theta, boolean dessert, String tag){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        isDessert = dessert;
        this.radius = radius;

        this.tags = new String[] {tag};
        this.amount = 80;
    }

    public boolean isDessert() {return isDessert;}
    public float getRadius() {return radius;}

    public int getValue(){return amount;}

}
