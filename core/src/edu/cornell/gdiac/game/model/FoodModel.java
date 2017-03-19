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
    private float amount;
    private float maxAmount;

    public FoodModel(float x, float y, float radius, float theta, boolean dessert, float amount, String[] tags){
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

        this.tags = tags;
        this.amount = amount;
        this.maxAmount = amount;
    }

    public boolean isDessert() {return isDessert;}
    public float getRadius() {return radius;}

    public float getAmount(){return amount;}

    public float eat(float biteSize) {
        if (biteSize > amount) {
            float oldAmount = amount;
            amount = 0f;
            return oldAmount;
        }
        else {
            amount -= biteSize;
            return biteSize;
        }
    }

}
