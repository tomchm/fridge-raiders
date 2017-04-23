package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

/**
 * Created by Sal on 3/28/2017.
 */
public class LevelSelect implements Screen {
    private GameCanvas canvas;
    private ScreenListener listener;

    public LevelSelect(GameCanvas c) {
        canvas = c;
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

        canvas.begin();
        // DRAW STUFF
        canvas.end();
    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}


}
