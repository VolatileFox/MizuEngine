package network;

import java.io.IOException;

public class LobbyTester
{
	public static void main (String[] args)
	{
		try
		{
			Lobby lobby = new Lobby(10, 4444);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int x = 1;
		
		while (true)
		{
			//Keep doing something.
			x =+ x;
		}
	}
}
