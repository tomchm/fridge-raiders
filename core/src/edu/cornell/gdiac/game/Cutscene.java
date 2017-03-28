package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Sal on 3/28/2017.
 */
public class Cutscene implements Screen {
    private GameCanvas canvas;
    private Texture[] hands;
    private Texture background;
    private Texture blackBar;
    private float time;

    public Cutscene(GameCanvas c) {
        canvas = c;
        hands = new Texture[9];
        for (int i = 0; i < 9; i++) {
            hands[i] = new Texture("cutscenes/hand" + (i+1) + ".png");
        }
        background = new Texture("cutscenes/background.png");
        blackBar = new Texture("cutscenes/black_bar.png");
        time = 0f;
    }

    public void dispose() {
        canvas = null;
    }
    public void hide() {}
    public void show() {}
    public void render(float dt) {
        canvas.moveCamera(0f, 0f);
        time += dt;

        canvas.begin();
        canvas.draw(background, -640f, -360f);
        canvas.draw(hands[0], Color.WHITE, hands[0].getWidth()*0.5f, hands[0].getHeight(), 0f, 100f, 0f, 1f, 1f);
        canvas.draw(blackBar, -640f, 260f);
        canvas.draw(blackBar, -640f, -260f);
        canvas.end();
    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}


}
