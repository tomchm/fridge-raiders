package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.InputController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Created by Sean on 4/18/17.
 */

public class ResetGUIModel extends GUIModel{

    private boolean isFirstStage;
    private WorldModel worldModel;
    private static float IMAGE_TIME = 4f;
    private static float TEXT_TIME = 3f;
    private static float FADE_TIME = 1f;
    private String message;
    private int messageStep;
    private int countdown;
    private boolean didCountdown = false;
    /** Overall time that the story scene has been up */
    private float sceneTime;
    /** Time that the current image has been up */
    private float imgTime;
    private InputController input;
    public boolean hardReset =false;
    public boolean softReset = false;
    public boolean quitScreen = false;
    public boolean levelSelectScreen = false;
    public boolean soundOn = true;
    public boolean musicOn = true;
    private Color tint;
    private Color hoverTint;
    private Color textTint1;
    private Color textTint2;
    private Color textTint3;
    private Color textTint4;
    private Color textTint5;
    private Color textTint6;
    private Color textTint7;
    private Color standard;
    private Color blackTint;
    private Texture gradient;
    private Texture music;
    private Texture soundSfx;
    private ScreenListener listener;
    private InputController inputController;

    public ResetGUIModel(WorldModel worldModel, InputController input){
        tags = new String[] {"gunny72"};
        guiTag = "ResetGUI";
        isFirstStage = true;
        this.worldModel = worldModel;
        this.messageStep = -1;
        this.message = "";
        this.inputController = input;
        imgTime = 0f;
        sceneTime = 0f;
        tint = new Color(1f, 1f, 1f, 1f);
        hoverTint =  new Color(0.09803921568627451f, 0.3686274509803922f, 0.7490196078431373f,1f);
        textTint1 = new Color((float) (227.0/255.0), 0.7450980392156863f, (float) 0.25882354f, 0f);
        textTint2 = new Color(0.25f,0.25f,0.25f,0f);
        textTint3 = new Color(0.25f,0.25f,0.25f,0f);
        textTint4 = new Color(0.25f,0.25f,0.25f,0f);
        textTint5 = new Color(0.25f,0.25f,0.25f,0f);
        textTint6 = new Color(0.25f,0.25f,0.25f,0f);
        textTint7 = new Color(0.25f,0.25f,0.25f,0f);
        standard = new Color(0.25f,0.25f,0.25f,1f);        blackTint = new Color(0f, 0f, 0f, 0f);
        this.listener = listener;
        countdown = 100;
        gradient = ((ImageAsset) AssetLoader.getInstance().getAsset("pausemenu")).getTexture().getTexture();
        music = ((ImageAsset) AssetLoader.getInstance().getAsset("MusicOn")).getTexture().getTexture();
        soundSfx = ((ImageAsset) AssetLoader.getInstance().getAsset("SoundOn")).getTexture().getTexture();

    }

