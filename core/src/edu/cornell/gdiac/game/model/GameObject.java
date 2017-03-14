package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;


/**
 * Created by tomchm on 3/9/17.
 */
public abstract class GameObject implements Comparable{
    protected static Vector2 drawScale;
    protected Body body;
    protected BodyDef bodyDef;
    protected FixtureDef fixtureDef;
    protected TextureRegion texture;
    protected Vector2 origin;
    protected Vector2 imageScale;
    protected boolean isRemoved;
    protected String tag;

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

    public void setTexture(TextureRegion texture, Vector2 origin, Vector2 imageScale){
        this.texture = texture;
        this.origin = origin;
        this.imageScale = imageScale;
    }

    public void draw(GameCanvas canvas){
        if(texture != null && body != null){
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,body.getAngle(),imageScale.x,imageScale.y);
        }
    }

    public void activate(World world){
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    public void deactivate(World world){
        world.destroyBody(body);
    }

    public String getTag(){
        return tag;
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
                canvas.drawPhysics((CircleShape)fixtureDef.shape, Color.YELLOW, body.getPosition().x, body.getPosition().y);
            }
            else if(fixtureDef.shape.getType() == Shape.Type.Polygon){
                canvas.drawPhysics((PolygonShape)fixtureDef.shape, Color.YELLOW, body.getPosition().x, body.getPosition().y, body.getAngle(), drawScale.x, drawScale.y);
            }
        }
    }

    public float getZ(){
        if(body != null){
            return body.getPosition().y*getDrawScale().y - origin.y*imageScale.y;
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
}
