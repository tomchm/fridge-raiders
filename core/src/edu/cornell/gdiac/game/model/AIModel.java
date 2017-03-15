package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


/**
 * Created by vanyaivan on 3/15/2017.
 */
public class AIModel extends GameObject{
    // Constants

    // Variable Fields
    /*speed of the ai movement*/
    protected float speed = 3.0f;
    /*the path the ai takes around level*/
    protected Vector2[] path;

    /**
     * @return speed of the ai model
     */
    public float getSpeed() {return speed;}

    /** Sets speed of ai model
     *
     * @param val, speed to set
     */
    public void setSpeed(float val) {speed = val;}

    /**
     *@return the path of this ai
     */
    public Vector2[] getPath() {return path;}

    /**
     * @return the radius of this ai
     */
    public float getRadius() {return getBody().getFixtureList().get(0).getShape().getRadius();}


    public AIModel(Vector2[] path){
        System.out.println(path);
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(path[0]);

        Shape shape = new CircleShape();
        shape.setRadius(1.2f);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        this.path = path;
        this.tag = "ai";
    }
}
