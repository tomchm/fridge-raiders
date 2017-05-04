package edu.cornell.gdiac.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.FurnitureModel;

/**
 * Created by Sal on 3/12/2017.
 */
public class GrabController {
    /** Piece of furniture that the player is currently holding onto.
     * null if none */
    private FurnitureModel current = null;
    private WorldModel worldModel;

    public GrabController(WorldModel wm) {
        worldModel = wm;
    }

    /** Weld the player to the piece of furniture they grabbed. */
    public void grab(FurnitureModel furniture) {
        // make sure we're not already holding one!
        // shouldn't be necessary to check.
        if (current != null) return;
        current = furniture;

        // set filter
        Filter filter = current.getBody().getFixtureList().get(0).getFilterData();
        filter.groupIndex = -1;
        current.getBody().getFixtureList().get(0).setFilterData(filter);


        WeldJointDef jointDef = new WeldJointDef();
        jointDef.initialize(worldModel.getPlayer().getBody(), furniture.getBody(), new Vector2());
        jointDef.collideConnected = false;

        Vector2 dif = new Vector2(furniture.getBody().getPosition()).sub(worldModel.getPlayer().getBody().getPosition());
        double angle = Math.atan2((double)dif.y, (double)dif.x);

        if(angle <= Math.PI/4 && angle >=  -Math.PI/4) {
            worldModel.getPlayer().setAnimation(DetectiveModel.Animation.RIGHT_GRAB);
        }
        else if(angle <= 3* Math.PI/4 && angle >= Math.PI/4) {
            worldModel.getPlayer().setAnimation(DetectiveModel.Animation.UP_GRAB);
        }
        else if(angle >= 3* Math.PI/4 || angle <= -3*Math.PI/4) {
            worldModel.getPlayer().setAnimation(DetectiveModel.Animation.LEFT_GRAB);
        }
        else if(angle <= - Math.PI/4 && angle >= -3*Math.PI/4) {
            worldModel.getPlayer().setAnimation(DetectiveModel.Animation.DOWN_GRAB);
        }

        worldModel.getPlayer().setGrappled(true);
        switch(worldModel.getPlayer().getAnimation()){
            case DOWN_MOVE:
            case DOWN_STOP:
                worldModel.getPlayer().setAnimation(DetectiveModel.Animation.DOWN_GRAB);
                break;
            case UP_MOVE:
            case UP_STOP:
                worldModel.getPlayer().setAnimation(DetectiveModel.Animation.UP_GRAB);
                break;
            case RIGHT_MOVE:
            case RIGHT_STOP:
                worldModel.getPlayer().setAnimation(DetectiveModel.Animation.RIGHT_GRAB);
                break;
            case LEFT_MOVE:
            case LEFT_STOP:
                worldModel.getPlayer().setAnimation(DetectiveModel.Animation.LEFT_GRAB);
                break;
        }
        worldModel.addJoint(jointDef);
        worldModel.setDynamic(current.getBody());
        worldModel.turnOnOffObjSensors(current, 0);

    }

    /** Unweld the player. */
    public void release() {
        // this check IS needed! Otherwise you'll get null pointer exceptions
        if (current == null) return;

        Filter filter = current.getBody().getFixtureList().get(0).getFilterData();
        filter.groupIndex = 0;
        current.getBody().getFixtureList().get(0).setFilterData(filter);

        worldModel.getPlayer().setGrappled(false);
        worldModel.setStatic(current.getBody());
        worldModel.turnOnOffObjSensors(current, 1);
        current = null;
        worldModel.clearJoints();
    }
}
