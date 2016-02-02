package environment;

import java.util.Random;

public class Cuboid 
{
	double l;
	double w;
	double h;
	public Cuboid(double... lwh)
	{
		if(lwh.length == 1)
		{
			l = lwh[0];
			w = lwh[0];
			h = lwh[0];
		}
		
		if(lwh.length == 2)
		{
			l = lwh[0];
			w = lwh[0];
			h = lwh[1];
		}
		
		if(lwh.length == 3)
		{
			l = lwh[0];
			w = lwh[1];
			h = lwh[2];
		}
		
		throw new IllegalArgumentException("There are three dimensions!");
	}
	
	double[][] inside(int size)
	{
		Random r = new Random();
		double[][] coor = new double[size][3];
		for(int i=0; i<size; i++)
		{
			coor[i][0] = r.nextDouble()*l;
			coor[i][1] = r.nextDouble()*w;
			coor[i][2] = r.nextDouble()*h;
			i++;
		}
		
		return coor;
	}

}
