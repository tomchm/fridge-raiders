package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.GameObject;

import java.awt.*;

/**
 * Created by tomchm on 3/19/17.
 */
public class AimGUIModel extends GUIModel{

    private final static float AIM_SCALE = 0.3f;
    private final static int AIM_BALLS = 5;
    private final static float MAX_FORCE = 500f;
    private Vector2 aimVector, aimPosition;
    private boolean isAiming;
    private float foodAmount;
    private float maxAmount = 200;

    public AimGUIModel(){
        isAiming = false;
        aimVector = null;
        aimPosition = null;
        foodAmount = 0;
        tags = new String[] {"ball", "foodbar"};
        guiTag = "AimGUI";
    }

    public void draw(GameCanvas canvas){
        if(isAiming && aimVector != null && aimPosition != null){
            for(int i=0; i<AIM_BALLS; i++){
                ImageAsset asset = (ImageAsset) assetMap.get("ball");

                if(asset != null){
                    float x = aimPosition.x*GameObject.getDrawScale().x + aimVector.x*AIM_SCALE*i;
                    float y = aimPosition.y*GameObject.getDrawScale().y - aimVector.y*AIM_SCALE*i;
                    Color color = new Color(1-(aimVector.len()/MAX_FORCE),1,1-(aimVector.len()/MAX_FORCE), (((float)AIM_BALLS-i)/AIM_BALLS));
                    canvas.draw(asset.getTexture(), color,asset.getOrigin().x,asset.getOrigin().y,x,y,0,asset.getImageScale().x,asset.getImageScale().y);

                }
            }
        }
        ImageAsset asset = (ImageAsset) assetMap.get("foodbar");
        if(asset != null){

            float x = origin.x*GameObject.getDrawScale().x - asset.getTexture().getRegionWidth()/2f;
            float y = origin.y*GameObject.getDrawScale().y + 310;
            //System.out.println(origin.x +" "+ origin.y);
            TextureRegion bar = asset.getTexture();
            bar.setRegion(0,0,400, 35);
            canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);
            int width = (int)(foodAmount*400f/maxAmount);
            //System.out.println(foodAmount);
            bar.setRegion(0, 35, width ,35);
            canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);
            bar.setRegion(0, 70, 400, 35);
            canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);

        }




    }

    public void setAimVector(Vector2 aim, Vector2 position){
        aimVector = aim;
        aimPosition = position;
    }

    public void setAim(boolean isAiming){
        this.isAiming = isAiming;
    }

    public void setFoodAmount(float amount){
        foodAmount = amount;
    }
}