    public void update(float dt){
        if(worldModel.hasLost() && worldModel.getPlayer().isSecondStage()){

            sceneTime += dt;
            imgTime += dt;

            if (imgTime < FADE_TIME) {
                tint.r = tint.g = tint.b = imgTime / FADE_TIME;
                if (textTint1.a < 1f) {
                    textTint1.a =  imgTime / FADE_TIME;
                    textTint2.a =  imgTime / FADE_TIME;
                    textTint3.a =  imgTime / FADE_TIME;
                    textTint4.a =  imgTime / FADE_TIME;
                    textTint5.a =  imgTime / FADE_TIME;
                    textTint6.a =  imgTime / FADE_TIME;
                    textTint7.a =  imgTime / FADE_TIME;


                    blackTint.a = imgTime / FADE_TIME;
                }
            }

            int myX = input.getInstance().getMyProcessor().menuX;
            int myY = input.getInstance().getMyProcessor().menuY;

            int hoverX = inputController.getInstance().getMyProcessor().hoveringX;
            int hoverY = inputController.getInstance().getMyProcessor().hoveringY;
            hoverText(hoverX,hoverY);

            if (myX >= 515 && myX <= 830 && myY >= 105 && myY <= 175) {
                // first option


            } else if (myX >= 515 && myX <= 830 && myY >= 190 && myY <= 265) {
                //second option
                softReset = true;
                inputController.getInstance().getMyProcessor().pauseX = 0;
                inputController.getInstance().getMyProcessor().pauseY = 0;
                tint = new Color(1f, 1f, 1f, 1f);
                blackTint = new Color(0f, 0f, 0f, 0f);
                sceneTime = 0;
                imgTime = 0;

            } else if (myX >= 515 && myX <= 830 && myY >= 275 && myY <= 350) {
                //third option
                hardReset = true;
                inputController.getInstance().getMyProcessor().pauseX = 0;
                inputController.getInstance().getMyProcessor().pauseY = 0;
                tint = new Color(1f, 1f, 1f, 1f);
                blackTint = new Color(0f, 0f, 0f, 0f);
                sceneTime = 0;
                imgTime = 0;

            } else if (myX >= 515 && myX <= 830 && myY >= 360 && myY <= 425) {
                //fourth option
                levelSelectScreen = true;
                inputController.getInstance().getMyProcessor().pauseX = 0;
                inputController.getInstance().getMyProcessor().pauseY = 0;
                tint = new Color(1f, 1f, 1f, 1f);
                blackTint = new Color(0f, 0f, 0f, 0f);
                sceneTime = 0;
                imgTime = 0;


            }
            else if (myX >= 515 && myX <= 830 && myY >= 435 && myY <= 490) {
                //fifth option
                quitScreen = true;
                inputController.getInstance().getMyProcessor().pauseX = 0;
                inputController.getInstance().getMyProcessor().pauseY = 0;
                tint = new Color(1f, 1f, 1f, 1f);
                blackTint = new Color(0f, 0f, 0f, 0f);
                sceneTime = 0;
                imgTime = 0;

            }


            else if (myX >= 545 && myX <= 605 && myY >= 490 && myY <= 555) {
                //Left Music option
                if(musicOn){
                    musicOn = false;
                    music = ((ImageAsset) AssetLoader.getInstance().getAsset("MusicOff")).getTexture().getTexture();
                }
                else{
                    musicOn = true;
                    music = ((ImageAsset) AssetLoader.getInstance().getAsset("MusicOn")).getTexture().getTexture();
                }
            } else if (myX >= 620 && myX <= 680 && myY >= 490 && myY <= 555) {
                //Right Sound option
                if(soundOn){
                    soundOn = false;
                    soundSfx = ((ImageAsset) AssetLoader.getInstance().getAsset("SoundOff")).getTexture().getTexture();
                }
                else{
                    soundOn = true;
                    soundSfx = ((ImageAsset) AssetLoader.getInstance().getAsset("SoundOn")).getTexture().getTexture();
                }

            }
            inputController.getInstance().getMyProcessor().menuX = 0;
            inputController.getInstance().getMyProcessor().menuY = 0;




        }
    }

