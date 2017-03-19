package edu.cornell.gdiac.game;

import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.DoorModel;

/**
 * Created by Sal on 3/14/2017.
 */
public class HingeController {

    public HingeController(){

    }

    public void toggle(DoorModel dm){
        System.out.println("CLICKING DOOR D0000d");
        if (dm.getStatus()){
            dm.close();
        }
        else{
            dm.open();
        }
    }
}
