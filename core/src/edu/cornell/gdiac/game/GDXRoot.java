/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter.
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
 package edu.cornell.gdiac.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

import edu.cornell.gdiac.game.asset.*;
import edu.cornell.gdiac.game.asset.AssetLoader;
import edu.cornell.gdiac.util.*;

/**
 * Root class for a LibGDX.
 *
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However,
 * those classes are unique to each platform, while this class is the same across all
 * plaforms. In addition, this functions as the root class all intents and purposes,
 * and you would draw it as a root class in an architecture specification.
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	/** List of all WorldControllers */
	private WorldController[] controllers;
	private WorldController controller;
	private Cutscene cutscene;
	private LevelSelect levelSelect;
	private StoryScene storyScene;

	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {
		manager = new AssetManager();
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}

	/**
	 * Called when the Application is first created.
	 *
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode(canvas,manager,1);
		cutscene = new Cutscene(canvas);
		cutscene.setScreenListener(this);
		levelSelect = new LevelSelect(canvas);
		levelSelect.setScreenListener(this);
		storyScene = new StoryScene(canvas);
		storyScene.setScreenListener(this);

		// Initialize the three game worlds
		//controllers = new WorldController[3];
		//controllers[0] = new WorldController("levels/betaHard.json");
		//controllers[1] = new WorldController("levels/alphaLevel.json");
		//controllers[2] = new WorldController("levels/simple.json");

		//controller = new WorldController("levels/betaHard.json");
		AssetLoader.getInstance().preLoadContent(manager);
		current = 0;
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		//controller.getAssetLoader().unloadContent(manager);
		if(controller != null){
			controller.dispose();
		}


		/*
		for(int i=0; i<controllers.length; i++){
			controllers[i].dispose();
		}
*/

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			AssetLoader.getInstance().loadContent(manager);
			SoundController.getInstance().play("titlemusic", true, 0.75f);
			levelSelect.activate();
			setScreen(levelSelect);
			loading.dispose();
			loading = null;
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
		else if (exitCode == WorldController.CUTSCENE) {
			setScreen(cutscene);
		}
		else if (exitCode >= 200) {
			System.out.println("playing a story scene now!");
			storyScene.setUp(exitCode - 100);
			setScreen(storyScene);
		}
		else if (exitCode == WorldController.GAMEVIEW) {
			setScreen(controller);
			canvas.zoomOut();
		}
		else if (exitCode == WorldController.LEVEL_SELECT) {
			SoundController.getInstance().play("titlemusic", true, 0.75f);
			SoundController.getInstance().stop("levelmusic");
			levelSelect.activate();
			setScreen(levelSelect);
		}
		else if(exitCode == 100){
			controller = new WorldController("levels/Level1.json");
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			controller.setHardReset();
			controller.reset();
			setScreen(controller);
			SoundController.getInstance().stop("titlemusic");
		}
		else if(exitCode == 101){
			controller = new WorldController("levels/Level2.json");
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			controller.setHardReset();
			controller.reset();
			setScreen(controller);
			SoundController.getInstance().stop("titlemusic");
		}
		else if(exitCode == 102){
			controller = new WorldController("levels/Level3.json");
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			controller.setHardReset();
			controller.reset();
			setScreen(controller);
			SoundController.getInstance().stop("titlemusic");
		}

	}

}
