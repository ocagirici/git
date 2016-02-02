package environment;

import java.util.Random;

public class Plane 
{
	double l;
	double w;
	
	public Plane()
	{
		l = 1;
		w = 1;
	}
	
	public Plane(double... lw)
	{
		if(lw.length == 2)
		{
			this.l = lw[0];
			this.w = lw[1];
		}
		if(lw.length == 1)
		{
			this.l = lw[0];
			this.w = lw[0];
		}
		if(lw.length >= 2)
			throw new IllegalArgumentException("2 dimensions!");
	}
	
	public double[][] createRandom(int n)
	{
		Random r = new Random();
		double[][] points = new double[n][2];
		for(int i=0; i<n; i++)
		{
			points[i][0] = r.nextDouble()*l;
			points[i][1] = r.nextDouble()*w;
		}
		return points;
	}
}
