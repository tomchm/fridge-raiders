package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.ImageAsset;

import java.util.Random;


/**
 * Created by tomchm on 3/9/17.
 */
public class CrumbModel extends GameObject{

    private static final int MAX_AGE = 360;
    Color tint;
    float ZZ;
    int age;

    public CrumbModel(Vector2 pos, Color tint, float z){
        this.tint = tint;
        ZZ = z-1;

        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = false;
        bodyDef.linearDamping = 2.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(pos);
        bodyDef.bullet = true;

        Random random = new Random();
        float vx = random.nextFloat()*16f - 8f;
        float vy = random.nextFloat()*16f - 8f;

        bodyDef.linearVelocity.set(new Vector2(vx ,vy));

        filter = new Filter();
        filter.categoryBits = 0x0004;
        filter.maskBits = 0x0002;

        Shape shape = new CircleShape();
        shape.setRadius(0.1f);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0.0f;

        this.tags = new String[]{"crumb"};
        age = 0;
    }

    public void update(float dt){
        age++;
    }

    public boolean isOld(){
        return age > MAX_AGE;
    }

    public void draw(GameCanvas canvas){
        ImageAsset asset = (ImageAsset) assetMap.get("crumb");
        if(asset != null){
            float alpha = 1f;
            if(age > (2 * MAX_AGE / 3)){
                alpha = 1- ((float)age / MAX_AGE);
            }
            Color c = new Color(tint.r, tint.g, tint.b, alpha);
            canvas.draw(asset.getTexture(), c, asset.getOrigin().x,asset.getOrigin().y,
                    body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,
                    asset.getImageScale().x,asset.getImageScale().y);
        }
    }

    public float getZ(){
        return ZZ;
    }



}
