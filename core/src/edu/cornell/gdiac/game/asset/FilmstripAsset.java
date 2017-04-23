package edu.cornell.gdiac.game.asset;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Tomasz on 3/15/2017.
 */
public class FilmstripAsset extends Asset{
    private String fileName;
    private TextureRegion texture;
    private Vector2 origin;
    private Vector2 imageScale;
    private int numFrames, speed;
    private int width, height;

    public FilmstripAsset(String fileName, Vector2 origin, Vector2 imageScale, int numFrames, int speed){
        this.fileName = fileName;
        this.origin = origin;
        this.imageScale = imageScale;
        this.numFrames = numFrames;
        this.speed = speed;
    }

    public String getFileName() {
        return fileName;
    }

    public TextureRegion getTexture(int frame) {
        if(texture != null){
            frame = frame % numFrames;
            texture.setRegion(width*frame, 0, width, height);
        }
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
        width = texture.getRegionWidth() / numFrames;
        height = texture.getRegionHeight();
    }

    /** Width of just one frame. */
    public int getWidth() {
        return width;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public int getSpeed() {
        return speed;
    }
}
