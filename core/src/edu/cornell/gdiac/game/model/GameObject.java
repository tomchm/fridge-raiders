package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;

import java.awt.*;


/**
 * Created by tomchm on 3/9/17.
 */
public abstract class GameObject implements Comparable{
    protected static  Vector2 drawScale = new Vector2(32,32);
    protected Body body;
    protected BodyDef bodyDef;
    protected FixtureDef fixtureDef;
    protected Filter filter = null;
    protected boolean isRemoved;
    protected String[] tags;
    protected ObjectMap<String, Asset> assetMap = new ObjectMap<String, Asset>();
    protected static int counter;

    public boolean isRemoved() {
        return isRemoved;
    }

    public Body getBody(){
        return body;
    }

    public void setBody(Body body){
        this.body = body;
    }

    public BodyDef getBodyDef(){
        return bodyDef;
    }

    public void addAsset(String tag, Asset asset){
        assetMap.put(tag, asset);
    }

    public void draw(GameCanvas canvas){
        if(tags.length > 0 && body != null){
            Asset asset = assetMap.get(tags[0]);
            if(asset instanceof ImageAsset){
                ImageAsset ia = (ImageAsset) asset;
                canvas.draw(ia.getTexture(), Color.WHITE,ia.getOrigin().x,ia.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),ia.getImageScale().x,ia.getImageScale().y);
            }
        }
    }

    public void activate(World world){
        body = world.createBody(bodyDef);
        Fixture fix = body.createFixture(fixtureDef);
        if(filter == null){
            filter = new Filter();
            filter.categoryBits = 0x0002;
            filter.maskBits = 0x0006;
        }
        fix.setFilterData(filter);
        fix.setUserData(this);
    }

    public void deactivate(World world){
        world.destroyBody(body);
    }

    public String[] getTags(){
        return tags;
    }

    public void update(float dt){}

    public static void setDrawScale(Vector2 ds){
        drawScale = ds;
    }

    public static Vector2 getDrawScale(){
        return drawScale;
    }

    public void drawDebug(GameCanvas canvas){
        if(fixtureDef != null){
            if(fixtureDef.shape.getType() == Shape.Type.Circle){
                canvas.drawPhysics((CircleShape)getBody().getFixtureList().get(0).getShape(), Color.YELLOW, body.getPosition().x, body.getPosition().y);
            }
            else if(fixtureDef.shape.getType() == Shape.Type.Polygon){
                canvas.drawPhysics((PolygonShape)getBody().getFixtureList().get(0).getShape(), Color.YELLOW, body.getPosition().x, body.getPosition().y, body.getAngle(), drawScale.x, drawScale.y);
            }
        }
    }

    public float getZ(){
            if(tags.length > 0 && body != null) {
                Asset asset = assetMap.get(tags[0]);
                if (asset instanceof ImageAsset) {
                    ImageAsset ia = (ImageAsset) asset;
                    return body.getPosition().y*getDrawScale().y - ia.getOrigin().y*ia.getImageScale().y;
                }
                else if(asset instanceof FilmstripAsset){
                    FilmstripAsset fa = (FilmstripAsset) asset;
                    return body.getPosition().y*getDrawScale().y - fa.getOrigin().y*fa.getImageScale().y;
                }
            }
        return 0;
    }

    public int compareTo(Object o){
        if(o instanceof GameObject){
            GameObject other = (GameObject) o;
            return (int) (other.getZ() - this.getZ());
        }
        return 0;
    }

    public static void incCounter(){
        counter++;
        if(counter < 0){
            counter=0;
        }
    }
}
