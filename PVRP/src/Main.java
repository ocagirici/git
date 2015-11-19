import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
	
	
	
	public static void main(String[] args)
	{
		Instance instance = new Instance("p5.txt",0);
		System.out.println(instance);
	
//		Scanner scanner = null;
//		try {
//			scanner = new Scanner(new File("p1.txt"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ArrayList<ArrayList<Double>> header = new ArrayList<>();
//		ArrayList<ArrayList<Double>> data = new ArrayList<>();
//		ArrayList<Customer> customers = new ArrayList<>();
//		
//		while(scanner.hasNextLine()){
//		   String line = scanner.nextLine();
//		   Scanner lineScanner = new Scanner(line);
//		   ArrayList<Double> list = new ArrayList<>();
//		   while(lineScanner.hasNextDouble())
//			   list.add(lineScanner.nextDouble());
//		   if(list.size() == 0)
//			   System.out.println("Empty line");
//		   else if(list.size() < 8)
//			   header.add(new ArrayList<Double>(list));
//		   else
//			  customers.add(new Customer(list));
//		}
//		
//		for(Customer c : customers)
//			System.out.println(c);
//		int N = data.size();
//		double[][] adj = new double[N][N];
//		for(int i=0; i<N-1; i++)
//			for(int j=i+1; j<N; j++)
//			{
//				double x1 = data.get(i).get(1);
//				double y1 = data.get(i).get(2);
//				double x2 = data.get(j).get(1);
//				double y2 = data.get(j).get(2);
//				double d = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
//				adj[i][j] = d;
//				adj[j][i] = d;
//			}
//		
//		for(int i=0; i<N; i++)
//			adj[i][i] = 0;
//		Double[][] m = new Double[N][N];
//		for(int i=0; i<N; i++)
//		{
//			for(int j=0; j<N; j++)
//			{
//				m[i][j] = adj[i][j];
////				System.out.printf("%.2f ", adj[i][j]);
//			}
////			System.out.println();
//		}
//		
//		
//		double avg = 0;
//		double d[] = new double[N];
//
//		for(int i=0; i<N; i++)
//		{
//			Arrays.sort(m[i]);
//			double sum = 0;
//			for(int j=1; j<6; j++)
//				sum += m[i][j];
//			
//			d[i] = (7*avg)/3 + (sum/5);
//		}
//		System.out.println("avg: " + avg/N);
//		
//		for(int i=0; i<N; i++)
//			System.out.println(d[i]);
		
		
		
		
				
	}

}
