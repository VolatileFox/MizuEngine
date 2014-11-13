/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: SceneManager                                                    |
| Date: 09/02/2011                                                      |
| Description: This class handles the update and render methods of all  |
| game scenes. It also handles the switching between different scenes.  |
=========================================================================
 */
package mizuEngine;

import java.util.Queue;
import java.awt.Graphics2D;

public class SceneManager
{

	//SceneManager variables
	private static SceneManager sceneManager;
	private static Scene currentScene;

	//ImageManager constructor
	private SceneManager ()
	{
		//Empty
	}

	//Creates a new SceneManager with MizuEngine.
	public static SceneManager getSceneManager ()
	{
		//Checks to see if sceneManager is null. If so, it creates one.
		if (sceneManager == null)
		{
			sceneManager = new SceneManager();
		}
		//Returns the sceneManager.
		return sceneManager;
	}

	//Updates current Scene
	protected static void update (long elapsedTime)
	{
		currentScene.update(elapsedTime);
	}

	//Renders current Scene
	protected static void render (Graphics2D g2d)
	{
		currentScene.render(g2d);
	}

	//Renders current Scene
	protected static void renderDebug (Queue<String> debugQ)
	{
		currentScene.renderDebug(debugQ);
	}


	//Allows anyone to change current scene.
	public static void changeScene (Scene newScene)
	{
		changeScene(newScene, true);
	}
	
	//Pass false if you want to reinstate a Scene that has already been initialized.
	public static void changeScene (Scene newScene, boolean initialize)
	{
		//If the there is a currentScene, it is stopped.
		if (SceneManager.currentScene != null)
		{
			SceneManager.currentScene.stop();
		}

		//Reset KeyboardManager and MouseManager.
		MouseManager.reset();
		KeyboardManager.reset();
		
		if (initialize)
		{
			//newScene is loaded.
			newScene.load();
			newScene.init();
			newScene.start();
		}
		
		//newScene becomes current Scene.
		SceneManager.currentScene = newScene;
	}

	//Calls stop method for current Scene.
	protected static void stop ()
	{
		// If currentScene not null.
		if (currentScene != null) currentScene.stop();
	}
}