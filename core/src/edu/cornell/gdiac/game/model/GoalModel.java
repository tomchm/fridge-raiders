package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.SoundController;

/**
 * Created by Sal on 3/14/2017.
 */
public class GoalModel extends GameObject implements ContactListener{
    private float[] coords;
    private boolean active;
    private boolean playerCollided;
    PolygonRegion black;
    PolygonRegion color;

    public GoalModel(float[] coords) {
        active = false;
        playerCollided = false;

        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        //bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        //bodyDef.position.set(x,y);
        bodyDef.angle = 0f;

        filter = new Filter();
        filter.groupIndex = -1;
        filter.categoryBits = 0x0002;
        filter.maskBits = 0x0006;

        this.coords = coords; //new float[]{48,26,54,13,68,19,62,31};

        PolygonShape shape = new PolygonShape();
        shape.set(this.coords);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 1f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;



        this.tags = new String[]{};
    }

    public float[] getCoords() {return coords;}

    public void setActive(){
        active = true;
    }

    public boolean hasPlayerCollided(){
        return playerCollided;
    }

    public float getZ() {
        return 9000f + GameObject.getDrawScale().y*0.25f*(coords[1] + coords[3] + coords[5] + coords[7]);
    }

    public void draw(GameCanvas canvas){
        // draw the black part
        if(active){
            float alpha = 0.3f* MathUtils.sinDeg(counter*1%360) + 0.5f;

            canvas.drawPolygon(coords, 1f, 1f, 1f, alpha);
        }

    }

    @Override
    public void beginContact(Contact contact) {
        if(active){
            if(contact.getFixtureA().getUserData() instanceof DetectiveModel && contact.getFixtureB().getUserData() instanceof GoalModel){
                playerCollided = true;
            }
            else if(contact.getFixtureB().getUserData() instanceof DetectiveModel && contact.getFixtureA().getUserData() instanceof GoalModel){
                playerCollided = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Object a = contact.getFixtureA().getUserData();
        Object b = contact.getFixtureB().getUserData();
        if (a instanceof DetectiveModel) {
            if (((DetectiveModel)a).isSecondStage()) SoundController.getInstance().play("wall_hit", false);
        }
        if (b instanceof DetectiveModel) {
            if (((DetectiveModel)b).isSecondStage()) SoundController.getInstance().play("wall_hit", false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
