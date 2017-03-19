package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;

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


    private int frame = 0;
    private Animation animation;

    private int capacity = 0;
    private int maxCapacity = 80;
    private boolean isSecondStage = false;

    public enum Animation {
        LEFT_MOVE, RIGHT_MOVE, UP_MOVE, DOWN_MOVE, LEFT_STOP, RIGHT_STOP, UP_STOP, DOWN_STOP
    }

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
        fixtureDef.restitution = 0.0f;
        animation = Animation.DOWN_MOVE;

        tags = new String[] {"player_down", "player_up", "player_left", "player_right"};

        force = new Vector2();
        velocity = new Vector2();
    }

    public void setAnimation(Animation animation){
        this.animation = animation;
    }

    public void draw(GameCanvas canvas){
        if(body != null){
            FilmstripAsset fa = null;
            switch (animation){
                case DOWN_MOVE:
                    frame++;
                case DOWN_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_down");
                    break;
                case UP_MOVE:
                    frame++;
                case UP_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_up");
                    break;
                case LEFT_MOVE:
                    frame++;
                case LEFT_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_left");
                    break;
                case RIGHT_MOVE:
                    frame++;
                case RIGHT_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_right");
                    break;
            }
            if(fa != null){
                int nFrame = (frame / fa.getSpeed()) % fa.getNumFrames();
                TextureRegion texture = fa.getTexture(nFrame);
                canvas.draw(texture, Color.WHITE,fa.getOrigin().x,fa.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,fa.getImageScale().x,fa.getImageScale().y);
            }

        }
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

    public float getVX() {return velocity.x;}
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

    public void setDefaultRestitution(){
        fixtureDef.restitution = DEFAULT_RESTITUTION;
    }

    public void eatFood(int value){
        this.capacity = this.capacity + value;
    }
    public int getCapacity(){
        return capacity;
    }

    public  int getMaxCapacity(){
        return maxCapacity;
    }

    public void setStage(boolean b){
        this.isSecondStage = b;
    }
    public boolean isSecondStage(){
        return this.isSecondStage;
    }
}
