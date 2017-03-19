package edu.cornell.gdiac.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.GameObject;

import java.awt.*;

/**
 * Created by tomchm on 3/19/17.
 */
public class GUIModel {

    private final static float AIM_SCALE = 3;
    private final static int AIM_BALLS = 5;
    private Vector2 aimVector, aimPosition;
    private boolean isAiming;
    protected String[] tags;
    protected ObjectMap<String, Asset> assetMap = new ObjectMap<String, Asset>();

    public GUIModel(){
        isAiming = false;
        aimVector = null;
        aimPosition = null;
        tags = new String[] {"ball"};
    }

    public void addAsset(String tag, Asset asset){
        assetMap.put(tag, asset);
    }

    public String[] getTags(){
        return tags;
    }

    public void draw(GameCanvas canvas){
        if(isAiming && aimVector != null && aimPosition != null){
            for(int i=0; i<AIM_BALLS; i++){
                ImageAsset asset = (ImageAsset) assetMap.get("ball");
                if(asset != null){
                    float x = aimPosition.x*GameObject.getDrawScale().x - aimVector.x*AIM_SCALE;
                    float y = aimPosition.y*GameObject.getDrawScale().y- aimVector.y*AIM_SCALE;
                    canvas.draw(asset.getTexture(), Color.WHITE,asset.getOrigin().x,asset.getOrigin().y,x,y,0,asset.getImageScale().x,asset.getImageScale().y);
                }
            }
        }
    }

    public void setAimVector(Vector2 aim, Vector2 position){
        aimVector = aim;
        aimPosition = position;
    }
}
