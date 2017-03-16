package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;

/**
 * Created by Sal on 3/12/2017.
 */

/** Used for all button-interactions. At the moment, grabbing/releasing furniture,
 *  opening/shutting doors, and eating food. */
public class SpacebarController {
    private HingeController hingeController;
    private EatController eatController;
    private GrabController grabController;
    private WorldModel worldModel;

    public SpacebarController(WorldModel wm) {
        hingeController = new HingeController();
        grabController = new GrabController(wm);
        eatController = new EatController();
        worldModel = wm;
    }

    /** Closest object to the player which is both an interactible type AND close
     * enough to interact with. Another name for this method could have been
     * "getHighlightable." */
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
        if (bestdist < 7f) {return closest;}
        else {return null;}
    }

    /** Called once per secondary action button keydown. */
    public void keyDown() {
        GameObject closest = getClosest();
        if (closest == null) return;
        else {
            Class objType = closest.getClass();
            if (objType == FurnitureModel.class) {
                grabController.grab((FurnitureModel) closest);
            }
            else if (objType == FoodModel.class) {
                eatController.eat((FoodModel)closest);
            }
            // else if (objType == DoorModel.class) {hingeController.toggle(); }
        }
    }

    /** Called once per secondary action button key release. */
    public void keyUp() {
        grabController.release();
        eatController.stop();
    }
}
