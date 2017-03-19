package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;

/**
 * Created by tomchm on 3/19/17.
 */
public class GUIController {

    private ObjectMap<String, GUIModel> guiMap = new ObjectMap<String, GUIModel>();

    public GUIController(){
        GUIModel aimGUI = new AimGUIModel();
        guiMap.put(aimGUI.guiTag, aimGUI);
    }

    public GUIModel[] getGUIs(){
        return guiMap.values().toArray().items;
    }

    public void draw(GameCanvas canvas){
        for(GUIModel gui: guiMap.values()){
            gui.draw(canvas);
        }
    }

    public GUIModel getGUI(String tag){
        return guiMap.get(tag);
    }

}
