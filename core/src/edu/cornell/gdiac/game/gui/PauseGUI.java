package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.model.GameObject;

/**
 * Created by Sean on 4/11/17.
 */



/**
 * Created by Sean on 4/10/17.
 */

public class PauseGUI extends GUIModel {


        private boolean isFirstStage;
        private WorldModel worldModel;
        private String message;
        private int messageStep;
        private int countdown;
        private boolean didCountdown = false;
        public PauseGUI(WorldModel worldModel){
            tags = new String[] {"gothic72"};
            guiTag = "TextGUI";
            isFirstStage = false;
            this.worldModel = worldModel;
            this.messageStep = -1;
            this.message = "";
            countdown = 100;
        }
        public void update(float dt){
            if (countdown == 0 ){
                countdown += 1000;
                isFirstStage = true;

            }
            if (countdown == 500){
                isFirstStage = false;
            }
            else{
                countdown -= 1;
            }
        }
        public void draw(GameCanvas canvas){
            float x = origin.x* GameObject.getDrawScale().x;
            float y = origin.y*GameObject.getDrawScale().y;
            FontAsset font = (FontAsset) assetMap.get("gothic72");
            if(isFirstStage) {
                canvas.drawRect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 0);
                BitmapFont bf = font.getFont();
                bf.setColor(Color.BLACK);
                canvas.drawText("RESUME", font.getFont(), x - 100 + 2, y + 230 - 2);
                bf.setColor(Color.YELLOW);
                canvas.drawText("RESUME", font.getFont(), x - 100, y + 230);

                bf.setColor(Color.BLACK);
                canvas.drawText("MENU", font.getFont(), x - 100 + 2, y + 130 - 2);
                bf.setColor(Color.YELLOW);
                canvas.drawText("MENU", font.getFont(), x - 100, y + 130);

                bf.setColor(Color.BLACK);
                canvas.drawText("QUIT", font.getFont(), x - 100 + 2, y + 30 - 2);
                bf.setColor(Color.YELLOW);
                canvas.drawText("QUIT", font.getFont(), x - 100, y + 30);

            }
        }
    }

