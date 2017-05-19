package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.asset.FontAsset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

/**
 * Created by Sal on 5/18/2017.
 */
public class Credits implements Screen {
    private static float TOTAL_TIME = 32f;
    private static float IMAGE_SPACE = 300f;
    private static float IMAGE_HEIGHT = 300f;
    private static int NUM_IMAGES = 7;

    private GameCanvas canvas;
    /** Overall time that the credits have been up */
    private float creditsTime;
    private float totalHeight;
    private Color tint;
    private ScreenListener listener;
    private Queue<Texture> imgQueue;

    public Credits(GameCanvas c) {
        canvas = c;
        creditsTime = 0f;
        tint = new Color(1f, 1f, 1f, 1f);
        totalHeight = NUM_IMAGES*IMAGE_HEIGHT + (NUM_IMAGES-1)*IMAGE_SPACE + 720;
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
        SoundController.getInstance().update();

        canvas.moveCamera(0f, 0f);
        canvas.zoomCamera(1);
        creditsTime += 1f / 60f;

        canvas.begin();
        canvas.clear();

        for (int k = 0; k < NUM_IMAGES; ++k) {
            ImageAsset ia = (ImageAsset)AssetLoader.getInstance().getAsset("credits"+k);
            float y = -k * (IMAGE_HEIGHT + IMAGE_SPACE) + totalHeight * creditsTime / TOTAL_TIME;
            canvas.draw(ia.getTexture(), Color.WHITE, 0f, 300f,-640f, y-360f, 0f, 1f, 1f);
        }

        canvas.end();

        InputController.getInstance().readInput();
        if (InputController.getInstance().didSkip() || creditsTime >= TOTAL_TIME) {
            listener.exitScreen(this, WorldController.LEVEL_SELECT);
            SoundController.getInstance().safeStop("music_credits");
        }
    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}
    public void reset() {creditsTime = 0f;}




}
