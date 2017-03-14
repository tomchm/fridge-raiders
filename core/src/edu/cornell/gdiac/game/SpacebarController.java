package edu.cornell.gdiac.game;

import edu.cornell.gdiac.EatController;
import edu.cornell.gdiac.GrabController;
import edu.cornell.gdiac.game.model.*;
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

    public SpacebarController(WorldModel wm) {
        hingeController = new HingeController();
        grabController = new GrabController();
        eatController = new EatController();
        worldModel = wm;
    }

    private GameObject getClosest() {
        PooledList<GameObject> gobjs = worldModel.getGameObjects();
        float bestdist = Float.POSITIVE_INFINITY;
        GameObject closest = null;
        DetectiveModel player = worldModel.getPlayer();

        for (GameObject gob : gobjs) {
            if (gob == player) continue;
            if (gob.getClass() == WallModel.class) continue;

            float dist = (player.getBody().getPosition().sub(gob.getBody().getPosition())).len();
            if (dist < bestdist) {
                closest = gob;
                bestdist = dist;
            }
        }
        if (bestdist < 5f) {return closest;}
        else {return null;}
    }

    public void keyDown() {
        GameObject closest = getClosest();
        if (closest == null) return;
        else {
            Class objType = closest.getClass();
            if (objType == FurnitureModel.class) { grabController.grab((FurnitureModel) closest); }
            else if (objType == FoodModel.class) {eatController.eat((FoodModel)closest);}
            // else if (objType == DoorModel.class) {hingeController.toggle(); }
        }
    }
}
