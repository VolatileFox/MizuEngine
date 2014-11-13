/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: ImageManager                                                    |
| Date: 09/02/2011                                                      |
| Description: This class is responsibility is finding and loading      |
| images. It may be changed later to read .txt files that list all the  |
| images' paths needed by a particular Scene, then monitor its loading  |
| progress notifying the Scene when it has finished. The way this       |
| class is designed, makes it so that only one ImageManager is created, |
| and allows any other class to access its variables and methods by     |
| making things static.                                                 |
=========================================================================
*/
package mizuEngine;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.GraphicsConfiguration;

import java.net.URL;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public class ImageManager
{  
	//Allows only one AudioManager
	private static ImageManager imageManager;
	
	//Hashtable keeps all loaded images paired with a path key.
	private static Hashtable<String, Image> imageHashtable;
	private static BufferedImage bufferedImage;
	
	//Returns the imageManager.
	public static ImageManager getImageManager ()
	{
		//Checks to see if imageManager is null. If so, it creates one.
		if (imageManager == null)
		{
			//Makes a single ImageManager.
			imageManager = new ImageManager();
			//Makes a single Hashtable.
			imageHashtable = new Hashtable<String, Image>();
		}
		//Returns the imageManager.
		return imageManager;
	}
	
	//Returns image located at imagePath.
	public static Image loadImage(String imagePath)
	{
		//Hashtable checks to see if Image has already been loaded.
		if (imageHashtable.containsKey(imagePath))
		{
			return (Image)imageHashtable.get(imagePath);
		}
		
		//Clears the BufferedImage
		bufferedImage = null;
		
		//Gets location of resource.
		URL url = imageManager.getClass().getClassLoader().getResource("resources/images/" + imagePath);
		
		//Tries to load image.
		try
		{
			//Reads in file to buffereImage.
			bufferedImage = ImageIO.read(url);
		}
		catch (Exception e)
		{
			//Notifies Console that the Image at imagePath could not be loaded.
			System.out.println("Image at \"" + imagePath + "\" could not be loaded.");
			//Creates a blank image to return instead of null.
			//bufferedImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		}
		
		//Converts the image into an accelerated Image.
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(bufferedImage.getWidth(), bufferedImage.getHeight(), Transparency.BITMASK);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		
		//Adds newly loaded image to the Hashtable.
		imageHashtable.put(imagePath, image);
		
		//Returns the image.
		return image;
	}
	
	//Removes Image from imageHashtable
	public static void unload (String imagePath)
	{
		imageHashtable.remove(imagePath);
	}
}