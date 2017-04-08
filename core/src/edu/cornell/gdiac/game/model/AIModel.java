package edu.cornell.gdiac.game.model;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;

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
    /*scaling factor of speed after ai finds player*/
    protected float speedUpScale = 1.5f;
    /*cone Light of the ai*/
    protected ConeLight coneLight;
    /*radius of light cone*/
    protected  float lightRadius = 15.0f;
    /*angle of light cone*/
    protected float lightAngle = 45.0f;
    /*the path the ai takes around level*/
    protected Vector2[] path;

    public boolean isDead = false;


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
     * @return the speed scale of this ai model
     */
    public float getSpeedUpScale() {return speedUpScale;}

    /**
     *@return the path of this ai
     */
    public Vector2[] getPath() {return path;}

    /**
     * @return the light radius of this ai
     */
    public float getLightRadius() {return lightRadius;}

    /**
     * @return the light angle of this ai
     */
    public float getLightAngle() {return lightAngle;}

    /**
     * @return the radius of this ai
     */
    public float getRadius() {return getBody().getFixtureList().get(0).getShape().getRadius();}

    public AIModel(Vector2[] path, String[] tags){
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
        fixtureDef.density = 2.0f;
        fixtureDef.shape = shape;

        this.path = path;
        this.tags = tags;
    }

    /**
     * creates and sets a new Cone Light
     */
    public void createConeLight(RayHandler rayHandler) {
        Vector2 pos = getBody().getPosition();
        coneLight = new ConeLight(rayHandler, NUM_RAYS, Color.WHITE, lightRadius, pos.x, pos.y, getBody().getAngle(), lightAngle);
        coneLight.setContactFilter((short)1, (short)-1, (short)1);
    }

    /**
     * updates the cone light position based on the position of the AI
     */
    public void updateConeLight() {
        coneLight.setPosition(getBody().getPosition());
        coneLight.setDirection((float)(getBody().getAngle()*180 / Math.PI));
    }

    /**
     * updates the angle of the model based on the vector given
     */
    public void updateAngle(Vector2 angleVector) {
        Vector2 vel = angleVector;
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

    /** Overwrite GameObject draw method so the sprite does not turn akwardly*/
    public void draw(GameCanvas canvas){
        if(tags.length > 0 && body != null){
            Asset asset = assetMap.get(tags[0]);
            if(asset instanceof ImageAsset){
                ImageAsset ia = (ImageAsset) asset;
                float angle = isDead ? body.getAngle() : 0;
                canvas.draw(ia.getTexture(), Color.WHITE,ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,angle,ia.getImageScale().x,ia.getImageScale().y);
            }
        }
    }
}
