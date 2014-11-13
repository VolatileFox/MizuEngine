/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: Scene                                                           |
| Date: 09/02/2010                                                      |
| Description: This is the abstract class. It allows you to develop     |
| game mechanics such as menus, splash screens, and gameplay that       |
| extends this class.                                                   |
=========================================================================
*/
package mizuEngine;

import java.util.Queue;
import java.awt.Graphics2D;

//Abstract class Scene.
public abstract class Scene
{
    //Scene loads any resources it needs.
	protected abstract void load ();
    //Scene initializes
	protected abstract void init ();
    //Scene starts running.
    protected abstract void start ();
    //Scene finishes itself and then ends.
    protected abstract void stop ();
    //Scene updates using elapsedTime
    protected abstract void update (long elapsedTime);
    //Scene renders to Graphics2D object.
    protected abstract void render (Graphics2D g2d);
    //Scene debug renders to Graphics2D object.
    protected abstract void renderDebug (Queue<String> debugQ);
}
