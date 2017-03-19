package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.asset.Asset;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.GameObject;

import java.awt.*;

/**
 * Created by tomchm on 3/19/17.
 */
public abstract class GUIModel {

    protected String[] tags;
    protected ObjectMap<String, Asset> assetMap = new ObjectMap<String, Asset>();
    protected String guiTag;

    public void addAsset(String tag, Asset asset){
        assetMap.put(tag, asset);
    }

    public String[] getTags(){
        return tags;
    }

    public String getGUITag() { return guiTag; }

    public abstract void draw(GameCanvas canvas);

}
