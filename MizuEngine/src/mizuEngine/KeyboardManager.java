/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: KeyboardManager                                                 |
| Date: 09/03/2011                                                      |
| Description: This class is responsible for listening for keyboard     |
| input. It stores key states in an array, and also records if any key  |
| is currently being pressed.                                           |
=========================================================================
 */
package mizuEngine;

import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardManager implements KeyListener
{
	//Allows only one keyboardManager.
	private static KeyboardManager mouseManager;

	//Array of booleans for each key.
	private static boolean[] keyStatesHeld;
	private static boolean[] keyStatesPressed;
	//Indicates if any key has been pressed.
	private static boolean keyPressed;

	//KeyboardManager constructor
	private KeyboardManager()
	{
		//New boolean array sets all to false.
		keyStatesHeld = new boolean[256];
		Arrays.fill(keyStatesHeld, false);
		keyStatesPressed = new boolean[256];
		Arrays.fill(keyStatesPressed, false);
		//Default keyPressed state.
		keyPressed = false;
	}

	//Returns the mouseManager.
	public static KeyboardManager getKeyboardManager ()
	{
		//Checks to see if inputManager is null. If so, it creates one.
		if (mouseManager == null)
		{
			//Makes a single KeyboardManager.
			mouseManager = new KeyboardManager();
		}
		//Returns the inputManager.
		return mouseManager;
	}

	//Listener for pressed keys.
	public void keyPressed (KeyEvent e)
	{
		if (e.getKeyCode() >= 0 && e.getKeyCode() <= 256)
		{
			keyStatesHeld[e.getKeyCode()] = true;
			keyStatesPressed[e.getKeyCode()] = true;
			keyPressed = true;
		}
	}

	//Listener for released keys.
	public void keyReleased (KeyEvent e)
	{
		if (e.getKeyCode() >= 0 && e.getKeyCode() <= 256)
		{
			keyStatesHeld[e.getKeyCode()] = false;
			keyStatesPressed[e.getKeyCode()] = false;
		}
	}

	//Listener for typed keys.
	public void keyTyped (KeyEvent e)
	{
		if (e.getKeyCode() >= 0 && e.getKeyCode() <= 256)
		{
			//Do nothing.
		}
	}

	//Returns if specific key is being pressed.
	public static boolean isPressed (int key)
	{
		return keyStatesHeld[key];
	}

	//Returns if specific key was pressed.
	public static boolean wasPressed (int key)
	{
		return keyStatesPressed[key];
	}

	//Returns if any key has been pressed during this frame.
	public static boolean isAnyKeyPressed ()
	{
		return keyPressed;
	}

	//Returns an array of names corrosponding to keys being held.
	protected static String[] getKeysPressed ()
	{
		int numberOfKeys = 0;
		int[] keyCodes = new int[keyStatesHeld.length];

		for (int i = 0; i < keyStatesHeld.length; i++)
		{
			if (keyStatesHeld[i])
			{
				keyCodes[numberOfKeys] = i;
				numberOfKeys++;
			}
		}

		String[] keyNames = new String[numberOfKeys];
		for (int i = 0; i < numberOfKeys; i++)
		{
			keyNames[i] = KeyEvent.getKeyText(keyCodes[i]);
		}

		return keyNames;
	}

	//Between frame maintenance.
	protected static void update ()
	{
		keyPressed = false;
		Arrays.fill(keyStatesPressed, false);
	}

	//Resets keyStates array to all false.
	protected static void reset ()
	{
		Arrays.fill(keyStatesHeld, false);
		Arrays.fill(keyStatesPressed, false);
	}
}