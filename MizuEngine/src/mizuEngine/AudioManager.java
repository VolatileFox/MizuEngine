/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: AudioManager                                                    |
| Date: 09/03/2011                                                      |
| Description: This class is responsible for locating, loading, storing,|
| and playing audio files. It is built with a Hashtable to avoid 		|
| loading the same Audio twice. The way this class is designed, makes	|
| it so that only one AudioManager is created.							|
=========================================================================
*/
package mizuEngine;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.applet.Applet;
import java.applet.AudioClip;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Enumeration;


public class AudioManager
{
    //Allows only one AudioManager
    private static AudioManager audioManager;
    //Hashtable keeps all loaded audio paired with a path key.
    private static Hashtable<String, Clip> clipHashtable;
    
    //Returns the audioManager.
    public static AudioManager getAudioManager ()
    {
        //Checks to see if audioManager is null. If so, it creates one.
        if (audioManager == null)
        {
            //Makes a single AudioManager.
            audioManager = new AudioManager();
            //Makes a single Hashtable.
            clipHashtable = new Hashtable<String, Clip>();
        }
        //Returns the audioManager.
        return audioManager;
    }
    
    //Plays once.
    public static void play (String clipPath)
    {
        play(clipPath, false);
    }
    
    //Plays or loops an audio clip located at clipPath.
    public static void play (String clipPath, boolean loop)
    {
        //Hashtable checks to see if Clip has already been loaded.
        if (clipHashtable.containsKey(clipPath))
        {
            //Try to play clip from clipHashtable.
            tryToPlay((Clip)clipHashtable.get(clipPath), loop);
        }
        else
        {
            //Tries to load clip. 
            load(clipPath);
            //Plays audio clip.
            tryToPlay((Clip)clipHashtable.get(clipPath), loop);
        }
    }
    
    //Loads clip located at clipPath.
    public static void load (String clipPath)
    {
        //Hashtable checks to see if Clip has already been loaded.
        if (clipHashtable.containsKey(clipPath))
        {
            //Notifies Console that clip has already been loaded.
            System.out.println("Audio clip at \"" + clipPath + "\" has already been loaded");
        }
        else
        {
            //Gets url from relative class path.
            URL url = audioManager.getClass().getClassLoader().getResource("resources/sounds/" + clipPath);
        	
            //Tries to load clip
            try
            {
                //Gets an AudioInputStream object to stream the file from the url.
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                //Gets a sound clip
                Clip clip = AudioSystem.getClip();
                //Loads audio from AudioInputStream into the clip.
                clip.open(audioInputStream);
                //Adds newly loaded clip to the Hashtable.
                clipHashtable.put(clipPath, clip);
            }
            catch (UnsupportedAudioFileException e)
            {
                 System.out.println("Audio clip at \"" + clipPath + "\" could not be loaded. Make sure it is of proper format.");
            }
            catch (Exception e)
            {
                System.out.println("Audio clip at \"" + clipPath + "\" could not be loaded. Make sure file exists in this directory.");
            }
        }
    }
    
    //Plays the audio clip passed to it.
    private static void tryToPlay (Clip clip, boolean loop)
    {
    	//Attempts to play clip.
    	try
    	{
	        //If the clip is supposed to loop
	        if (loop)
	        {
	            //loop the clip continuously.
	            clip.loop(Clip.LOOP_CONTINUOUSLY);
	        }
	        else
	        {
	            //Otherwise just play clip once.
	            clip.start();
	        }
    	}
    	catch (Exception e)
    	{
    		System.out.println("Audio clip " + clip + " could not be played.");
    	}
    }
    
    //Stops a clip that is currently playing.
    public static void stop (String clipPath)
    {
        //Hashtable checks to see if Clip has already been loaded.
        if (clipHashtable.containsKey(clipPath))
        {
        	//Stop the clip from playing
            ((Clip)clipHashtable.get(clipPath)).stop();
            //and rewind it to the beginning.
            ((Clip)clipHashtable.get(clipPath)).setFramePosition(0);
        }
    }
    
    //Pauses a clip that is currently playing.
    public static void pause (String clipPath)
    {
        //If the Audio clip is currently playing
        if (((Clip)clipHashtable.get(clipPath)).isRunning())
        {
            //stop the clip
            ((Clip)clipHashtable.get(clipPath)).stop();
        }
    }
    
    //Checks if selected clip is playing.
    public static boolean isPlaying (String clipPath)
    {
        //If the Audio clip is currently playing
        return ((Clip)clipHashtable.get(clipPath)).isRunning();
    }
    
    //Being tested for playing large .wav files.
    public static void playLarge (String clipPath)
    {
        try 
        {
            AudioClip clip = Applet.newAudioClip(new File(clipPath).toURI().toURL());
            clip.loop();
        }
        catch (Exception e) {}
    }
    
    //Stops all audio clips currently playing.
    public static void stopAll ()
    {
        //Creates a list of all the keys in clipHashtable
        Enumeration<String> keyList = clipHashtable.keys();
     
        //Iterates through clipHashtable's keys
        while(keyList.hasMoreElements())
        {
            //and runs the stop() method of the object corresponding to that key.
            stop((String)keyList.nextElement());
        }
    }
    
    //Removes audio clip from clipHashtable.
    public static void unload (String clipPath)
    {
        clipHashtable.remove(clipPath);
    }
    
    //Returns clipPath's extension.
    //This will be used later when support for file types other
    //then .wav are added.
    @SuppressWarnings("unused")
	private static String getExtension (String clipPath)
    {
        return clipPath.substring(clipPath.lastIndexOf('.') + 1);
    }
}