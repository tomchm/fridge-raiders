package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.FoodModel;

/**
 * Created by Sal on 3/12/2017.
 */
public class EatController {
    public void eat(FoodModel food) { System.out.println(food.getTags()[0]); }
    public void stop() {}
}
