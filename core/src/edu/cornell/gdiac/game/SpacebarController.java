package edu.cornell.gdiac.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;

/**
 * Created by Sal on 3/12/2017.
 */

/** Used for all button-interactions. At the moment, grabbing/releasing furniture,
 *  opening/shutting doors, and eating food. */
public class SpacebarController implements RayCastCallback {
    private HingeController hingeController;
    private EatController eatController;
    private GrabController grabController;
    private WorldModel worldModel;

    /** Will shoot out 8 rays to look for objects the player can grab.
    * Put those 8 physics bodies in this array before checking if
    * any is an interactible type */
    private Body[] interactionCandidates;
    private Vector2[] candidateIntersections;
    private int k = 0; // to loop over interactionCandidates

    public SpacebarController(WorldModel wm) {
        hingeController = new HingeController();
        grabController = new GrabController(wm);
        eatController = new EatController();
        worldModel = wm;
        interactionCandidates = new Body[8];
        candidateIntersections = new Vector2[8];
    }

    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        interactionCandidates[k] = fixture.getBody();
        candidateIntersections[k] = point.cpy();
        return 0f;
    }

    /** Find an object close enough to the player to interact with.
     *  null if no such object. */
    private GameObject getInteractible() {
        float bestdist2 = Float.POSITIVE_INFINITY;
        GameObject closest = null;
        DetectiveModel player = worldModel.getPlayer();
        Vector2 playerPos = player.getBody().getPosition();

        // look for closest physics body in 8 cardinal directions
        for (k=0; k < 8; ++k) {
            interactionCandidates[k] = null;
            worldModel.getWorld().rayCast(
                    this,
                    playerPos.x,
                    playerPos.y,
                    playerPos.x + 2f * (float)Math.cos(k*Math.PI/4),
                    playerPos.y + 2f * (float)Math.sin(k*Math.PI/4)
            ); // an object, if hit, will get placed into interactionCandidates[k]
        }

        // now check if any is the body of an interactible gameobject type
        for (k = 0; k < 8; ++k) {
            GameObject gob = getInteractibleGameObject(interactionCandidates[k]);
            if (gob == null) continue;
            // otherwise we have found an interactible gameobject!
            // close enough, and a proper class!
            float dist2 = player.getBody().getPosition().sub(candidateIntersections[k]).len2();
            if (dist2 < bestdist2) {
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
            float dist2 = player.getBody().getPosition().sub(gob.getBody().getPosition()).len2();
            if (dist2 < bestdist2) {
                bestdist2 = dist2;
                closest = gob;
            }
        }

        return closest;
    }

    /** Get the GameObject corresponding to the Body. Only return a GameObject
     * if it is also an interactible type, ie FoodModel, FurnitureModel, or DoorModel.
     * null otherwise. */
    public GameObject getInteractibleGameObject(Body b) {
        PooledList<GameObject> gobjs = worldModel.getGameObjects();
        if (b == null) return null;
        for (GameObject gob : gobjs) {
            if (gob.getBody() == b) {
                if (gob.getClass() == FoodModel.class
                        || gob.getClass() == FurnitureModel.class
                        || gob.getClass() == DoorModel.class) return gob;
            }
        }
        return null;
    }

    /** Called once per secondary action button keydown. */
    public void keyDown() {
        GameObject closest = getInteractible();
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
