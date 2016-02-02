package test;

import java.io.IOException;

import entity.WSN;
import environment.Cuboid;
import environment.Plane;

public class WSNGenerator 
{
	public static boolean between(double value, double min, double max)
	{
		return (value >= min) && (value <= max);
	}
	public static void main(String[] args)
	{
		WSN wsn = null;
		for(int i=0; i<100; i++)
		{
			do
			{

				Plane plane = new Plane(1);
				wsn = new WSN(plane.createRandom(100));
			}
			while(!wsn.isConnected());
			try {
				wsn.toFile("Graphs", i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
