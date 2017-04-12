package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.SpacebarController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;

/**
 * Created by tomchm on 3/19/17.
 */
public class GUIController {

    private ObjectMap<String, GUIModel> guiMap = new ObjectMap<String, GUIModel>();

    public GUIController(WorldModel worldModel, SpacebarController controller){
        GUIModel spacebarGUI = new SpacebarGUIModel(controller, worldModel);
        guiMap.put(spacebarGUI.guiTag, spacebarGUI);
        GUIModel aimGUI = new AimGUIModel(worldModel);
        guiMap.put(aimGUI.guiTag, aimGUI);
        GUIModel pauseGUI = new PauseGUI(worldModel);
        guiMap.put(pauseGUI.guiTag, pauseGUI);

    }

    public Array<GUIModel> getGUIs(){
        return guiMap.values().toArray();
    }

    public void update(float dt){
        for(GUIModel gui : guiMap.values()){
            gui.update(dt);
        }
    }

    public void draw(GameCanvas canvas){
        canvas.begin();
        for(GUIModel gui: guiMap.values()){
            gui.draw(canvas);
        }
        canvas.end();
    }

    public GUIModel getGUI(String tag){
        return guiMap.get(tag);
    }

    public void setOrigin(Vector2 new_origin){
        GUIModel.setOrigin(new_origin);
    }

}
