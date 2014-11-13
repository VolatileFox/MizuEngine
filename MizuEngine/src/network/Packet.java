package network;

import java.io.Serializable;

public class Packet <T> implements Serializable
{
	//Serial Version ID.
	private static final long serialVersionUID = 3468451536531473807L;
	
	//Finals to be called externally.
	public static final int CLOSING_MESSAGE = 0;
	public static final int USERNAME = 1;
	public static final int MESSAGE = 2;
	
	//Packet properties.
	public final int SENDERID;
	public final int TYPE;
	public final T DATA;
	
	//Constructor
	public Packet (int type, T data, int senderID)
	{
		SENDERID = senderID;
		TYPE = type;
		DATA = data;
	}
	
	//toString
	public String toString ()
	{
		return ("\nType: " + TYPE +
				"\nDATA: " + DATA + 
				"\nSenderID: " + SENDERID);
	}
}
