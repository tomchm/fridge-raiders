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
    private int messageStep;
    private int countdown;
    private boolean didCountdown = false;

    public TextGUIModel(WorldModel worldModel){
        tags = new String[] {"gothic72"};
        guiTag = "TextGUI";
        isFirstStage = true;
        this.worldModel = worldModel;
        this.messageStep = -1;
        this.message = "";
        countdown = 0;

    }

    public void update(float dt){
        System.out.println(countdown);
        if(countdown > 0){
            countdown -= 1;
            didCountdown = true;
            if(countdown == 0){
                this.message = "";
            }
        }

        if(worldModel.getPlayer().isSecondStage()){
            this.isFirstStage = false;
            this.message = "";
        }
        if(isFirstStage && worldModel.getPlayer().canEatDesser() && !didCountdown){
            this.message = "DESSERT UNLOCKED";
            this.messageStep = 1;
            this.countdown += 300;
        }
        else{
            if((!isFirstStage) && worldModel.hasExited()){
                this.message = "LEVEL COMPLETE!";
                this.messageStep = 2;
                this.countdown += 6000;
            }
            else if( (!isFirstStage) && worldModel.getPlayer().getShotsRemaining() == 0){
                this.message = "   GAME OVER! \nCLICK TO RESTART";
                this.messageStep = 3;
                this.countdown += 6000;
            }
        }

    }

    public void draw(GameCanvas canvas){
        float x = origin.x*GameObject.getDrawScale().x;
        float y = origin.y*GameObject.getDrawScale().y;
            FontAsset font = (FontAsset) assetMap.get("gothic72");
            if(font != null) {
                if(this.messageStep == 1) {
                    BitmapFont bf = font.getFont();
                    bf.setColor(Color.BLACK);
                    canvas.drawText(this.message, font.getFont(), x - 320 + 2, y + 320 - 2);
                    bf.setColor(Color.YELLOW);
                    canvas.drawText(this.message, font.getFont(), x - 320, y + 320);
                }
                else if(this.messageStep == 2) {
                    BitmapFont bf = font.getFont();
                    bf.setColor(Color.BLACK);
                    canvas.drawText(this.message, font.getFont(), x - 285 + 2, y + 320 - 2);
                    bf.setColor(Color.YELLOW);
                    canvas.drawText(this.message, font.getFont(), x - 285, y + 320);
                }
                else if(this.messageStep == 3) {
                    BitmapFont bf = font.getFont();
                    bf.setColor(Color.BLACK);
                    canvas.drawText(this.message, font.getFont(), x - 280 + 2, y + 320 - 2);
                    bf.setColor(Color.YELLOW);
                    canvas.drawText(this.message, font.getFont(), x - 280, y + 320);
                }
                else {
                    BitmapFont bf = font.getFont();
                    bf.setColor(Color.BLACK);
                    canvas.drawText(this.message, font.getFont(), x - 270 + 2, y + 230 - 2);
                    bf.setColor(Color.YELLOW);
                    canvas.drawText(this.message, font.getFont(), x - 270, y + 230);
                }
            }

    }
}
