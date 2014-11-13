package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Patron implements Runnable
{
	//Patron properties.
	private String username;
	private int patronIDNumber;
	private boolean alive;
	
	//Network things.
	Socket socket;
	
	//Object streams.
	private ObjectOutputStream objectStreamOut;
	private ObjectInputStream objectStreamIn;
	
	//Packet buffer
	private Queue<Packet<?>> incomingPackets;
	
	//Constructor
	public Patron () throws IOException
	{
		this("localhost", 6498, "default_user");
	}
	
	//Constructor
	public Patron (String host, int port, String name) throws IOException
	{
		//Initialize properties.
		username = name;
		
		//Initialize messages buffer.
		incomingPackets = new LinkedList<Packet<?>>();
		
		//Initialize network things.
		socket = new Socket(InetAddress.getByName(host), port);
		
		//Connected to server.
		System.out.println("Successfully connected to server.");
		
		//Initialize streams.
		objectStreamOut = new ObjectOutputStream(socket.getOutputStream());
		System.out.println("Object output stream succesfully setup.");
		objectStreamOut.flush();
		System.out.println("Connection stream out flushed.");
		objectStreamIn = new ObjectInputStream(socket.getInputStream());
		System.out.println("Object input stream succesfully setup.");
		
		//Send username.
		objectStreamOut.writeObject(new Packet<String>(Packet.USERNAME, username, -1));
		objectStreamOut.flush();
		
		//Receive ID number.
		Packet<?> firstPacket;
		try
		{
			//Attempt to read in Packet.
			firstPacket = (Packet<?>)objectStreamIn.readObject();
			
			//Retrieve ID number from Packet;
			patronIDNumber = (Integer)firstPacket.DATA;
			//Print out username recieved from client.
			System.out.println("Server has assigned user ID: " + patronIDNumber);
		}
		//Object type not recognized.
		catch (ClassNotFoundException e)
		{
			//Notify the console.
			System.out.println("Patron was unable to retrieve ID number.");
			//Assign -1 as patron ID number.
			patronIDNumber = -1;
		}
		
		//Connection is alive.
		alive = true;
		
		//Start this Patron.
		new Thread(this).start();
	}
	
	//Send a Packet to connected Lobby.
	public void sendPacket(Packet<String> packet) throws IOException
	{
		System.out.println("Trying to send packet: " + packet);
		
		//Attempt to send Packet.
		objectStreamOut.writeObject(packet);
		objectStreamOut.flush();
	}
	
	//Dequeues packet from the packet buffer.
	public Packet<?> receivePacket ()
	{
		synchronized (incomingPackets)
		{
			return incomingPackets.poll();
		}
	}
	
	//Returns true if new Packets available.
	public boolean newPacket ()
	{
		return !incomingPackets.isEmpty();
	}
	
	//Enqueue Packet into messages.
	private void packageArrived (Packet<?> newPacket)
	{
		synchronized (incomingPackets)
		{
			//Enqueue Packet into incomingPackets.
			incomingPackets.offer(newPacket);
		}
	}
	
	//Waits for the arrival of a packet.
	public void waitForPacket () throws InterruptedException
	{
		synchronized (incomingPackets)
		{
			System.out.println("Waiting for Packet...");
			wait();
		}
	}
	
	//Returns the livelihood of Patron.
	public boolean isAlive ()
	{
		return alive;
	}
	
	//Retun ip address of the server.
	public String getHost ()
	{
		return socket.getInetAddress().getHostName();
	}
	
	//Returns ID number.
	public int getID ()
	{
		return patronIDNumber;
	}
	
	//Closes the connection.
	public void close () throws IOException
	{
		//Patron is dead.
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
	
	//Starts the Patron.
	public void run()
	{
		//Run till killed.
		while (alive)
		{
			//Attempt to queue incomingPackets.
			try
			{
				//Read in new Packet.
				Packet<?> newPacket = (Packet<?>)(objectStreamIn.readObject());
				
				System.out.println("Recieved packet " + newPacket);
				
				//If message is a disconnect.
				if (newPacket.TYPE == Packet.CLOSING_MESSAGE)
				{
					//Kill this thread.
					this.kill();
				}
				
				//Enqueue Packet into messages.
				packageArrived(newPacket);
			}
			//Object type not recognized.
			catch (ClassNotFoundException e)
			{
				System.out.println("Recieved unknown Object.");
			}
			//Object was not received.
			catch (IOException e)
			{
				System.out.println("Failed to queue packet.");
				this.kill();
			}
		}
	}
}