/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: MizuEngineScene                                                 |
| Date: 09/04/2011                                                      |
| Description: This is the opening scene for running the game engine.   |
=========================================================================
*/
package mizuEngine;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Queue;

public class MizuEngineScene extends Scene
{
    //MizuEngineScene Variables
    private Scene nextScene;
    private Image mizuEngineLogo;
    private int timeDisplayed;
    private final int DISPLAY_TIME = 4;

    public MizuEngineScene (Scene scene)
    {
        //The scene to load next.
        nextScene = scene;
        //How long the Scene has been running.
        timeDisplayed = 0;
    }

    //Scene loads any resources it needs.
    public void load ()
    {
    	//Loads MizuEngine logo.
        mizuEngineLogo = ImageManager.loadImage("mizuEngine/mizuEngineLogo.png");
        //Loads MizuEngine audio.
        AudioManager.load("mizuEngine/mizuPour.wav");
    }
    
    //Scene initializes
    public void init ()
    {
        
    }
    
    //Scene starts running.
    public void start ()
    {
    	//Plays opening audio.
        AudioManager.play("mizuEngine/mizuPour.wav");
    }
    
    //Scene finishes itself and then ends.
    public void stop ()
    {
        //Stops all audio currently playing.
        AudioManager.stopAll(); //("sounds/mizuEngine/mizuPour.wav");
        //Frees Unneeded audio from AudioManager
        AudioManager.unload("sounds/mizuEngine/mizuPour.wav");
        //Frees Unneeded images from ImageManager
        ImageManager.unload("images/mizuEngine/mizuEngineLogo.png");
    }
    
    //Scene updates using elapsedTime
    public void update (long elapsedTime)
    {
        //If time limit reached, skips to next scene.
        timeDisplayed += elapsedTime;
        if ((int)(timeDisplayed / 1000) >= DISPLAY_TIME)
        {
            changeToNextScene();
        }
        
        //If ESC or Enter is pressed, skips to next scene.
        if (KeyboardManager.wasPressed(KeyEvent.VK_ESCAPE) || KeyboardManager.wasPressed(KeyEvent.VK_ENTER))
        {
            changeToNextScene();
        }

		//Check if mouse clicker released, skips to next scene.
		if (MouseManager.getClickerClicked())
		{
			changeToNextScene();
		}
    }
    
    //Scene renders to Graphics2D object.
    public void render (Graphics2D g2d)
    {
    	//Draws logo centered.
        g2d.drawImage(mizuEngineLogo, -(mizuEngineLogo.getWidth(null) - MizuEngine.getMizuEngine().getWidth()) / 2,
				  					  -(mizuEngineLogo.getHeight(null) - MizuEngine.getMizuEngine().getHeight()) / 2, null);
    }
    
    //Debug renders to Graphics2D object.
    public void renderDebug (Queue<String> debugQ)
    {
        //Renders MizuEngineScene debug.
        debugQ.offer("MizuEngineScene debug");
        debugQ.offer("Test: " + 123);
        debugQ.offer("Test2: " + 1234);
    }
    
    //This is where changeScene() calls are made.
    private void changeToNextScene ()
    {
        //If newScene is null shutdown.
        if (nextScene == null)
        {
            //Send shutDown command to the engine.
            MizuEngine.getMizuEngine().shutDown();
        }
        
        //Changes to the next Scene
        SceneManager.changeScene(nextScene);
    }
}