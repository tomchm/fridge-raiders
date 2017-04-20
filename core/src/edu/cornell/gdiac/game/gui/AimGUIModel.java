package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.game.DetectiveController;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.game.model.TrajectoryModel;

import java.awt.*;

/**
 * Created by tomchm on 3/19/17.
 */
public class AimGUIModel extends GUIModel{

    private final static int PAR_PLUS = 5;
    private final static float MAX_FORCE = DetectiveController.MAX_FORCE;
    private final static float SHOOT_FORCE = DetectiveController.SHOOT_FORCE;
    private final static int MAX_BALLS = 15;
    private int ballCount;
    private Vector2 aimVector, aimPosition, prevAimVector;
    private boolean isAiming;
    private float foodAmount;
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
        tags = new String[] {"ball", "foodbar", "modernFoodbar", "gothic32", "gothic72"};
        guiTag = "AimGUI";
        this.worldModel = worldModel;
        tqueue = new Queue<TrajectoryModel>();
        tsize = 0;
        ballCount = 1;
    }

    public void update(float dt){
        if(isAiming && aimVector != null && aimPosition != null) {
            Vector2 velocity = aimVector.cpy().nor();
            TrajectoryModel tm = new TrajectoryModel(aimPosition.x, aimPosition.y, 1.2f, new Vector2(velocity.x, -velocity.y));
            worldModel.addGameObjectQueue(tm);
            tqueue.addFirst(tm);
            tsize++;
            if (tsize > MAX_BALLS) {
                if(tsize > 0){
                    tsize--;
                    TrajectoryModel tr = tqueue.removeLast();
                    worldModel.removeGameObject(tr);
                }
            }
            if(tsize > ballCount){
                if(tsize > 0){
                    if(tsize - ballCount >= 2){
                        tsize--;
                        TrajectoryModel tr = tqueue.removeLast();
                        worldModel.removeGameObject(tr);
                    }
                }

                if(tsize > 0){
                    tsize--;
                    TrajectoryModel tr = tqueue.removeLast();
                    worldModel.removeGameObject(tr);
                }
            }

            float fx = aimVector.x * SHOOT_FORCE;
            if(fx > MAX_FORCE){
                fx = MAX_FORCE;
            }
            else if(fx < -MAX_FORCE){
                fx = -MAX_FORCE;
            }
            float fy = (-aimVector.y) * SHOOT_FORCE;
            if(fy > MAX_FORCE){
                fy = MAX_FORCE;
            }
            else if(fy < -MAX_FORCE){
                fy = -MAX_FORCE;
            }

            float frac = (float)Math.sqrt((fx / MAX_FORCE)*(fx / MAX_FORCE) + (fy / MAX_FORCE)*(fy / MAX_FORCE));

            Color tint = Color.WHITE;
            if(frac <= 0.5f){
                tint = AIM_YELLOW.cpy().lerp(AIM_ORANGE, (frac)/0.5f);
            }
            else {
                tint = AIM_ORANGE.cpy().lerp(AIM_RED, (frac-0.5f)/0.5f);
            }
            for(TrajectoryModel tt : tqueue){
                tt.setTint(tint);
            }

            if(frac < 0.8f){
                ballCount = (int)(frac * 1.25f * MAX_BALLS);
            }
            else{
                ballCount = MAX_BALLS;
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
        if(!worldModel.getPlayer().isSecondStage()){
            ImageAsset asset = (ImageAsset) assetMap.get("modernFoodbar");
            if( asset != null){
                float x = origin.x*GameObject.getDrawScale().x - 480f/2f;
                float y = origin.y*GameObject.getDrawScale().y - 320;
                TextureRegion bar = asset.getTexture();

                // DRAW GRAY BAR
                bar.setRegion(0,80,480, 40);
                canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);

                // DRAW GREEN BAR
                float threshold = worldModel.getPlayer().getThreshold();
                int width = 480;
                if(foodAmount < threshold){
                    width = (int)(foodAmount*480f / threshold);
                }
                bar.setRegion(0,0,width, 40);
                canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);

                //DRAW 'Food Meter' TEXT
                FontAsset font = (FontAsset)assetMap.get("gothic32");
                BitmapFont bf = font.getFont();
                if(bf != null){
                    bf.setColor(Color.BLACK);
                    canvas.drawText("FOOD METER", font.getFont(), x-210+2, y+32-2);
                    bf.setColor(Color.WHITE);
                    canvas.drawText("FOOD METER", font.getFont(), x-210, y+32);
                }

                if(foodAmount >= threshold){

                    // DRAW YELLOW BAR
                    float maximum = worldModel.getPlayer().getMaximumFood();
                    width = 480;
                    if(foodAmount < maximum){
                        width = (int)((foodAmount - threshold)*480f / (maximum - threshold));
                    }
                    bar.setRegion(0,40,width, 40);
                    canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);

                    bar.setRegion(0, 120, 4, 40);
                    for(int i=1; i<PAR_PLUS; i++){
                        canvas.draw(bar, Color.WHITE, asset.getOrigin().x+2, asset.getOrigin().y, x+i*(480f/PAR_PLUS), y, 0, asset.getImageScale().x, asset.getImageScale().y);
                    }

                    if(bf != null){
                        float percent = ((foodAmount - threshold)*100f / (maximum - threshold));
                        String text = "";
                        if(percent < 20){
                            text = "PAR ACHIEVED";
                        }
                        else {
                            int i = (int)percent / 20;
                            text = "PAR +"+i;
                        }

                        bf.setColor(Color.BLACK);
                        canvas.drawText(text, font.getFont(), x+490+2, y+32-2);
                        bf.setColor(Color.WHITE);canvas.drawText(text, font.getFont(), x+490, y+32);
                    }

                }
            }
        }
        else{
            FontAsset font32 = (FontAsset)assetMap.get("gothic32");
            BitmapFont bf32 = font32.getFont();
            FontAsset font72 = (FontAsset)assetMap.get("gothic72");
            BitmapFont bf72 = font72.getFont();
            ImageAsset asset = (ImageAsset)assetMap.get("modernFoodbar");
            if(bf32 != null && font72 != null && asset != null){
                float x = origin.x*GameObject.getDrawScale().x - 480f/2f;
                float y = origin.y*GameObject.getDrawScale().y - 320;
                TextureRegion bar = asset.getTexture();

                // DRAW GRAY BAR
                bar.setRegion(0,80,480, 40);
                canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);

                // DRAW GREEN BAR
                int shotsRemaining = worldModel.getPlayer().getShotsRemaining();
                int par = worldModel.getPlayer().getPar();
                int width = 480;
                if(shotsRemaining < par){
                    width = (int)(shotsRemaining*480f / par);
                }
                bar.setRegion(0,0,width, 40);
                canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);

                // DRAW YELLOW BAR
                if(shotsRemaining > par){
                    width = (int)((shotsRemaining - par)*480f / (PAR_PLUS));
                    bar.setRegion(0,40,width, 40);
                    canvas.draw(bar, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);
                }


                bar.setRegion(0, 120, 4, 40);

                int sects = par;
                if(shotsRemaining > par){
                    sects = PAR_PLUS;
                }

                for(int i=1; i<sects; i++){
                    canvas.draw(bar, Color.WHITE, asset.getOrigin().x+2, asset.getOrigin().y, x+i*(480f/sects), y, 0, asset.getImageScale().x, asset.getImageScale().y);
                }

                bf32.setColor(Color.BLACK);
                canvas.drawText("FOOD METER", font32.getFont(), x-210+2, y+32-2);
                bf32.setColor(Color.WHITE);
                canvas.drawText("FOOD METER", font32.getFont(), x-210, y+32);

                bf32.setColor(Color.BLACK);
                canvas.drawText("SCORE:", font32.getFont(), x+650+2, y+32-2);
                bf32.setColor(Color.WHITE);
                canvas.drawText("SCORE:", font32.getFont(), x+650, y+32);


                String score = "" + (worldModel.getPlayer().getShotsTaken() - par);
                bf72.setColor(Color.BLACK);
                canvas.drawText(score, font72.getFont(), x+780+4, y+54-4);
                bf72.setColor(Color.WHITE);
                canvas.drawText(score, font72.getFont(), x+780, y+54);

            }
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
