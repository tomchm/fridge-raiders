package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
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
    private float[] coords;
    private PolygonRegion region = null;
    private float x, y; // bottom left
    private float maxx, maxy;

    public FloorModel(float[] coords, String[] tags){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        //bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(-1000f,-1000f);
        bodyDef.angle = 0f;

        CircleShape shape = new CircleShape();
        shape.setRadius(0.0f);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 1f;
        fixtureDef.shape = shape;

        this.coords = coords;
        this.tags = tags;

        x = coords[0]; y = coords[1];
        maxx = coords[0]; maxy = coords[1];
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                if (coords[i] < x) x = coords[i];
                if (coords[i] > maxx) maxx = coords[i];
            } else {
                if (coords[i] < y) y = coords[i];
                if (coords[i] > maxy) maxy = coords[i];
            }
        }
    }

    public void draw(GameCanvas canvas) {
        if (region == null) {
            Asset ass = assetMap.get(tags[0]);
            ImageAsset ia = (ImageAsset) ass;
            if(ia != null){
                Texture floorTex = ia.getTexture().getTexture();
                floorTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                //TextureRegion region = new TextureRegion(ia.getTexture().getTexture());
                //float width = floorTex.getWidth();
                //float height = floorTex.getHeight();
                ia.getTexture().setRegion((int)0, (int)0, (int)((maxx-x)*drawScale.x/ia.getImageScale().x), (int)((maxy-y)*drawScale.y/ia.getImageScale().y));
                //float[] scaledCoords = new float[8];
                //for (int i=0; i<8; i++) {scaledCoords[i] = (i%2==0 ? drawScale.x : drawScale.y)*coords[i];}
                //region = new PolygonRegion(ia.getTexture(), scaledCoords, new short[] {0,1,2,0,3,2});
                canvas.draw(ia.getTexture(), Color.WHITE, 0f,0f,x*drawScale.x,y*drawScale.x,0f,ia.getImageScale().x,ia.getImageScale().y);
            }
        }
        //canvas.draw(region, drawScale.x*x, drawScale.y*y);
/*
        canvas.drawPolygon(coords, 0.2f, 0.0f, 0.6f);
        if (region == null) {
            Asset ass = assetMap.get(tags[0]);
            ImageAsset ia = (ImageAsset) ass;
            float[] scaledCoords = new float[8];
            for (int i=0; i<8; i++) {scaledCoords[i] = (i%2==0 ? drawScale.x : drawScale.y)*coords[i];}
            ia.getTexture().getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion tr = new TextureRegion(ia.getTexture().getTexture());
            region = new PolygonRegion(tr, scaledCoords, new short[]{0,2,1,0,2,3});
        }
        Asset ass = assetMap.get(tags[0]);
        ImageAsset ia = (ImageAsset) ass;
        //canvas.draw(ia.getTexture(), x*drawScale.x, y*drawScale.y);
        canvas.draw(region,Color.WHITE,0f,0f,x*drawScale.x,y*drawScale.y,0f,ia.getImageScale().x,ia.getImageScale().y);
        //canvas.drawPolygon(coords, 0.5f, 0.5f, 0.5f);
        //canvas.draw(ia.getTexture(), Color.WHITE, ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x,ia.getImageScale().y);
    */
    }

    public float getZ(){
        return 10f*WallModel.LARGE_Z;
    }



}
