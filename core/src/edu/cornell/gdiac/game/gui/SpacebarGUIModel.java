package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.SpacebarController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.FoodModel;
import edu.cornell.gdiac.game.model.FurnitureModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.game.model.TrajectoryModel;

/**
 * Created by tomchm on 3/19/17.
 */
public class SpacebarGUIModel extends GUIModel{

    private GameObject nearest;
    private SpacebarController controller;
    private int counter;
    private boolean isFirstStage;
    private WorldModel worldModel;

    private static final int MAX_COUNT = 5;

    private static final Color AIM_GREEN = new Color(0x00CC00FF);
    private static final Color AIM_YELLOW = new Color(0xFFFF00FF);
    private static final Color AIM_ORANGE = new Color(0xFF8000FF);
    private static final Color AIM_RED = new Color(0xCC0000FF);

    public SpacebarGUIModel(SpacebarController controller, WorldModel worldModel){
        this.controller = controller;
        nearest = null;
        counter = 0;
        tags = new String[] {"foodtick"};
        guiTag = "SpacebarGUI";
        isFirstStage = true;
        this.worldModel = worldModel;
    }

    public void update(float dt){
        counter++;
        if(counter == MAX_COUNT){
            counter = 0;
            if(isFirstStage){
                if(!worldModel.getPlayer().isSecondStage()){
                    if(worldModel.getPlayer().getChewing() == null){
                        nearest = controller.getInteractible();
                    }

                }
                else {
                    isFirstStage = false;
                    worldModel.getGoal().setActive();
                }
            }
        }
        if(nearest != null){
            if(nearest instanceof FoodModel){
                ((FoodModel) nearest).highlight();
            }
            else if(nearest instanceof FurnitureModel){
                ((FurnitureModel) nearest).highlight();
            }

        }
    }

    public void draw(GameCanvas canvas){
        if(nearest != null){
            if(nearest instanceof FoodModel){
                FilmstripAsset asset = (FilmstripAsset) assetMap.get("foodtick");
                if(asset != null){
                    FoodModel food = (FoodModel) nearest;
                    if(!food.isDessert() || food.isUnlocked()){
                        int frame = 10 - MathUtils.ceil((float)food.getAmount() / food.getMaxAmount() * 10f);
                        if(frame < 10){
                            TextureRegion texture = asset.getTexture(frame);
                            float x = food.getBody().getPosition().x*GameObject.getDrawScale().x;
                            float y = food.getBody().getPosition().y*GameObject.getDrawScale().y;
                            canvas.draw(texture, Color.WHITE, asset.getOrigin().x, asset.getOrigin().y, x, y, 0, asset.getImageScale().x, asset.getImageScale().y);
                        }
                    }
                }
            }
        }
    }

}
