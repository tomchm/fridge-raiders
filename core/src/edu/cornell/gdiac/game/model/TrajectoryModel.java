package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.ImageAsset;


/**
 * Created by tomchm on 3/9/17.
 */
public class TrajectoryModel extends GameObject{

    private static final int MAX_TIME = 120;
    private int time;
    private Color tint;


    public TrajectoryModel(float x, float y, float radius, Vector2 velocity){
        tint = Color.WHITE;
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        velocity = velocity.scl(350);
        bodyDef.linearVelocity.set(velocity);

        filter = new Filter();
        filter.categoryBits = 0x0004;
        filter.maskBits = 0x0002;

        Shape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0.0f;

        this.tags = new String[]{"ball"};
        time = MAX_TIME;
    }

    public void setTint(Color tint){
        this.tint = tint;
    }

    public void draw(GameCanvas canvas){
        time++;
        ImageAsset coat = (ImageAsset) assetMap.get("ball");
        if(coat != null){
            //canvas.setBlendState(GameCanvas.BlendState.ADDITIVE);
            canvas.draw(coat.getTexture(), tint, coat.getOrigin().x,coat.getOrigin().y,
                    body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,
                    coat.getImageScale().x,coat.getImageScale().y);
            //canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
        }
    }




}
