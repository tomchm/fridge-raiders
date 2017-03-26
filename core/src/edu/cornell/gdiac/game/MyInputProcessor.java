package edu.cornell.gdiac.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sean on 3/2/17.
 */
public class MyInputProcessor implements InputProcessor {

    public int lastX = 0;
    public int lastY = 0;
    public int dragX = 0;
    public int dragY = 0;
    public boolean released = false;
    public boolean shouldRecordClick = true;

    public Vector2 magnitude = new Vector2(0,0);

    public boolean keyDown (int keycode) {
        return false;
    }

    public boolean keyUp (int keycode) {
        return false;
    }

    public boolean keyTyped (char character) {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int button) {
        if(shouldRecordClick) {
            lastX = x;
            lastY = y;
            released = false;
            magnitude = new Vector2(0,0);
        }
            return true;

    }

    public boolean touchUp (int x, int y, int pointer, int button) {
        released = true;
        magnitude = new Vector2(lastX - dragX,lastY-dragY);
        return true;
    }

    public boolean touchDragged (int x, int y, int pointer) {
        dragX = x;
        dragY = y;
        magnitude = new Vector2(lastX - dragX,lastY-dragY);

        return true;
    }

    public boolean mouseMoved (int x, int y) {
        return false;
    }

    public boolean scrolled (int amount) {
        return false;
    }
}
