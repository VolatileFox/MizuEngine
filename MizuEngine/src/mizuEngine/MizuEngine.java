/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: MizuEngine                                                      |
| Date: 08/31/2011                                                      |
| Description: This is the core of the game engine. It initializes      |
| the different game managers, runs the gameloop, and closes up         |
| shop when the game has finished running.                              |
=========================================================================
*/
package mizuEngine;

import java.awt.Color;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

import java.util.Queue;
import java.util.LinkedList;

public class MizuEngine extends Canvas
{
    //Serial Version for Canvas.
	private static final long serialVersionUID = 00001337L;

	//Allows only one mizuEngine.
    private static MizuEngine mizuEngine;
    
    //Engine Constants
    private final int CANVAS_WIDTH;
    private final int CANVAS_HEIGHT;
    public final int DEFAULT_SLEEP = 13;
    
    //JFrame title
    private final String ENGINE_TITLE = new String("MizuEngine - VolatileFox 2011");
    
    //Buffer Strategy
    private BufferStrategy bufferStrategy;

    //KrystalfoxGames.com game.
    private boolean krystalfoxGame;
    
    //Engine Variables
    private boolean isRunning;
    private boolean debug;
    private int sleepTime;
    
    //Used for FPS
    private long second;
    private int FPSCounter;
    private int FPS;
    
    //Debug on screen spacing.
    private final int DEBUG_SPACING_X = 20;
    private final int DEBUG_SPACING_Y = 20;
    private final int DEBUG_WIDTH = 170;
    
    //Used for debug background.
    private final Color debugBackgroundColor;
    
    //Graphics settings.
    private boolean antiAliasing;
    
    private MizuEngine (int canvasWidth, int canvasHeight)
    {
    	//Gives the current graphics configuration of the machine being used to the super constructor .
    	super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    	
    	//Initializes width height.
        CANVAS_WIDTH = canvasWidth;
        CANVAS_HEIGHT = canvasHeight;
        
        //Sets MizuEngine's size.
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        
        //Handle rendering ourselves.
        setIgnoreRepaint(true);
        
        //Is a KrystalfoxGames.com game.
        krystalfoxGame = false;
        
        //Debug.
        debugBackgroundColor = new Color(0.1f, 0.1f, 0.1f, 0.5f);
        isRunning = false;
        debug = false;
        sleepTime = DEFAULT_SLEEP;
        
        //FPS.
        second = 0;
        FPSCounter = 0;
        FPS = 0;
        
        //Default graphics settings.
        antiAliasing = true;
        
        //Denies Canvas focus.
        this.setFocusable(false);
    }
    
    //Returns the mizuEngine.
    public static MizuEngine getMizuEngine (int canvasWidth, int canvasHeight)
    {
        //Checks to see if mizuEngine is null. If so, it creates one.
        if (mizuEngine == null)
        {
            //Makes a single MizuEngine.
            mizuEngine = new MizuEngine(canvasWidth, canvasHeight);
        }
        //Returns the mizuEngine.
        return mizuEngine;
    }
    //Returns the mizuEngine.
    public static MizuEngine getMizuEngine ()
    {
        return getMizuEngine(0, 0);
    }
    
    //This method is called to start the engine.
    public void run (Scene initialScene)
    {
    	//Prevents run being called twice.
    	if (!isRunning)
    	{
    		//Scene after MizuEngine Scene.
    		Scene mizuEngineSceneParam = initialScene;
    		
    		//If KrystalfoxGames, add scene.
    		if (krystalfoxGame)
    		{
    			//Creates new instance of KrystalfoxGames Scene.
    			KrystalfoxGames krystalfoxGames = new KrystalfoxGames(initialScene);
    			//mizuEngineSceneParam points to krystalfoxGames.
    			mizuEngineSceneParam = krystalfoxGames;
    		}
			
			//Creates new MizuEngineScene and passes the next Scene to it.
			MizuEngineScene mizuEngineScene = new MizuEngineScene(mizuEngineSceneParam);
    		
    		//Initializes the engine.
    		init(mizuEngineScene);
    		
    		//Starts engine.
        	start();
    	}
    }
    
