package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.game.GameCanvas;


/**
 * Created by tomchm on 3/9/17.
 */
public abstract class GameObject {
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

    public abstract void draw(GameCanvas canvas);

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
}
