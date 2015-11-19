import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Instance {
	int id;
	int type, m, n, t;
	int[] D;
	int[] Q;
	Customer[] customer;
	public Instance(String instance, int id)
	{
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(instance));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scanner lineScanner = new Scanner(scanner.nextLine());
		int[] header = new int[4];
		for(int i=0; i<4; i++)
			header[i] = lineScanner.nextInt();
		type = header[0];
		m = header[1];
		n = header[2];
		t = header[3];
		D = new int[t];
		Q = new int[t];
		customer = new Customer[n];
		for(int i=0; i<t; i++)
		{
			lineScanner = new Scanner(scanner.nextLine());
			D[i] = lineScanner.nextInt();
			Q[i] = lineScanner.nextInt();
		}

		ArrayList<Customer> cList = new ArrayList<>();
	
		for(int i=0; i<n; i++)
		{
			ArrayList<Double> list = new ArrayList<>();
			lineScanner = new Scanner(scanner.nextLine());
			while(lineScanner.hasNextDouble())
				list.add(lineScanner.nextDouble());
			customer[i] = new Customer(list);
		}
	
		
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				customer[i].addNeighbor(customer[j]);
				customer[j].addNeighbor(customer[i]);
			}
		setServiceDurations();
	}
	
	public int timeLimit()
	{
		int min = 0;
		for(int i=1; i<t; i++)
			if(Q[i] < Q[min])
				min = i;
		return Q[min];
	}
	
	public void setServiceDurations()
	{
		int timeLimit = timeLimit();
		double sum = 0;
		for(int i=0; i<n; i++)
			sum += customer[i].q;
		double avgDemand = sum/n;
		for(int i=0; i<n; i++)
			customer[i].avgComputeServiceDuration(m, n, timeLimit);
		
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append(String.format("%d %d %d %d\n", type,m,n,t));
		for(int i=0; i<t; i++)
			str.append(D[i] + " " + Q[i] + "\n");
		for(int i=0; i<n; i++)
			str.append(customer[i] + "\n");
		return str.toString();
	}



}
