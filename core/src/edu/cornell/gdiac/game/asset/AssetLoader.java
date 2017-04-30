package edu.cornell.gdiac.game.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.gui.GUIController;
import edu.cornell.gdiac.game.gui.GUIModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.SoundController;

import java.io.FileReader;

/**
 * Created by tomchm on 3/10/17.
 */
public class AssetLoader
{
    protected enum AssetState {
        /** No assets loaded */
        EMPTY,
        /** Still loading assets */
        LOADING,
        /** Assets are complete */
        COMPLETE
    }

    /** Track asset loading from all instances and subclasses */
    protected AssetState worldAssetState = AssetState.EMPTY;

    protected ObjectMap<String, Asset> assetMap;

    private static AssetLoader instance;

    public static AssetLoader getInstance(){
        if(instance == null){
            instance = new AssetLoader();
        }
        return instance;
    }

    private AssetLoader(){
        assetMap = new ObjectMap<String, Asset>();

        JsonReader parser = new JsonReader();
        JsonValue value;
        try{
            value = parser.parse(Gdx.files.local("assets.json"));
            JsonValue map = value.get("data");
            for (JsonValue entry = map.child; entry != null; entry = entry.next){
                String type = entry.getString("type");
                if(type.equals("image")){
                    String tag = entry.getString("tag");
                    String filename = entry.getString("filename");
                    float origin_x = entry.getFloat("origin_x");
                    float origin_y = entry.getFloat("origin_y");
                    float scale_x = entry.getFloat("scale_x");
                    float scale_y = entry.getFloat("scale_y");
                    Asset asset = new ImageAsset(filename, new Vector2(origin_x, origin_y), new Vector2(scale_x, scale_y));
                    assetMap.put(tag, asset);
                }
                else if(type.equals("filmstrip")){
                    String tag = entry.getString("tag");
                    String filename = entry.getString("filename");
                    float origin_x = entry.getFloat("origin_x");
                    float origin_y = entry.getFloat("origin_y");
                    float scale_x = entry.getFloat("scale_x");
                    float scale_y = entry.getFloat("scale_y");
                    int numFrames = entry.getInt("frames");
                    int speed = entry.getInt("speed");
                    Asset asset = new FilmstripAsset(filename, new Vector2(origin_x, origin_y), new Vector2(scale_x, scale_y), numFrames, speed);
                    assetMap.put(tag, asset);
                }
                else if (type.equals("sound")) {
                    String tag = entry.getString("tag");
                    String filename = entry.getString("filename");
                    Asset asset = new SoundAsset(filename, tag);
                    assetMap.put(tag, asset);
                }
            }
        }
        catch(Exception e){
            System.out.println("assets.json not found.");
        }
        assetMap.put("gothic32", new FontAsset("shared/gothic32.ttf", "gothic32", 32));
        assetMap.put("gothic72", new FontAsset("shared/gothic72.ttf", "gothic72", 72));
        assetMap.put("fridgeMagnets72", new FontAsset("shared/FridgeMagnets72.ttf", "fridgeMagnets72", 72));
    }

    public void preLoadContent(AssetManager manager) {
        if (worldAssetState != AssetState.EMPTY) {
            return;
        }
        worldAssetState = AssetState.LOADING;

        for(Asset asset : assetMap.values()){
            if (asset instanceof FilmstripAsset || asset instanceof ImageAsset) {
                manager.load(asset.getFileName(), Texture.class);
            }
            else if (asset instanceof SoundAsset){
                manager.load(asset.getFileName(), Sound.class);
            }
            else if(asset instanceof FontAsset){
                FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
                size2Params.fontFileName = asset.getFileName();
                size2Params.fontParameters.size = ((FontAsset) asset).getSize();
                manager.load(asset.getFileName(), BitmapFont.class, size2Params);
            }
        }
    }

    public void loadContent(AssetManager manager) {
        if (worldAssetState != AssetState.LOADING) {
            return;
        }

        for(Asset asset : assetMap.values()){
            if(asset instanceof ImageAsset){
                ((ImageAsset) asset).setTexture(createTexture(manager,asset.getFileName(),false));
            }
            else if(asset instanceof FilmstripAsset){
                ((FilmstripAsset) asset).setTexture(createTexture(manager,asset.getFileName(),false));
            }
            else if(asset instanceof SoundAsset){
                ((SoundAsset) asset).setSound(createSound(manager, asset.getFileName()));
                SoundController.getInstance().addSound((SoundAsset) asset);
            }
            else if(asset instanceof FontAsset){
                ((FontAsset) asset).setFont(manager.get(asset.getFileName(),BitmapFont.class));
            }
        }

        //SoundController sounds = SoundController.getInstance();


        worldAssetState = AssetState.COMPLETE;
    }

    public void assignContent(WorldModel worldModel){
        for(GameObject go : worldModel.getGameObjects()){
            String[] tags = go.getTags();
            for(String tag : tags){
                Asset asset = assetMap.get(tag);
                if(asset != null){
                    go.addAsset(tag, asset);
                }
            }
        }
    }

    public void assignContent(GUIController guiController){
        for(GUIModel gui : guiController.getGUIs()){
            String[] tags = gui.getTags();
            for(String tag : tags){
                Asset asset = assetMap.get(tag);
                if(asset != null){
                    gui.addAsset(tag, asset);
                }
            }
        }
    }

    public Asset getAsset(String tag){
        return assetMap.get(tag);
    }

    protected TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
        if (manager.isLoaded(file)) {
            TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
            region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            if (repeat) {
                region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
            return region;
        }
        return null;
    }

    protected FilmStrip createFilmStrip(AssetManager manager, String file, int rows, int cols, int size) {
        if (manager.isLoaded(file)) {
            FilmStrip strip = new FilmStrip(manager.get(file, Texture.class),rows,cols,size);
            strip.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return strip;
        }
        return null;
    }

    protected Sound createSound(AssetManager manager, String file){
        if(manager.isLoaded(file)){
            Sound sound = manager.get(file, Sound.class);
            return sound;
        }
        return null;
    }

    public void unloadContent(AssetManager manager) {
        for(Asset asset : assetMap.values()) {
            String s = asset.getFileName();
            if (manager.isLoaded(s)) {
                manager.unload(s);
            }
        }
    }

}
