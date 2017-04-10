package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;

/**
 * Created by Sal on 3/12/2017.
 */
public class FoodModel extends GameObject {
    private boolean isDessert;
    private float radius;
    private float amount;
    private int intAmount;
    private float maxAmount;
    private boolean isHighlight;

    public FoodModel(float x, float y, float radius, float theta, boolean dessert, float amount, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = 1;
        fixtureDef.filter.maskBits = 1;
        fixtureDef.filter.groupIndex = -1;
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        isDessert = dessert;
        this.radius = radius;

        this.tags = tags;
        this.amount = amount;
        this.maxAmount = amount;
        this.intAmount = (int) amount;
        isHighlight = false;
    }

    public boolean isDessert() {return isDessert;}
    public float getRadius() {return radius;}

    public int getAmount(){return intAmount;}

    public float getMaxAmount(){ return maxAmount; }

    public int eat(float biteSize) {
        if (biteSize > amount) {
            amount = 0f;
            return intAmount;
        }
        else {
            amount -= biteSize;
            int eaten = intAmount - ((int) amount);
            intAmount -= eaten;
            return eaten;
        }
    }

    public void highlight(){
        isHighlight = true;
    }

    public void draw(GameCanvas canvas){
        if(tags.length > 0 && body != null){
            Asset asset = assetMap.get(tags[0]);
            if(asset instanceof ImageAsset){
                ImageAsset ia = (ImageAsset) asset;

                if(isHighlight){
                    isHighlight = false;
                    float alpha = 0.3f* MathUtils.sinDeg(counter*1%360) + 0.5f;
                    float hscale_x = (ia.getTexture().getRegionWidth()*ia.getImageScale().x + 20f) / (ia.getTexture().getRegionWidth()*ia.getImageScale().x);
                    float hscale_y = (ia.getTexture().getRegionHeight()*ia.getImageScale().y + 20f) / (ia.getTexture().getRegionHeight()*ia.getImageScale().y);
                    canvas.setBlendState(GameCanvas.BlendState.ADDITIVE);
                    canvas.draw(ia.getTexture(), new Color(alpha,alpha,alpha,1), ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x*hscale_x,ia.getImageScale().y*hscale_y);
                    canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
                }


                float alpha = (amount/maxAmount)*0.7f + 0.3f;
                if(amount == 0){
                    alpha = 0;
                }
                float col = 1;
                if(isDessert){
                    counter+=3;
                    float step = MathUtils.cosDeg(counter);
                    col = step*0.15f + 0.85f;
                }
                Color color = new Color(col,col,col, alpha);

                canvas.draw(ia.getTexture(), color,ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x,ia.getImageScale().y);
            }
        }
    }



}