    //This method initializes resource managers.
    private void init (Scene initialScene)
    {
        //Initializes DsiplayManager and gives it MizuEngine.
        DisplayManager.getDisplayManager();
        DisplayManager.display(this);
        DisplayManager.displayWindowed();
        //Sets MizuEngine title.
        setTitle(ENGINE_TITLE);
        //Creates Buffer Strategy.
        refreshBufferStrategy();
        
        //Initializes ImageManager.
        ImageManager.getImageManager();
        //Initializes AudioManager.
        AudioManager.getAudioManager();
        //Initializes MouseManager.
        MouseManager.getMouseManager();
        //Adds MouseManager to MizuEngine as a MouseListener.
        this.addMouseListener(MouseManager.getMouseManager());
        this.addMouseWheelListener(MouseManager.getMouseManager());
        this.addMouseMotionListener(MouseManager.getMouseManager());
        //Initializes KeyboardManager.
        KeyboardManager.getKeyboardManager();
        //Adds KeyboardManager to frame as a KeyListener.
        DisplayManager.getFrame().addKeyListener(KeyboardManager.getKeyboardManager());
        
        //Initializes sceneManager starts initial Scene.
        SceneManager.getSceneManager();
        SceneManager.changeScene(initialScene);
        
        //Allows gameloop to run.
        isRunning = true;
    }
    
    //This method starts the gameloop.
    private void start ()
    {
    	//The current time updated at the beginning of each update.
    	long currentTime;
    	
        //The last time lastTime was updated.
    	long lastTime = System.currentTimeMillis();
        
        //elapsedTime updates game according to how much time has passed since last update.
        long elapsedTime;
        
        //Gameloop
        while (isRunning)
        {
            //Calculates elapsedTime.
        	currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            
            //Retrieves Graphics2D object from bufferStrategy and clears it.
            Graphics2D graphics2D = (Graphics2D)bufferStrategy.getDrawGraphics();
            clear(graphics2D);

            //Updates.
            update(elapsedTime);

            // If Scene change.
            
            //Renders.
            render(graphics2D);
            
            //Debug.
            if (debug)
            {
                //Updates FPS.
                calculateFPS(elapsedTime);
                //Render debug.
                debugRender(graphics2D);
            }
            
            //Shows the buffer and disposes the graphics.
            flipPage(graphics2D);
            
            //Temporary limiter to free up CPU.
            /* Consider running rendering processes in
             * a separate thread.
             */
            try {Thread.sleep(sleepTime);} 
            catch (InterruptedException ex) {}
        }
    }
    
    //This method tells the SceneManager to update the active scene.
    private void update (long elapsedTime)
    {
        //Updates Function Key toggles.
    	updateToggles();
        
        //Sends update request to sceneManager.
        SceneManager.update(elapsedTime);
        
        //Refreshes the inputManagers.
        KeyboardManager.update();
        MouseManager.update();
    }
    
    //This method tells the SceneManager to render the active scene.
    private void render (Graphics2D g2d)
    {
    	if (antiAliasing)
    	{
    		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	}
    	
        //Sends render request to sceneManager.
        SceneManager.render(g2d);
    }
    
    //This method tells the SceneManager to render debug info.
    private void debugRender (Graphics2D g2d)
    {
    	//Uses a Queue to build a list of debug info.
    	Queue<String> debugQ = new LinkedList<String>();
    	
        //Sends debug render request to current Scene.
        SceneManager.renderDebug(debugQ);
    	
    	//Enqueues MizuEngine's debug.
    	debugQ.offer("");
    	debugQ.offer("MizuEngine debug");
    	debugQ.offer("Mouse Click Held: " + MouseManager.getClickerHeld());
    	debugQ.offer("Mouse Dragging: " + MouseManager.getDragging());
    	debugQ.offer("Mouse Delta X: " + MouseManager.getDeltas().x);
    	debugQ.offer("Mouse Delta Y: " + MouseManager.getDeltas().y);
    	debugQ.offer("MouseHidden: " + MouseManager.getHidden());
    	debugQ.offer("MouseLook: " + MouseManager.getMouseLook());
    	debugQ.offer("JailedCursor: " + MouseManager.getJailedCursor());
    	debugQ.offer("Anti Aliasing: " + antiAliasing);
    	debugQ.offer("Mouse X: " + MouseManager.getCoordinates().x);
    	debugQ.offer("Mouse Y: " + MouseManager.getCoordinates().y);
    	debugQ.offer("FPS: " + FPS);
    	
    	//Renders which keys are currently being held.
        String[] keyNames = KeyboardManager.getKeysPressed();
        for (int j = 0; j < keyNames.length; j++)
        {
            debugQ.offer("Key Pressed: " + keyNames[j]);
        }
    	
    	//Draws a transparent grey debug background.
        g2d.setColor(debugBackgroundColor);
        g2d.fillRect(DEBUG_SPACING_X / 2, 0, DEBUG_WIDTH, CANVAS_HEIGHT);
        
        //Sets color to white.
        g2d.setColor(Color.white);
    	
        //Unloads debugQ and renders to screen.
    	for (int i = 1; debugQ.peek() != null; i++)
    	{
    		g2d.drawString(debugQ.poll(), DEBUG_SPACING_X, DEBUG_SPACING_Y * i);
    	}
    }
    
