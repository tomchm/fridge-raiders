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
    private boolean isSecondStage;
    private int lastMove = -1;

//    private ShotModel[] dots;



    public  DetectiveController( DetectiveModel playerModel, WorldModel world){
        player = playerModel;
        worldModel = world;
        isSecondStage = player.isSecondStage();
    }

    private boolean didClickOnPlayer(MyInputProcessor myProcessor){
        // Checks center of screen +/- the player radius to see if we clicked inside the player box

        boolean rx = myProcessor.lastX <= Gdx.graphics.getWidth()/2 + player.getRadius() * 32 /2;
        boolean lx = myProcessor.lastX >= Gdx.graphics.getWidth()/2 - player.getRadius() * 32 /2;
        boolean ty = myProcessor.lastY <= Gdx.graphics.getHeight()/2 + player.getRadius() * 32/2;
        boolean by = myProcessor.lastY >= Gdx.graphics.getHeight()/2 - player.getRadius() * 32 /2;

        return rx && lx && ty && by;
    }

    private void handleShots(MyInputProcessor myProcessor){
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

    private void animatePlayer(InputController input){
        // Check which direction we are allowing the player to move
        if(input.getHorizontal() != 0.0){
            if(input.getHorizontal() == -1.0){
                player.setAnimation(DetectiveModel.Animation.LEFT_MOVE);
                player.getBody().setLinearVelocity(player.getThrust() * input.getHorizontal(), 0);
                lastMove = 3;

            }
            else{
                player.setAnimation(DetectiveModel.Animation.RIGHT_MOVE);
                player.getBody().setLinearVelocity(player.getThrust() * input.getHorizontal(), 0);
                lastMove =1;
            }
        }
        else if(input.getVertical() != 0.0){
            if(input.getVertical() == -1.0){
                player.setAnimation(DetectiveModel.Animation.DOWN_MOVE);
                player.getBody().setLinearVelocity(0, player.getThrust() * input.getVertical());
                lastMove =2;
            }
            else{
                player.setAnimation(DetectiveModel.Animation.UP_MOVE);
                player.getBody().setLinearVelocity(0, player.getThrust() * input.getVertical());
                lastMove = 0;
            }
        }
        else{
            switch (lastMove) {
                case -1:
                    player.setAnimation(DetectiveModel.Animation.DOWN_STOP);
                    break;
                case 0:
                    player.setAnimation(DetectiveModel.Animation.UP_STOP);
                    break;
                case 1:
                    player.setAnimation(DetectiveModel.Animation.RIGHT_STOP);
                    break;
                case 2:
                    player.setAnimation(DetectiveModel.Animation.DOWN_STOP);
                    break;
                case 3:
                    player.setAnimation(DetectiveModel.Animation.LEFT_STOP);
                    break;
            }

            player.getBody().setLinearVelocity(player.getThrust()*input.getHorizontal(), player.getThrust() * input.getVertical());


        }
    }

    public void update(InputController input){
        isSecondStage = player.isSecondStage();

        // First we want to update walking mechanics if it's in stage one.
        if(!isSecondStage) {
            animatePlayer(input);
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
