package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.game.GameCanvas;

/**
 * Created by Sal on 3/14/2017.
 */
public class WallModel extends GameObject {
    private float[] coords;
    private float r, g, b;
    PolygonRegion black;
    PolygonRegion color;

    public static float WALL_HEIGHT = 6f;

    public WallModel(float[] coords, float red, float green, float blue, String[] tags) {
        r = red; g = green; b = blue;

        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        //bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        //bodyDef.position.set(x,y);
        bodyDef.angle = 0f;

        PolygonShape shape = new PolygonShape();
        shape.set(coords);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 1f;
        fixtureDef.shape = shape;
        this.coords = coords;

        this.tags = tags;
    }

    public float[] getCoords() {return coords;}

    public float getZ() {
        return 10000f + GameObject.getDrawScale().y*0.25f*(coords[1] + coords[3] + coords[5] + coords[7]);
    }

    public void draw(GameCanvas canvas){
        // draw the black part
        canvas.drawPolygon(coords, 0f, 0f, 0f);
        int a1=0, b1=1, a2=2, b2=3;
        // line up 1's and 2's vertically
        if (Math.abs(coords[2*a1] - coords[2*b1]) > 0.01) {int temp=b2; b2=b1; b1=temp;}
        // put b's below the a's
        if (coords[2*b1+1] > coords[2*a1+1]) {int temp=b1; b1=a1; a1=temp;}
        if (coords[2*b2+1] > coords[2*a2+1]) {int temp=b2; b2=a2; a2=temp;}
        // draw the colorful part
        canvas.drawPolygon(new float[]{
                coords[2*b1], coords[2*b1+1], coords[2*b1], coords[2*b1+1]+WALL_HEIGHT,
                coords[2*b2], coords[2*b2+1]+WALL_HEIGHT, coords[2*b2], coords[2*b2+1]
        }, r,g,b);
    }
}
