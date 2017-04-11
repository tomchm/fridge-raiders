package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.SpacebarController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.model.FoodModel;
import edu.cornell.gdiac.game.model.FurnitureModel;
import edu.cornell.gdiac.game.model.GameObject;

import javax.sound.midi.SysexMessage;
import java.awt.*;

/**
 * Created by Sean on 4/10/17.
 */
public class TextGUIModel extends GUIModel{

    private boolean isFirstStage;
    private WorldModel worldModel;
    private String message;


    public TextGUIModel(WorldModel worldModel){
        tags = new String[] {"gothic72"};
        guiTag = "TextGUI";
        isFirstStage = true;
        this.worldModel = worldModel;
        this.message = "";
    }

    public void update(float dt){
        if(worldModel.getPlayer().isSecondStage()){
            this.isFirstStage = false;
            this.message = "";
        }
        if(isFirstStage && worldModel.getPlayer().canEatDesser()){
            this.message = "DESSERT UNLOCKED";
        }
        else{
            if((!isFirstStage) && worldModel.hasExited()){
                this.message = "LEVEL COMPLETE!";
            }
            else if( (!isFirstStage) && worldModel.getPlayer().getShotsRemaining() < 1){
                this.message = "GAME OVER!";
            }
        }

    }

    public void draw(GameCanvas canvas){
        float x = origin.x*GameObject.getDrawScale().x;
        float y = origin.y*GameObject.getDrawScale().y;
        if(this.message != "") {
            FontAsset font = (FontAsset) assetMap.get("gothic72");
            if(font != null) {
                BitmapFont bf = font.getFont();
                bf.setColor(Color.BLACK);
                canvas.drawText(this.message, font.getFont(), x-270+2, y+230-2);
                bf.setColor(Color.YELLOW);
                canvas.drawText(this.message, font.getFont(), x-270, y+230);
            }
        }

    }
}
