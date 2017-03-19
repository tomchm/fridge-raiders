package edu.cornell.gdiac.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
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

        WeldJointDef jointDef = new WeldJointDef();
        jointDef.initialize(worldModel.getPlayer().getBody(), furniture.getBody(), new Vector2());
        jointDef.collideConnected = false;
        worldModel.addJoint(jointDef);
        worldModel.setDynamic(current.getBody());
    }

    /** Unweld the player. */
    public void release() {
        // this check IS needed! Otherwise you'll get null pointer exceptions
        if (current == null) return;

        worldModel.setStatic(current.getBody());
        current = null;
        worldModel.clearJoints();
        worldModel.updateSensors();
    }
}
