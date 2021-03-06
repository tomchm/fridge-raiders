package edu.cornell.gdiac.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.sun.deploy.util.ArrayUtil;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;

import java.util.Arrays;

/**
 * Created by Sal on 3/12/2017.
 */

/** Used for all button-interactions. At the moment, grabbing/releasing furniture,
 *  opening/shutting doors, and eating food. */
public class SpacebarController implements RayCastCallback {
    private static final float MAX_DIST = 2f;

    private HingeController hingeController;
    private EatController eatController;
    private GrabController grabController;
    private WorldModel worldModel;

    /** Will shoot out 8 rays to look for objects the player can grab.
    * Put those 8 physics bodies in this array before checking if
    * any is an interactible type */
    private GameObject[] interactionCandidates;
    private Vector2[] candidateIntersections;
    private int k = 0; // to loop over interactionCandidates

    public SpacebarController(WorldModel wm) {
        hingeController = new HingeController();
        grabController = new GrabController(wm);
        eatController = new EatController(wm);
        worldModel = wm;
        interactionCandidates = new GameObject[8];
        candidateIntersections = new Vector2[8];
    }

    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        GameObject go = (GameObject) fixture.getUserData();
        if (go instanceof FurnitureModel || go instanceof DoorModel || go instanceof FoodModel) {
            interactionCandidates[k] = go;
            candidateIntersections[k] = point.cpy();
            return 0;
        }
        return 1;
    }

    /** Find an object close enough to the player to interact with.
     *  null if no such object. */
    public GameObject getInteractible() {
        float bestdist2 = Float.POSITIVE_INFINITY;
        GameObject closest = null;
        DetectiveModel player = worldModel.getPlayer();
        Vector2 playerPos = player.getBody().getPosition();
        DetectiveModel.Animation playerDir = player.getAnimation();
        int startInd =0;
        int endInd=8;
        int[] dirArr = new int []{0,1,2,3,4,5,6,7};
        if(playerDir == DetectiveModel.Animation.LEFT_MOVE || playerDir == DetectiveModel.Animation.LEFT_STOP || playerDir == DetectiveModel.Animation.LEFT_GRAB){
            dirArr = new int[]{3, 4, 5};
        }
        else if(playerDir == DetectiveModel.Animation.DOWN_MOVE || playerDir == DetectiveModel.Animation.DOWN_STOP || playerDir == DetectiveModel.Animation.DOWN_GRAB){
            dirArr = new int[]{5, 6, 7};
        }
        else if(playerDir == DetectiveModel.Animation.RIGHT_MOVE || playerDir == DetectiveModel.Animation.RIGHT_STOP || playerDir == DetectiveModel.Animation.RIGHT_GRAB){
            dirArr = new int[]{1, 0,7};
        }
        else if(playerDir == DetectiveModel.Animation.UP_MOVE || playerDir == DetectiveModel.Animation.UP_STOP || playerDir == DetectiveModel.Animation.UP_GRAB){
            dirArr = new int[]{1, 2, 3};
        }
        // look for closest physics body in 8 cardinal directions
        for (k=0; k < 8; ++k) {
            interactionCandidates[k] = null;
            boolean contained = false;
            for(int mn = 0; mn < dirArr.length ; mn++){
                if (dirArr[mn] == k){
                    contained = true;
                }
            }
            if(contained) {
                worldModel.getWorld().rayCast(
                        this,
                        playerPos.x,
                        playerPos.y,
                        playerPos.x + 2f * (float) Math.cos(k * Math.PI / 4),
                        playerPos.y + 2f * (float) Math.sin(k * Math.PI / 4)
                ); // an object, if hit, will get placed into interactionCandidates[k]
            }
        }

        // now check if any is the body of an interactible gameobject type
        for (k = 0; k < 8; ++k) {
            GameObject gob = interactionCandidates[k];
            if (gob == null) continue;
            // otherwise we have found an interactible gameobject!
            // close enough, and a proper class!
            float dist2 = new Vector2(player.getBody().getPosition()).sub(candidateIntersections[k]).len2();
            if (dist2 < bestdist2 && dist2 < MAX_DIST*MAX_DIST) {
                bestdist2 = dist2;
                closest = gob;
            }
        }

        // Issue: raycasting ignores shapes containing the start point of the raycast.
        // this will include food that we're standing on.
        // So we need to do another loop over all the GameObjects to check simple distance
        // and see if there's anything even closer.
        PooledList<GameObject> gobjs = worldModel.getGameObjects();
        for (GameObject gob : gobjs) {
            if (gob == player) continue;
            if (gob.getClass() != FoodModel.class
                    && gob.getClass() != FurnitureModel.class
                    && gob.getClass() != DoorModel.class) continue;
            float dist2 = new Vector2(player.getBody().getPosition()).sub(gob.getBody().getPosition()).len2();
            if (dist2 < bestdist2 && dist2 < MAX_DIST*MAX_DIST) {
                bestdist2 = dist2;
                closest = gob;
            }
        }
        return closest;
    }

    /** Called once per secondary action button keydown. */
    public void keyDown() {
        GameObject closest = getInteractible();
        if (closest == null) return;
        else {
            Class objType = closest.getClass();
            if (objType == FurnitureModel.class) {
                worldModel.tutGrabPlayer();
                grabController.grab((FurnitureModel) closest);
            }
            else if (objType == FoodModel.class) {
                worldModel.tutEatPlayer();
                eatController.eat((FoodModel)closest);
            }
             else if (objType == DoorModel.class) {hingeController.toggle((DoorModel) closest); }
        }
    }

    /** Called once per secondary action button key release. */
    public void keyUp() {
        grabController.release();
        eatController.stop();
    }
}
