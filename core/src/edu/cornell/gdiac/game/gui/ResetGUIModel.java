package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.InputController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.model.GameObject;

/**
 * Created by Sean on 4/18/17.
 */

public class ResetGUIModel extends GUIModel{

    private boolean isFirstStage;
    private WorldModel worldModel;
    private String message;
    private int messageStep;
    private int countdown;
    private boolean didCountdown = false;
    private InputController input;
    public boolean hardReset = false;
    public boolean softReset = false;

    public ResetGUIModel(WorldModel worldModel, InputController input){
        tags = new String[] {"gothic72"};
        guiTag = "ResetGUI";
        isFirstStage = true;
        this.worldModel = worldModel;
        this.messageStep = -1;
        this.message = "";
        this.input = input;
        countdown = 0;

    }

    public void update(float dt){
        if(worldModel.hasLost() && worldModel.getPlayer().isSecondStage()){
            int myX = input.getInstance().getMyProcessor().menuX;
            int myY = input.getInstance().getMyProcessor().menuY;
            if(myX >= 290 && myX <= 1080){
                if(myY >= 120 && myY <= 195){
                    //reset hard
                    hardReset = true;
                }
                else if(myY >= 300 && myY <= 375){
                    //reset softs
                    worldModel.setNotLost();
                    softReset=true;
                }
            }

        }
    }

    public void draw(GameCanvas canvas){
        float x = origin.x* GameObject.getDrawScale().x;
        float y = origin.y*GameObject.getDrawScale().y;
        FontAsset font = (FontAsset) assetMap.get("gothic72");
        if(font != null) {
            if(this.worldModel.hasLost() && this.worldModel.getPlayer().isSecondStage()) {
                canvas.drawRect(-5000, -5000, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, 0.5f, 0.5f, 0.5f);
                BitmapFont bf = font.getFont();
                bf.setColor(Color.BLACK);
                canvas.drawText("RESTART FROM STAGE 1", font.getFont(), x - 350 + 2, y + 230 - 2);
                bf.setColor(Color.YELLOW);
                canvas.drawText("RESTART FROM STAGE 1", font.getFont(), x - 350, y + 230);

                bf.setColor(Color.BLACK);
                canvas.drawText("RESTART FROM STAGE 2", font.getFont(), x - 350 + 2, y + 50 - 2);
                bf.setColor(Color.YELLOW);
                canvas.drawText("RESTART FROM STAGE 2", font.getFont(), x - 350, y + 50);

                bf.setColor(Color.BLACK);
                canvas.drawText("QUIT TO MENU", font.getFont(), x - 350 + 2, y - 100 - 2);
                bf.setColor(Color.YELLOW);
                canvas.drawText("QUIT TO MENU", font.getFont(), x - 350, y - 100);
            }
        }

    }
}
