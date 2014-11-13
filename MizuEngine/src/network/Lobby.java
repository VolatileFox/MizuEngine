/*
=========================================================================
| Author: Andrew Hammil                                                 |
| Site: KrystalfoxGames.com                                             |
|                                                                       |
| Name: Lobby		                                                    |
| Date: 09/14/2012                                                      |
| Description: This class provides and interface for network games. It  |
| allows you to create a new server to which clients can connect. There |
| are capacity limits. If a client tries to connect to a full Lobby, or	|
| the Lobby is currently busy, they are notified and then disconnected. |
=========================================================================
 */
package network;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.LinkedList;
import java.util.Queue;

import java.io.IOException;

public class Lobby implements Runnable
{
	//Lobby properties.
	private boolean active;
	private int numberOfPatrons;
	private final int PORT_NUMBER;
	private final int MAX_CAPACITY;
	private boolean acceptingPatrons;
	
	//Lobby Patrons.
	private Connection[] patrons;
	
	//Network things.
	ServerSocket serverSocket;
	
	//Incoming Packets.
	Queue<Packet<?>> incomingPackets = new LinkedList<Packet<?>>();
	
	//Default Constructor.
	public Lobby () throws IOException
	{
		this(1, 6498);
	}
	
	//Standard Constructor.
	public Lobby (int capacity, int port) throws IOException
	{
		//Initialize constants.
		MAX_CAPACITY = capacity;
		PORT_NUMBER = port;
		
		//Initialize properties.
		acceptingPatrons = true;
		numberOfPatrons = 0;
		active = false;
		
		//Initialize Connection Array.
		patrons = new Connection[MAX_CAPACITY];
		
		//Attempt ServerSocket initialization.
		serverSocket = new ServerSocket(PORT_NUMBER, 20);
		
		//Run lobby in a new Thread
		new Thread(this).start();
	}
	
	//Send a Packet to a specific user.
	public void sendPacket (Packet<?> packet, int userID) throws IOException
	{
		//Attempt to send Packet.
		patrons[userID].sendPacket(packet);
	}
	
	//Broadcast a packet on LAN.
	public void broadcast (Packet<?> packet) throws IOException
	{
		//Not sure hot to do this yet.
	}
	
	//Relay a Packet to everyone.
	public void relay (Packet<?> packet) throws IOException
	{
		//Cycle through all Patrons.
		for (Connection patron : patrons)
		{
			//If not null.
			if (patron != null)
			{
				//Send Packet to Patron.
				patron.sendPacket(packet);
			}
		}
	}

	//Dequeues packet from the packet buffer.
	public Packet<?> receivePacket ()
	{
		//Synch on incoming packages.
		synchronized (incomingPackets)
		{
			return incomingPackets.poll();
		}
	}
	
	//Waits for the arrival of a packet.
	public void waitForPacket () throws InterruptedException
	{
		synchronized (incomingPackets)
		{
			wait();
		}
	}
	
	//Returns true if new Packets available.
	public boolean newPacket ()
	{
		return !incomingPackets.isEmpty();
	}

	//Enqueues Packet sent from a connection.
	protected synchronized void packageArrived (Packet<?> packet)
	{
		System.out.println("Trying to enqueue: " + packet);
		
		//Synch on this object.
		synchronized (incomingPackets)
		{
			//Enqueues Packet received.
			incomingPackets.offer(packet);
			//Notify waiting threads.
			notify();
		}

		System.out.println("Successfully enqueued: " + packet);
	}
	
	//Retrieves username and assigns an IDNumber.
	private void welcomePatron (Socket newSocket) throws IOException
	{
		//Assign IDNumber and store connection.
		for (int i = 0; i < MAX_CAPACITY; i++)
		{
			//If slot is empty.
			if (patrons[i] == null || !patrons[i].isAlive())
			{
				//Store Connection and assign IDNumber.
				Connection newConnection  = new Connection(this, newSocket, i);
				patrons[i] = newConnection;
				//Run new connection.
				newConnection.run();
			}
		}
	}
	
	//Returns true if Lobby active.
	public boolean isActive ()
	{
		return active;
	}
	
	//Returns number of connected Patrons.
	public int getNumberOfPatrons ()
	{
		return numberOfPatrons;
	}
	
	//Returns lobby's max capacity.
	public int getMAX_CAPACITY()
	{
		return MAX_CAPACITY;
	}
	
	//Returns port number being used.
	public int getPORT_NUMBER()
	{
		return PORT_NUMBER;
	}

	//Close the Lobby and disconnect Patrons.
	public void close () throws IOException
	{	
		//Cycle through all Patrons.
		for (Connection patron : patrons)
		{
			//If not null.
			if (patron != null)
			{
				//Send closing message.
				patron.sendPacket(new Packet<String>(Packet.CLOSING_MESSAGE, "Server is closing.", -1));
			}
		}

		//Lobby not active.
		active = false;
		
		//Cycle through all Patrons.
		for (Connection patron : patrons)
		{
			//If not null.
			if (patron != null)
			{
				//Disconnect each Patron.
				patron.close();
			}
		}
		
		//Close socket.
		serverSocket.close();
	}
	
	//Starts the Lobby.
	public void run ()
	{
		//Lobby is listening for connections.
		System.out.println("Lobby is listening on " + serverSocket);
		
		//Lobby is active.
		active = true;
		
		//Start accepting Patrons.
		while (active)
		{
			//Attempt connection.
			Socket newSocket;
			try
			{
				//Waiting for a connection
				System.out.println("Waiting for new connections...");
				
				//Accept incoming connection.
				newSocket = serverSocket.accept();
				
				System.out.println("Socket acepted.");
				
				//Lobby received new connection.
				System.out.println("Connection from " + newSocket);
				
				//If lobby is full.
				if (newSocket != null && numberOfPatrons >= MAX_CAPACITY)
				{
					//Create new connection.
					Connection newConnection  = new Connection(this, newSocket, MAX_CAPACITY);
					//Run new connection.
					newConnection.run();
					//Attempt to tell connection lobby is full and disconnect.
					try
					{
						newConnection.sendPacket(new Packet<String>(Packet.MESSAGE, "SERVER: " +
								"Lobby is full, you have been disconnected.", -1));
					}
					//Failed to send Packet.
					catch (IOException e)
					{
						//Beep and display error message.
						System.out.println("Failed to tell patron lobby was full.");
					}
				}
				//If lobby is not accepting Patrons.
				else if (!acceptingPatrons)
				{
					//Create new connection.
					Connection newConnection  = new Connection(this, newSocket, MAX_CAPACITY);
					//Run new connection.
					newConnection.run();
					//Attempt to tell connection lobby is in session and disconnect.
					try
					{
						newConnection.sendPacket(new Packet<String>(Packet.MESSAGE, "SERVER: " +
								"Lobby is in a session, you have been disconnected.", -1));
					}
					//Failed to send Packet.
					catch (IOException e)
					{
						//Beep and display error message.
						System.out.println("Failed to tell patron lobby was in a session.");
					}
				}
				//No problems.
				else
				{
					//Create Connection and add.
					welcomePatron(newSocket);
				}
			}
			//Connection failure.
			catch (IOException e)
			{
				//Lobby failed to accept connection.
				System.out.println("Failed to accept new connection.");
			}
		}
	}
}