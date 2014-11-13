package network;

import java.io.IOException;

public class PatronTester
{
	public static void main (String[] args)
	{
			//Make Patron
			try
			{
				Patron patron = new Patron("localhost", 4444, "defaultUser");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
