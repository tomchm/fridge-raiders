package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.FoodModel;

/**
 * Created by Sal on 3/12/2017.
 */
public class EatController {

    private WorldModel world;

    public  EatController(WorldModel wm){
        this.world = wm;
    }

    public void eat(FoodModel food) {
        DetectiveModel player = world.getPlayer();
        player.eatFood(food.getAmount());

        if(player.getCapacity() >= player.getMaxCapacity()){
            player.setStage(true);
            player.getBody().getFixtureList().get(0).setRestitution(1.0f);

        }


    }

    public void stop() {}
}
