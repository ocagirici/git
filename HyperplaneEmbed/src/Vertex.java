import javax.vecmath.Point3d;


public class Vertex 
{
	private int id;
	public void setId(int id) { this.id = id;}
	public int getId() { return id; }
	
	private int hyperPlaneId;
	public void setHyperplane(int id) { this.hyperPlaneId = id; }
	public int getHyperplane() { return hyperPlaneId; }
	
	private int actualHyperplane;
	public void setActualHyperplane(int id) { this.actualHyperplane = id; }
	public int getActualHyperplane() { return actualHyperplane; }
	
	private Point3d position;
	public Point3d getPosition() { return position; }
	public double[] getPositionDouble() { return new double[]{position.x, position.y, position.z};}
	public Vertex(int id, int actualHyperplane, Point3d position)
	{
		this.id = id;
		this.actualHyperplane = actualHyperplane;
		this.position = position;
	}
	
	public double distance(Vertex that)
	{
		return this.position.distance(that.position);
	}
	
	public double plugIn(double[] xyz)
	{
		return position.x*xyz[0] + position.y*xyz[1] + position.z*xyz[2];
	}
	
	public String toFile()
	{ 
		StringBuilder str = new StringBuilder();
		str.append("(");
		str.append(position.x);
		str.append(",");
		str.append(position.y);
		str.append(",");
		str.append(position.z);
		str.append(")");
		return str.toString();
	}
	
	
	
	
	

}
