package edu.cornell.gdiac.game;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.game.model.*;


import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by vanyaivan on 3/15/2017.
 */
public class AIController implements RayCastCallback{
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
    /** store distance from raycast collision*/
    private float distCache;

    // Path attributes
    /** The ai's currrent index in the path*/
    private int pathIndex;
    /** The direction the ai is traversing the path*/
    private int direction;
    /** The previous position of the Ai*/
    private Vector2 prevPos;
    /** If the AI is stuck*/
    private boolean isStuck;
    /** Amount of time in seconds for user to be stuck */
    protected static float STUCK_TIME = 0.5f;
    /** How much longer (by number of occurances) should the pushing phase be to the checking if stuck phase*/
    protected static float STUCK_TIME_MULT = 5.0f;

    // Chase attributes
    /** the weight to apply to light time*/
    protected static float DIST_SCL = 6.0f;
    /** the limit of light time*/
    protected static float LIGHT_LIM = 5.0f;
    /** the threshold before chase*/
    protected static float CHASE_LIM = 1.0f;
    /** the distance at which the ai catches the player*/
    protected static float CATCH_DIST = 2.0f;
    /** the weighted time the player has been in the light*/
    private float lightTime;
    /** caches dt value for callback*/
    private float dtCache;
    /** true if player has been seen*/
    private boolean seen;
    /** true if player is blocked from sight by object*/
    private boolean blocked;

    /** true is converted to second stage */
    private boolean isSecondStage;
    /** true if AI was run over */
    private boolean isDead;

    public float tempCount = 0;

    /**
     * Creates an AIController for the given ai.
     *
     * @param ai the ai model this controller is controlling
     * @param worldModel the worldModel
     */
    public AIController(AIModel ai, WorldModel worldModel) {
        //physics
        this.ai = ai;
        this.worldModel = worldModel;
        this.player = worldModel.getPlayer();

        //lights
//        DEBUG
//        for(Vector2 step: ai.getPath()) {
//            new PointLight(worldModel.rayhandler, 10, Color.GREEN, 1, step.x, step.y );
//        }
        ai.createConeLight(worldModel.rayhandler);
        lightTime = 0;
        seen = false;
        blocked = false;

        //states
        state = FSMState.SPAWN;
        ticks = 0;

        // Select an initial target
        target = null;
        next = new Vector2(ai.getBody().getPosition());
        pathIndex = 0;
        direction = 1;
        prevPos = new Vector2(ai.getBody().getPosition());
        isStuck = false;
    }


    public AIModel getModel(){
        return this.ai;
    }

    public void reset() {
        isDead = false;
        ai.isDead = false;
    }
    /**
     * updates AI
     */
    public void update(float dt) {
        if (!this.worldModel.isPaused) {
            dtCache = dt;
            ticks++;
//        System.out.println(target);
//        System.out.println(next);
//        System.out.println(isStuck);

            // check if AI is stuck
            double interval = Math.round(STUCK_TIME * 60);
            if (ticks % interval == 0) {
                if (prevPos.dst2(ai.getBody().getPosition()) < 0.001) {
                    System.out.println("STUCK");
                    isStuck = true;
                } else {
                    if (ticks % (interval * STUCK_TIME_MULT) == 0) {
                        isStuck = false;
                    }
                }
                prevPos = new Vector2(ai.getBody().getPosition());
            }

            // calculate all attributes
            checkSight();
            setNextAction(dt);
            updateLightColor();

            // update ai model
            if (state == FSMState.CHASE) {
                ai.updateAngle(new Vector2(target).sub(ai.getBody().getPosition()));
            } else {
                ai.updateAngle(ai.getBody().getLinearVelocity());
            }
            ai.updateConeLight();

            // update light handler
            worldModel.rayhandler.update();
        }
    }

    /**
     * Checks and updates lightTime appropriately given a player is standing in the light
     */
    private void checkSight() {
        // Callback to figure out when ai can see player
        Vector2 aiPos = ai.getBody().getPosition();
        Vector2 playerPos = player.getBody().getPosition();
        worldModel.world.rayCast(this, aiPos.x, aiPos.y, playerPos.x, playerPos.y);

        //Callback to figure out when ai can see moving furniture
        FurnitureModel grappledFurniture =  player.getGrappledFurniture();
        if (grappledFurniture != null) {
            Vector2 furniturePos = grappledFurniture.getBody().getPosition();
            worldModel.world.rayCast(this, aiPos.x, aiPos.y, furniturePos.x, furniturePos.y);
        }
        updateLightTime();
        if (ai.getBody().getPosition().dst(player.getBody().getPosition()) - ai.getRadius() - player.getRadius() < CATCH_DIST && !blocked) {
            lightTime = (CHASE_LIM > lightTime) ? CHASE_LIM : lightTime;
        }
        seen = false;
        blocked = false;
    }

    /**
     * Updates the lightTime value based on the rayCastCount
     */
    private void updateLightTime() {
        if (seen && (!blocked)) {
            lightTime = lightTime + dtCache * ((2-distCache/ai.getLightRadius()) * DIST_SCL);
            lightTime = (lightTime > LIGHT_LIM) ? LIGHT_LIM : lightTime;
        }
        else {
            lightTime = lightTime - dtCache/2;
            lightTime = (lightTime < 0) ? 0 : lightTime;
        }
    }


    /**
     *A callback upon hitting fixture with a raycast. It figures out if player is in sight of ai and updates
     * lighttime appropriately
     */
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        Vector2 temp = new Vector2(point);
        temp.sub(ai.getBody().getPosition());

