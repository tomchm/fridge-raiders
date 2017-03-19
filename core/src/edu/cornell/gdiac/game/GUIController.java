package edu.cornell.gdiac.game;

/**
 * Created by tomchm on 3/19/17.
 */
public class GUIController {

    private GUIModel[] guis;

    public GUIController(){
        GUIModel gameGUI = new GUIModel();
        guis = new GUIModel[] {gameGUI};
    }

    public GUIModel[] getGUIs(){
        return guis;
    }

    public void draw(GameCanvas canvas){
        for(GUIModel gui : guis){
            gui.draw(canvas);
        }
    }

}
