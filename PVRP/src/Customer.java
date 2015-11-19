import java.util.ArrayList;
import java.util.Collections;

public class Customer
{
	int i,q,f,a,e,l;
	double d;
	int[] list;
	double x,y;
	ArrayList<Neighbor> neighbors = new ArrayList<>();
	boolean timeWindow = false;
	public Customer(ArrayList<Double> data)
	{
		if(data.size() == 7)
		{
			this.i = 0;
			this.x = data.get(1);
			this.y = data.get(2);
			this.d = 0;
			this.q = 0;
			this.f = 0;
			this.a = 0;
		}
		this.i = data.get(0).intValue();
		this.x = data.get(1);
		this.y = data.get(2);
		this.d = data.get(3);
		this.q = data.get(4).intValue();
		this.f = data.get(5).intValue();
		this.a = data.get(6).intValue();
		list = new int[a];
		for(int j=0; j<a; j++)
			list[j] = data.get(7+j).intValue();
		if(data.size() > 8+a)
		{
			timeWindow = true;
			e = data.get(9+a).intValue();
			l = data.get(10+a).intValue();
		}

	}
	
	public void addNeighbor(Customer that)
	{
		neighbors.add(new Neighbor(that.i, distance(that)));
	}
	
	private double distance(Customer that)
	{
		return Math.sqrt((this.x - that.x)*(this.x - that.x) + (this.y - that.y)*(this.y - that.y));
	}
	
	public void avgComputeServiceDuration(int m, int n, int timeLimit)
	{
		double sum = 0;
//		for(int j=1; j<6; j++)
//			sum += m[i][j];
//		
//		d[i] = (7*avg)/3 + (sum/5);
		double avgDist = 0;
		Collections.sort(neighbors);
		for(int i=0; i<5; i++)
			sum+=neighbors.get(i).distance;
		avgDist = sum/5;
		
		double lambda = timeLimit - (avgDist*(n+m))/m;
		System.out.println(lambda);
		d = ((7*lambda)/3);
	}

	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append(String.format("%d %.2f %.2f %.2f %d %d %d",i,x,y,d,q,f,a).toString());
		for(int i=0; i<list.length; i++)
			str.append(list[i] + " ");
		if(timeWindow)
			str.append("%d %d",e,l);
		return str.toString();
	}
	//		i	=	customer number
	//		x	=	x coordinate
	//		y	=	y coordinate
	//		d	=	service duration
	//		q	=	demand
	//		f	=	frequency of visit
	//		a	=	number of possible visit combinations
	//		list	=	list of all possible visit combinations
	//		e	=	beginning of time window (earliest time for start of service), if any
	//		l	=	end of time window (latest time for start of service), if any

}