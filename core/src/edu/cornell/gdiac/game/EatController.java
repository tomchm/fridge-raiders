package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.FoodModel;
import edu.cornell.gdiac.util.SoundController;

import static sun.audio.AudioPlayer.player;

/**
 * Created by Sal on 3/12/2017.
 */
public class EatController {

    private WorldModel worldModel;

    public  EatController(WorldModel wm){
        worldModel = wm;
    }

    public void eat(FoodModel food) {
        worldModel.getPlayer().startEating(food);
        worldModel.getPlayer().setSpeed();
        SoundController.getInstance().play("chewing", true);
    }

    public void stop() {
        if (SoundController.getInstance().isActive("chewing")) SoundController.getInstance().stop("chewing");
        worldModel.getPlayer().stopEating();
    }
}
