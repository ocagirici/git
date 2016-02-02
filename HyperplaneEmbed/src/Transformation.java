import javax.vecmath.Point3d;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import ProGAL.geom3d.Point;
import ProGAL.geom3d.PointList;
import ProGAL.geom3d.Vector;
import ProGAL.geom3d.Plane;
import ProGAL.math.Matrix;
public class Transformation
{
	
	private Point originI;
	private Point originO;
	private Matrix matrix;
	

	public Transformation(Point[] in, Point[] out)
	{
		matrix = createTransformationMatrix(in, out);
	}
	

	
	static double[][] getRotationToZ(Plane eq)
	{
		
		double[] equation = new double[]{eq.getNormal().x(), eq.getNormal().y(), eq.getNormal().z()};
		Rotation rotation = new Rotation(new Vector3D(equation), new Vector3D(0,0,1));
		return rotation.getMatrix();
	}

	static double[][] makeMatrix(Point p1, Point p2, Point p3)
	{
	    Vector v1 = new Vector(p1.subtract(p2));
	    Vector v2 = new Vector(p3.subtract(p1));
	    Vector v3 = v1.cross(v2);
	    double[][] M = { { v1.x(), v2.x(), v3.x(), p1.x() },
	                     { v1.y(), v2.y(), v3.y(), p1.y() },
	                     { v1.z(), v2.z(), v3.z(), p1.z() },
	                     {   0.0,   0.0,   0.0,   1.0 } };
	    return M;
	}

	public double[] get(double[] point)
	{
		Vector input = new Vector(point);
		Vector I = new Vector(originI);
		Vector O = new Vector(originO);
		Vector output =  matrix.multiply(input.subtract(I));
		output.addThis(O);
		Point target = new Point(output);
		return target.getCoords();
	}
	
	static double[] d(Double[] d)
	{
		double[] res = new double[d.length];
		for(int i=0; i<d.length; i++)
			res[i] = d[i];
		return res;
	}

	static Point multiply(double[][] matrix, Point point)
	{
	     double[] temp = new double[4];
	     for (int i = 0; i < 3; ++i) temp[i] = point.getCoords()[i];
	     temp[3] = 1.0;

	     double[] res = new double[3];
	     for (int i = 0; i < 3; ++i)
	         for (int j = 0; j < 4; ++j)
	             res[i] += temp[j] * matrix[i][j];
	     
	     return new Point(res);
	}
	
	public static Point dot(double[][] A, double[] x) {
		int m = A.length;
		int n = A[0].length;
		if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
		double[] y = new double[m];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				y[i] += (A[i][j] * x[j]);
		return new Point(y);
	}
	
	
	
	private Matrix createMatrix(Point... P)
	{
		PointList list = new PointList(P);
		Point C = list.getCentroid();
		Vector x = new Vector(C, P[0]);
		x.normalizeThis();
		Vector v = new Vector(C, P[1]);
		v.normalizeThis();
		Vector z = x.cross(v);
		z.normalizeThis();
		Vector y = x.cross(z);
		
		return Matrix.createColumnMatrix(x, y, z);
	}
	
	private Matrix createTransformationMatrix(Point[] in, Point[] out)
	{
		originI = new Point(new PointList(in).getCentroid());
		originO = new Point(new PointList(out).getCentroid());
		Matrix input = createMatrix(in);
		Matrix output = createMatrix(out);
		return output.multiply(input.invert());
	}
	

	
	public double[] applyTo(double[] point)
	{
		Vector input = new Vector(point);
		Vector I = new Vector(originI);
		Vector O = new Vector(originO);
		Vector output =  matrix.multiply(input.subtract(I));
		output.addThis(O);
		Point target = new Point(output);
		return target.getCoords();
	}

	
}
