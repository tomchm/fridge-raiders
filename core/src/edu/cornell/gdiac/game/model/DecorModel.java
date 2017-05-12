package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;

/**
 * Created by Sal on 4/22/2017.
 */
public class DecorModel extends GameObject {
    private float x, y;
    private float height, width;

    public DecorModel(float x, float y, float width, float height, String[] tags) {
        this.x = x;
        this.y = y;
        this.tags = tags;

        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f*width, 0.5f*height);
        this.width = width;
        this.height = height;
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.restitution = 1f;
    }

    public float getZ() {
        return 0.1f*WallModel.LARGE_Z + y*GameObject.getDrawScale().y;
    }

    public void draw(GameCanvas canvas) {
        Asset asset = assetMap.get(tags[0]);
        ImageAsset ia = (ImageAsset) asset;

        Affine2 aff = new Affine2();
        aff.setToScaling(ia.getImageScale().x * 1,ia.getImageScale().y * 0.9f);
        aff.preShear(0, 0);
        aff.preTranslate((body.getPosition().x-0.1f) * drawScale.x, (body.getPosition().y - (height * 0.05f))*drawScale.y);
        canvas.draw(ia.getTexture(), new Color(0,0,0,0.6f), ia.getOrigin().x, ia.getOrigin().y,aff);

        canvas.draw(ia.getTexture(), Color.WHITE,ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,ia.getImageScale().x,ia.getImageScale().y);

    }
}
