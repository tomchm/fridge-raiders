package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.game.model.TrajectoryModel;

import java.awt.*;

/**
 * Created by tomchm on 3/19/17.
 */
public class AimGUIModel extends GUIModel{

    private final static float AIM_SCALE = 0.3f;
    private final static int AIM_BALLS = 5;
    private final static float MAX_FORCE = 500f;
    private final static int MAX_BALLS = 10;
    private Vector2 aimVector, aimPosition, prevAimVector;
    private boolean isAiming;
    private float foodAmount;
    private float maxAmount = 200;
    private WorldModel worldModel;
    private Queue<TrajectoryModel> tqueue;
    private int tsize;

    private static final Color AIM_GREEN = new Color(0x00CC00FF);
    private static final Color AIM_YELLOW = new Color(0xFFFF00FF);
    private static final Color AIM_ORANGE = new Color(0xFF8000FF);
    private static final Color AIM_RED = new Color(0xCC0000FF);

    public AimGUIModel(WorldModel worldModel){
        isAiming = false;
        aimVector = null;
        prevAimVector = null;
        aimPosition = null;
        foodAmount = 0;
        tags = new String[] {"ball", "foodbar"};
        guiTag = "AimGUI";
        this.worldModel = worldModel;
        tqueue = new Queue<TrajectoryModel>();
        tsize = 0;
    }

    public void update(float dt){
        if(isAiming && aimVector != null && aimPosition != null) {
            if(aimVector != prevAimVector){
                if (tsize > 0) {
                    tsize--;
                    TrajectoryModel tr = tqueue.removeLast();
                    worldModel.removeGameObject(tr);
                }
                prevAimVector = aimVector;
            }
            Vector2 velocity = aimVector.cpy().nor();
            TrajectoryModel tm = new TrajectoryModel(aimPosition.x, aimPosition.y, 1.2f, new Vector2(velocity.x, -velocity.y));
            worldModel.addGameObjectQueue(tm);
            tqueue.addFirst(tm);
            tsize++;
            if (tsize > MAX_BALLS) {

                tsize--;
                TrajectoryModel tr = tqueue.removeLast();
                worldModel.removeGameObject(tr);
            }
            float frac = aimVector.len()/MAX_FORCE;
            Color tint = Color.WHITE;
            if(frac <= 0.5f){
                tint = AIM_GREEN.cpy().lerp(AIM_YELLOW, frac/0.5f);
            }
            else if(frac <= 0.75f){
                tint = AIM_YELLOW.cpy().lerp(AIM_ORANGE, (frac-0.5f)/0.25f);
            }
            else {
                tint = AIM_ORANGE.cpy().lerp(AIM_RED, (frac-0.75f)/0.25f);
            }
            for(TrajectoryModel tt : tqueue){
                tt.setTint(tint);
            }


        }
        else {
            while (tsize > 0) {
                tsize--;
                TrajectoryModel tr = tqueue.removeLast();
                worldModel.removeGameObject(tr);
            }
        }
    }

    public void draw(GameCanvas canvas){
        /*
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
        */
        ImageAsset asset = (ImageAsset) assetMap.get("foodbar");
        if(asset != null){

            float x = origin.x*GameObject.getDrawScale().x - asset.getTexture().getRegionWidth()/2f;
            float y = origin.y*GameObject.getDrawScale().y + 310;
            TextureRegion bar = asset.getTexture();
            bar.setRegion(0,0,400, 35);
            canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);
            int width = (int)(foodAmount*400f/maxAmount);
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
