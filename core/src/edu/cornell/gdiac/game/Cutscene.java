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

    private static float LOWER_TIME = 2.5f;

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

        Texture hand = hands[0];
        if (time > 1f) hand = hands[1]; if (time > 1.33f) hand = hands[2];
        if (time > 1.67f) hand = hands[3]; if (time > 2f) hand = hands[4];
        if (time > 2.5f) hand = hands[5]; if (time > 2.7f) hand = hands[6];
        if (time > 2.9) hand = hands[7]; if (time > 3.1f) hand = hands[8];

        float x = 10f*(float)Math.random() - 5f;
        float y = 360f;
        if (time < LOWER_TIME) { y = 500f + (y - 500f)*time/LOWER_TIME; }
        y += 10f*(float)Math.random() - 5f;
        canvas.draw(hand, Color.WHITE, hand.getWidth()*0.5f, hand.getHeight(), 0f, y, 0f, 1f, 1f);
        canvas.draw(blackBar, -640f, 260f);
        canvas.draw(blackBar, -640f, -360f);
        canvas.end();
    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}


}
