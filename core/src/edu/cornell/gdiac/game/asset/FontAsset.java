package edu.cornell.gdiac.game.asset;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Created by Sal on 3/20/2017.
 */
public class FontAsset extends Asset {
    private String tag;
    private BitmapFont font;
    private int size;

    public FontAsset(String filename, String tag, int size) {
        this.fileName=filename;
        this.tag = tag;
        this.size = size;
        font = null;

    }

    public int getSize(){ return size; }

    public void setFont(BitmapFont font){
        this.font = font;
    }

    public BitmapFont getFont(){
        return font;
    }

    public String getTag(){
        return tag;
    }
}
