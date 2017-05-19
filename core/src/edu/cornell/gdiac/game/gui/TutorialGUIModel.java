package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.SpacebarController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.FoodModel;
import edu.cornell.gdiac.game.model.FurnitureModel;
import edu.cornell.gdiac.game.model.GameObject;

/**
 * Created by tomchm on 3/19/17.
 */
public class TutorialGUIModel extends GUIModel{

    private GameObject nearest;
    private SpacebarGUIModel spacebarGUI;
    private WorldModel worldModel;

    private static final int CYCLE_TIME = 40;

    private int startCount;
    private boolean drawSpacebar;
    private boolean drawMouse;
    private boolean drawArrows;

    public TutorialGUIModel(SpacebarGUIModel spacebarGUI, WorldModel worldModel){
        this.spacebarGUI = spacebarGUI;
        nearest = null;
        tags = new String[] {"Arrowkeys1", "Arrowkeys2", "Spacebar1", "Spacebar2", "Mouse1", "Mouse2"};
        guiTag = "TutorialGUI";
        this.worldModel = worldModel;
    }

    public void update(float dt){
        if(!worldModel.isTutorial()){
            return;
        }
        if(!worldModel.getPlayer().isSecondStage()){
            drawArrows = !worldModel.tutGetPlayerMoved();

            nearest = spacebarGUI.nearest;
            drawSpacebar = false;
            if(nearest != null){
                if(nearest instanceof FoodModel){
                    if(!worldModel.tutGetPlayerAte()){
                        drawSpacebar = true;
                    }
                }
                else if(nearest instanceof FurnitureModel){
                    if(!worldModel.tutGetPlayerGrabbed()){
                        drawSpacebar = true;
                    }
                }
            }
        }
        else{
            drawMouse = false;
            if(!worldModel.tutGetPlayerShot()){
                drawMouse = true;
            }
        }

    }

    public void draw(GameCanvas canvas){
        int count = 1 + (GameObject.counter / CYCLE_TIME) % 4;
        if(count > 2){
            count = 2;
        }

        if(nearest != null){
            float x = nearest.getBody().getPosition().x * GameObject.getDrawScale().x;
            float y = nearest.getBody().getPosition().y * GameObject.getDrawScale().y - 80f;


            ImageAsset sp = (ImageAsset)assetMap.get("Spacebar"+count);
            if(drawSpacebar){
                canvas.draw(sp.getTexture(), Color.WHITE, sp.getOrigin().x, sp.getOrigin().y, x, y, 0, sp.getImageScale().x, sp.getImageScale().y);
            }
        }

        ImageAsset ak = (ImageAsset)assetMap.get("Arrowkeys"+count);
        float x = worldModel.getPlayer().getBody().getPosition().x * GameObject.getDrawScale().x;
        float y = worldModel.getPlayer().getBody().getPosition().y * GameObject.getDrawScale().y - 160;
        if(drawArrows){
            canvas.draw(ak.getTexture(), Color.WHITE, ak.getOrigin().x, ak.getOrigin().y, x, y, 0, ak.getImageScale().x, ak.getImageScale().y);
        }




        if(drawMouse){
            count = (GameObject.counter % (CYCLE_TIME * 4));
            ImageAsset mouse = (ImageAsset)assetMap.get("Mouse1");
            x = worldModel.getPlayer().getBody().getPosition().x * GameObject.getDrawScale().x;
            y = worldModel.getPlayer().getBody().getPosition().y * GameObject.getDrawScale().y;
            if(count >= CYCLE_TIME*3){
                mouse = (ImageAsset)assetMap.get("Mouse1");
                x += 100;
                y -= 100;
            }
            else if(count >= CYCLE_TIME){
                mouse = (ImageAsset)assetMap.get("Mouse2");
                x += ((count-CYCLE_TIME) / (float)(2*CYCLE_TIME))*110;
                y -= ((count-CYCLE_TIME) / (float)(2*CYCLE_TIME))*110;
            }
            else if(count >= CYCLE_TIME/2){
                mouse = (ImageAsset)assetMap.get("Mouse2");
            }


            canvas.draw(mouse.getTexture(), Color.WHITE, mouse.getOrigin().x, mouse.getOrigin().y, x, y, 0, mouse.getImageScale().x, mouse.getImageScale().y);
        }
    }

}
