package edu.cornell.gdiac.game.model;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.game.GameCanvas;

import java.awt.*;


/**
 * Created by vanyaivan on 3/15/2017.
 */
public class AIModel extends GameObject{
    // Constants
    /* number of rays in a cone light*/
    protected static final int NUM_RAYS = 128;
    /* angular velocity */
    protected static final float ANG_VEL = 3.0f;
    // Variable Fields
    /*speed of the ai movement*/
    protected float speed = 3.0f;
    /*cone Light of the ai*/
    protected ConeLight coneLight;
    /*radius of light cone*/
    protected  float lightRadius = 15.0f;
    /*angle of light cone*/
    protected float lightAngle = 30f;
    /*the path the ai takes around level*/
    protected Vector2[] path;


    /**
     * @return speed of the ai model
     */
    public ConeLight getConeLight() {return coneLight;}

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

    /**
     * creates and sets a new Cone Light
     */
    public void createConeLight(RayHandler rayHandler) {
        Vector2 pos = getBody().getPosition();
        coneLight = new ConeLight(rayHandler, NUM_RAYS, Color.WHITE, lightRadius, pos.x, pos.y, getBody().getAngle(), lightAngle);
    }

    /**
     * updates the cone light position based on the position of the AI
     */
    public void updateConeLight() {
        coneLight.setPosition(getBody().getPosition());
        coneLight.setDirection((float)(getBody().getAngle()*180 / Math.PI));
    }

    /**
     * updates the angle of the model based on the velocity of the ai
     */
    public void updateAngle() {
        Vector2 vel = getBody().getLinearVelocity();
        if((vel.x != 0 || vel.y != 0)) {
            //regularizes angles to be between 0 and 2pi
            float angle = (float)((getBody().getAngle() + Math.PI * 4) % (Math.PI * 2));
            float nextangle = (float)((Math.atan2(vel.y, vel.x) + Math.PI * 4) % (Math.PI * 2));
            float altangle = (float)((nextangle > angle) ? nextangle - Math.PI * 2 : nextangle + Math.PI *2);

            //checks which direction to turn angle
            float dist1 = Math.abs(nextangle - angle);
            float dist2 = Math.abs(altangle - angle);

            //sets direction appropriately
            int direction = (nextangle > angle) ? 1 : -1;;
            if (dist2 < dist1) {
                direction = direction * -1;
            }
            getBody().setAngularVelocity(ANG_VEL * direction * Math.min(dist1, dist2));
        }
    }

    /** Overwrite GameObject draw method so the sprite does not turn akwardly*/
    public void draw(GameCanvas canvas){
        if(texture != null && body != null){
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0f,imageScale.x,imageScale.y);
        }
    }
}
