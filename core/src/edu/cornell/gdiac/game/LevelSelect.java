package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

import javax.xml.soap.Text;

/**
 * Created by Sal on 3/28/2017.
 */
public class LevelSelect implements Screen {

    private static final float ZOOM_TIME = 700f, FADE_TIME = 120f;
    private GameCanvas canvas;
    private ScreenListener listener;
    private AssetLoader assets;
    private boolean started;
    private int time;
    private float originx, originy, bgScale, px, py, pscale;
    private Color newsTint;

    public LevelSelect(GameCanvas c) {
        canvas = c;
        assets= AssetLoader.getInstance();
        started = false;
        time = 0;
        newsTint = new Color(0.1f, 0.1f, 0.1f, 1f);
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

        canvas.moveCamera(0, 0);

        canvas.clear();
        canvas.begin();

        ImageAsset bg = (ImageAsset) assets.getAsset("fridgeMenu");
        ImageAsset nOpen = (ImageAsset) assets.getAsset("newspaperOpen");
        if(bg != null && nOpen != null){
            if(!started){
                if(time < ZOOM_TIME){
                    time++;
                    bgScale = 0.54f + time/ZOOM_TIME*0.64f;
                    originx = -640f - time/ZOOM_TIME*1500f;
                    originy = -360 - time/ZOOM_TIME*500f;

                    pscale = 0.15f + time/ZOOM_TIME*0.64f;
                    px = 240 - time/ZOOM_TIME*1500f;
                    py = 200 - time/ZOOM_TIME*500f;
                }
                else if(time < ZOOM_TIME + FADE_TIME){
                    time++;
                    float ratio = time / (ZOOM_TIME + FADE_TIME);
                    newsTint = new Color(ratio, ratio, ratio, 1f);
                }

                canvas.draw(bg.getTexture(), Color.WHITE, 0, 0, originx, originy, 0, bgScale, bgScale);
                canvas.draw(nOpen.getTexture(), newsTint, 0, 0, px, py, 0, pscale, pscale);


            }
            else{

            }

        }


        canvas.end();
    }
    public void pause() {}
    public void resume() {}
    public void resize(int width, int height) {}


}
