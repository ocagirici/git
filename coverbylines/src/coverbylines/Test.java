package coverbylines;

import java.util.Random;

import javax.vecmath.Point2d;

import org.jacop.core.*; 
import org.jacop.constraints.*; 
import org.jacop.floats.constraints.PplusCeqR;
import org.jacop.floats.constraints.PplusQeqR;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatVar;
import org.jacop.floats.search.SmallestDomainFloat;
import org.jacop.floats.search.SplitSelectFloat;
import org.jacop.floats.search.WeightedDegreeFloat;
import org.jacop.search.*; 
 
public class Test { 
 
	
	static Random r = new Random();
    static Test m = new Test(); 
    static double P = 0.2;
    static int yes = 0;
    public static double[] collinear(double a, double b, double c)
    {
    	double aMin = a - a*P;
		double aMax = a + a*P;
		double bMin = b - b*P;
		double bMax = b + b*P;
		double cMin = c - c*P;
		double cMax = c + c*P;
		for(double i = aMin; i <= aMax; i = (double)Math.round((i + 0.01)*100)/100 )
			for(double j = bMin; j <= bMax; j = (double)Math.round((j + 0.01)*100)/100 )
				for(double k = cMin; k <= cMax; k = (double)Math.round((k + 0.01)*100)/100 )
					System.out.println(i + " " + j + " " + k);
		return null;
        
        
    }
 
    public static void main (String[] args) 
    {
//    	collinear(4.21, 6.332, 5.123);
    	Maze m = Maze.createRandomMaze(5, 5);
    	Graph g = new Graph(m.getOrthogonal(2));
    	g.findHyperplanes();
    	g.plotHyperplanes();
    	
    } 
}
