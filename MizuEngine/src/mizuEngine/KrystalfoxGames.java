/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: KrystalFoxGamesScene                                            |
| Date: 09/04/2011                                                      |
| Description: This is the opening scene for running a game developed   |
| KrystalFoxGames. Its purpose is just to give credit.                  |
=========================================================================
*/
package mizuEngine;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Queue;

public class KrystalfoxGames extends Scene
{
    //MizuEngineScene Variables
    private Scene nextScene;
    private Image krystalfoxGamesLogo;
    private int timeDisplayed;
    private final int DISPLAY_TIME = 2;
    
    public KrystalfoxGames (Scene scene)
    {
        //The scene to load next.
        nextScene = scene;
        //How long the Scene has been running.
        timeDisplayed = 0;
    }

    //Scene loads any resources it needs.
    public void load ()
    {
        krystalfoxGamesLogo = ImageManager.loadImage("krystalfoxGames/krystalfoxGamesLogo.png");
        AudioManager.load("krystalfoxGames/krystalfoxGames.wav");
    }
    
    //Scene initializes
    public void init ()
    {
    	
    }
    
    //Scene starts running.
    public void start ()
    {
        MizuEngine.getMizuEngine().setTitle("");
        AudioManager.play("krystalfoxGames/krystalfoxGames.wav");
    }
    
    //Scene finishes itself and then ends.
    public void stop ()
    {
        //Stops all audio currently playing.
        AudioManager.stopAll(); //("sounds/mizuEngine/mizuPour.wav");
        //Frees Unneeded audio from AudioManager
        AudioManager.unload("sounds/krystalFoxGames/krystalfoxGames.wav");
        //Frees Unneeded images from ImageManager
        ImageManager.unload("images/krystalFoxGames/krystalfoxGamesLogo.png");
    }
    
    //Scene updates using elapsedTime
    public void update (long elapsedTime)
    {
    	//Changes scene after DISPLAY_TIME has passed.
        timeDisplayed += elapsedTime;
        if ((int)(timeDisplayed / 1000) == DISPLAY_TIME)
        {
            changeToNextScene();
        }
        
        //If any key is pressed, skips to next scene.
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
        g2d.drawImage(krystalfoxGamesLogo, -(krystalfoxGamesLogo.getWidth(null) - MizuEngine.getMizuEngine().getWidth()) / 2,
        								   -(krystalfoxGamesLogo.getHeight(null) - MizuEngine.getMizuEngine().getHeight()) / 2, null);
    }
    
    //Debug renders to Graphics2D object.
    public void renderDebug (Queue<String> debugQ)
    {
        //Renders KrystalFoxGames debug.
        debugQ.offer("KrystalfoxGames debug");
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