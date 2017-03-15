package edu.cornell.gdiac.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;

import java.util.Collections;

/**
 * Created by tomchm on 3/9/17.
 */
public class WorldModel {

    protected World world;
    protected Vector2 scale;
    protected PooledList<GameObject> addGameObjectQueue;
    protected PooledList<GameObject> solidGameObjects;
    protected PooledList<GameObject> gameObjects;
    protected PooledList<AIModel> aiList;
    protected PooledList<JointDef> jointQueue;
    protected PooledList<Body> staticQueue;
    protected PooledList<Body> dynamicQueue;
    protected boolean clearJoints;
    protected DetectiveModel detective;

    /**
     * @return a list of all AI models currently populating the level
     */
    public PooledList<AIModel> getAIList() {return aiList;}

    /** Adds ai to list of AIModels populating the level
     *
     * @param ai a new ai model to add to list of ai models in the current level
     */
    public void addAI(AIModel ai){aiList.add(ai);}

    /**
     * clears aiList
     */
    public void clearAIList() {aiList.clear();}


    public WorldModel(float sx, float sy){
        world = new World(new Vector2(), false);
        scale = new Vector2(sx,sy);
        addGameObjectQueue = new PooledList <GameObject>();
        solidGameObjects = new PooledList <GameObject>();
        gameObjects = new PooledList <GameObject>();
        aiList = new PooledList <AIModel>();
        jointQueue = new PooledList<JointDef>();
        staticQueue = new PooledList<Body>();
        dynamicQueue = new PooledList<Body>();
        clearJoints = false;
    }

    public DetectiveModel getPlayer() { return detective; }
    public void setPlayer(DetectiveModel dm) {detective=dm;}

    public void addGameObjectQueue(GameObject gameObject){
        assert gameObject != null : "Tried to add null GameObject";
        addGameObjectQueue.add(gameObject);
    }

    public void addGameObject(GameObject gameObject){
        assert gameObject != null : "Tried to add null GameObject";
        gameObjects.add(gameObject);
        gameObject.activate(world);
        if (gameObject.getClass() == FurnitureModel.class || gameObject.getClass() == WallModel.class) {
            solidGameObjects.add(gameObject);
        }
    }

    public void addGameObjects(){
        while(!addGameObjectQueue.isEmpty()){
            GameObject go = addGameObjectQueue.pop();
            addGameObject(go);
        }
    }

    public void addJoint(JointDef jointDef){
        assert jointDef != null : "Tried to add null JointDef";
        jointQueue.add(jointDef);
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

    public void drawGameObjects(GameCanvas canvas){
        Collections.sort(gameObjects);
        for(GameObject gm : gameObjects){
            gm.draw(canvas);
        }
    }

    public void drawDebugGameObjects(GameCanvas canvas){
        for(GameObject gm : gameObjects){
            gm.drawDebug(canvas);
        }
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

    /** returns true if the point is not in some object*/
    public boolean isAccessible(float x, float y) {
        for(GameObject obj: gameObjects) {
            if (obj.getBody().getFixtureList().get(0).testPoint(x,y)){return true;}
        }
        return false;
    }

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
