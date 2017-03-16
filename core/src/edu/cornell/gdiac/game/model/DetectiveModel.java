package edu.cornell.gdiac.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;

/**
 * Created by tomchm on 3/9/17.
 */
public class DetectiveModel extends GameObject{

    private int frame = 0;
    private Animation animation;

    public enum Animation {
        LEFT_MOVE, RIGHT_MOVE, UP_MOVE, DOWN_MOVE, LEFT_STOP, RIGHT_STOP, UP_STOP, DOWN_STOP
    }

    public DetectiveModel(float x, float y){
        bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.5f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake  = true;
        bodyDef.allowSleep = true;
        bodyDef.position.set(x,y);

        Shape shape = new CircleShape();
        shape.setRadius(1.2f);
        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;
        animation = Animation.DOWN_MOVE;

        tags = new String[] {"player_down"};
    }

    public void setAnimation(Animation animation){
        this.animation = animation;
    }

    public void draw(GameCanvas canvas){
        if(body != null){
            FilmstripAsset fa = (FilmstripAsset)assetMap.get("player_down");
            if(fa != null){
                frame++;
                int nFrame = (frame / fa.getSpeed()) % fa.getNumFrames();
                TextureRegion texture = fa.getTexture(nFrame);
                canvas.draw(texture, Color.WHITE,fa.getOrigin().x,fa.getOrigin().y,body.getPosition().x*drawScale.x,body.getPosition().y*drawScale.x,0,fa.getImageScale().x,fa.getImageScale().y);
            }
        }
    }

}
