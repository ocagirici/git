import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.vecmath.Point3d;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.math.plot.Plot3DPanel;

import ProGAL.geom3d.Point;
import ProGAL.geom3d.Vector;


public class Cuboid 
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
	
	int numberOfPlanes;
	int pointsPerPlane;
	public int pointsPerPlane() { return pointsPerPlane; }
	int clusterColor;
	int basedOn;
	double[][] base;
	
	public double H;
	public double W;
	public double L;
	public double[] cubicleLWH;
	double[][][] faces;
	Random random;
	ArrayList<PointCluster> clusters;
	int[] clusterId;
	public Cuboid(int numberOfPlanes, int pointsPerPlane, double... LWH)
	{
		clusters = new ArrayList<PointCluster>();
		clusterColor = 0;
		this.numberOfPlanes = numberOfPlanes;
		this.pointsPerPlane = pointsPerPlane;
		random = new Random();
		if(LWH.length == 1)
		{
			L  = W = H = LWH[0];
		}
		else if(LWH.length == 2)
		{
			L = W = LWH[0];
			H = LWH[1];
		}
		else if(LWH.length == 3)
		{
			L = LWH[0];
			W = LWH[1];
			H = LWH[2];
		}
		base = new double[][]{{0,0,0},{L,0,0},{0,W,0}}; // 0 4 2
		faces = new double[][][]
				{
				{{0,0,0},{L,0,0},{0,W,0}}, //0 4 2
				{{0,0,0},{L,0,0},{0,0,H}}, //0 4 1
				{{0,0,0},{0,W,0},{0,0,H}}, //0 2 1
				{{L,0,0},{L,W,0},{L,0,H}}, // 4 6 5
				{{0,0,H},{L,0,H},{0,W,H}}, // 1 5 3
				{{0,W,H},{L,W,H},{0,W,0}}  // 3 7 2
				};
	}
	
	double[][] makeMatrix(double[] p1, double[] p2, double[] p3)
	{
	    double[] v1 = difference(p2,p1);
	    double[] v2 = difference(p3,p1);
	    double[] v3 = cross(v1, v2);
	    double[][] M = { { v1[0], v2[0], v3[0], p1[0] },
	                     { v1[1], v2[1], v3[1], p1[1] },
	                     { v1[2], v2[2], v3[2], p1[2] },
	                     {   0.0,   0.0,   0.0,   1.0 } };
	    return M;
	}

	private double[] cross(double[] v, double[] w) 
	{
		double x = (v[1]*w[2]) - (v[2]*w[1]);
	    double y = (v[2]*w[0]) - (v[0]*w[2]);
	    double z = (v[0]*w[1]) - (v[1]*w[0]);
	    return new double[]{x,y,z};
	}
	private double[] difference(double[] p2, double[] p1) 
	{
		
		return new double[]{p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]};
	}

	double[][] createTransform(double[] P, double[] Q, double[] R)
	{
	    RealMatrix c = new Array2DRowRealMatrix(makeMatrix(base[0], base[1], base[2]));
	    RealMatrix t = new Array2DRowRealMatrix(makeMatrix(P,Q,R));
	    System.out.println(t.multiply(MatrixUtils.inverse(c)));
	    return t.multiply(MatrixUtils.inverse(c)).getData();
	}
	
	

	double[][] transformPoints(double[][] points, double[][] matrix)
	{
	    double[][] tfd = new double[points.length][];
	    for (int i = 0; i < points.length; ++i)
	    {
	        tfd[i] = multiply(matrix, points[i]);
	    }
	    return tfd;
	}

	double[] multiply(double[][] matrix, double[] point)
	{
	     double[] temp = new double[4];
	     for (int i = 0; i < 3; ++i) temp[i] = point[i];
	     temp[3] = 1.0;

	     double[] res = new double[3];
	     for (int i = 0; i < 3; ++i)
	     {
	         for (int j = 0; j < 4; ++j)
	         {
	             res[i] += temp[j] * matrix[i][j];
	         }
	     }

	     return res;
	}
	
	private double[][] createPointsInside(int numberOfPoints)
	{
		double[][] points = new double[numberOfPoints][3];
		for(int i=0; i<numberOfPoints; i++)
			points[i] = new double[]{random.nextDouble()*L, random.nextDouble()*W, random.nextDouble()*H};
		return points;
	}
	
	public double[][] getPointsOnFaces(int numberOfPoints)
	{
		Random rnd = new Random();
		int[] ar = new int[6];
		for(int i=0; i<6; i++)
			ar[i] = i; 
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }

	    double[][] points = new double[numberOfPoints][3];
	    int j = ar[0];
		for(int i=0; i<numberOfPoints; i++)
		{
			
			points[i] = new double[]{random.nextDouble()*L, random.nextDouble()*W, 0};
//			points[i] = createTransform(faces[j][0], faces[j][1], faces[j][2]);
//			points[i] = new Transformation(new Point[]{new Point(base[0]), new Point(base[1]), new Point(base[2])}, new Point[]{new Point(faces[j][0]), new Point(faces[j][1]), new Point(faces[j][2])}).applyTo(points[i]);
		}
		points = transformPoints(points, createTransform(faces[j][0], faces[j][1], faces[j][2]));

		

		return points;
	}
	private boolean inside(double[] point)
	{
		if(point[0] < 0) return false;
		if(point[0] > L) return false;
		if(point[1] < 0) return false;
		if(point[1] > W) return false;
		if(point[2] < 0) return false;
		if(point[2] > H) return false;
		return true;
	}
	
	private double[] generateType1()
	{
		Random random = new Random();
		double a = 0;
		double b = 0;
		double c = 0;
		double d = 0;
		double rangeMin;
		double rangeMax;
		rangeMin = (-1)*minLength();
		rangeMax = minLength();
		d = rangeMin + (rangeMax - rangeMin) * random.nextFloat();
		basedOn = random.nextInt(3); 
		switch(basedOn)
		{
		case 0:
		{
			a = 1;
			rangeMin = ((-1)*d)/W;
			rangeMax = (W - d)/W;
			b = rangeMin + (rangeMax - rangeMin) * random.nextFloat();
			rangeMin = ((-1)*d)/H;
			rangeMax = (H - d)/H;
			c = rangeMin + (rangeMax - rangeMin) * random.nextFloat();

			break;
		}
		case 1:
		{
			b = 1;
			rangeMin = ((-1)*d)/L;
			rangeMax = (L - d)/L;
			a = rangeMin + (rangeMax - rangeMin) * random.nextFloat();
			rangeMin = ((-1)*d)/H;
			rangeMax = (H - d)/H;
			c = rangeMin + (rangeMax - rangeMin) * random.nextFloat();

			break;
		}
		case 2:
		{
			c = 1;
			rangeMin = ((-1)*d)/L;
			rangeMax = (L - d)/L;
			a = rangeMin + (rangeMax - rangeMin) * random.nextFloat();
			rangeMin = ((-1)*d)/W;
			rangeMax = (W - d)/W;
			b = rangeMin + (rangeMax - rangeMin) * random.nextFloat();
			break;
		}
		}

		return new double[]{a,b,c,d};
	}

	private double[] generateType2()
	{
		Random random = new Random();
		double a = 0;
		double b = 0;
		double c = 0;
		double d = 0;

		d = random.nextFloat()*(2*minLength()/3);
		basedOn = random.nextInt(3);
		switch(basedOn)
		{
		case 0:
		{
			a = 1;
			b = (W - d)/W*random.nextFloat();
			c = (H - d)/H*random.nextFloat();

			break;
		}
		case 1:
		{
			b = 1;			
			a = (L - d)/L*random.nextFloat();
			c = (H - d)/H*random.nextFloat();
			break;
		}
		case 2:
		{
			c = 1;			
			a = (L - d)/L*random.nextFloat();
			b = (W - d)/W*random.nextFloat();
			break;
		}
		}

		return new double[]{a,b,c,d};
	}
	
	private double[] generateType3()
	{
		Random random = new Random();
		double a = 0;
		double b = 0;
		double c = 0;
		double d = 0;

		d = random.nextFloat()*(2*minLength()/3);
		basedOn = random.nextInt(3);
		switch(basedOn)
		{
		case 0:
		{
			a = 1;				
			b = (d/W)*random.nextFloat();
			c = (d/H)*random.nextFloat();
			break;
		}
		case 1:
		{
			b = 1;
			a = (d/L)*random.nextFloat();
			c = (d/H)*random.nextFloat();
			break;
		}
		case 2:
		{
			c = 1;
			a = (d/L)*random.nextFloat();
			b = (d/W)*random.nextFloat();
			break;
		}
		}
		return new double[]{a,b,c,d};
	}
	public double[][] getRandomCoplanarPoints()
	{
		Random random = new Random();
		double[] equation = new double[4];
		switch (random.nextInt(3)) 
		{
			case 0: equation = generateType1(); break;
			case 1: equation = generateType2(); break;
			case 2: equation = generateType3(); break;
		}
		
		double a = equation[0];
		double b = equation[1];
		double c = equation[2];
		double d = equation[3];
				
		double x = 0;
		double y = 0;
		double z = 0;
		double[][] points = new double[pointsPerPlane][3];
		for(int i=0; i<pointsPerPlane; i++)
		{
			do
			{
				
				switch(basedOn)
				{
					case 0:
					{
						y = (W)*random.nextDouble();
						z = (H)*random.nextDouble();
						x = b*y + c*z + d;
						break; 
					}
					case 1:
					{
						x = (L)*random.nextDouble();
						z = (H)*random.nextDouble();
						y = a*x + c*z + d;
						break;
					}
					case 2:
					{
						x = (L)*random.nextDouble();
						y = (W)*random.nextDouble();
						z = a*x + b*y + d;
						break;
					}	
				}
			}
			while(!inside(new double[]{x,y,z}));
				
			points[i] = new double[] {x,y,z};
		}
		return points;
	}
	
	private double minLength()
	{
		double min = L;
		if(W < min)
			min = W;
		if(H < min)
			min = H;
		
		return min;
		
	}

	private double[] computeEquation(double[][] points)
	{
		Point P = new Point(points[0]);
		Point Q = new Point(points[1]);
		Point R = new Point(points[2]);
		Vector PQ = new Vector(Q.subtract(P));
		Vector PR = new Vector(R.subtract(P));
		Vector n = PQ.cross(PR);
		double d = P.x()*n.x() + P.y()*n.y() + P.z()*n.z();
		return new double[]{n.x(), n.y(), n.z(), -1*d};
	}
	
	public void createParallel()
	{
		double[][] points = new double[pointsPerPlane][3];
		double sotreyDistance = H/(double)numberOfPlanes;
		
		for(int i=0; i<numberOfPlanes; i++)
		{
			int k=0;
			for(int j=0; j<pointsPerPlane; j++)
			{
				points[k++] = new double[]{random.nextDouble()*L, random.nextDouble()*W, 0}; 
			}
			clusters.add(new PointCluster(points,Colors.get(clusterColor++),0,0,i*sotreyDistance));
		}
	}
	
	public void createRandom()
	{
		double[][] points = new double[pointsPerPlane*numberOfPlanes][3];
		for(int i=0; i<points.length; i++)
			points[i] = new double[]{random.nextDouble()*L, random.nextDouble()*W, random.nextDouble()*H};
		clusters.add(new PointCluster(points,Colors.get(clusterColor++),0,0,0));
	}
	
	public void createIntersecting()
	{
		for(int i=0; i<numberOfPlanes; i++)
			clusters.add(new PointCluster(getRandomCoplanarPoints(),Colors.get(clusterColor++),0,0,0));
	}
	
	
	public void createDisjoint()
	{
		int[] div = primeFactors(numberOfPlanes);
		double[] LWH = new double[]{L/div[0], W/div[1], H/div[2]};
		cubicleLWH = LWH;
		for(double l=0; l<L; l+=LWH[0])
			for(double w=0; w<W; w+=LWH[1])
				for(double h=0; h<H; h+=LWH[2])
						clusters.add(new PointCluster(new Cuboid(1, pointsPerPlane, LWH).getRandomCoplanarPoints(), Colors.get(clusterColor++), l, w, h));
	}
	
	public void createOrthogonal()
	{
		int[] div = primeFactors(numberOfPlanes);
		double[] LWH = new double[]{L/div[0], W/div[1], H/div[2]};
		cubicleLWH = LWH;
		for(double l=0; l<L; l+=LWH[0])
			for(double w=0; w<W; w+=LWH[1])
				for(double h=0; h<H; h+=LWH[2])
						clusters.add(new PointCluster(new Cuboid(3, pointsPerPlane, LWH).getPointsOnFaces(pointsPerPlane), Colors.get(clusterColor++), l,w,h));// + random.nextDouble()*l/2, w + random.nextDouble()*w/2, h + random.nextDouble()*h/2));
	}
	
	
	public void plotPoints()
	{

		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout());
		Plot3DPanel plot = new Plot3DPanel();
		int i=0;
		for(PointCluster cluster : clusters)
		{
			plot.addScatterPlot(Integer.toString(i), cluster.color, getCoords(cluster.points));
		}
		
		frame.add(plot);
		
		frame.setSize(800, 800);
		plot.setFixedBounds(new double[]{0, 0, 0}, new double[]{100,100,100});
		frame.setVisible(true);
	}
	
	public int size()
	{
		int sum = 0;
		for(PointCluster cluster : clusters)
			sum += cluster.points.length;
		return sum;
	}
	
	public ArrayList<PointCluster> clusters()
	{
		return this.clusters;
	}
	
	public double[][] getPoints()
	{
		double[][] points = new double[pointsPerPlane*numberOfPlanes][3];
		int index = 0;
		for(int i=0; i<numberOfPlanes; i++)
			for(int j=0; j<pointsPerPlane; j++)
				points[index++] = getCoords(clusters.get(i).points[j]);
		return points;
	}
	
	private double[] getCoords(Point3d point) {
		return new double[]{point.x, point.y, point.z};
	}
	
	private double[][] getCoords(Point3d[] points) 
	{
		double[][] coords = new double[points.length][3];
		for(int i=0; i<points.length; i++)
			coords[i] = getCoords(points[i]);
		return coords;
	}

	public int numberOfClusters() {
		// TODO Auto-generated method stub
		return numberOfPlanes;
	}
				
	

}
