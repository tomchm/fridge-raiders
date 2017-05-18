package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;

/**
 * Created by Sal on 3/12/2017.
 */
public class FurnitureModel extends GameObject {
    private float width, height, radius;
    private boolean isHighlight;

    public FurnitureModel(float x, float y, float width, float height, float theta, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f*width, 0.5f*height);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.restitution = 1f;

        this.width = width;
        this.height = height;

        this.tags = tags;
        isHighlight = false;
    }

    public FurnitureModel(float x, float y, float radius, float theta, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        PolygonShape shape = new PolygonShape();
        float[] coords = new float[16];
        for(int i = 0; i < 8; i ++) {
            double angle = i*Math.PI/4 + Math.PI/8;
            float tempx = (float)(Math.cos(angle)) * radius;
            float tempy = (float)(Math.sin(angle)) * radius;
            coords[2*i] = tempx;
            coords[2*i+1] = tempy;
        }
        shape.set(coords);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;

        this.radius = radius;

        this.tags = tags;
    }


    public float getWidth() {return width;}
    public float getHeight() {return height;}
    public float getRadius() {return radius;}

    public void highlight(){
        isHighlight = true;
    }

    public void draw(GameCanvas canvas){
        if(tags.length > 0 && body != null){
            Asset asset = assetMap.get(tags[0]);
            if(asset instanceof ImageAsset){
                ImageAsset ia = (ImageAsset) asset;

                Affine2 aff = new Affine2();
                aff.setToScaling(ia.getImageScale().x * 1, ia.getImageScale().y * 0.8f);
                aff.preShear(-0.2f, 0);
                aff.preTranslate((body.getPosition().x)* drawScale.x  - ia.getImageScale().x * ia.getTexture().getRegionWidth() * 0.05f , (body.getPosition().y ) * drawScale.y - ia.getTexture().getRegionHeight() * ia.getImageScale().y * 0.05f);
                canvas.draw(ia.getTexture(), new Color(0,0,0,0.6f), ia.getOrigin().x, ia.getOrigin().y, aff);
                

                if(isHighlight){
                    isHighlight = false;
                    float alpha = 0.3f* MathUtils.sinDeg(counter*1%360) + 0.5f;
                    float hscale_x = (ia.getTexture().getRegionWidth()*ia.getImageScale().x + 16f) / (ia.getTexture().getRegionWidth()*ia.getImageScale().x);
                    float hscale_y = (ia.getTexture().getRegionHeight()*ia.getImageScale().y + 16f) / (ia.getTexture().getRegionHeight()*ia.getImageScale().y);
                    canvas.setBlendState(GameCanvas.BlendState.ADDITIVE);
                    canvas.draw(ia.getTexture(), new Color(alpha,alpha,alpha,1), ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,ia.getImageScale().x*hscale_x,ia.getImageScale().y*hscale_y);
                    canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
                }

                canvas.draw(ia.getTexture(), Color.WHITE,ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,ia.getImageScale().x,ia.getImageScale().y);
            }
        }
    }
}
