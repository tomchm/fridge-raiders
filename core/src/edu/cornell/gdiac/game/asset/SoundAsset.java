package edu.cornell.gdiac.game.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Sal on 3/20/2017.
 */
public class SoundAsset extends Asset {
    private String filename, tag;
    private Sound sound;

    public SoundAsset(String filename, String tag) {
        this.filename=filename;
        this.tag = tag;
        try{
            this.sound = Gdx.audio.newSound(Gdx.files.internal(filename));
        }
        catch(Exception e){}
    }

    public String getFilename() {
        return filename;
    }

    public Sound getSound(){
        return sound;
    }

    public String getTag(){
        return tag;
    }
}
