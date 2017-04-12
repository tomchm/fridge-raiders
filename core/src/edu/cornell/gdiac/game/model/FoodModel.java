package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;

import java.util.Random;

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
    private boolean isUnlocked;

    public FoodModel(float x, float y, float radius, float theta, boolean dessert, float amount, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);
        bodyDef.angle = theta;

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = !dessert;

        filter = new Filter();
        filter.groupIndex = (short) ((dessert) ? 0 : -1);
        filter.categoryBits = 0x0002;
        filter.maskBits = 0x0006;

        isDessert = dessert;
        this.radius = radius;

        this.tags = tags;
        this.amount = amount;
        this.maxAmount = amount;
        this.intAmount = (int) amount;
        isHighlight = false;
        isUnlocked = false;
    }

    public FoodModel(float x, float y, float width, float height, float theta, boolean dessert, float amount, String[] tags){
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
        fixtureDef.isSensor = !dessert;

        filter = new Filter();
        filter.groupIndex = (short) ((dessert) ? 0 : -1);
        filter.categoryBits = 0x0002;
        filter.maskBits = 0x0006;

        isDessert = dessert;
        this.radius = radius;

        this.tags = tags;
        this.amount = amount;
        this.maxAmount = amount;
        this.intAmount = (int) amount;
        isHighlight = false;
        isUnlocked = false;
    }

    public boolean isDessert() {return isDessert;}
    public boolean isUnlocked() {return isUnlocked;}
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

    public void unlock(){
        if(isDessert){
            isUnlocked = true;
        }
    }

    public Color getCrumbColor(){
        Asset asset = assetMap.get(tags[0]);
        if(asset != null){
            ImageAsset ia = (ImageAsset) asset;
            if (!ia.getTexture().getTexture().getTextureData().isPrepared()) {
                ia.getTexture().getTexture().getTextureData().prepare();
            }
            Pixmap pixmap = ia.getTexture().getTexture().getTextureData().consumePixmap();
            Random random = new Random();
            int x = random.nextInt(pixmap.getWidth());
            int y = random.nextInt(pixmap.getHeight());
            Color col = new Color(pixmap.getPixel(x,y));
            while(col.a != 1.0f){
                x = random.nextInt(pixmap.getWidth());
                y = random.nextInt(pixmap.getHeight());
                col = new Color(pixmap.getPixel(x,y));
            }
            return col;
        }
        return null;
    }

    public void draw(GameCanvas canvas){
        if(tags.length > 0 && body != null){
            Asset asset = assetMap.get(tags[0]);
            if(asset instanceof ImageAsset){
                ImageAsset ia = (ImageAsset) asset;

                if(isHighlight && (!isDessert || isUnlocked)){
                    isHighlight = false;
                    float alpha = 0.3f* MathUtils.sinDeg(counter*1%360) + 0.5f;
                    float hscale_x = (ia.getTexture().getRegionWidth()*ia.getImageScale().x + 20f) / (ia.getTexture().getRegionWidth()*ia.getImageScale().x);
                    float hscale_y = (ia.getTexture().getRegionHeight()*ia.getImageScale().y + 20f) / (ia.getTexture().getRegionHeight()*ia.getImageScale().y);
                    canvas.setBlendState(GameCanvas.BlendState.ADDITIVE);
                    canvas.draw(ia.getTexture(), new Color(alpha,alpha,alpha,1), ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x*hscale_x,ia.getImageScale().y*hscale_y);
                    canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
                }

                Color color = new Color(1,1,1, 1);
                if(isDessert && !isUnlocked){
                    color = new Color(0.4f,0.4f,0.4f,1);
                }
                canvas.draw(ia.getTexture(), color,ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x,ia.getImageScale().y);

                if(isDessert && isUnlocked){
                    float alpha = 0.15f* MathUtils.sinDeg(counter*2%360) + 0.15f;
                    canvas.setBlendState(GameCanvas.BlendState.ADDITIVE);
                    canvas.draw(ia.getTexture(), new Color(alpha, alpha, alpha, 1),ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x,ia.getImageScale().y);
                    canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
                }
            }
        }
    }



}