    public void draw(GameCanvas canvas){
        float x = origin.x* GameObject.getDrawScale().x;
        float y = origin.y*GameObject.getDrawScale().y;
        FontAsset font = (FontAsset) assetMap.get("gunny72");
        if(font != null) {
            if(this.worldModel.hasLost() && this.worldModel.getPlayer().isSecondStage()) {
                canvas.draw(gradient, tint, worldModel.getPlayer().getBody().getPosition().x * GameObject.getDrawScale().x - (Gdx.graphics.getWidth() / 2), worldModel.getPlayer().getBody().getPosition().y * GameObject.getDrawScale().y - (Gdx.graphics.getHeight() / 2), 1280, 720);
                BitmapFont bf = font.getFont();
                bf.setColor(blackTint);
                canvas.drawText("GAME OVER", font.getFont(), x - 110 + 2, y + 240 - 2);
                bf.setColor(textTint1);
                canvas.drawText("GAME OVER", font.getFont(), x - 110, y + 240);

                bf.setColor(blackTint);
                canvas.drawText("ROLL AGAIN", font.getFont(), x - 110 + 2, y + 160 - 2);
                bf.setColor(textTint2);
                canvas.drawText("ROLL AGAIN", font.getFont(), x - 110, y + 160);

                bf.setColor(blackTint);
                canvas.drawText("RESTART", font.getFont(), x - 110 + 2, y + 80 - 2);
                bf.setColor(textTint3);
                canvas.drawText("RESTART", font.getFont(), x - 110, y + 80);

                bf.setColor(blackTint);
                canvas.drawText("LEVELS", font.getFont(), x - 110 + 2, y - 2);
                bf.setColor(textTint4);
                canvas.drawText("LEVELS", font.getFont(), x - 110, y);

                bf.setColor(blackTint);
                canvas.drawText("QUIT", font.getFont(), x - 110 + 2, y -70 - 2);
                bf.setColor(textTint5);
                canvas.drawText("QUIT", font.getFont(), x - 110, y - 70);

                canvas.draw(music, textTint6, x-100, y-200, 70, 70);
                canvas.draw(soundSfx, textTint7, x-25, y-200, 70, 70);
            }
        }

    }

    public void hoverText(int hoverX, int hoverY){
        if((hoverX >= 515 && hoverX <= 830 && hoverY >= 105 && hoverY <= 175)){

            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;

            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;

            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;

            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;


        }



        else if (hoverX >= 515 && hoverX <= 830 && hoverY >= 190 && hoverY <= 265) {
            //second option
            textTint2.b = hoverTint.b;
            textTint2.r = hoverTint.r;
            textTint2.g = hoverTint.g;


            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;

            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;

            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;


        } else if (hoverX >= 515 && hoverX <= 830 && hoverY >= 275 && hoverY <= 350) {
            //third option
            textTint3.b = hoverTint.b;
            textTint3.r = hoverTint.r;
            textTint3.g = hoverTint.g;


            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;


            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;

            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;



        } else if (hoverX >= 515 && hoverX <= 830 && hoverY >= 360 && hoverY <= 425) {
            //fourth option
            textTint4.b = hoverTint.b;
            textTint4.r = hoverTint.r;
            textTint4.g = hoverTint.g;

            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;

            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;


            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;


        }
        else if (hoverX >= 515 && hoverX <= 830 && hoverY >= 435 && hoverY <= 490) {
            //fifth option
            textTint5.b = hoverTint.b;
            textTint5.r = hoverTint.r;
            textTint5.g = hoverTint.g;

            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;

            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;

            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;


            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;

        }


        else if (hoverX >= 545 && hoverX <= 605 && hoverY >= 490 && hoverY <= 555) {
            //Left Music option
            textTint6.b = hoverTint.b;
            textTint6.r = hoverTint.r;
            textTint6.g = hoverTint.g;


            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;

            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;

            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;

            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;

        } else if (hoverX >= 620 && hoverX <= 680 && hoverY >= 490 && hoverY <= 555) {
            //Right Sound option
            textTint7.b = hoverTint.b;
            textTint7.r = hoverTint.r;
            textTint7.g = hoverTint.g;



            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;

            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;

            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;

            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;



        }
        else{

            textTint2.b = standard.b;
            textTint2.r = standard.r;
            textTint2.g = standard.g;

            textTint3.b = standard.b;
            textTint3.r = standard.r;
            textTint3.g = standard.g;

            textTint4.b = standard.b;
            textTint4.r = standard.r;
            textTint4.g = standard.g;

            textTint5.b = standard.b;
            textTint5.r = standard.r;
            textTint5.g = standard.g;

            textTint6.b = standard.b;
            textTint6.r = standard.r;
            textTint6.g = standard.g;

            textTint7.b = standard.b;
            textTint7.r = standard.r;
            textTint7.g = standard.g;
        }
    }
}
