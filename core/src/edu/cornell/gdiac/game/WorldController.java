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

import java.awt.*;
import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.game.gui.AimGUIModel;
import edu.cornell.gdiac.game.gui.GUIController;
import edu.cornell.gdiac.game.gui.PauseGUI;
import edu.cornell.gdiac.game.gui.ResetGUIModel;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class WorldController implements Screen {

	private static final float DRAW_SCALE = 32f;
	private static final float PAN_TIME = 1.5f;
	private static final float HOLD_TIME = 1.5f;

	private WorldModel worldModel;
	private boolean debug = false;
	private SpacebarController spacebarController;
	private GUIController guiController;

	private boolean playedScene = false;
	private boolean didIntroPan = false;
	private Queue<Vector2> panQueue = null;
	private float panS = 0f; // ranges from 0 to 1 as you vary between pan start & pan end.
	private float panT = 0f; // ranges from 0 to 1 as you hold on the destination.

	/** List of ai controllers (one for each ai)*/
	private PooledList<AIController> aiControllers;

	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	public static final int CUTSCENE = 1;
	public static final int GAMEVIEW = 2;
	public static final int LEVEL_SELECT = 3;
	// STORY SCREEN: 100 + the level code. eg, 203 will play a cutscene, then exit with levelCode 103
	/** The amount of time for a game engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;

	/** Reference to the game canvas */
	protected GameCanvas canvas;
	protected GameCanvas guicanvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	private AssetLoader assetLoader;
	private FileIOController fileIOController;

	private DetectiveController detectiveController;
	private InputController input;

	private int resetCounter;
	private String worldModelSave;
	private WorldModel wmSave;
	private DetectiveController dcSave;
	private DetectiveModel dmSave;
	private Vector2 resetLevelTwoPosition;
	private float resetLevelAngle;
	private boolean hardReset = false;
	private boolean didShowMenu = false;
	private boolean shouldPlayScene = true;
	private boolean isPaused = false;
	private String levelFile;
	private boolean panToDessert = false;


	public WorldController(String levelFile) {
		setDebug(false);
		WorldModel worldModel = new WorldModel(DRAW_SCALE, DRAW_SCALE);
		assetLoader = AssetLoader.getInstance();
		GameObject.setDrawScale(worldModel.getScale());
		setDebug(false);
		spacebarController = new SpacebarController(worldModel);
		aiControllers = new PooledList <AIController>();
		fileIOController = new FileIOController(worldModel);
		guiController = new GUIController(worldModel, spacebarController, input);
		panQueue = new Queue<Vector2>();
		resetCounter = 0;
		guicanvas = new GameCanvas();
		this.levelFile = levelFile;
	}

	public boolean isPanning() {
		return panQueue.size >= 2;
	}

	public void reset() {
		playedScene = false;
		if (SoundController.getInstance().isActive("music_rolling")) {
			SoundController.getInstance().stop("music_rolling");
		}
		if(this.detectiveController == null || hardReset) {
			shouldPlayScene = true;
            worldModel = new WorldModel(DRAW_SCALE, DRAW_SCALE);
            spacebarController = new SpacebarController(worldModel);
            aiControllers.clear();
            fileIOController = new FileIOController(worldModel);
            populateLevel();
            guiController = new GUIController(worldModel, spacebarController, input);
            detectiveController = new DetectiveController(worldModel.getPlayer(), worldModel, (AimGUIModel) guiController.getGUI("AimGUI"));
            assetLoader.assignContent(guiController);
            canvas.resetZoom();
            worldModel.resetZoomRaycamera();
            resetCounter = 0;
		}
		else{
			worldModel.hasLost = false;
			if(this.detectiveController.inSecondStage()){
			    worldModel = wmSave;
			    detectiveController = dcSave;
			    worldModel.getPlayer().setFX(0.0f);
			    worldModel.getPlayer().setFY(0.0f);
			    worldModel.getPlayer().applyForce();
			    worldModel.getPlayer().getBody().setTransform(resetLevelTwoPosition,resetLevelAngle);
			    worldModel.getPlayer().resetStickers();
			    worldModel.getPlayer().resetShots();

			}
			else{
				shouldPlayScene = true;
                worldModel = new WorldModel(DRAW_SCALE, DRAW_SCALE);
                spacebarController = new SpacebarController(worldModel);
                aiControllers.clear();
                fileIOController = new FileIOController(worldModel);
                populateLevel();
                guiController = new GUIController(worldModel, spacebarController, input);
                detectiveController = new DetectiveController(worldModel.getPlayer(), worldModel, (AimGUIModel) guiController.getGUI("AimGUI"));
                assetLoader.assignContent(guiController);
                canvas.resetZoom();
                worldModel.resetZoomRaycamera();
                resetCounter = 0;
			}
		}
	}

	public void setHardReset(){
		hardReset = true;
	}


	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		fileIOController.load(levelFile);
		assetLoader.assignContent(worldModel);
		for (AIModel ai: worldModel.getAIList()) {
			aiControllers.add(new AIController(ai, worldModel));
		}
		worldModel.updateAllSensors();
		worldModel.setMaximumFood();
		if (!didIntroPan) {
		    didIntroPan = true;
            panQueue.addLast(worldModel.getPlayer().getBody().getPosition());
            for (AIModel ai : worldModel.aiList) {
                panQueue.addLast(ai.getBody().getPosition());
            }
            panQueue.addLast(worldModel.getDessertPosition());
            panQueue.addLast(worldModel.getPlayer().getBody().getPosition());
        }

		SoundController.getInstance().play("levelmusic", true, 0.75f);
		//fileIOController.save("levels/testOutput.json");
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

	public GameCanvas getGUICanvas() {
		return guicanvas;
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

		if (isPanning()) {
		    input.lockPlayer();
        }

		if (input.didSkip()) {
		    panQueue.clear();
		    panS = 0f;
		    panT = 0f;
        }

		if (input.didDebug()) {
			debug = !debug;
		}

		if (input.didReset()) {
			reset();
		}

		if (input.didLightTest()) {
			worldModel.getPlayer().getPointLight().setXray(!worldModel.getPlayer().getPointLight().isXray());
		}



		if(input.didExit()){
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		}
		if(input.didCutscene()) {
			//listener.exitScreen(this, CUTSCENE);
			//return false;
		}
		if(!worldModel.getPlayer().isSecondStage()){
			for(AIController ai : aiControllers){
				if(ai.hasBeenCaught()){
					worldModel.setLost();
					break;
				}
			}

		}

		PauseGUI pauseGui = (PauseGUI) this.guiController.getGUI("PauseGUI");
		if(pauseGui.paused){
			this.worldModel.setPaused(true);
		}
		else{
			this.worldModel.setPaused(false);
		}
		if(pauseGui.shouldQuit()){
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		}
		else if(pauseGui.shouldLevelSelect()){
			listener.exitScreen(this, LEVEL_SELECT);
			return false;
		}

		if(worldModel.isLevelSelect()){
			listener.exitScreen(this, LEVEL_SELECT);
			worldModel.setLevelSelect(false);
			return false;
		}


		// Tried to pan to dessert????



//		if((!(panToDessert)) && worldModel.getPlayer().getAmountEaten() >= worldModel.getPlayer().getThreshold()){
//			float dessertX = 0f;
//			float dessertY = 0f;
//			for(GameObject gm: worldModel.getGameObjects()){
//				if(gm instanceof FoodModel){
//					if(((FoodModel) gm).isDessert()){
//						dessertX = gm.getBody().getPosition().x;
//						dessertY = gm.getBody().getPosition().y;
//					}
//				}
//			}
//
//			canvas.moveCamera(dessertX,dessertY);
//			guicanvas.moveCamera(dessertX, dessertY);
//			panToDessert = true;
//		}

		else {
			if(worldModel.getGoal().hasPlayerCollided()){
				worldModel.setWon();
			}
			else if(!worldModel.getPlayer().hasShots()){
				worldModel.setLost();
				if(!didShowMenu) {
					input.getMyProcessor().menuX = 0;
					input.getMyProcessor().menuY = 0;
					didShowMenu = true;
				}
			}
		}

		ResetGUIModel resetGUI = (ResetGUIModel) this.guiController.getGUI("ResetGUI");
		if(resetGUI.hardReset){
			hardReset = true;
			reset();
			hardReset = false;
			didShowMenu = false;
			resetGUI.hardReset = false;
		}

		else if(resetGUI.softReset){
			worldModel.hasLost = false;
			reset();
			didShowMenu = false;
			resetGUI.softReset = false;
		}

		if(worldModel.hasLost() && (!this.worldModel.getPlayer().isSecondStage())){
			reset();
		}

		if(worldModel.hasWon()){
		    resetCounter++;
		    if(resetCounter == 180){
		    	listener.exitScreen(this, LEVEL_SELECT);
		    	hardReset = true;
		        reset();
		        hardReset = false;
            }
        }



		return true;
	}

	public void update(float dt) {
		detectiveController.update(input);

		if (worldModel.getPlayer().isSecondStage() && !playedScene) {
			playedScene = true;
			if(shouldPlayScene) {
				if (SoundController.getInstance().isActive("levelmusic"))
					SoundController.getInstance().stop("levelmusic");
				listener.exitScreen(this, CUTSCENE);
				shouldPlayScene = false;
			}
			this.wmSave = worldModel;
			this.dcSave = detectiveController;
			this.dmSave = new DetectiveModel(worldModel.getPlayer().getBody().getPosition().x, worldModel.getPlayer().getBody().getPosition().y);
			resetLevelTwoPosition = new Vector2(worldModel.getPlayer().getBody().getPosition().x, worldModel.getPlayer().getBody().getPosition().y);
			resetLevelAngle = worldModel.getPlayer().getBody().getAngle();
		}

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

		if(!worldModel.hasLost()){
            worldModel.getWorld().step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);
        }

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
		// here, update position if we're supposed to be panning somewhere
		if (isPanning()) {
			position.x = panQueue.get(0).x + panS * (panQueue.get(1).x - panQueue.get(0).x);
			position.y = panQueue.get(0).y + panS * (panQueue.get(1).y - panQueue.get(0).y);
		}
		if (isPanning() && panS < 1f) {
			panS += 1f / (PAN_TIME * 60f);
		}
		else if (isPanning() && panS > 1f) {
            if (panQueue.size == 2) { panT = 1f; } // prevent hold delay on last object panned to
			panT += 1f / (HOLD_TIME * 60f);
		}
		if (panT > 1f) {
			panQueue.removeFirst();
			panS = 0f;
			panT = 0f;
			if (panQueue.size == 1) panQueue.clear();
		}

		canvas.moveCamera(position.x, position.y);
		guicanvas.moveCamera(position.x, position.y);
		worldModel.moveRayCamera(position.x, position.y);
		if(worldModel.getPlayer().isSecondStage()){
			//canvas.zoomOut();
			worldModel.zoomOutRaycamera();
		}


		guiController.setOrigin(position);
	}

	public void draw(float delta) {
		canvas.clear();
		guicanvas.clear();
		GameObject.incCounter();
		worldModel.draw(canvas);
		if (!isPanning()) {
		    guiController.draw(guicanvas);
		}
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