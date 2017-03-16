package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import edu.cornell.gdiac.game.model.DetectiveModel;
//import edu.cornell.gdiac.game.model.ShotModel;

/**
 * Created by Sean on 3/14/17.
 */
public class DetectiveController {

    private DetectiveModel player;
    private WorldModel worldModel;
    private boolean isSecondStage = false;

//    private ShotModel[] dots;



    public  DetectiveController( DetectiveModel playerModel, WorldModel world){
        player = playerModel;
        worldModel = world;
    }

    private boolean didClickOnPlayer(MyInputProcessor myProcessor){
        // Checks center of screen +/- the player radius to see if we clicked inside the player box

        boolean rx = myProcessor.lastX <= Gdx.graphics.getWidth()/2 + player.getRadius() * 32 /2;
        boolean lx = myProcessor.lastX >= Gdx.graphics.getWidth()/2 - player.getRadius() * 32 /2;
        boolean ty = myProcessor.lastY <= Gdx.graphics.getHeight()/2 + player.getRadius() * 32/2;
        boolean by = myProcessor.lastY >= Gdx.graphics.getHeight()/2 - player.getRadius() * 32 /2;

        return rx && lx && ty && by;
    }

    public void handleShots(MyInputProcessor myProcessor){
        // Need to check if they have let go of the mouse click to actually shoot.
        if(myProcessor.released){
            // Applying forces on the player based on their shot.
            player.setFX(myProcessor.magnitude.x * 100);
            player.setFY(-myProcessor.magnitude.y * 100);
            player.applyForce();
            myProcessor.lastY = 0;
            myProcessor.lastX = 0;
        }
    }

    public void update(InputController input){

        // First we want to update walking mechanics if it's in stage one.
        if(!isSecondStage) {
            player.getBody().setLinearVelocity(player.getThrust() * input.getHorizontal(), player.getThrust() * input.getVertical());
        }

        // If we are in stage two, no walking mechanics allowed
        if(isSecondStage){
            // Need the special input processor to do mouse commands from the input controller.
            MyInputProcessor processor = input.getMyProcessor();
            float speed = player.getSpeed();

            // Only want the player shooting when they've come to a stop
            // Slow them down otherwise.
            if(speed == 0) {
                if (didClickOnPlayer(processor)) {
                    handleShots(processor);
                }
            }
            if (speed < 20) {
                player.getBody().setLinearDamping(0.9f);
            }
            if (speed < 5) {
                player.getBody().setLinearVelocity(0,0);
            }


        }

    }



}
