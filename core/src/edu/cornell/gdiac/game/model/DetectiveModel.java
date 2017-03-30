package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Pixmap;
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
import edu.cornell.gdiac.util.PooledList;

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
    /** Amount of food eaten per SECOND. */
    private static final float CHEWING_RATE = 50.0f;

    /** The force to apply to this rocket */
    private Vector2 force;
    private Vector2 velocity;
    private float radius;

    /** The food the player is currently eating. */
    private FoodModel chewing = null;

    /** The list of stickers on the rolling ball version of the character. */
    private PooledList<Sticker> stickers;

    /** Cache object for transforming the force according the object angle */
    public Affine2 affineCache = new Affine2();
    /** Cache object for left afterburner origin */

    private boolean isGrappled = false;
    public boolean isGrappled() {return isGrappled;}
    public void setGrappled(boolean b) {isGrappled = b;}

    public boolean isEating() {return chewing != null;}

    private int frame = 0;
    private Animation animation;

    private float amountEaten = 0f;
    /** Amount required to enter second stage. */
    private float threshold = 150f;
    private boolean hasEatenDessert = false;
    private boolean isSecondStage = false;
    public float eatDelay = 0.0f;


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
        radius = 2.2f;
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0.0f;
        animation = Animation.DOWN_MOVE;

        tags = new String[] {"player_down", "player_up", "player_left", "player_right",
                "player_down_idle", "player_up_idle", "player_left_idle", "player_right_idle",
                "fat", "coat", "hat", "mask", "hand", "foot", "buckle", "loop", "tie", "backpocket"};

        stickers = new PooledList<Sticker>();
        stickers.add(new Sticker("hat", 0f, 0f, 0f));
        stickers.add(new Sticker("tie", 0f, 50f, 0f));
        stickers.add(new Sticker("hand", 75f, 65f, 180f));
        stickers.add(new Sticker("hand", -75f, 65f, 180f));
        stickers.add(new Sticker("buckle", 0f, 90f, 0f));
        //stickers.add(new Sticker("backpocket", 155f, 120f, 0f));
        //stickers.add(new Sticker("backpocket", -155f, 120f, 0f));
        stickers.add(new Sticker("foot", 45f, 145f, 0f));
        stickers.add(new Sticker("foot", -45f, 145f, 0f));

        force = new Vector2();
        velocity = new Vector2();
    }

    public void startEating(FoodModel f) {
        chewing = f;
        eatDelay += 30.0f;
    }
    public void stopEating() { chewing = null; }

    public void update(float dt) {
        //System.out.println(body.getPosition().x + " : "+body.getPosition().y);

        /** Rotate all of the body parts in 3D on the balled-up character. */
        if (isSecondStage) {
            Vector2 vel = body.getLinearVelocity();
            float omega = vel.len() / getRadius();
            float phi = (float)Math.atan2(vel.y, vel.x) - 0.5f*(float)Math.PI;
            for (Sticker s : stickers) {
                s.rotate(phi, -omega*dt, -phi);
            }
        }

        if (chewing != null) {
            float tryToEat = CHEWING_RATE * dt;
            boolean isDessert = chewing.isDessert();
            if(!isDessert || amountEaten >= 0.999f*threshold){
                float actuallyAte = chewing.eat(tryToEat);
                if(!isDessert){
                    amountEaten += actuallyAte;
                }
                if (chewing.getAmount() == 0f) {
                    if (isDessert){
                        hasEatenDessert = true;
                    }
                    chewing = null;
                }



                if (amountEaten >= 0.999f*threshold && hasEatenDessert) {
                    setStage(true);
                    getBody().getFixtureList().get(0).setRestitution(1f);
                }
            }

            //System.out.println(amountEaten);
        }
    }

    public void setAnimation(Animation animation){
        this.animation = animation;
    }

    public void draw(GameCanvas canvas){
        if(body == null) return;

        if(!isSecondStage){
            FilmstripAsset fa = null;
            frame++;
            switch (animation){
                case DOWN_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_down");
                    break;
                case DOWN_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_down_idle");
                    break;
                case UP_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_up");
                    break;
                case UP_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_up_idle");
                    break;
                case LEFT_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_left");
                    break;
                case LEFT_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_left_idle");
                    break;
                case RIGHT_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_right");
                    break;
                case RIGHT_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_right_idle");
                    break;
            }
            if(fa != null){
                int nFrame = (frame / fa.getSpeed()) % fa.getNumFrames();
                TextureRegion texture = fa.getTexture(nFrame);
                canvas.draw(texture, Color.WHITE,fa.getOrigin().x,fa.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,fa.getImageScale().x,fa.getImageScale().y);
            }
        }
        else{
            drawFat(canvas);
        }
    }

    public void drawFat(GameCanvas canvas) {
        /*ImageAsset mask = (ImageAsset) assetMap.get("mask");
        canvas.drawMask(mask.getTexture(), mask.getOrigin().x,mask.getOrigin().y,
                body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,
                mask.getImageScale().x,mask.getImageScale().y);*/
        ImageAsset coat = (ImageAsset) assetMap.get("coat");
        canvas.draw(coat.getTexture(), Color.WHITE,coat.getOrigin().x,coat.getOrigin().y,
                body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,
                coat.getImageScale().x,coat.getImageScale().y);
        for (Sticker s : stickers) s.draw(canvas);
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

    public float getThrust() {
        return DEFAULT_THRUST;
    }

    public float getSpeed() {
        return this.getBody().getLinearVelocity().dst2(new Vector2(0,0));
    }
    public void setSpeed() {
        this.getBody().setLinearVelocity(0,0);
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

    public float getAmountEaten(){
        return amountEaten;
    }

    public  float getThreshold(){
        return threshold;
    }

    public void setStage(boolean b){
        this.isSecondStage = b;
    }
    public boolean isSecondStage(){
        return this.isSecondStage;
    }

    public void consumeShot(){
        amountEaten -= 10f;
        if(amountEaten < 0f){
            amountEaten = 0f;
        }
    }

    private class Sticker {
        public float x1, y1, z1;
        public float x2, y2, z2;
        public String tag;

        /** Constructor takes angles in DEGREES */
        public Sticker(String tag, float phi, float theta, float psi) {
            this.tag = tag;
            x1 = 0f; y1 = 0f; z1 = 1f;
            x2 = 1f; y2 = 0f; z2 = 0f;
            rotate(phi*(float)Math.PI/180f, theta*(float)Math.PI/180f, psi*(float)Math.PI/180f);
        }
        public void rotate(float phi, float theta, float psi) {
            float c1 = (float)Math.cos(phi); float c2 = (float)Math.cos(theta); float c3 = (float)Math.cos(psi);
            float s1 = (float)Math.sin(phi); float s2 = (float)Math.sin(theta); float s3 = (float)Math.sin(psi);

            /** From Wolfram MathWorld, "Euler Angles."
             *  This is the passive rotation. Use the transpose. */
            float a11 = c3*c1 - c2*s1*s3; float a12 = c3*s1 + c2*c1*s3; float a13 = s3*s2;
            float a21 = -s3*c1 - c2*s1*c3; float a22 = -s3*s1 + c2*c1*c3; float a23 = c3*s2;
            float a31 = s2*s1; float a32 = -s2*c1; float a33 = c2;

            float newX = a11*x1 + a21*y1 + a31*z1;
            float newY = a12*x1 + a22*y1 + a32*z1;
            float newZ = a13*x1 + a23*y1 + a33*z1;
            x1 = newX; y1 = newY; z1 = newZ;

            newX = a11*x2 + a21*y2 + a31*z2;
            newY = a12*x2 + a22*y2 + a32*z2;
            newZ = a13*x2 + a23*y2 + a33*z2;
            x2 = newX; y2 = newY; z2 = newZ;
        }
        public void draw(GameCanvas canvas) {
            ImageAsset ia = (ImageAsset) assetMap.get(tag);
            if (ia == null) return;
            if (z1 <= 0f) return;
            // x, y relative to center of body. In physics coordinates.
            float scale = (getRadius() + 0.5f*ia.getImageScale().x*ia.getTexture().getRegionWidth()/drawScale.x)/getRadius();
            scale =1f;
            float x = scale*getRadius()*x1;
            float y = scale*getRadius()*y1;
            float angle = (float)Math.atan2(y2, x2);
            canvas.draw(ia.getTexture(), Color.WHITE, ia.getOrigin().x,ia.getOrigin().y,
                    (x+body.getPosition().x)*drawScale.x,(y+body.getPosition().y)*drawScale.x,
                    angle,ia.getImageScale().x,ia.getImageScale().y);
        }
    }
}
