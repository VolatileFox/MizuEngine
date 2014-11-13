package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Runnable
{
	//Connection properties.
	private int patronIDNumber;
	private String username;
	private boolean alive;
	
	//Network things.
	Socket socket;
	Lobby lobby;
	
	//Object streams.
	ObjectOutputStream objectStreamOut;
	ObjectInputStream objectStreamIn;
	
	//Constructor
	public Connection (Lobby lby, Socket skt, int id) throws IOException
	{
		//Initialize properties.
		patronIDNumber = id;
		
		//Initialize network things.
		socket = skt;
		lobby = lby;
		
		//Initialize streams.
		objectStreamOut = new ObjectOutputStream(socket.getOutputStream());
		System.out.println("Object output stream succesfully setup.");
		objectStreamOut.flush();
		System.out.println("Connection stream out flushed.");
		objectStreamIn = new ObjectInputStream(socket.getInputStream());
		System.out.println("Object input stream succesfully setup.");
		
		//Send patron ID number.
		objectStreamOut.writeObject(new Packet<Integer>(4, patronIDNumber, -1));
		objectStreamOut.flush();
		
		//Receive patron username.
		Packet<?> firstPacket;
		try
		{
			//Attempt to read in Packet.
			firstPacket = (Packet<?>)objectStreamIn.readObject();
			
			//Retrieve username from Packet;
			username = (String)firstPacket.DATA;
			//Print out username recieved from client.
			System.out.println("First Packet recieved from: " + username);
		}
		//Object type not recognized.
		catch (ClassNotFoundException e)
		{
			//Notify the console.
			System.out.println("Server was unable to retrieve username.");
			//Assign defaut_username as patron username.
			username = "default_username";
		}
		
		//Connection is alive.
		alive = true;
		
		//Start this Patron.
		new Thread(this).start();
	}
	
	//Send a Packet to Patron.
	public void sendPacket (Packet<?> packet) throws IOException
	{
		//Attempt to send Packet.
		objectStreamOut.writeObject(packet);
		objectStreamOut.flush();
	}
	
	//Returns associated ID number.
	public int getpatronIDNumber()
	{
		return patronIDNumber;
	}
	
	//Returns associated username.
	public String getUsername()
	{
		return username;
	}
	
	//Returns livelihood of connection.
	public boolean isAlive ()
	{
		return alive;
	}
	
	//Closes the connection.
	public void close () throws IOException
	{
		//Connection is dead.
		kill();
		
		//Close streams.
		objectStreamOut.flush();
		objectStreamOut.close();
		objectStreamIn.close();
		//Close the socket.
		socket.close();
	}
	
	//Kills the thread.
	public void kill ()
	{
		//Patron is dead.
		alive = false;
	}
	
	//Starts the Connection.
	public void run()
	{
		//Run till killed.
		while (alive)
		{
			//Attempt to queue incomingPackets.
			try
			{
				//Read in new Packet.
				//Packet<?> newPacket = (Packet<?>)objectStreamIn.readObject();
				Object newPacket = (objectStreamIn.readObject());
				
				System.out.println("Recieved this packet " + newPacket);
				
				//If message is a disconnect.
				//if (newPacket.TYPE == Packet.CLOSING_MESSAGE)
				{
					//Kill thread.
					this.kill();
				}
				
				//Enqueue Packet into messages.
				//lobby.packageArrived(newPacket);
			}
			//Object type not recognized.
			catch (ClassNotFoundException e)
			{
				System.out.println("Recieved unknown Object.");
			}
			//Object was not received.
			catch (IOException e)
			{
				this.kill();
			}
		}	
	}
}
