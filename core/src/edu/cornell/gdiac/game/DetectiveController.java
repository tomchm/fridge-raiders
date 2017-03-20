package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.gui.AimGUIModel;
import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.GameObject;
//import edu.cornell.gdiac.game.model.ShotModel;

/**
 * Created by Sean on 3/14/17.
 */
public class DetectiveController {

    private DetectiveModel player;
    private WorldModel worldModel;
    private AimGUIModel aimGUI;
    private boolean isSecondStage;
    private int lastMove = -1;
    private final static float SHOOT_FORCE = 50f;
    private final static float MAX_FORCE = 500*SHOOT_FORCE;


    public  DetectiveController( DetectiveModel playerModel, WorldModel world, AimGUIModel aimGUI){
        player = playerModel;
        worldModel = world;
        this.aimGUI = aimGUI;
        isSecondStage = player.isSecondStage();
    }

    private boolean didClickOnPlayer(MyInputProcessor myProcessor){
        // Checks center of screen +/- the player radius to see if we clicked inside the player box



        boolean rx = myProcessor.lastX <= Gdx.graphics.getWidth()/2 + player.getRadius() * GameObject.getDrawScale().x /2;
        boolean lx = myProcessor.lastX >= Gdx.graphics.getWidth()/2 - player.getRadius() * GameObject.getDrawScale().x /2;
        boolean ty = myProcessor.lastY <= Gdx.graphics.getHeight()/2 + player.getRadius() * GameObject.getDrawScale().y /2;
        boolean by = myProcessor.lastY >= Gdx.graphics.getHeight()/2 - player.getRadius() * GameObject.getDrawScale().y /2;

        return rx && lx && ty && by;
    }

    private void handleShots(MyInputProcessor myProcessor){
        // Need to check if they have let go of the mouse click to actually shoot.
        if(myProcessor.released){
            // Applying forces on the player based on their shot.
            float fx = myProcessor.magnitude.x * SHOOT_FORCE;
            if(fx > MAX_FORCE){
                fx = MAX_FORCE;
            }
            else if(fx < -MAX_FORCE){
                fx = -MAX_FORCE;
            }
            float fy = (-myProcessor.magnitude.y) * SHOOT_FORCE;
            if(fy > MAX_FORCE){
                fy = MAX_FORCE;
            }
            else if(fy < -MAX_FORCE){
                fy = -MAX_FORCE;
            }
            player.setFX(fx);
            player.setFY(fy);
            player.applyForce();
            myProcessor.lastY = 0;
            myProcessor.lastX = 0;
            aimGUI.setAim(false);
            myProcessor.shouldRecordClick = false;
        }
        else {
            aimGUI.setAimVector(myProcessor.magnitude, player.getBody().getPosition());
            aimGUI.setAim(true);
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
            if(!player.isEating()){
                animatePlayer(input);
            }
        }

        // If we are in stage two, no walking mechanics allowed
        if(isSecondStage){
            player.setAnimation(DetectiveModel.Animation.DOWN_STOP);
            // Need the special input processor to do mouse commands from the input controller.
            MyInputProcessor processor = input.getMyProcessor();
            float speed = player.getSpeed();

            // Only want the player shooting when they've come to a stop
            // Slow them down otherwise.
            if(speed == 0) {
                processor.shouldRecordClick = true;
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
