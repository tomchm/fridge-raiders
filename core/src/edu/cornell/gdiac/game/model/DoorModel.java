package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Sal on 3/16/2017.
 */
public class DoorModel extends GameObject {

    private float width, height;
    private boolean isOpen = false;

    public DoorModel(float x, float y, float width, float height, float theta, String tag){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = false;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f*width, 0.5f*height);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        this.width = width;
        this.height = height;

        this.tags = new String[] {tag};
    }

    public float getWidth() {return width;}
    public float getHeight() {return height;}
    public boolean getStatus() {return  isOpen;}

    public void open(){
        // Rotate The Body
        this.getBody().setTransform(this.getBody().getPosition().x-this.getWidth()/2 , this.getBody().getPosition().y  + this.getWidth()/2, (float) Math.toRadians(90));
        // Move the body into position
        isOpen = true;
    }
    public void close(){
        this.getBody().setTransform(this.getBody().getPosition().x+this.getWidth()/2,this.getBody().getPosition().y - this.getWidth()/2, 0);
        isOpen = false;
    }
}
