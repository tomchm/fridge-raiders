package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by tomchm on 3/9/17.
 */
public class DetectiveModel extends GameObject{

    // Default physics values
    /** The density of this rocket */
    private static final float DEFAULT_DENSITY  =  1.0f;
    /** The friction of this rocket */
    private static final float DEFAULT_FRICTION = 0f;
    /** The restitution of this rocket */
    private static final float DEFAULT_RESTITUTION = 1f;
    /** The thrust factor to convert player input into thrust */
    private static final float DEFAULT_THRUST = 20.0f;
    private static final float DEFAULT_SPEED = 10.0f;

    /** The force to apply to this rocket */
    private Vector2 force;
    private Vector2 velocity;
    private float radius;

    /** Cache object for transforming the force according the object angle */
    public Affine2 affineCache = new Affine2();
    /** Cache object for left afterburner origin */

    /** True: walking. False: rolling. */
    private boolean isWalking = true;
    public boolean isWalking() { return isWalking; }
    public void setWalking(boolean b) { isWalking = b; }

    private boolean isGrappled = true;
    public boolean isGrappled() {return isGrappled;}
    public void setGrappled(boolean b) {isGrappled = b;}

    private boolean isEating = false;
    public boolean isEating() {return isEating;}
    public void setEating(boolean b ) {isEating = b;}


    public DetectiveModel(float x, float y){
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
        radius = 1.2f;
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        force = new Vector2();
        velocity = new Vector2();

        tag = "detective";
    }

    public Vector2 getForce() {
        return force;
    }

    public float getFX() {
        return force.x;
    }
    public void setFX(float value) {
        force.x = value;
    }
    public float getFY() {
        return force.y;
    }
    public void setFY(float value) {
        force.y = value;
    }

    public void setVX(float value) {
        velocity.x = value;
    }
    public void setVY(float value) {
        velocity.y = value;
    }
    public void setVelocity(Vector2 v) {
        velocity = v;
    }

    public float getVX() {
        return velocity.x;
    }
    public float getVY() {
        return velocity.y;
    }
    public Vector2 getVelocity() {
        return velocity;
    }

    public float getThrust() {
        return DEFAULT_THRUST;
    }

    public float getSpeed() {
        return this.getBody().getLinearVelocity().dst2(new Vector2(0,0));
    }

    public float getRadius() {
        return radius;
    }

    public void applyForce() {

        //body.setLinearVelocity(getVX(), getVY());
        body.setLinearVelocity(getFX(), getFY());
        body.applyForce(getFX(), getFY(), getBody().getPosition().x, getBody().getPosition().y, true);

    }

}
