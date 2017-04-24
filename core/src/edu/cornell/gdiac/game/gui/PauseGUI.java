package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.InputController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Created by Sean on 4/11/17.
 */



/**
 * Created by Sean on 4/10/17.
 */

public class PauseGUI extends GUIModel {


        public boolean paused;
        private WorldModel worldModel;
        private String message;
        private int messageStep;
        private int countdown;
        private boolean didCountdown = false;
        public boolean quitScreen = false;
        private ScreenListener listener;
        private InputController inputController;
        public PauseGUI(WorldModel worldModel, InputController input){
            tags = new String[] {"gothic72"};
            guiTag = "PauseGUI";
            paused = false;
            this.worldModel = worldModel;
            this.messageStep = -1;
            this.message = "";
            this.inputController = input;
            this.listener = listener;
            countdown = 100;
        }

        public boolean shouldQuit(){return quitScreen;}

        public void update(float dt){
            if(inputController.getInstance().didRetreat()){
                paused = !paused;
                inputController.getInstance().getMyProcessor().pauseX = 0;
                inputController.getInstance().getMyProcessor().pauseY = 0;
            }
            if(paused){
                int myX = inputController.getInstance().getMyProcessor().pauseX;
                int myY = inputController.getInstance().getMyProcessor().pauseY;
                if(myX >= 530 && myX <= 820 && myY>=110 && myY <= 190){
                    // first option
                    inputController.getInstance().prevPressed = !inputController.getInstance().prevPressed;
                    paused = !paused;


                }
                else if (myX >= 530 && myX <= 750 && myY>=220 && myY <= 295){
                    //second option

                }
                else if (myX >= 530 && myX <= 720 && myY>=320 && myY <= 400){
                    //third option
                    quitScreen = true;

                }
            }

        }
        public void draw(GameCanvas canvas){
            float x = origin.x* GameObject.getDrawScale().x;
            float y = origin.y*GameObject.getDrawScale().y;
            FontAsset font = (FontAsset) assetMap.get("gothic72");
            if(paused) {
                canvas.drawRect(-500, -500, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, 0.5f, 0.5f, 0.5f);
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

