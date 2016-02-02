import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point3d;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ProGAL.geom2d.Circle;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom3d.Plane;

public class PointCluster
{
	Point3d[] points;
	
	public Point3d[] points() { return points; }
	int i = 0;
	double l,w,h;
	public Color color;
	public PointCluster(double[][] points, Color color, double l, double w, double h)
	{
		this.color = color;
		this.l = l;
		this.w = w;
		this.h = h;
		this.points = new Point3d[points.length];
		for(int i=0; i<points.length; i++)
			this.points[i] = new Point3d(points[i][0]+l, points[i][1]+w, points[i][2]+h);
	
	}

	public PointCluster(PointCluster p) {
		this.color = p.color;
		this.l = p.l;
		this.w = p.w;
		this.h = p.h;
		this.points = new Point3d[p.points.length];
		for(int i=0; i<points.length; i++)
			this.points[i] = new Point3d(points[i].x+l, points[i].y+w, points[i].z+h);
	}
	public boolean isEmpty()
	{
		return points.length == 0;
	}
	private static double[][] getRotationToZ(Plane eq)
	{
		
		double[] equation = new double[]{eq.getNormal().x(), eq.getNormal().y(), eq.getNormal().z()};
		Rotation rotation = new Rotation(new Vector3D(equation), new Vector3D(0,0,1));
		return rotation.getMatrix();
	}
	
	private static double[] dot(double[][] A, double[] x)
	{
		int m = A.length;
		int n = A[0].length;
		if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
		double[] y = new double[m];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				y[i] += (A[i][j] * x[j]);
		return y;
	}
	
	public void plotWithPoints()
	{
		J2DScene scene = J2DScene.createJ2DSceneInFrame();
		ArrayList<ProGAL.geom2d.Point> twod = new ArrayList<>();

		for(int i=0; i<points.length; i++)
		{
			
			ProGAL.geom2d.Point tmp = new ProGAL.geom2d.Point(points[i].x, points[i].y); 
			twod.add(tmp);
			scene.addShape(new Circle(tmp,1), Color.BLACK, 0.5, true);
		}
		scene.addShape(ProGAL.geom2d.convexHull.GrahamScan.getConvexHull(twod), Color.GRAY, 0.5);
	}

	public int size() {
		return points.length;
	}
	
	
	
	
	
	
}