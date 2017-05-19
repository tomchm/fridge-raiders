package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.InputController;
import edu.cornell.gdiac.game.SpacebarController;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.util.ScreenListener;

import java.util.ArrayList;

/**
 * Created by tomchm on 3/19/17.
 */
public class GUIController {

    private ObjectMap<String, GUIModel> guiMap = new ObjectMap<String, GUIModel>();
    private WorldModel wm;
    private ArrayList<GUIModel> guiList = new ArrayList<GUIModel>();

    public GUIController(WorldModel worldModel, SpacebarController controller, InputController inputControlla){
        wm = worldModel;
        GUIModel minimapGUI = new MinimapGUIModel(worldModel);
        guiMap.put(minimapGUI.guiTag, minimapGUI);
        GUIModel spacebarGUI = new SpacebarGUIModel(controller, worldModel);
        guiMap.put(spacebarGUI.guiTag, spacebarGUI);
        GUIModel aimGUI = new AimGUIModel(controller, worldModel);
        guiMap.put(aimGUI.guiTag, aimGUI);
        GUIModel textGUI = new TextGUIModel(worldModel);
        guiMap.put(textGUI.guiTag, textGUI);
        GUIModel resetGUI = new ResetGUIModel(worldModel,inputControlla);
        guiMap.put(resetGUI.guiTag, resetGUI);
        GUIModel pauseGUI = new PauseGUI(worldModel, inputControlla);
        guiMap.put(pauseGUI.guiTag, pauseGUI);
        GUIModel tutorialGUI = new TutorialGUIModel((SpacebarGUIModel)spacebarGUI, worldModel);
        guiMap.put(tutorialGUI.guiTag, tutorialGUI);

        guiList.add(minimapGUI);
        guiList.add(spacebarGUI);
        guiList.add(aimGUI);
        guiList.add(textGUI);
        guiList.add(pauseGUI);
        guiList.add(resetGUI);
        guiList.add(tutorialGUI);

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

        if(wm.isPaused()){
            GUIModel gui = guiList.get(4);
            gui.draw(canvas);
        }
        else if(wm.hasLost()){
            GUIModel gui = guiList.get(5);
            gui.draw(canvas);
        }
        else {
            for (GUIModel gui : guiList) {
                gui.draw(canvas);
            }

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
