package coverbylines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Plane 
{
	
	int multiply(Integer[] array, int begin, int end)
	{
		int result = 1;
		for(int i=begin; i<end; i++)
			result*=array[i];
		
		return result;
	}
	
	int sum(Integer[] array, int i, int j)
	{
		return multiply(array, 0, i) + multiply(array, i, j) + multiply(array, j, array.length);
	}
	
	int[] getFactors(Integer[] array, int i, int j)
	{
		return new int[]{multiply(array, 0, i), multiply(array, i, j), multiply(array, j, array.length)};
	}
	int[] primeFactors(int numbers) 
	{
		int n = numbers;
		List<Integer> factors = new ArrayList<Integer>();
		for(int i=2; i <= n; i++)
		{
			while(n % i == 0)
			{
				factors.add(i);
				n /= i;
			}
		}
		if (n > 1) 
		{
			factors.add(n);
		}
		for(int i=factors.size(); i<3; i++)
			factors.add(1);
		
		Integer[] ar = new Integer[factors.size()];
		
		int sum = Integer.MAX_VALUE;
		int[] separator = new int[3];
		factors.toArray(ar);
		for(int i=1; i<ar.length-1; i++)
			for(int j=i+1; j<ar.length; j++)
				if(sum(ar, i, j) < sum)
				{
					sum = sum(ar,i,j);
					separator = new int[]{i,j};
				}
		
		return getFactors(ar, separator[0], separator[1]);
	}
	
	double l;
	double w;
	Random r = new Random();
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
		
		double[][] points = new double[n][2];
		for(int i=0; i<n; i++)
		{
			points[i][0] = r.nextDouble()*l;
			points[i][1] = r.nextDouble()*w;
		}
		return points;
	}
	
	public double[][] createOrthogonal(int numberOfLines, int numberOfPoints)
	{
		double[][] points = new double[numberOfPoints][2];
		for(int i=0; i<numberOfPoints; i++)
		{
			points[i][0] = r.nextDouble()*l;
			points[i][1] = r.nextDouble()*w;
		}
		return points;
	}
	
	public double[][] createTwoLines(int n)
	{
		double[][] points = new double[n][2];
		for(int i=0; i<n/2; i++)
		{
			points[i][0] = 0;
			points[i][1] = r.nextDouble()*w;
		}
		
		for(int i=n/2+1; i<n; i++)
		{
			points[i][0] = r.nextDouble()*w;
			points[i][1] = 0;
		}
		return points;
	}
}
