package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.asset.FilmstripAsset;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.util.RandomController;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

import javax.xml.soap.Text;
import java.awt.*;
import java.util.Random;

/**
 * Created by Sal on 3/28/2017.
 */
public class LevelSelect implements Screen, InputProcessor {

    private static final float ZOOM_TIME = 80f, FADE_TIME = 80f;
    private static final Color MAGNET_COLOR = new Color(84f/255f, 64f/255f, 48f/255f, 1f);
    private GameCanvas canvas;
    private ScreenListener listener;
    private AssetLoader assets;
    private boolean started;
    private int time, fadeTime;
    private float originx, originy, bgScale, pratio, pscale;
    private Color newsTint, magnetTint;
    private SelectState state;
    private GuyState animation;
    private int levelCode;
    private int count, guyX;
    private boolean playHighlight;

    private enum GuyState {WALK_LEFT, WALK_RIGHT, IDLE_LEFT, IDLE_RIGHT};

    private enum SelectState {ZOOM_IN, ZOOM_OUT, SELECTION, HOME, FADE_OUT, FADE_IN};

    private Level[][] levels;

    public LevelSelect(GameCanvas c) {
        SoundController.getInstance().play("levelmusic", true, 0.75f);
        canvas = new GameCanvas();
        assets= AssetLoader.getInstance();
        started = false;
        time = 0;
        fadeTime = 0;
        newsTint = new Color(0.1f, 0.1f, 0.1f, 1f);
        magnetTint = new Color(0.1f, 0.1f, 0.1f, 1f);
        state = SelectState.HOME;
        animation = GuyState.WALK_RIGHT;
        count = 0;
        guyX = -400;

        levels = new Level[3][3];
        ScoreIOController.LevelData levelData[] = ScoreIOController.getScores();

        for(int i=0; i<9; i++){
            levels[i/3][i%3] = new Level(100+i, "newspaperOpen1", levelData[i].unlocked, levelData[i].foodMedal, levelData[i].golfMedal);
        }

        /*
        levels[0][0] = new Level(101, "newspaperOpen1", true);
        levels[0][1] = new Level(102, "newspaperOpen2", true);
        levels[0][2] = new Level(103, "newspaperOpen3", true);

        levels[1][0] = new Level(104, "newspaperOpen1", false);
        levels[1][1] = new Level(105, "newspaperOpen1", false);
        levels[1][2] = new Level(106, "newspaperOpen1", false);

        levels[2][0] = new Level(107, "newspaperOpen1", false);
        levels[2][1] = new Level(108, "newspaperOpen1", false);
        levels[2][2] = new Level(109, "newspaperOpen1", false);
        */
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

        ImageAsset bg = (ImageAsset) assets.getAsset("fridgeMenu");
        ImageAsset closed = (ImageAsset) assets.getAsset("newspaperClosed");
        ImageAsset magnet = (ImageAsset) assets.getAsset("magnet");
        ImageAsset title = (ImageAsset) assets.getAsset("title");
        if(bg != null && closed != null && magnet != null && title != null){
            if(state == SelectState.ZOOM_IN){
                time++;
                if(time > ZOOM_TIME+FADE_TIME){
                    state = SelectState.SELECTION;
                }
            }
            else if(state == SelectState.ZOOM_OUT){
                time--;
                if(time == 0){
                    state = SelectState.HOME;
                }
            }
            if(time < ZOOM_TIME){
                float ratio = time/ZOOM_TIME;
                canvas.zoomCamera(1-0.60f*ratio);
                canvas.positionCamera( 0+ ratio*370f,  0+ratio*50f);


                /*
                pratio = ratio;

                bgScale = 0.54f + ratio*0.64f;
                originx = -640f - ratio*1500f;
                originy = -360 - ratio*500f;

                pscale = 0.17f*(1-ratio) + (ratio*0.4f);
                */

            }
            else if(time < ZOOM_TIME + FADE_TIME){
                float ratio = 0.1f + 0.9f*(time-ZOOM_TIME) / (FADE_TIME);
                newsTint = new Color(ratio, ratio, ratio, 1f);
                magnetTint = new Color(0.1f, 0.1f, 0.1f, 1f).lerp(MAGNET_COLOR, ratio);
            }



            canvas.draw(bg.getTexture(), Color.WHITE, 0, 0, -640f, -360f, 0, 0.54f, 0.54f);
            canvas.draw(title.getTexture(), Color.WHITE, 0, 0, -640f, -360f, 0, 1, 1);



            FilmstripAsset far = (FilmstripAsset)assets.getAsset("player_right_idle");
            FilmstripAsset fal = (FilmstripAsset)assets.getAsset("player_left_idle");
            if(far != null && fal != null){
                int nFrame = (count / far.getSpeed()) % far.getNumFrames();
                if(animation == GuyState.WALK_RIGHT){
                    guyX += 1f;
                    if(guyX == 0){
                        animation = GuyState.IDLE_RIGHT;
                    }
                }
                else if(animation == GuyState.WALK_LEFT){
                    guyX -= 1f;
                    if(guyX == -400){
                        animation = GuyState.IDLE_LEFT;
                    }
                }
                else if(animation == GuyState.IDLE_RIGHT){
                    Random random = new Random();
                    if(random.nextFloat() < 0.01f){
                        animation = GuyState.WALK_LEFT;
                    }
                }
                else if(animation == GuyState.IDLE_LEFT){
                    Random random = new Random();
                    if(random.nextFloat() < 0.01f){
                        animation = GuyState.WALK_RIGHT;
                    }
                }
                TextureRegion texture = null;
                switch(animation){
                    case WALK_LEFT:
                    case IDLE_LEFT:
                        texture = fal.getTexture(nFrame);
                        break;
                    case WALK_RIGHT:
                    case IDLE_RIGHT:
                        texture = far.getTexture(nFrame);
                        break;
                }
                canvas.draw(texture, Color.BLACK,far.getOrigin().x,far.getOrigin().y,guyX,-450,0,1.5f,1.5f);
            }

            if(state == SelectState.HOME){
                ImageAsset play = (ImageAsset)assets.getAsset("playButton");
                if(play != null){
                    Color tint = (playHighlight ? Color.GRAY: Color.WHITE);
                    float xx = 440f+5f* MathUtils.sin(count*0.1f);
                    canvas.draw(play.getTexture(), tint, play.getOrigin().x, play.getOrigin().y, xx, -240f, 0, 1f, 1f);

                }
            }
            else if(state == SelectState.SELECTION){
                ImageAsset play = (ImageAsset)assets.getAsset("playButton");
                if(play != null){
                    Color tint = (playHighlight ? Color.GRAY: Color.WHITE);
                    float xx = 180f-2.5f* MathUtils.sin(count*0.1f);
                    canvas.draw(play.getTexture(), tint, play.getOrigin().x, play.getOrigin().y, xx, 150f, 0, -0.5f, 0.5f);

                }
            }


            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    /*
                    float px = -640 + ((930f+j*140f)*(1-pratio)) + (pratio*(520f+j*200f));
                    float py = 360 - ((210f+i*140f)*(1-pratio)) -  (pratio*(80f+i*200f));
                    */
                    float px = -640 + (920f+j*100f);
                    float py = 360 - (190f+i*100f);
                    ImageAsset ia = (ImageAsset)assets.getAsset(levels[i][j].tag);
                    if(ia != null){
                        if(levels[i][j].unlocked){
                            canvas.draw(ia.getTexture(), newsTint, 0, 480, px, py, 0, 0.17f, 0.17f);
                            if(levels[i][j].highlight && state == SelectState.SELECTION){
                                canvas.setBlendState(GameCanvas.BlendState.ADDITIVE);
                                canvas.draw(ia.getTexture(), new Color(0.3f, 0.3f, 0.3f, 1f), 0, 480, px, py, 0, 0.17f, 0.17f);
                                canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);
                            }
                        }
                        else {
                            canvas.draw(closed.getTexture(), newsTint, 0, 480, px, py, 0, 0.17f, 0.17f);
                        }
                    }

                    canvas.draw(magnet.getTexture(), magnetTint, 0, 0, px+28, py-10, 0, 0.10f, 0.10f);

                }
            }
        }

        if(state == SelectState.FADE_OUT){
            fadeTime++;
            if(fadeTime == FADE_TIME){
                state = SelectState.FADE_IN;
                listener.exitScreen(this, 100 + levelCode);
            }
            float ratio = fadeTime / FADE_TIME;
            canvas.drawRect(0, -500, 2000, 2000, 0, 0, 0, ratio);
        }
        else if(state == SelectState.FADE_IN){
            fadeTime--;
            if(fadeTime == 0){
                state = SelectState.SELECTION;
            }
            float ratio = fadeTime / FADE_TIME;
            canvas.drawRect(0, -500, 2000, 2000, 0, 0, 0, ratio);
        }


        canvas.end();
    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}

    public void activate(){
        Gdx.input.setInputProcessor(this);
        SoundController.getInstance().update();

        SoundController.getInstance().play("levelMusic", true, 0.75f);
    }

    private class Level{
        String foodScore, golfScore;
        int exitCode;
        String tag;
        boolean unlocked, completed, highlight;
        String foodMedal, golfMedal;

        public Level(int exitCode, String tag, boolean unlocked, String foodMedal, String golfMedal){
            this.exitCode = exitCode;
            this.tag = tag;
            this.unlocked = unlocked;
            this.foodMedal = foodMedal;
            this.golfMedal = golfMedal;
            highlight = false;
        }

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
        if(Input.Buttons.LEFT == button){
            if(state == SelectState.SELECTION){
                for(int i=0; i< 3; i++){
                    for(int j=0; j<3; j++){
                        int x = 430 + i*247;
                        int y = 70 + j*247;
                        if(screenX >= x && screenX <= x+187 && screenY >= y && screenY <= y + 187){
                            if(levels[j][i].unlocked){
                                state = SelectState.FADE_OUT;
                                levelCode = 100+j*3+i;
                            }
                        }
                    }
                }

                float radius = 64f;
                float dist = (screenX-200f)*(screenX-200f)+(screenY-120f)*(screenY-120f);
                if (dist < radius*radius) {
                    state = SelectState.ZOOM_OUT;
                    playHighlight = false;
                }
            }
            else if(state == SelectState.HOME){
                float radius = 64f;
                float dist = (screenX-1080f)*(screenX-1080f)+(screenY-600f)*(screenY-600f);
                if (dist < radius*radius) {
                    state = SelectState.ZOOM_IN;
                    playHighlight = false;
                }
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
        if(state == SelectState.SELECTION){
            for(int i=0; i< 3; i++){
                for(int j=0; j<3; j++){
                    int x = 430 + i*247;
                    int y = 70 + j*247;
                    levels[j][i].highlight = false;
                    if(screenX >= x && screenX <= x+187 && screenY >= y && screenY <= y + 187){
                        levels[j][i].highlight = true;
                    }
                }
            }

            playHighlight = false;
            float radius = 64f;
            float dist = (screenX-200f)*(screenX-200f)+(screenY-120f)*(screenY-120f);
            if (dist < radius*radius) {
                playHighlight = true;
            }
        }
        else if(state == SelectState.HOME){
            playHighlight = false;
            float radius = 64f;
            float dist = (screenX-1080f)*(screenX-1080f)+(screenY-600f)*(screenY-600f);
            if (dist < radius*radius) {
                playHighlight = true;
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
