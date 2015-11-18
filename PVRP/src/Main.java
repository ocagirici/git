import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args)
	{
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("p1.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Integer> tall = new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> header = new ArrayList<>();
		ArrayList<ArrayList<Double>> data = new ArrayList<>();

		int i = 0;
		while(scanner.hasNextLine()){
		   String line = scanner.nextLine();
		   Scanner lineScanner = new Scanner(line);
		   ArrayList<Double> list = new ArrayList<>();
		   while(lineScanner.hasNextDouble())
			   list.add(lineScanner.nextDouble());
		   if(list.size() == 0)
			   System.out.println("Empty line");
		   else if(list.size() < 5)
			   header.add(new ArrayList<Double>(list));
		   else
			   data.add(new ArrayList<Double>(list));
		}
		
		System.out.println(header);
		System.out.println(data);
	}

}
