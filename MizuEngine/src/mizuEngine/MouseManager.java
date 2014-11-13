/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: MouseManager                                                    |
| Date: 12/08/2011                                                      |
| Description: This class is responsible for listening for mouse input. |
| Originally these functions were a part of a larger InputMnagager      |
| class which included a KeyboardListener. I separated it into two      |
| different classes to make things neater and easier to read.           |
| This class will update the current mouse location and perform action  |
| events unless it has been turned off.                                 |
|																		|
| Notes: Mouselook does not work using a Synergy based mouse. Jailed	|
| cursor still has issues. I still need to find a non buggy way to keep |
| cursor bounded.														|
=========================================================================
*/
package mizuEngine;

import java.awt.Robot;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;


public class MouseManager implements MouseListener, MouseWheelListener, MouseMotionListener
{
    //Allows only one mouseManager.
    private static MouseManager mouseManager;
    
    //Special cursors.
    private static Cursor transparentCursor;
    private static Cursor customCursor;
    
    //jailedCursor pixel buffer.
    @SuppressWarnings("unused")
	private final int PIXEL_BUFFER = 30;
    
    //True if cursor outside JFrame.
    private static boolean outsideFrame;
    //Booleans for mouse clicker states.
    private static boolean clickerClicked;
    private static boolean clickerHeld;
    //If true, cursor locked within frame.
    private static boolean jailedCursor;
    //If true, mouse look becomes active.
    private static boolean mouseLook;
    //True if mouse is being dragged.
    private static boolean dragging;
    //True if cursor is transparent.
    private static boolean hidden;
    
    //Mouse coordinates in Point form.
    private static Point coordinates;
    //Mouse deltas in Point form.
    private static Point deltas;
    //Point used for centering.
    private static Point center;
    //Robot used for recentering.
    private static Robot robot;
    
    //MouseManager constructor
    private MouseManager ()
    {
        //Mouse states false on start.
        clickerClicked = false;
        outsideFrame = false;
        clickerHeld = false;

        //jailedCursor inactive by default.
        jailedCursor = false;
        //mouseLook inactive by default.
        mouseLook = false;
        //Dragging false.
        dragging = false;
        //Cursor visible by default.
        hidden = false;
        
        //Creates a blank image to be used as blank cursor.
        BufferedImage blankImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        //Instantiates the transparent cursor.
        transparentCursor = makeCursor(blankImage);
        
        //Instantiates Points
        coordinates = new Point();
        deltas = new Point();
        center = new Point();
        //Instantiates robot
        try
        {
            robot = new Robot();
        }
        catch (Exception e)
        {
            System.out.println("Robot was not successfully instantiated");
            MizuEngine.getMizuEngine().shutDown();
        }
    }
    
    //Returns the mouseManager.
    public static MouseManager getMouseManager ()
    {
        //Checks to see if mouseManager is null. If so, it creates one.
        if (mouseManager == null)
        {
            //Makes a single MouseManager.
            mouseManager = new MouseManager();
        }
        //Returns the mouseManager.
        return mouseManager;
    }
    
    //Between frame maintenance.
    protected static void update ()
    {
    	//Reset clicked boolean.
        clickerClicked = false;
        //Update mouseLook deltas.
        if (deltas.x > 0)
            deltas.x -= 1;
        if (deltas.x < 0)
            deltas.x += 1;
        if (deltas.y > 0)
            deltas.y -= 1;
        if (deltas.y < 0)
            deltas.y += 1;
    }
    
    //Event Listeners for mouse motion.
    public void mouseMoved (MouseEvent e)
    {
        //Recenters mouse if mouseLook true.
        if (mouseLook)
        {
            //Sets deltas and recenter mouse.
            deltas.x = (coordinates.x - e.getX());
            deltas.y = (coordinates.y - e.getY());
            recenterMouse();
        }
        
        //Updates coordinates.
        coordinates.x = e.getX();
        coordinates.y = e.getY();
        
        
    }
    public void mouseDragged (MouseEvent e)
    {
        //Cursor is being dragged
        dragging = true;
        //Updates coordinates.
        mouseMoved(e);
    }
    public void mouseWheelMoved (MouseWheelEvent e)
    {
        //Scrolling actions.
    }
    
    //Listener for mouse clicker depression.
    public void mousePressed (MouseEvent e)
    {
        clickerHeld = true;
        //clickerClicked = true;
    }
    
    //Listener for mouse clicker release.
    public void mouseReleased (MouseEvent e)
    {
        //Clicker is not longer held.
        clickerHeld = false;
        //Cursor is not being dragged.
        dragging = false;
        //Clicker completed a click.
        clickerClicked = true;
    }
    
    //Listener for mouse clicked.
    public void mouseClicked (MouseEvent e)
    {
        
    }
    
