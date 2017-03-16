package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.FurnitureModel;

/**
 * Created by Sal on 3/12/2017.
 */
public class GrabController {
    /** Piece of furniture that the player is currently holding onto.
     * null if none */
    private FurnitureModel current = null;

    public void grab(FurnitureModel furniture) {
        if (current != null) return;
    }
    public void release() {
        current = null;
    }
}
