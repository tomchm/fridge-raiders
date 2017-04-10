package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import edu.cornell.gdiac.game.asset.AssetLoader;
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
    protected PooledList<GameObject> removeGameObjectQueue;

    protected PooledList<GameObject> gameObjects;
    protected PooledList<GameObject> solidGameObjects;

    protected PooledList<AIModel> aiList;
    protected PooledList<FurnitureModel> furnitureList;
    protected PooledList<FoodModel> foodList;
    protected PooledList<WallModel> wallList;

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

    /**
     * clears aiList
     */
    public void clearAIList() {aiList.clear();}


    public WorldModel(float sx, float sy){
        world = new World(new Vector2(), false);
        scale = new Vector2(sx,sy);
        bounds = new Vector2(Gdx.graphics.getWidth()/scale.x, Gdx.graphics.getHeight()/scale.y);
        addGameObjectQueue = new PooledList <GameObject>();
        removeGameObjectQueue = new PooledList <GameObject>();
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
        height = -1;
        width = -1;
        initContactListener();
    }

    public void setPar(int par) {}
    public void setThreshold(int threshold) {}

    /**
     * Creates new contact listener to allow for dynamic interactions between ai and furniture
     */
    private void initContactListener(){
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                GameObject objectA = (GameObject)contact.getFixtureA().getUserData();
                GameObject objectB = (GameObject)contact.getFixtureB().getUserData();
                boolean temp1 = objectA.getClass() == AIModel.class && objectB.getClass() == FurnitureModel.class;
                boolean temp2 = objectB.getClass() == AIModel.class && objectA.getClass() == FurnitureModel.class;
                if(temp1 || temp2){
                    GameObject furniture = ((temp1) ? objectB : objectA);
                    turnOnOffObjSensors(furniture, 0);
                    setDynamic(furniture.getBody());
                }
            }

            @Override
            public void endContact(Contact contact) {
                GameObject objectA = (GameObject)contact.getFixtureA().getUserData();
                GameObject objectB = (GameObject)contact.getFixtureB().getUserData();
                boolean temp1 = objectA.getClass() == AIModel.class && objectB.getClass() == FurnitureModel.class;
                boolean temp2 = objectB.getClass() == AIModel.class && objectA.getClass() == FurnitureModel.class;
                if(temp1 || temp2){
                    GameObject furniture = ((temp1) ? objectB : objectA);
                    turnOnOffObjSensors(furniture, 1);
                    setStatic(furniture.getBody());
                }
            }

            @Override
            public void postSolve(Contact arg0, ContactImpulse arg1) {}

            @Override
            public void preSolve(Contact arg0, Manifold arg1) {}
        });
    }

    /**
     * Finds the total dimensions of the level (the height and width) and sets the appropriate
     * attributes
     */
    private void findAndSetDimensions(){
        float max = Float.MIN_VALUE;
        float f;
        float [] coords;
        for(GameObject g : gameObjects) {
            if (g.getClass() == WallModel.class) {
                coords =  ((WallModel)g).getCoords();
                for(int i = 0; i < coords.length; i++) {
                    f = coords[i];
                    if (f > max) max  = f;
                }
            }
        }
        width = (int)max;
        height = (int)max;
        sensors = new int[height*width];
    }


    /**
     * Updates all sensors with static objects on them over the course of several ticks
     */
    public void updateAllSensors(){
        if(height == -1) {
            findAndSetDimensions();
        }
        for(int i = 0; i < width ;i ++){
            for(int j = 0; j < height; j++){
                // CHANGE magic number
                sensors[i*width + j] = isAccessibleWithRadius(i,j,1.2f);
            }
        }
    }

    /**
     * Turns value of all sensors for a given object to onoff
     */
    public void turnOnOffObjSensors(GameObject object, int onoff){
        for(int i = 0; i < width ;i ++){
            for(int j = 0; j < height; j++){
                // CHANGE magic number
                if (!(isAccessibleWithRadiusSingleObject(i,j,1.2f, object))) {
                    sensors[i*width + j] = onoff;
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
        rayhandler.setAmbientLight(0,0,0,1f);
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
        if (gameObject.getClass() == AIModel.class) {
            aiList.add((AIModel)gameObject);
        }
    }

    public void removeEatenFood(){
        for(GameObject gm : gameObjects){
            if(gm instanceof FoodModel){
                if(((FoodModel) gm).getAmount() <= 0){
                    removeGameObject(gm);
                }
            }
        }
    }

    public void removeGameObject(GameObject gameObject){
        assert gameObject != null : "Tried to remove null GameObject";
        removeGameObjectQueue.add(gameObject);
    }

    public void removeGameObjects(){
        while(!removeGameObjectQueue.isEmpty()){
            GameObject gm = removeGameObjectQueue.pop();
            world.destroyBody(gm.getBody());
            gameObjects.remove(gm);
        }
    }

    public void addGameObjects(){
        while(!addGameObjectQueue.isEmpty()){
            GameObject go = addGameObjectQueue.pop();
            String[] tags = go.getTags();
            for(String tag : tags){
                go.addAsset(tag, AssetLoader.getInstance().getAsset(tag));
            }
            addGameObject(go);

        }
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

    /** returns true sensor at position x y is not overlapping with some object */
    public boolean isAccessibleByAI(int x, int y, boolean noFurniture) {
        if (x < 0 || y < 0 || x > width - 1 || y > width -1) {
            return false;
        }
        return sensors[x*width + y] <= 0 + ((noFurniture) ? 1 : 0);
    }

    /** returns 0 if any of 8 cardinal points radius away from x y
     *  are not in some solid object
     *
     *  returns 1 if contained in a wall
     *
     *  returns 2 if contained in any other object
     */
    public int isAccessibleWithRadius(float x, float y, float radius) {
        for(GameObject obj: solidGameObjects) {
            if (!isAccessibleWithRadiusSingleObject(x, y, radius, obj)) {
                if (obj.getClass() == WallModel.class) {
                    return 2;
                }
                else {
                    return 1;
                }
            }
        }
        return 0;
    }

    /** returns true if any of 8 cardinal points radius away from x y
     *  are not in the given object
     */
    public boolean isAccessibleWithRadiusSingleObject(float x, float y, float radius, GameObject obj) {
        for(double i = 0; i < 2*Math.PI; i = i + Math.PI/4){
            float tempx = (float)Math.cos(i)*radius + x;
            float tempy = (float)Math.sin(i)*radius + y;
            if (obj.getBody().getFixtureList().get(0).testPoint(tempx,tempy)) {
                return false;
            }
        }
        return true;
    }
}
