package coverbylines;

import java.awt.Color;
import java.util.ArrayList;



import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom3d.Plane;

public class PointCluster
{
	List<Point> points = new ArrayList<Point>();
	
	public List<Point> points() { return points; }
	int i = 0;
	public Color color;
	public PointCluster(List<Point> points, Color color)
	{
		this.color = color;
		this.points.addAll(points);
	
	}
	
	

	public PointCluster(PointCluster p) {
		this.color = p.color;
		this.points.addAll(p.points());
	}
	public boolean isEmpty()
	{
		return points.size() == 0;
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
		for(Point p : points) 
			twod.add(p);
			
		
		scene.addShape(ProGAL.geom2d.convexHull.GrahamScan.getConvexHull(twod), Color.GRAY, 0.5);
	}

	public int size() {
		return points.size();
	}
	
	
	
	
	
	
}