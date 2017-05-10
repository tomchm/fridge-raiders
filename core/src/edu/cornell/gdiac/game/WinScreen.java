package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

import java.util.Random;

/**
 * Created by Sal on 3/28/2017.
 */
public class WinScreen implements Screen, InputProcessor {

    private static final int FADE_TIME = 80, TEXT_TIME = 70, NUM_TIME = 120, MEDAL_TIME = 80, MEDAL_ZOOM = 20;
    private GameCanvas canvas;
    private ScreenListener listener;
    private AssetLoader assets;
    private boolean started;
    private int time, fadeTime;
    private float originx, originy, bgScale, pratio, pscale;
    private Color newsTint, magnetTint;
    private Color textTint;
    private Color blackTint;
    private int levelCode;
    private int count, guyX;
    private boolean playHighlight;
    private float[] randAngles;

    public static int foodPercent, putts;
    public static String foodMedal, golfMedal;

    public WinScreen(GameCanvas c) {
        //SoundController.getInstance().play("levelmusic", true, 0.75f);
        canvas = new GameCanvas();
        assets= AssetLoader.getInstance();
        started = false;
        time = 0;
        fadeTime = 0;
        newsTint = new Color(0.1f, 0.1f, 0.1f, 1f);
        magnetTint = new Color(0.1f, 0.1f, 0.1f, 1f);
        textTint = new Color(0.3f, 0.3f, 0.3f, 1f);
        blackTint = new Color(0f, 0f, 0f, 1f);
        count = 0;
        guyX = -400;
    }

    public void dispose() {
        canvas = null;
    }
    public void hide() {}
    public void show() {}
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
    public void render(float dt) {
        count++;
        SoundController.getInstance().update();
        Gdx.input.setInputProcessor(this);

        canvas.clear();
        canvas.begin();

        ImageAsset bg = (ImageAsset) assets.getAsset("pausemenu");
        FontAsset font = (FontAsset) assets.getAsset("gunny48");
        BitmapFont bf = font.getFont();
        FontAsset font2 = (FontAsset) assets.getAsset("gunny72");
        BitmapFont bf2 = font2.getFont();
        if(bg != null){
            time++;
            if(time >= 0){
                canvas.drawRect(0, -500, 2000, 2000, 0.3f, 0.3f, 0.3f, 1);
                canvas.draw(bg.getTexture(), Color.WHITE, 0, 0, 1280, 720);

                if(time < FADE_TIME){
                    float ratio = 1 - time/(float)FADE_TIME;
                    canvas.drawRect(0, -500, 2000, 2000, 0, 0, 0, ratio);
                }
            }

            if(time > FADE_TIME){
                typewrite(bf, "FOOD EATEN:", 540, 580, FADE_TIME, 5, time);
            }
            if(time > FADE_TIME + TEXT_TIME){
                upwrite(bf2, foodPercent, 760, 520, FADE_TIME+TEXT_TIME, FADE_TIME+TEXT_TIME+NUM_TIME, time);
                bf2.setColor(blackTint);
                canvas.drawText("%", bf2, 770 + 2, 520 + 2);
                bf2.setColor(textTint);
                canvas.drawText("%", bf2, 770,520);
            }
            if (time == FADE_TIME + TEXT_TIME) {
                SoundController.getInstance().play(foodMedal+"sfx", false);
            }
            if(time > FADE_TIME + TEXT_TIME + NUM_TIME){
                String tag = "medal_" + foodMedal + "_food";
                drawMedal(tag, 230, 440, FADE_TIME + TEXT_TIME + NUM_TIME, time);
            }
            if(time > FADE_TIME + TEXT_TIME + NUM_TIME + MEDAL_TIME){
                typewrite(bf, "PUTTS TAKEN:", 535, 415, FADE_TIME + TEXT_TIME + NUM_TIME + MEDAL_TIME, 5, time);
            }
            if(time > FADE_TIME + 2*TEXT_TIME + NUM_TIME + MEDAL_TIME){
                upwrite(bf2, putts, 760, 349, FADE_TIME+2*TEXT_TIME+NUM_TIME+MEDAL_TIME, FADE_TIME+2*TEXT_TIME+2*NUM_TIME+MEDAL_TIME, time);
            }
            if (time == FADE_TIME + 2*TEXT_TIME + NUM_TIME + MEDAL_TIME) {
                SoundController.getInstance().play(golfMedal + "sfx", false);
            }
            if(time > FADE_TIME + 2*TEXT_TIME + 2*NUM_TIME + MEDAL_TIME){
                String tag = "medal_" + golfMedal + "_shots";
                drawMedal(tag, 1050, 300, FADE_TIME + 2*TEXT_TIME + 2*NUM_TIME + MEDAL_TIME, time);
            }
            if(time > FADE_TIME + 2*TEXT_TIME + 2*NUM_TIME + 2*MEDAL_TIME){
                ImageAsset play = (ImageAsset)assets.getAsset("playButton");
                if(play != null){
                    Color tint = (playHighlight ? Color.GRAY: Color.WHITE);
                    float xx = 1140f-2.5f* MathUtils.sin(count*0.1f);
                    canvas.draw(play.getTexture(), tint, play.getOrigin().x, play.getOrigin().y, xx, 100f, 0, 1f, 1f);

                }
            }
        }



        canvas.end();
    }

    private void typewrite(BitmapFont bf, String s, int x, int y, int start_time, int step, int time){
        int steps = Math.min((time - start_time) / step, s.length());
        String sub = s.substring(0, steps);
        bf.setColor(blackTint);
        canvas.drawText(sub, bf, x + 2, y + 2);
        bf.setColor(textTint);
        canvas.drawText(sub, bf, x, y);
    }

    private void upwrite(BitmapFont bf, int value, int x, int y, int start_time, int end_time, int time){
        float ratio = Math.min((float)(time-start_time)/ (end_time - start_time), 1f);
        int nvalue = (int)(value*ratio);
        bf.setColor(blackTint);
        canvas.drawTextRight(""+nvalue, bf, x + 2, y + 2);
        bf.setColor(textTint);
        canvas.drawTextRight(""+nvalue, bf, x, y);
    }

    private void drawMedal(String tag, int x, int y, int start_time, int time){
        ImageAsset medal = (ImageAsset) assets.getAsset(tag);
        float ratio = Math.min((time - start_time)/(float)(MEDAL_ZOOM), 1f);
        float angle = (float)(ratio*Math.PI*2);
        if(time > start_time + MEDAL_ZOOM){
            angle = (float)(Math.sin(time*0.05f)*Math.PI*(0.1f));
        }
        canvas.draw(medal.getTexture(), Color.WHITE, 70, 0, x, y, angle, ratio, ratio);
    }

    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}

    public void activate(){
        Gdx.input.setInputProcessor(this);
        //SoundController.getInstance().update();

        //SoundController.getInstance().play("levelmusic", true, 0.75f);
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(time > FADE_TIME + 2*TEXT_TIME + 2*NUM_TIME + 2*MEDAL_TIME){
            float radius = 64f;
            float dist = (screenX-1140f)*(screenX-1140f)+(screenY-620f)*(screenY-620f);
            if (dist < radius*radius) {
                listener.exitScreen(this, WorldController.LEVEL_SELECT);
                playHighlight = false;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        playHighlight = false;
        float radius = 64f;
        float dist = (screenX-1140f)*(screenX-1140f)+(screenY-620f)*(screenY-620f);
        if (dist < radius*radius) {
            playHighlight = true;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
