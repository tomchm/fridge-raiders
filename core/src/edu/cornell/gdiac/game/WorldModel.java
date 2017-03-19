package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;
import box2dLight.*;
import com.badlogic.gdx.graphics.*;


import java.util.Arrays;
import java.util.Collections;

/**
 * Created by tomchm on 3/9/17.
 */
public class WorldModel {

    protected World world;
    protected Vector2 scale;
    protected Vector2 bounds;
    protected int width;
    protected int height;
    protected PooledList<GameObject> addGameObjectQueue;
    protected PooledList<GameObject> solidGameObjects;
    protected PooledList<GameObject> gameObjects;
    protected PooledList<AIModel> aiList;
    protected PooledList<JointDef> jointQueue;
    protected PooledList<Body> staticQueue;
    protected PooledList<Body> dynamicQueue;
    protected int[] sensors;
    protected boolean clearJoints;
    protected boolean addJoints;
    protected DetectiveModel detective;

    // Box2D lights
    /** The camera defining the RayHandler view; scale is in physics coordinates */
    protected OrthographicCamera raycamera;
    /** The rayhandler for storing lights, and drawing them (SIGH) */
    protected RayHandler rayhandler;
    /** Active lights*/
    private Array<Light> lights = new Array<Light>();

    /**
     * @return a list of all AI models currently populating the level
     */
    public PooledList<AIModel> getAIList() {return aiList;}

    /** Adds ai to list of AIModels populating the level. Initializes sensors
     * if the first ai
     *
     * @param ai a new ai model to add to list of ai models in the current level
     */
    public void addAI(AIModel ai){
        aiList.add(ai);
    }

    /**
     * clears aiList
     */
    public void clearAIList() {aiList.clear();}


    public WorldModel(float sx, float sy){
        world = new World(new Vector2(), false);
        scale = new Vector2(sx,sy);
        bounds = new Vector2(Gdx.graphics.getWidth()/scale.x, Gdx.graphics.getHeight()/scale.y);
        addGameObjectQueue = new PooledList <GameObject>();
        gameObjects = new PooledList <GameObject>();
        jointQueue = new PooledList<JointDef>();
        staticQueue = new PooledList<Body>();
        dynamicQueue = new PooledList<Body>();
        clearJoints = false;
        addJoints = false;

        // ai info
        aiList = new PooledList <AIModel>();
        solidGameObjects = new PooledList <GameObject>();
        initLighting();

        // sensor info
        height = 60;
        width = 60;
        sensors = new int[height*width];
    }

    /**
     * Updates all sensors with static objects on them
     */
    public void updateSensors(){
        for(int i = 0; i < width ;i ++){
            for(int j = 0; j < height; j++){
                // CHANGE magic number
                if (isAccessibleWithRadius(i,j,1.2f)) {
                    sensors[i*width + j] = 0;
                }
                else {
                    sensors[i*width + j] = 1;
                }
            }
        }
    }

