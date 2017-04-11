/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game;

import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.gui.AimGUIModel;
import edu.cornell.gdiac.game.gui.GUIController;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.*;

public class WorldController implements Screen {

	private static final float DRAW_SCALE = 32f;
	private WorldModel worldModel;
	private boolean debug = false;
	private SpacebarController spacebarController;
	private GUIController guiController;

	/** List of ai controllers (one for each ai)*/
	private PooledList<AIController> aiControllers;

	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	public static final int CUTSCENE = 1;
	/** The amount of time for a game engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;

	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	private AssetLoader assetLoader;
	private FileIOController fileIOController;

	private DetectiveController detectiveController;
	private InputController input;

	public AssetLoader getAssetLoader() {
		return assetLoader;
	}

	public WorldController() {
		setDebug(false);
		WorldModel worldModel = new WorldModel(DRAW_SCALE, DRAW_SCALE);
		assetLoader = AssetLoader.getInstance();
		GameObject.setDrawScale(worldModel.getScale());
		setDebug(false);
		spacebarController = new SpacebarController(worldModel);
		aiControllers = new PooledList <AIController>();
		fileIOController = new FileIOController(worldModel);
		guiController = new GUIController(worldModel, spacebarController);
	}

	public void reset() {
		worldModel = new WorldModel(DRAW_SCALE, DRAW_SCALE);
		spacebarController = new SpacebarController(worldModel);
		aiControllers.clear();
		fileIOController = new FileIOController(worldModel);
		guiController = new GUIController(worldModel, spacebarController);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		fileIOController.load("levels/alphaLevel.json");
		//FloorModel floor = new FloorModel();
		//worldModel.addGameObject(floor);
		detectiveController = new DetectiveController(worldModel.getPlayer(), worldModel, (AimGUIModel) guiController.getGUI("AimGUI"));
		assetLoader.assignContent(worldModel);
		assetLoader.assignContent(guiController);
		for (AIModel ai: worldModel.getAIList()) {
			aiControllers.add(new AIController(ai, worldModel));
		}
		worldModel.updateAllSensors();
		fileIOController.save("levels/testOutput.json");
	}

	public boolean isDebug( ) {
		return debug;
	}

	public void setDebug(boolean value) {
		debug = value;
	}

	public GameCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
	}
	
	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		canvas = null;
	}

	public boolean preUpdate(float dt) {
		input = InputController.getInstance();
		input.readInput();
		if (listener == null) {
			return true;
		}

		if (input.didDebug()) {
			debug = !debug;
		}

		if (input.didReset()) {
			reset();
		}

		if(input.didExit()){
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		}
		if(input.didCutscene()) {
			listener.exitScreen(this, CUTSCENE);
			return false;
		}
		if(!worldModel.getPlayer().isSecondStage()){
			for(AIController ai : aiControllers){
				if(ai.hasBeenCaught()){
					reset();
					break;
				}
			}
		}
		else {
			if(!worldModel.getPlayer().hasShots()){
				reset();
			}
		}



		return true;
	}

	public void update(float dt) {
		detectiveController.update(input);

		if (InputController.getInstance().didSecondary()) spacebarController.keyDown();
		else if (InputController.getInstance().releasedSecondary()) spacebarController.keyUp();

		for (AIController aic : aiControllers) {
			if(worldModel.getPlayer().isSecondStage()){
				aic.update2(dt);
			}
			else {
				aic.update(dt);
			}
		}

		if(worldModel.getPlayer().getAmountEaten() >= worldModel.getPlayer().getThreshold()){
			worldModel.unlockDessert();
		}

		worldModel.updateGameObjects(dt);
        worldModel.removeEatenFood();

		SoundController.getInstance().update();
		guiController.update(dt);
	}

	public void postUpdate(float dt) {
		// Add any objects created by actions
		worldModel.addGameObjects();
		worldModel.removeGameObjects();
		worldModel.updateJoints();
		worldModel.applyDynamic();
		worldModel.applyStatic();

		worldModel.getWorld().step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

		Iterator<PooledList<GameObject>.Entry> iterator = worldModel.getGameObjects().entryIterator();
		while (iterator.hasNext()) {
			PooledList<GameObject>.Entry entry = iterator.next();
			GameObject obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivate(worldModel.getWorld());
				entry.remove();
			} else {
				obj.update(dt);
			}
		}
		Vector2 position = worldModel.getPlayer().getBody().getPosition();
		canvas.moveCamera(position.x, position.y);
		guiController.setOrigin(position);
	}

	public void draw(float delta) {
		canvas.clear();
		GameObject.incCounter();
		worldModel.draw(canvas);
		guiController.draw(canvas);
	}

	public void drawDebug(float delta){
		canvas.beginDebug();
		worldModel.drawDebugGameObjects(canvas);
		canvas.endDebug();
	}

	public void resize(int width, int height) {
		// IGNORE FOR NOW
	}


	public void render(float delta) {
		if (preUpdate(delta)) {
			update(delta); // This is the one that must be defined.
			postUpdate(delta);
		}
		draw(delta);
		if(isDebug()){
			drawDebug(delta);
		}
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

}