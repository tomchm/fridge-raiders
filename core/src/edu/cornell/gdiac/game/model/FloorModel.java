package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;


/**
 * Created by tomchm on 3/9/17.
 */
public class FloorModel extends GameObject{

    public FloorModel(){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(0,0);
        bodyDef.angle = 0;

        CircleShape shape = new CircleShape();
        shape.setRadius(1);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        this.tags = new String[]{"floor"};
    }

    public void draw(GameCanvas canvas) {
        Asset ass = assetMap.get(tags[0]);
        ImageAsset ia = (ImageAsset)ass;
        Texture floorTex = ia.getTexture().getTexture();
        floorTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        //TextureRegion region = new TextureRegion(ia.getTexture().getTexture());
        float width = floorTex.getWidth();
        float height = floorTex.getHeight();
        ia.getTexture().setRegion(-150*width,-150*height,150*width, 150*height);
        canvas.draw(ia.getTexture(), Color.WHITE, ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x,ia.getImageScale().y);
    }

    public float getZ(){
        return 1000000;
    }



}
