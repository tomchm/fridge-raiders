package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.FoodModel;

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
    }

    public void stop() {
        worldModel.getPlayer().stopEating();
    }
}
