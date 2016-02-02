package coverbylines;


import javax.vecmath.Point3d;

import ProGAL.geom2d.Point;

public class Vertex 
{
	private int id;
	public void setId(int id) { this.id = id;}
	public int getId() { return id; }
	
	private int hyperPlaneId;
	public void setHyperplane(int id) { this.hyperPlaneId = id; }
	public int getHyperplane() { return hyperPlaneId; }
		
	private Point position;
	public Point getPosition() { return position; }
	public double[] getPositionDouble() { return new double[]{position.x(), position.y()};}
	public Vertex(int id, Point position)
	{
		this.id = id;
		this.position = position;
	}
	
	public double distance(Vertex that)
	{
		return this.position.distance(that.position);
	}
	
	public double plugIn(double slope)
	{
		return position.x()*slope - position.y();
	}
	
	public String toFile()
	{ 
		StringBuilder str = new StringBuilder();
		str.append("(");
		str.append(position.x());
		str.append(",");
		str.append(position.y());
		str.append(")");
		return str.toString();
	}
	
	
	
	
	

}
