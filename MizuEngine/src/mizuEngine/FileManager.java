/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: FileManager                                                     |
| Date: 12/09/2011                                                      |
| Description: This class is responsible for the loading and saving of  |
| files. It is useful for loading .txt files containing levels,         |
| highscores, and other text based files.                               |
=========================================================================
 */
package mizuEngine;

//This class is still being developed!

public class FileManager
{
	//Allows only one FileManager.
	private static FileManager fileManager;

	//Returns the fileManager.
	public static FileManager getFileManager ()
	{
		//Checks to see if fileManager is null. If so, it creates one.
		if (fileManager == null)
		{
			//Makes a single FileManager.
			fileManager = new FileManager();
		}
		//Returns the fileManager.
		return fileManager;
	}

	//Between frame maintenance.
	protected static void update ()
	{

	}

	//Resets fileManager.
	protected static void reset ()
	{

	}
}