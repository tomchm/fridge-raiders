package edu.cornell.gdiac.game;

import edu.cornell.gdiac.EatController;
import edu.cornell.gdiac.GrabController;
import edu.cornell.gdiac.game.model.FurnitureModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.util.PooledList;

/**
 * Created by Sal on 3/12/2017.
 */
public class SpacebarController {
    private HingeController hingeController;
    private EatController eatController;
    private GrabController grabController;
    private WorldModel worldModel;

    public SpacebarController(HingeController hc, EatController ec, GrabController gc, WorldModel wm) {
        hingeController=hc;
        eatController = ec;
        grabController=gc;
        worldModel=wm;
    }

    private GameObject getClosest() {
        PooledList<GameObject> gobjs = worldModel.getGameObjects();
        for (GameObject gob : gobjs) {
            return gob;
        }
        return null;
    }

    private void keyDown() {
        GameObject closest = getClosest();
        if (closest == null) return;
        else {
            Class objType = closest.getClass();
            if (objType == FurnitureModel.class) { grabController.grab((FurnitureModel) closest); }
            // else if (objType == FoodModel.class) {eatController.eat(closest);}
            // else if (objType == DoorModel.class) {hingeController.toggle(); }
        }
    }
}