    //This method saves relevant data and shuts down the program.
    public void shutDown ()
    {
        //Ends the game loops
        isRunning = false;
    	
        //The currentScene runs its stop processes.
        SceneManager.stop();
        
        //The audioManager stops all audio
        AudioManager.stopAll();
        
        //Resets KeyboardManager.
        KeyboardManager.reset();
        
        //Resets MouseManager.
        MouseManager.reset();
        
        //Resets FileManager.
        FileManager.reset();
        
        //Resets DisplayManager.
        DisplayManager.reset();
        
        //Nullifies mizuEngine.
        mizuEngine = null;
        
        //Allows Managers to finish up.
        try {Thread.sleep(100);} 
        catch (InterruptedException ex) {}
        
        System.exit(0);
    }
    
    //This method pageFlips the BufferStrategy
    private void flipPage (Graphics2D g2d)
    {
        //Disposes of Graphics2D object.
        g2d.dispose();
        //Show if contents not lost.
        if (!bufferStrategy.contentsLost())
        	bufferStrategy.show();
        //Otherwise refresh.
        else
        	refreshBufferStrategy();
    }
    
    //This method clears the graphics2D
    private void clear (Graphics2D g2d)
    {
        //Sets draw color to black and clears screen with it.
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }
    
    //This method calculatesFPS.
    private void calculateFPS (long elapsedTime)
    {
        //Adds one to FPS
        FPSCounter++;
        second += elapsedTime;
        //Calculates FPS
        if ((int)(second / 1000) >= 1)
        {
            FPS = FPSCounter;
            FPSCounter = 0;
            second -= 1000;
        }
    }
    
    //Updates Function keys.
    private void updateToggles ()
    {
    	//F1 toggles debug rendering.
        if (KeyboardManager.wasPressed(KeyEvent.VK_F1))
        {
        	if (debug)
        	{
        		setDebug(false);
        	}
        	else
        	{
        		setDebug(true);
        	}
        }
        //F2 toggles hidden cursor.
        if (KeyboardManager.wasPressed(KeyEvent.VK_F2))
        {
        	if (MouseManager.getHidden())
        	{
        		MouseManager.resetCursor();
        	}
        	else
        	{
        		MouseManager.hideCursor();
        	}
        }
        //F3 toggles mouse look.
        if (KeyboardManager.wasPressed(KeyEvent.VK_F3))
        {
        	if (MouseManager.getMouseLook())
        	{
        		MouseManager.setMouseLook(false);
        	}
        	else
        	{
                MouseManager.setMouseLook(true);
        	}
        }
        //F4 toggles jailed cursor.
        if (KeyboardManager.wasPressed(KeyEvent.VK_F4))
        {
        	if (MouseManager.getJailedCursor())
        	{
        		MouseManager.setJailedCursor(false);
        	}
        	else
        	{
        		MouseManager.setJailedCursor(true);
        	}
        }
        //F5 toggles full screen mode.
        if (KeyboardManager.wasPressed(KeyEvent.VK_F5))
        {
        	if (DisplayManager.getFullScreen())
        	{
        		DisplayManager.displayWindowed();
        	}
        	else
        	{
        		DisplayManager.displayFullScreen();
        	}
        }
        //F6 toggles anti aliasing.
        if (KeyboardManager.wasPressed(KeyEvent.VK_F6))
        {
        	if (antiAliasing)
        	{
        		setAntiAliasing(false);
        	}
        	else
        	{
        		setAntiAliasing(true);
        	}
        }
    }	
    
    //This method changes the JFrame title.
    public void setTitle (String title)
    {
    	DisplayManager.setTitle(title);
    }
    //Sets debug.
    public void setDebug (boolean b)
    {
        debug = b;
    }
    //Sets sleepTime.
    public void setSleep (int milli)
    {
        sleepTime = milli;
    }
    //Sets anti aliasing.
    public void setAntiAliasing (boolean b)
    {
    	antiAliasing = b;
    }
    //Sets krystalfoxGame.
    public void setKrystalfoxGame (boolean b)
    {
    	krystalfoxGame = b;
    }
    
    protected void refreshBufferStrategy ()
    {
        //Creates Buffer Strategy.
        createBufferStrategy(2);
        bufferStrategy = this.getBufferStrategy();
    }
    
    //This is the main method.
    public static void main (String[] args)
    {
        //Creates new MizuEngine.
        MizuEngine.getMizuEngine(800, 600);
        //Tells the engine to start with the mizuEngineScene.
        MizuEngine.getMizuEngine().run(null);
    }
}