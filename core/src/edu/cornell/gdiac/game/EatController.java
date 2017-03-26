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
        if(worldModel.getPlayer().getSpeed() == 0.0) {
            worldModel.getPlayer().startEating(food);
        }
    }

    public void stop() {
        worldModel.getPlayer().stopEating();
    }
}