        float angle = (float) Math.atan2(temp.y, temp.x);
        double angledif = (ai.getBody().getAngle() - angle + Math.PI * 2) % (Math.PI * 2);
        double angledifDeg = angledif * 180 / Math.PI;
        float dist = ai.getBody().getPosition().dst(point);
        boolean fixtureTest = (fixture.getBody() == player.getBody() || fixture.getUserData() == player.getGrappledFurniture());

        if ( fixtureTest && (angledifDeg < (ai.getLightAngle()) || angledifDeg > (360 - ai.getLightAngle())) && dist < ai.getLightRadius()) {
            seen = true;
            distCache = dist;
        }
        else if (!fixtureTest && fixture.getUserData().getClass() != CrumbModel.class && fixture.getUserData().getClass() != FoodModel.class){
            blocked = true;
        }
        return -1;
    }

    /**
     * Updates the color of AI Light based off lightTime
     */
    private void updateLightColor() {
        float lightscl = lightTime/LIGHT_LIM;
        ai.getConeLight().setColor(1, 0.8f-lightscl,0.8f-lightscl, 1);
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
            case PATHING:
                if (lightTime >= CHASE_LIM){
                    ai.setSpeed(ai.getSpeed()*ai.getSpeedUpScale());
                    state = FSMState.CHASE;
                }
                break;

            case CHASE:
                if (lightTime  == 0){
                    ai.setSpeed(ai.getSpeed()/ai.getSpeedUpScale());
                    state = FSMState.PATHING;
                }
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
        Vector2 temp = new Vector2(ai.getBody().getPosition());
        temp.sub(path[pathIndex]);
        if (temp.dst2(0,0) < 0.01) {
//            if (pathIndex == path.length - 1) direction = -1;
//            else if (pathIndex == 0) direction = 1;
            if (pathIndex == path.length-1) pathIndex = -1;

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
        Queue<searchPairs> queue = new LinkedList<searchPairs>();

        Vector2 temp = ai.getBody().getPosition();
        Vector2 aiPos = new Vector2((float)Math.round(temp.x), (float)Math.round(temp.y));
        Vector2 targetPos = getNearestValidLoc(target.x, target.y);

        queue.add(new searchPairs(aiPos));
        ObjectSet<Vector2> visited = new ObjectSet<Vector2>();

        while (!queue.isEmpty()){
            searchPairs loc = queue.poll();
            float x = loc.position.x;
            float y = loc.position.y;
            if (loc.position.equals(targetPos)) {
                return tracePathGetAction(loc);
            }
            if (visited.contains(loc.position)) {
                continue;
            }
            visited.add(loc.position);
            for (int i = -1; i < 2; i = i +2) {
                if (worldModel.isAccessibleByAI((int)x+i, (int)y, isStuck) && !visited.contains(new Vector2(x+i,y))) {
                    queue.add(new searchPairs(x + i, y, loc));
                }
                if (worldModel.isAccessibleByAI((int)x, (int)y+i, isStuck) && !visited.contains(new Vector2(x,y+i))) {
                    queue.add(new searchPairs(x, y + i, loc));
                }
            }
        }

        return aiPos;
    }

    /** Returns the nearest valid position for ai
     *
     * @return Vector2 which represents the point x,y which is a valid location
     */
    private Vector2 getNearestValidLoc(float x, float y) {
        Queue<Vector2> queue = new LinkedList<Vector2>();

        Vector2 targetPos = new Vector2((float)Math.round(x), (float)Math.round(y));

        queue.add(targetPos);
        ObjectSet<Vector2> visited = new ObjectSet<Vector2>();

        while (!queue.isEmpty()){
            Vector2 loc = queue.poll();
            x = loc.x;
            y = loc.y;
            if (worldModel.isAccessibleByAI((int)loc.x, (int)loc.y, isStuck)) {
                return loc;
            }
            if (visited.contains(loc)) {
                continue;
            }
            visited.add(loc);
            for (int i = -1; i < 2; i = i +2) {
                if (!visited.contains(new Vector2(x+i,y))) {
                    queue.add(new Vector2(x+i, y));
                }
                if (!visited.contains(new Vector2(x,y+i))) {
                    queue.add(new Vector2(x, y+i));
                }
            }
        }
        return null;
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

    /*
     * Returns whether player has been caught by this AI
     */
    public boolean hasBeenCaught(){
        return lightTime >= LIGHT_LIM;
    }

    /*
     * Updates the AI for the second stage
     */
    public void update2(float dt){
        if(!isSecondStage){
            ai.getBody().getFixtureList().first().setSensor(true);
            ai.getConeLight().setActive(false);
            worldModel.setStatic(ai.getBody());
            ai.setSpeed(0);
            isSecondStage = true;
            ai.isSecondStage = true;
        }
        Vector2 playerPos = player.getBody().getPosition().cpy();
        Vector2 myPos = ai.getBody().getPosition().cpy();
        Vector2 posDif = playerPos.cpy().sub(myPos);
        if(!isDead && playerPos.dst(myPos) < ai.getRadius() + worldModel.getPlayer().getRadius()){
            System.out.println("HERE");
            ai.deadAngle = Math.atan2(posDif.y, posDif.x);
            ai.isDead = true;
            isDead = true;
        }
//        if(tempCount > 200) {
//            isDead = false;
//            ai.isDead = false;
//            tempCount =0;
//        }
//        tempCount ++;



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