    /**
     * Creates the ambient lighting for the level
     *
     * This is the amount of lighting that the level has without any light sources.
     */
    private void initLighting() {
        raycamera = new OrthographicCamera(bounds.x,bounds.y);
        raycamera.position.set(bounds.x/2.0f, bounds.y/2.0f, 0);
        raycamera.update();

        rayhandler = new RayHandler(world, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
        rayhandler.setCombinedMatrix(raycamera);
        rayhandler.setAmbientLight(0,0,0,0.8f);
    }

    public DetectiveModel getPlayer() { return detective; }
    public void setPlayer(DetectiveModel dm) {detective=dm;}

    /*Adds new light source to all lights in level*/
    public void addLight(Light light) {
        lights.add(light);
    }

    public void addGameObjectQueue(GameObject gameObject){
        assert gameObject != null : "Tried to add null GameObject";
        addGameObjectQueue.add(gameObject);
    }

    public void addGameObject(GameObject gameObject){
        assert gameObject != null : "Tried to add null GameObject";
        gameObjects.add(gameObject);
        gameObject.activate(world);
        if (gameObject.getClass() == FurnitureModel.class || gameObject.getClass() == WallModel.class || gameObject.getClass() == DoorModel.class) {
            solidGameObjects.add(gameObject);
        }
    }

    public void addGameObjects(){
        while(!addGameObjectQueue.isEmpty()){
            GameObject go = addGameObjectQueue.pop();
            addGameObject(go);
        }
    }

    public void removeGameObject(GameObject gameObject) {
        gameObject.deactivate(world);
        gameObjects.remove(gameObject);
    }

    public void addJoint(JointDef jointDef){
        assert jointDef != null : "Tried to add null JointDef";
        jointQueue.add(jointDef);
        addJoints = true;
    }

    public void addJoints(){
        while(!jointQueue.isEmpty()){
            JointDef jd = jointQueue.pop();
            world.createJoint(jd);
        }
    }

    public void clearJoints(){
        clearJoints = true;
    }

    public void updateJoints(){
        if (addJoints) {
            addJoints();
            addJoints = false;
        }
        if(clearJoints){
            Array<Joint> joints = new Array<Joint>();
            world.getJoints(joints);
            for (Joint j : joints){
                world.destroyJoint(j);
            }
            clearJoints = false;
        }
    }

    public void setStatic(Body body){
        assert body != null : "Tried to set null Body to static";
        staticQueue.add(body);
    }

    public void applyStatic(){
        while(!staticQueue.isEmpty()){
            Body body = staticQueue.pop();
            body.setType(BodyDef.BodyType.StaticBody);
        }
    }

    public void setDynamic(Body body){
        assert body != null : "Tried to set null Body to dynamic";
        dynamicQueue.add(body);
    }

    public void applyDynamic(){
        while(!dynamicQueue.isEmpty()){
            Body body = dynamicQueue.pop();
            body.setType(BodyDef.BodyType.DynamicBody);
        }
    }

    public void clearGameObjects(){
        while(!gameObjects.isEmpty()){
            GameObject gm = gameObjects.pop();
            gm.deactivate(world);
        }
    }

    private void drawGameObjects(GameCanvas canvas){
        Collections.sort(gameObjects);
        for(GameObject gm : gameObjects){
            gm.draw(canvas);
        }
    }

    public void updateGameObjects(float dt){
        for(GameObject gm : gameObjects){
            gm.update(dt);
        }
    }

    private void drawAIModels(GameCanvas canvas) {
        Collections.sort(aiList);
        for(GameObject ai : aiList){
            ai.draw(canvas);
        }
    }

    public void drawDebugGameObjects(GameCanvas canvas){
        for(GameObject gm : gameObjects){
            gm.drawDebug(canvas);
        }
    }

    public void draw(GameCanvas canvas){
        // draw objects
        canvas.begin();
        drawGameObjects(canvas);
        canvas.end();

        updateRayCamera();

        // draw lights
        if (rayhandler != null) {
            rayhandler.render();
        }

        // draw AI again to cover light point source
        //canvas.begin();
        //drawAIModels(canvas);
        //canvas.end();

    }

    /**
     * updates camera used for drawing light
     */
    public void updateRayCamera() {
        raycamera.position.set(detective.getBody().getPosition(), 0);
        raycamera.update();
        rayhandler.setCombinedMatrix(raycamera);
        rayhandler.update();

    }

    public World getWorld() {
        return world;
    }

    public PooledList<GameObject> getAddGameObjectQueue() {
        return addGameObjectQueue;
    }

    public PooledList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public PooledList<JointDef> getJointQueue() {
        return jointQueue;
    }

    public PooledList<Body> getStaticQueue() {
        return staticQueue;
    }

    public PooledList<Body> getDynamicQueue() {
        return dynamicQueue;
    }

    public Vector2 getScale(){
        return scale;
    }

    /** returns true sensor at position x y is overlapping with some object */
    public boolean isAccessibleByAI(int x, int y) {
        if (x < 0 || y < 0 || x > width - 1 || y > width -1) {
            return false;
        }
        return sensors[x*width + y] <= 0;
    }

    /** returns true if any of 8 cardinal points radius away from x y
     *  are not in some solid object
     */
    public boolean isAccessibleWithRadius(float x, float y, float radius) {
        for(GameObject obj: solidGameObjects) {
            for(double i = 0; i < 2*Math.PI; i = i + Math.PI/4){
                float tempx = (float)Math.cos(i)*radius + x;
                float tempy = (float)Math.sin(i)*radius + y;
                if (obj.getBody().getFixtureList().get(0).testPoint(tempx,tempy)) {
                    return false;
                }
            }
        }
        return true;
    }
}
