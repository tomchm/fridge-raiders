package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.game.model.AIModel;
import edu.cornell.gdiac.game.model.DetectiveModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by vanyaivan on 3/15/2017.
 */
public class AIController {
    /**
     * Enumeration to encode the finite state machine.
     */
    private static enum FSMState {
        /** The ai just spawned */
        SPAWN,
        /** The ai is patrolling around given their path */
        PATHING,
        /** The ai has a target destination, but must get closer */
        CHASE
    }

    // Constants for chase algorithms


    // Instance Attributes
    /** The ai being controlled by this AIController */
    private AIModel ai;
    /** The world; used for pathfinding */
    private WorldModel worldModel;
    /** The player */
    private DetectiveModel player;
    /** The ai's current state*/
    private FSMState state;
    /** The target location */
    private Vector2 target;
    /** The nextStep location */
    private Vector2 next;
    /** The number of ticks since we started this controller */
    private long ticks;

    // Path attributes
    /** The ai's currrent index in the path*/
    private int pathIndex;
    /** The direction the ai is traversing the path*/
    private int direction;

    /**
     * Creates an AIController for the given ai.
     *
     * @param path the path of this ai
     * @param worldModel the worldModel
     */
    public AIController(Vector2[] path, WorldModel worldModel) {
        //physics
        this.ai = new AIModel(path);
        this.worldModel = worldModel;
        this.player = worldModel.getPlayer();
        worldModel.addGameObject(ai);
        worldModel.addAI(ai);

        //lights
        ai.createConeLight(worldModel.rayhandler);
        worldModel.addLight(ai.getConeLight());

        //states
        state = FSMState.SPAWN;
        ticks = 0;

        // Select an initial target
        target = null;
        next = new Vector2(ai.getBody().getPosition());
        pathIndex = 0;
        direction = 1;
    }

    /**
     * updates AI
     */
    public void update(float dt) {
        setNextAction(dt);
        ai.updateAngle();
        ai.updateConeLight();
        worldModel.rayhandler.update();

    }

    /** Given the position of the next step, calculates appropriate velocity
     *
     * @param dt
     * @return a velocity vector representing the AI's next move
     */
    private Vector2 getVelocityToNext(float dt) {
        Vector2 result = new Vector2(next);
        result.sub(ai.getBody().getPosition());
        if (result.dst(0,0) <= dt * ai.getSpeed()) {
            return result.scl(1.0f/dt);
        }
        float total = Math.abs(result.x) + Math.abs(result.y);
        float x_scaled = result.x / total;
        float y_scaled = result.y / total;

        result.x = ai.getSpeed() * x_scaled;
        result.y = ai.getSpeed() * y_scaled;
        return (result);
    }

    /**
     * Sets the aimodel parameters appropriately for AI to do next action
     */
    private void setNextAction(float dt) {
        // Increment the number of ticks.
        ticks++;

        // Do not need to rework ourselves every frame. Just every 10 ticks.
        if (ticks % 1 == 0) {
            // Process the FSM
            changeStateIfApplicable();

            // Pathfinding
            next = selectNextStep();
        }
        Vector2 aiVel = getVelocityToNext(dt);
        ai.getBody().setLinearVelocity(aiVel);
    }

    /**
     * Change the state of the ai;
     */
    private void changeStateIfApplicable() {
        Random rand_gen = new Random();

        // Next state depends on current state.
        switch (state) {
            // checks is there exists a path, switches to PATHING
            case SPAWN:
                if (ai.getPath() == null || ai.getPath().length == 0){
                    Gdx.app.error("AIController", "Invalid path", new IllegalStateException());
                }
                state = FSMState.PATHING;
                break;

            // checks if player is in sight, switches to CHASE
            // checks if pathing is not possible, switches to WANDER
            case PATHING:
                break;

            case CHASE:
                break;

            default:
                // Unknown or unhandled state, should never get here
                assert (false);
                state = FSMState.PATHING;
                break;
        }
    }

    /**
     * Sets target to the position of next step in ai path
     */
    private void selectPathTarget(){
        Vector2[] path = ai.getPath();
        if (ai.getBody().getPosition().equals(path[pathIndex])) {

            if (pathIndex == path.length - 1) direction = -1;
            else if (pathIndex == 0) direction = 1;

            pathIndex = pathIndex + direction;
        }
        target = path[pathIndex];
    }

    /**
     * Sets target to the position of the player
     */
    private void selectChaseTarget() {
        target = player.getBody().getPosition();
    }

    /**
     * Returns next location where ai should travel to
     */
    private Vector2 selectNextStep() {
        Vector2 nextStep;
        switch (state) {
            // do nothing
            case SPAWN:
                nextStep = new Vector2(ai.getBody().getPosition());
                break;

            // sets next to
            case PATHING:
                selectPathTarget();
                nextStep = getStepToTarget();
                break;

            case CHASE:
                selectChaseTarget();
                nextStep = getStepToTarget();
                break;

            default:
                // Unknown or unhandled state, should never get here
                assert (false);
                nextStep = new Vector2(0,0);
        }
        return nextStep;
    }

    /**
     * Uses breadth first search to find first step in valid path leading to target
     *
     * @return a valid position which is to first step in a path reaching the target
     */
    private Vector2 getStepToTarget() {
        //#region PUT YOUR CODE HERE
        Queue<searchPairs> queue = new LinkedList<searchPairs>();

        Vector2 temp = ai.getBody().getPosition();
        Vector2 aiPos = new Vector2((float)Math.floor(temp.x), (float)Math.floor(temp.y));
        Vector2 targetPos = new Vector2((float)Math.floor(target.x), (float)Math.floor(target.y));

        queue.add(new searchPairs(aiPos));
        Array<Vector2> visited = new Array <Vector2>();

        while (!queue.isEmpty()){
            searchPairs loc = queue.poll();
            float x = loc.position.x;
            float y = loc.position.y;
            if (visited.contains(loc.position, false)) {
                continue;
            }
            if (loc.position.equals(targetPos)) {
                return tracePathGetAction(loc);
            }
            visited.add(loc.position);
            for (int i = -1; i < 2; i = i +2) {
                if (worldModel.isAccessibleWithRadius(x+i, y, ai.getRadius()) && !visited.contains(new Vector2(x+i,y), false)) {
                    queue.add(new searchPairs(x + i, y, loc));
                }
                if (worldModel.isAccessibleWithRadius(x, y+i, ai.getRadius()) && !visited.contains(new Vector2(x,y+i), false)) {
                    queue.add(new searchPairs(x, y + i, loc));
                }
            }
        }

        return aiPos;
    }

    /**
     * Returns the first move made in the path to the pair result
     *
     * The value returned should be a control code.  See PlayerController
     * for more information on how to use control codes.
     *
     * @return a movement direction that moves towards the result coordinate pair.
     */
    private Vector2 tracePathGetAction (searchPairs result) {
        if (result.prev == null) return result.position;

        while (result.prev.prev != null) {
            result = result.prev;
        }

        return result.position;
    }

    /**
     * A class to store int pairs and create a linked list
     */
    private class searchPairs{
        public Vector2 position;
        public searchPairs prev;

        public searchPairs (Vector2 pos) {
            this(pos, null);
        }

        public searchPairs (Vector2 position, searchPairs prev) {
            this.position = position;
            this.prev = prev;
        }

        public searchPairs(float x, float y, searchPairs prev) {
            this.position = new Vector2(x,y);
            this.prev = prev;
        }
    }


}
