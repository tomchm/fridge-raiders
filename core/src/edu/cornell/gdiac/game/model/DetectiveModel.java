package edu.cornell.gdiac.game.model;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.SoundController;

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
    private static final float DEFAULT_RESTITUTION = 0f;
    /** The thrust factor to convert player input into thrust */
    private static final float DEFAULT_THRUST = 20.0f;
    private static final float DEFAULT_SPEED = 10.0f;
    /** Amount of food eaten per SECOND. */
    private static final float CHEWING_RATE = 5.0f;

    /** The force to apply to this rocket */
    private Vector2 force;
    private Vector2 velocity;
    private float radius = 1.2f;

    private Pixmap normalMan;
    private Pixmap fatMan;
    private Texture fatTex;

    private boolean didSoftReset;

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


    /** Amount required to enter second stage. */
    private int threshold = 50;
    /** Maximum amount of food in level */
    private int maximumFood = 200;
    /** Par shots for the level*/
    private int par;

    private int amountEaten = 0;
    private boolean hasEatenDessert = false;
    private boolean isSecondStage = false;
    public float eatDelay = 0.0f;

    private int shotsTaken;
    private int shotsRemaining;

    // lighting
    /** the players radius of light*/
    private PointLight pointLight;
    /** the players radius of light*/
    private float lightRadius = 13;


    public enum Animation {
        LEFT_MOVE, RIGHT_MOVE, UP_MOVE, DOWN_MOVE, LEFT_STOP, RIGHT_STOP, UP_STOP, DOWN_STOP, ROLL_MOVE, ROLL_STOP,
        LEFT_GRAB, RIGHT_GRAB, UP_GRAB, DOWN_GRAB
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

        filter = new Filter();
        filter.categoryBits = 0x0004;
        filter.maskBits = 0x0002;

        PolygonShape shape = new PolygonShape();
        shape.set(getCoords());
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = -1.0f;
        animation = Animation.DOWN_MOVE;

        tags = new String[] {"player_down", "player_up", "player_left", "player_right",
                "player_down_idle", "player_up_idle", "player_left_idle", "player_right_idle",
                "player_down_grab", "player_up_grab", "player_right_grab", "player_left_grab", "glow",
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

        shotsRemaining = -1;
        shotsTaken = 0;
    }

    public float[] getCoords() {
        float[] coords = new float[16];
        for(int i = 0; i < 8; i ++) {
            double angle = i*Math.PI/4 + Math.PI/8;
            float tempx = (float)(Math.cos(angle)) * radius;
            float tempy = (float)(Math.sin(angle)) * radius;
            coords[2*i] = tempx;
            coords[2*i+1] = tempy;
        }
        return  coords;
    }

    /**
     * creates and sets a new Point Light
     */
    public void createPointLight(RayHandler rayHandler) {
        Vector2 pos = getBody().getPosition();
        pointLight = new PointLight(rayHandler, 128, Color.WHITE, lightRadius, pos.x, pos.y);
        pointLight.attachToBody(getBody());
        pointLight.setContactFilter((short)0x0005, (short)-1, (short) 0xFFFF);
        pointLight.setSoft(false);
        pointLight.setXray(true);
    }

    /**
     * @return the point light associated with this player
     */
    public PointLight getPointLight() {
        return pointLight;
    }

    public FurnitureModel getGrappledFurniture() {
        if ( getBody().getJointList().size < 1) return null;
        return (FurnitureModel) getBody().getJointList().get(0).other.getFixtureList().get(0).getUserData();
    }

    public void startEating(FoodModel f) {
        chewing = f;
        if(!(f.isDessert() && this.getAmountEaten() < this.getThreshold())){
            System.out.println("INside");
            eatDelay += 3.0f;
        }
    }
    public void stopEating() { chewing = null; }

    public void setSoftReset() {this.didSoftReset = true;}
    public boolean didSoftReset() {return didSoftReset;}
    public void unsetSoftReset() {this.didSoftReset = false;}

    public FoodModel getChewing(){
        return chewing;
    }

    public Animation getAnimation(){
        return animation;
    }

    public void update(float dt) {
        //System.out.println(body.getPosition().x + " : "+body.getPosition().y);

        /** Rotate all of the body parts in 3D on the balled-up character. */
        if (isSecondStage) {
            if(shotsRemaining == -1){
                int extra = (int)(par*(amountEaten - threshold) / (maximumFood - threshold));
                shotsRemaining = par + extra;
            }

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

            if(!isDessert || amountEaten >= threshold){
                int actuallyAte = chewing.eat(tryToEat);
                if(!isDessert){
                    amountEaten += actuallyAte;
                }
                if (chewing.getAmount() == 0) {
                    if (isDessert){
                        hasEatenDessert = true;
                    }
                    chewing = null;
                }



                if (amountEaten >= threshold && hasEatenDessert) {
                    setStage(true);
                    setRadius(radius * 1.8f);
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

            switch (animation){
                case DOWN_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_down");
                    frame++;
                    break;
                case DOWN_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_down_idle");
                    frame++;
                    break;
                case UP_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_up");
                    frame++;
                    break;
                case UP_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_up_idle");
                    frame++;
                    break;
                case LEFT_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_left");
                    frame++;
                    break;
                case LEFT_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_left_idle");
                    frame++;
                    break;
                case RIGHT_MOVE:
                    fa = (FilmstripAsset)assetMap.get("player_right");
                    frame++;
                    break;
                case RIGHT_STOP:
                    fa = (FilmstripAsset)assetMap.get("player_right_idle");
                    frame++;
                    break;
                case DOWN_GRAB:
                    fa = (FilmstripAsset)assetMap.get("player_down_grab");
                    if(this.getSpeed() > 0.5f) {
                        frame++;
                    }
                    break;
                case UP_GRAB:
                    fa = (FilmstripAsset)assetMap.get("player_up_grab");
                    if(this.getSpeed() > 0.5f) {
                        frame++;
                    }
                    break;
                case LEFT_GRAB:
                    fa = (FilmstripAsset)assetMap.get("player_left_grab");
                    if(this.getSpeed() > 0.5f) {
                        frame++;
                    }
                    break;
                case RIGHT_GRAB:
                    fa = (FilmstripAsset)assetMap.get("player_right_grab");
                    if(this.getSpeed() > 0.5f) {
                        frame++;
                    }
                    break;
            }
            if(fa != null){
                int nFrame = (frame / fa.getSpeed()) % fa.getNumFrames();
                TextureRegion texture = fa.getTexture(nFrame);

                Affine2 aff = new Affine2();
                aff.setToScaling(fa.getImageScale().x * 1, fa.getImageScale().y * 0.7f);
                aff.preShear(-0.2f, 0);
                aff.preTranslate((body.getPosition().x)* drawScale.x  - fa.getImageScale().x * fa.getTexture(nFrame/2).getRegionWidth() * 0.05f , (body.getPosition().y) * drawScale.y - fa.getTexture(nFrame/2).getRegionHeight() * fa.getImageScale().y * 0.05f);
                canvas.draw(fa.getTexture(nFrame/2), new Color(0,0,0,0.5f), fa.getOrigin().x, fa.getOrigin().y, aff);

                canvas.draw(texture, Color.WHITE,fa.getOrigin().x,fa.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,fa.getImageScale().x,fa.getImageScale().y);
            }
        }
        else{
            if(animation == Animation.ROLL_STOP){
                ImageAsset glow = (ImageAsset)assetMap.get("glow");
                if(glow != null){
                    TextureRegion texture = glow.getTexture();
                    float alpha = 0.4f* MathUtils.sinDeg(counter*3%360) + 0.6f;
                    canvas.draw(texture, new Color(1,1,1,alpha),glow.getOrigin().x,glow.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,glow.getImageScale().x,glow.getImageScale().y);
                }

            }
            drawFat(canvas);
        }
    }

    /** The source is a pixmap which we would like to bulge. The bulged version is
     * calculated and placed in dest. (source is not modified.) sx0, sy0 is the center of the bulge effect,
     * in pixels, on the source. dx0, dy0 is the corresponding point, in pixels, on the source pixmap.
     * R is the radius, on the source, from the center point within which the bulge will be applied.
     * amount ranges from 0 (no bulge) to 1 (max bulge).
     * In order to clip the filmstrip down to just one frame, we need to give some extra information as well.
     * sx0, sy0 are given assuming the frame has already been clipped. */
    private void bulge(Pixmap source, Pixmap dest, int startx, int swidth, float sx0, float sy0, float dx0, float dy0, float R, float amount) {
        long start_time = System.currentTimeMillis();
        float c = -amount/R;
        int sheight = source.getHeight();
        int dwidth = dest.getWidth();
        int dheight = dest.getHeight();
        for (int dx = 0; dx < dwidth; dx++) { for (int dy = 0; dy < dheight; dy++) {
            // get distance from center of dest pixmap.
            float dx_rel = dx - dx0; float dy_rel = dy - dy0;
            double theta = Math.atan2(dy_rel, dx_rel);
            float s = (float)Math.sqrt(dx_rel*dx_rel + dy_rel*dy_rel);
            // s = (1-cR)r + cr^2. Want to invert.
            // where to sample from on original image to draw at distance s.
            float r = (c*R-1f-(float)Math.sqrt((1-c*R)*(1-c*R) + 4f*c*s)) / (2.0f*c);
            if (r > R) { r = s; }
            float sx = startx + sx0 + r * (float)Math.cos(theta);
            float sy = sy0 + r * (float)Math.sin(theta);
            if (sx >= 0 && sx < swidth && sy >= 0 && sy < sheight) {
                int col = source.getPixel((int)sx, (int)sy);
                dest.drawPixel(dx, dy, col);
            } else { // we went off of the source pixmap
                dest.setColor(0f, 1f, 0f, 1f); // transparent pixel
                dest.drawPixel(dx, dy);
            }
        } }
        long end_time = System.currentTimeMillis();
        System.out.println("Time to bulge entire filmstrip (4 million pixels): " + (end_time - start_time)  + " ms.");
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

    public void setRadius(float radius) {
        this.radius = radius;
        PolygonShape shape = ((PolygonShape)getBody().getFixtureList().get(0).getShape());
        shape.set(getCoords());
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
        fixtureDef.restitution = 0f;
    }

    public float getAmountEaten(){
        return amountEaten;
    }

    public float getThreshold(){
        return threshold;
    }
    public void setThreshold(int threshold){
        this.threshold = threshold;
    }

    public float getMaximumFood(){
        return maximumFood;
    }
    public void setMaximumFood(int maximumFood){
        this.maximumFood = maximumFood;
    }


    public void setStage(boolean b){
        this.isSecondStage = b;

    }
    public boolean isSecondStage(){
        return this.isSecondStage;
    }

    public void consumeShot(){
        shotsRemaining--;
        shotsTaken++;
        animation = Animation.ROLL_MOVE;
        if (SoundController.getInstance().isActive("stretch")) SoundController.getInstance().stop("stretch");
        //SoundController.getInstance().play("whistle", false);
    }

    public boolean hasShots(){
        return !((shotsRemaining == 0) && (animation == Animation.ROLL_STOP));
    }

    public int getShotsRemaining(){
        return shotsRemaining;
    }

    public int getShotsTaken(){ return shotsTaken; }

    public void resetShots() {
        this.shotsRemaining += this.shotsTaken;
        this.shotsTaken = 0;
    }


    public void resetStickers(){
        stickers = new PooledList<Sticker>();
        stickers.add(new Sticker("hat", 0f, 0f, 0f));
        stickers.add(new Sticker("tie", 0f, 50f, 0f));
        stickers.add(new Sticker("hand", 75f, 65f, 180f));
        stickers.add(new Sticker("hand", -75f, 65f, 180f));
        stickers.add(new Sticker("buckle", 0f, 90f, 0f));
        //stickers.add(new Sticker("backpocket", 155f, 120f, 0f));
        //stickers.add(new Sticker("backpocket", -155f, 120f, 0f));
        stickers.add(new Sticker("foot", 65f, 145f, 0f));
        stickers.add(new Sticker("foot", -65f, 145f, 0f));
    }

    public int getPar(){
        return par;
    }
    public void setPar(int par){
        this.par = par;
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
