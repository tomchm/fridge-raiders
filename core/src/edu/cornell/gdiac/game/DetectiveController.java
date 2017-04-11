package edu.cornell.gdiac.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.gui.AimGUIModel;
import edu.cornell.gdiac.game.model.CrumbModel;
import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.game.model.TrajectoryModel;

import javax.annotation.processing.SupportedSourceVersion;
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
    private final static float SHOOT_FORCE = 35f;
    private final static float MAX_FORCE = 350*SHOOT_FORCE;


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
            player.consumeShot();
            player.setAnimation(DetectiveModel.Animation.ROLL_MOVE);
        }
        else {
            Vector2 position = player.getBody().getPosition();
            aimGUI.setAimVector(myProcessor.magnitude, position);
            aimGUI.setAim(true);
        }
    }

    private void stopAnimating(){
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
    }

    private void animatePlayer(InputController input){
        boolean isDiag = input.getHorizontal() != 0.0 && input.getVertical() != 0.0;
        float multiplier = player.isGrappled() ? 0.30f : 0.55f;
        multiplier = isDiag ? multiplier* 0.75f : multiplier;

        // Check which direction we are allowing the player to move
        if(input.getHorizontal() != 0.0){
            if(input.getHorizontal() == -1.0){
                player.setAnimation(DetectiveModel.Animation.LEFT_MOVE);
                player.getBody().setLinearVelocity(player.getThrust() * input.getHorizontal() * multiplier, player.getThrust() * input.getVertical() * multiplier);
                lastMove = 3;

            }
            else{
                player.setAnimation(DetectiveModel.Animation.RIGHT_MOVE);
                player.getBody().setLinearVelocity(player.getThrust() * input.getHorizontal()* multiplier, player.getThrust() * input.getVertical() * multiplier);
                lastMove =1;
            }
        }
        else if(input.getVertical() != 0.0){
            if(input.getVertical() == -1.0){
                player.setAnimation(DetectiveModel.Animation.DOWN_MOVE);
                player.getBody().setLinearVelocity(player.getThrust()*input.getHorizontal()*multiplier, player.getThrust() * input.getVertical()* multiplier);
                lastMove =2;
            }
            else{
                player.setAnimation(DetectiveModel.Animation.UP_MOVE);
                player.getBody().setLinearVelocity(player.getThrust()*input.getHorizontal()*multiplier, player.getThrust() * input.getVertical()* multiplier);
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

            player.getBody().setLinearVelocity(player.getThrust()*input.getHorizontal()* multiplier, player.getThrust() * input.getVertical()* multiplier);


        }
    }

    public void update(InputController input){
        isSecondStage = player.isSecondStage();

        // First we want to update walking mechanics if it's in stage one.
        if(!isSecondStage) {
            if(!player.isEating() && player.eatDelay == 0.0){
                animatePlayer(input);
            }
            else{
                // player ate, need to decrement eatDelay

                if(player.eatDelay > 0.0) {
                    stopAnimating();
                    player.eatDelay -= 1.0f;
                }
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
                player.setAnimation(DetectiveModel.Animation.ROLL_STOP);
                processor.shouldRecordClick = true;
                if (didClickOnPlayer(processor)) {
                    handleShots(processor);
                }
            }
            else {
                player.setAnimation(DetectiveModel.Animation.ROLL_MOVE);
            }

            if (speed < 20) {
                player.getBody().setLinearDamping(0.9f);
            }
            if (speed < 5) {
                player.getBody().setLinearVelocity(0,0);
            }


        }

        aimGUI.setFoodAmount(player.getAmountEaten());

        if(player.getChewing() != null){
            worldModel.addGameObjectQueue(new CrumbModel(player.getChewing().getBody().getPosition(), player.getChewing().getCrumbColor(), player.getChewing().getZ()));

        }

    }



}
