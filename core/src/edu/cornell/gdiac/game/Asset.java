package edu.cornell.gdiac.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import javax.xml.soap.Text;

/**
 * Created by tomchm on 3/10/17.
 */
public class Asset {
    private String fileName;
    private TextureRegion texture;
    private Vector2 origin;
    private Vector2 imageScale;

    public Asset(String fileName, Vector2 origin, Vector2 imageScale){
        this.fileName = fileName;
        this.origin = origin;
        this.imageScale = imageScale;
    }

    public String getFileName() {
        return fileName;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public Vector2 getImageScale() {
        return imageScale;
    }

    public void setTexture(TextureRegion texture){
        this.texture = texture;
    }



}
