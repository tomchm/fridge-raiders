package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

/**
 * Created by Sal on 5/7/2017.
 */
public class StoryScene implements Screen {
    private static float IMAGE_TIME = 3f;
    private static float TEXT_TIME = 5f;
    private static float FADE_TIME = 1f;

    public enum Level { APARTMENT_1, APARTMENT_2, APARTMENT_3, CLUB_1, CLUB_2, CLUB_3, MANSION_1, MANSION_2, MANSION_3 };
    private static final String PLAY_FILE = "gui/play.png";

    private GameCanvas canvas;
    /** Overall time that the story scene has been up */
    private float sceneTime;
    /** Time that the current image has been up */
    private float imgTime;
    private Texture currentImg;
    private Texture gradient;
    private Color tint;
    private ScreenListener listener;
    private Queue<Texture> imgQueue;
    private int levelCode;
    private String storyText;
    private int numChars;
    private boolean highlight;

    public StoryScene(GameCanvas c) {
        canvas = c;
        sceneTime = 0f;
        imgTime = 0f;
        imgQueue = new Queue<Texture>();
        tint = new Color(1f, 1f, 1f, 1f);
    }

    public void dispose() {
        canvas = null;
    }
    public void hide() {}
    public void show() {}
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    /** This will be called with a code for each level. */
    public void setUp(int levelCode) {
        sceneTime = 0f;
        imgTime = 0f;
        imgQueue.clear();
        this.levelCode = levelCode;
        numChars = 0;

        // this can't go in the constructor! The assets wouldn't be ready yet.
        gradient = ((ImageAsset)AssetLoader.getInstance().getAsset("gradient")).getTexture().getTexture();
        switch (levelCode) {
            case 100:
                add("cutscene1");
                storyText = "I go by many names. Some say I'm a monster. I call myself an \nartist. And tonight, I will paint my masterpiece.";
                break;
            case 104:
                add("cutscene4");
                storyText = "The scents wafting from that mansion are heavenly. I'm sure \nthe homeowner wouldn't mind a little company for dinner.";
                break;
            case 108:
                add("cutscene5");
                storyText = "I've arrived at my last target. A lotta guys spend their nights\nat the jazz club dancing, but I stop by for the in-house meal.";
                break;
            case 200:
                add("cutscene2");
                storyText = "Night falls across the city like a slice of provolone on bread. \nI'm not sentimental, but there's something pretty about it.";
                break;
            case 204:
                add("cutscene3");
                storyText = "On a night like this, a guy could really go for seconds. Or \nfifths. Who am I to resist the beckoning of a home-cooked meal?";
                break;
            default:
                add("cutscene1");
                storyText = "The rain comes down and the moon shimmers overhead.\nI must eat.";
                break;
        }

        currentImg = imgQueue.removeFirst();
    }

    private void add(String tag){
        Asset a = AssetLoader.getInstance().getAsset(tag);
        Texture t = ((ImageAsset)a).getTexture().getTexture();
        imgQueue.addLast(t);
    }

    public void render(float dt) {
        SoundController.getInstance().update();

        canvas.moveCamera(0f, 0f);
        canvas.zoomCamera(1);
        sceneTime += dt;
        imgTime += dt;

        if (imgTime < FADE_TIME) {
            tint.r = tint.g = tint.b = imgTime / FADE_TIME;
        }

        numChars = Math.min((int)(storyText.length() * sceneTime / TEXT_TIME), storyText.length());

        canvas.begin();

        canvas.draw(currentImg, tint, -640, -360, 1280, 720);
        canvas.draw(gradient, -640, -360);
        //canvas.drawText("Space to skip", ((FontAsset)AssetLoader.getInstance().getAsset("gothic32")).getFont(), -600, 300);
        canvas.drawText(storyText.substring(0,numChars), ((FontAsset)AssetLoader.getInstance().getAsset("typewriter")).getFont(), -620, -270);

        if (imgTime > IMAGE_TIME && imgQueue.size > 0) {
            imgTime = 0f;
            currentImg = imgQueue.removeFirst();
        }

        if (sceneTime > TEXT_TIME) {
            ImageAsset play = (ImageAsset)AssetLoader.getInstance().getAsset("playButton");
            if(play != null){
                Color tint = (highlight ? Color.GRAY: Color.WHITE);
                float xx = 440f+5f* MathUtils.sin(sceneTime*0.1f*60f);
                canvas.draw(play.getTexture(), tint, play.getOrigin().x, play.getOrigin().y, xx, 240f, 0, 1f, 1f);
            }
        }

        InputController.getInstance().readInput();
        if (InputController.getInstance().didSkip()) {
            listener.exitScreen(this, levelCode);
            //System.out.println(levelCode / 100);
            if (levelCode / 100 == 1) SoundController.getInstance().safeStop("blah");
            if (levelCode / 100 == 2) SoundController.getInstance().safeStop("blah2");
        }
        ImageAsset play = (ImageAsset)AssetLoader.getInstance().getAsset("playButton");
        int clickx = InputController.getInstance().getMyProcessor().menuX;
        int clicky = InputController.getInstance().getMyProcessor().menuY;
        int width = 1280;
        int height = 720;
        int centerX = width/2+440;
        int centerY = (int)(0.25f*height)-60;
        if(play !=null){
            Texture playb = new Texture(PLAY_FILE);
            float radius = playb.getWidth()/2.0f;

            float dist = (clickx-centerX)*(clickx-centerX)+(clicky-centerY)*(clicky-centerY);
            if (dist < radius*radius) {
                listener.exitScreen(this, levelCode);
            }
            InputController.getInstance().getMyProcessor().menuX = 0;
            InputController.getInstance().getMyProcessor().menuY = 0;
        }

        int mx = InputController.getInstance().getMyProcessor().hoveringX;
        int my = InputController.getInstance().getMyProcessor().hoveringY;
        highlight = false;
        float radius = 64f;
        float dist = (mx-centerX)*(mx-centerX)+(my-centerY)*(my-centerY);
        if (dist < radius*radius) {
            highlight = true;
        }

        canvas.end();

    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}
    public void reset() {this.sceneTime = 0f; this.imgTime = 0f; this.imgQueue.clear();}




}
