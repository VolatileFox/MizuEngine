/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: DisplayManager													|
| Date: 04/13/2012                                                      |
| Description: This class is responsible for managing the display. It	|
| takes care of switching between full screen mode and windowed mode.	|
| More functionality may be added later.								|
|																		|
| Notes: Full screen works oddly on some monitors. I have not found a	|
| fix for this yet, neither have I found any reason for the behavior.	|
| On some displays the Canvas draws the images in the wrong spot, yet	|
| on others the picture is larger than the monitor.						|
=========================================================================
 */
package mizuEngine;

import java.awt.Color;
import java.awt.Canvas;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GridBagLayout;
import java.awt.GraphicsDevice;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.GraphicsEnvironment;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class DisplayManager
{
	//Allows only one DisplayManager
	private static DisplayManager displayManager;

	//Graphics device used for switching to full screen.
	private static GraphicsDevice graphicsDevice;

	//True if MizuEngine displayed full screen.
	private static boolean fullScreen;

	//JFrame and JPanel used for MizuEngine.
	private static JFrame frame;
	private static JPanel panel;

	//Current game resolution.
	private final int WIDTH;
	private final int HEIGHT;

	private DisplayManager ()
	{
		//Initializes resolution constants.
		WIDTH = MizuEngine.getMizuEngine().getWidth();
		HEIGHT = MizuEngine.getMizuEngine().getHeight();

		//fullScreen false by default.
		fullScreen = false;

		//Creates new JFrame.
		frame = new JFrame("Default");

		//Creates new JPanel.
		panel = new JPanel();
		//Keeps MizuEngine centered in panel.
		panel.setLayout(new GridBagLayout());
		//Sets up the correct size.
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		//Sets panel background color.
		//panel.setBackground(Color.RED);
		panel.setBackground(Color.BLACK);

		//Gets the default screen device to use when switching to full screen.
		graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}

	//Returns the displayManager.
	public static DisplayManager getDisplayManager ()
	{
		//Checks to see if displayManager is null. If so, it creates one.
		if (displayManager == null)
		{
			//Makes a single DisplayManager.
			displayManager = new DisplayManager();
		}
		//Returns the displayManager.
		return displayManager;
	}

	//Used to displayMizuEngine.
	protected static void display (Canvas canvas)
	{ 
		//Adds canvas to panel.
		panel.add(canvas);
	}

	//Sets MizuEngine display full screen.
	public static void displayFullScreen ()
	{
		//Sets up a new JFrame.
		setupJFrame(true);

		//This takes frame into full screen mode.
		graphicsDevice.setFullScreenWindow(frame);

		//Finds best display mode available for game.
		DisplayMode mode = findCompatibleDisplayMode();

		//Try to change DisplayMode.
		try
		{
			//Sets displayMode to mode. 
			graphicsDevice.setDisplayMode(mode);
		}
		//If going full screen failed.
		catch (IllegalArgumentException e)
		{
			//Switch to windowed.
			displayWindowed();
			//Display error message.
			JOptionPane.showMessageDialog(frame, "Full screen not supported on this system.", "Incompatible Display Mode", JOptionPane.ERROR_MESSAGE);
			//Notify console of failed switch.
			System.out.println("Switching to full screen mode has failed.");
			//Leave this method.
			return;
		}
		
		//Prints display mode out to console.
		System.out.println("Switched DisplayMode");
		System.out.println("Width: " + mode.getWidth());
		System.out.println("Height: " + mode.getHeight());
		System.out.println("Bit Depth: " + mode.getBitDepth());
		System.out.println("Refresh Rate: " + mode.getRefreshRate());

		//Now in full screen.
		fullScreen = true;
	}

	//Adds panel to a new windowed JFrame.
	public static void displayWindowed ()
	{
		//Sets up a new JFrame.
		setupJFrame(false);

		//Moves JFrame into center of screen.
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((dimension.width - MizuEngine.getMizuEngine().getWidth()) / 2,
				(dimension.height - MizuEngine.getMizuEngine().getHeight() - 100) / 2);

		//Makes the JFrame visible.
		frame.setVisible(true);

		//Now in windowed mode.
		fullScreen = false;
	}

	//Used for setting up new JFrame.
	private static void setupJFrame (boolean undecorated)
	{
		//Used to give title to new JFrame.
		String title = frame.getTitle();

		//Release frame's resources.
		frame.dispose();

		//Creates new JFrame..
		frame = new JFrame(title);

		//frame.setBackground(Color.GREEN);
		frame.setBackground(Color.BLACK);
		
		//Tells frame not to paint.
		frame.setIgnoreRepaint(true);
		//Removes window decoration.
		frame.setUndecorated(undecorated);
		//Adds panel to the frame.
		//frame.setContentPane(panel);
		frame.add(panel);

		//Listener for JPanel 'X' for a custom shutdown.
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing (WindowEvent e)
			{
				MizuEngine.getMizuEngine().shutDown();
			}
		});

		//Adds KeyboardManager to the JFrame.
		frame.addKeyListener(KeyboardManager.getKeyboardManager());
		
		//Packs frame and locks the dimensions.
		frame.pack();
		frame.validate();
		frame.setResizable(false);
	}

	//Finds a compatible display mode.
	private static DisplayMode findCompatibleDisplayMode ()
	{
		//Gets array of compatible display modes.
		DisplayMode[] modes = graphicsDevice.getDisplayModes();

		//List of modes with proper resolution.
		List<DisplayMode> compatibleResolutions = new LinkedList<DisplayMode>();
		
		//Best DisplayMode found.
		DisplayMode bestDisplayMode = null;

		//Iterates through DisplayModes.
		for (DisplayMode mode : modes)
		{
			//Searching for a DisplayMode with a perfect resolution match.
			if (mode.getWidth() == MizuEngine.getMizuEngine().getWidth() && mode.getHeight() == MizuEngine.getMizuEngine().getHeight())
				compatibleResolutions.add(mode);
		}

		//If no perfect matches found.
		if (compatibleResolutions.isEmpty())
		{
			//Creates a DisplayMode for comparison.
			DisplayMode secondChoice = null;

			//Search for display mode slightly larger then needed.
			for (DisplayMode mode : modes)
			{
				//Find the smallest one available that is greater than or equal to MizuEngine.
				if (secondChoice == null || mode.getWidth() >= MizuEngine.getMizuEngine().getWidth() && mode.getHeight() >= MizuEngine.getMizuEngine().getHeight() &&
					mode.getWidth() <= secondChoice.getWidth() && mode.getHeight() <= secondChoice.getHeight())
				{
					//Found better second choice.
					secondChoice = mode;
				}
			}

			//Search through all DisplayModes.
			for (DisplayMode mode : modes)
			{
				//Add all DisplayModes with the same resolution as secondChoice.
				if (mode.getWidth() <= secondChoice.getWidth() && mode.getHeight() <= secondChoice.getHeight())
				{
					//Add similar DisplayMode.
					compatibleResolutions.add(mode);
				}
			}
		}

		//Gets an Iterator to search through compatibleResolutions.
		Iterator<DisplayMode> iterator = compatibleResolutions.iterator();
		
		//Search for best color depth.
		while (iterator.hasNext())
		{
			//DisplayMode being compared.
			DisplayMode modeBeingCompared = iterator.next();
			
			//If modeBeingCompared has higher color depth then bestDisplayMode, it becomes bestDisplayMode.
			if (bestDisplayMode == null || modeBeingCompared.getBitDepth() > bestDisplayMode.getBitDepth())
				bestDisplayMode = modeBeingCompared;
		}

		//Returns best DisplayMode.
		return bestDisplayMode;
	}

	//Returns JFrame being used.
	protected static JFrame getFrame ()
	{
		return frame;
	}
	//Returns true if in full screen mode.
	public static boolean getFullScreen ()
	{
		return fullScreen;
	}

	//Sets the title of frame.
	protected static void setTitle (String title)
	{
		frame.setTitle(title);
	}

	//Resets DisplayManager.
	protected static void reset ()
	{
		//Switches to windowed.
		displayWindowed();
		
		//Disposes of frame.
		frame.setVisible(false);
		frame.dispose();
	}
}