    //Listener for mouse entering window.
    public void mouseEntered (MouseEvent e)
    {
        refreshCenterPoint();
        outsideFrame = false;
    }
    
    //Listener for mouse exiting window.
    public void mouseExited (MouseEvent e)
    {
    	/*
    	//TODO Lock mouse within frame.
    	// Still buggy, if cursor in the corner,
    	// the cursor will jump to the X edge.
    	// Could solve this by checking which edge its closer to.
    	// Helper method that returns distance from X and Y bounds.
        if (jailedCursor)
        {
        	//Move Mouse inside x bounds of frame.
        	if (coordinates.x <= PIXEL_BUFFER)   		
        	{
        		moveMouse(0, e.getY());
        	}
        	else if (coordinates.x >= (MizuEngine.getMizuEngine().getWidth() - PIXEL_BUFFER))
        	{
        		moveMouse(MizuEngine.getMizuEngine().getWidth() - 1, e.getY());
        	}
        	//Move Mouse inside y bounds of frame.
        	else if (coordinates.y <= PIXEL_BUFFER)   		
        	{
        		moveMouse(e.getX(), 0);
        	}
        	else if (coordinates.y >= (MizuEngine.getMizuEngine().getHeight() - PIXEL_BUFFER))
        	{
        		moveMouse(e.getX(), MizuEngine.getMizuEngine().getHeight() - 1);
        	}
        	
        	return;
        }
        */
    	
        outsideFrame = true;
    }
    
    //Getters.
    public static Point getCoordinates ()
    {
        return coordinates;
    }
    public static Point getDeltas ()
    {
        return deltas;
    }
    public static boolean getOutsideFrame ()
    {
        return outsideFrame;
    }
    public static boolean getClickerClicked ()
    {
        return clickerClicked;
    }
    public static boolean getClickerHeld ()
    {
        return clickerHeld;
    }
    public static boolean getJailedCursor ()
    {
        return jailedCursor;
    }
    public static boolean getDragging ()
    {
        return dragging;
    }
    public static boolean getMouseLook ()
    {
        return mouseLook;
    }
    public static boolean getHidden ()
    {
    	return hidden;
    }
    
    //Recenters cursor.
    private static void recenterMouse ()
    {
        robot.mouseMove(center.x, center.y);
    }
    
    //Moves cursor.
    public static void moveMouse (int x, int y)
    {
    	//Gets Point indicating the location of MizuENgine on the screen.
        Point canvasLocation = DisplayManager.getFrame().getLocationOnScreen();
        //Uses robot to move cursor relative to that point.
        robot.mouseMove(canvasLocation.x + x, canvasLocation.y + y);
    }
    
    //Gets Point center of JFrame.
    private static void refreshCenterPoint ()
    {
    	//Finds the location of the Panel within desktop.
        Point canvasLocation = MizuEngine.getMizuEngine().getLocationOnScreen();
        //Uses that to find the center location of the MizuEngine.
        center.x = canvasLocation.x + (MizuEngine.getMizuEngine().getWidth() / 2);
        center.y = canvasLocation.y + (MizuEngine.getMizuEngine().getHeight() / 2);
    }
    
    //Reverts cursor back to normal.
    public static void resetCursor ()
    {
        //Sets original cursor as current cursor.
        MizuEngine.getMizuEngine().setCursor(Cursor.getDefaultCursor());
        //Resets hidden.
        hidden = false;
    }
    
    //Show/Hide cursor.
    public static void hideCursor ()
    {
        //Sets transparentCursor as the current cursor.
        MizuEngine.getMizuEngine().setCursor(transparentCursor);
        //Resets hidden.
        hidden = true;
    }
    
    //Change cursor image to custom one.
    public static void setCustomCursor (String path)
    {
        //Loads image.
        BufferedImage image = (BufferedImage)ImageManager.loadImage(path);
        //Makes cursor from loaded image.
        customCursor = makeCursor(image);
        //Sets custom cursor as current cursor.
        MizuEngine.getMizuEngine().setCursor(customCursor);
    }
    
    //Makes a cursor with given image.
    private static Cursor makeCursor (BufferedImage img)
    {
        return Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "Custom Cursor");
    }
    
    //Turns mouselook on or off.
    public static void setMouseLook (boolean b)
    {
        mouseLook = b;
    }
    
    //Turns jailedCursor on or off.
    public static void setJailedCursor (boolean b)
    {
        jailedCursor = b;
    }
    
    //Resets mouseManager.
    protected static void reset ()
    {
        //Resets mouse states to all false.
        clickerClicked = false;
        clickerHeld = false;
        //Resets Points used for mouse look.
        coordinates = new Point(0, 0);
        deltas = new Point(0, 0);
        refreshCenterPoint();
        //Default cursor.
        resetCursor();
    }
}