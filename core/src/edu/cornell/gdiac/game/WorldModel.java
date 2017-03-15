package edu.cornell.gdiac.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.game.model.AIModel;
import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.game.model.WallModel;
import edu.cornell.gdiac.util.PooledList;

import java.util.Collections;

/**
 * Created by tomchm on 3/9/17.
 */
public class WorldModel {

    protected World world;
    protected Vector2 scale;
    protected PooledList<GameObject> addGameObjectQueue = new PooledList <GameObject>();
    protected PooledList<GameObject> gameObjects = new PooledList <GameObject>();
    protected PooledList<AIModel> aiList = new PooledList <AIModel>();
    protected PooledList<JointDef> jointQueue = new PooledList<JointDef>();
    protected PooledList<Body> staticQueue = new PooledList<Body>();
    protected PooledList<Body> dynamicQueue = new PooledList<Body>();
    protected boolean clearJoints;
    protected DetectiveModel detective;

    public WorldModel(float sx, float sy){
        world = new World(new Vector2(), false);
        scale = new Vector2(sx,sy);
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
}
