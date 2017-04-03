package edu.cornell.gdiac.game.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Sal on 3/20/2017.
 */
public class SoundAsset extends Asset {
    private String tag;
    private Sound sound;

    public SoundAsset(String filename, String tag) {
        this.fileName=filename;
        this.tag = tag;
        sound = null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setSound(Sound sound){
        this.sound = sound;
    }

    public Sound getSound(){
        return sound;
    }

    public String getTag(){
        return tag;
    }
}
