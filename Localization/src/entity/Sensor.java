package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ProGAL.geom2d.Point;

public class Sensor 
{
	private int id;
	public int count = 0;
	private boolean localized;
	private Point actualPos;
	private Point estimatedPos;
	private Map<Integer, Double> neighbors;
	private List<Integer> LN = new ArrayList<>();
	Sensor(int id, Point pos)
	{ 
		this.id = id;
		this.actualPos = pos;
		localized = false;
		neighbors = new HashMap<>();
		
	}
	
	public Sensor(Sensor sensor) 
	{
		this.id = sensor.id;
		this.localized = sensor.localized;
		this.actualPos = sensor.actualPos;
		this.estimatedPos = sensor.estimatedPos;
		this.neighbors = new HashMap<>(sensor.neighbors);
		
		
	}
	public Sensor(int id, double[] coor) { this(id, new Point(coor)); }
	double actualDistanceTo(Sensor s) { return actualPos.distance(s.actualPos); }
	int id() { return id; }
	void seed() { setPos(actualPos); }
	void setPos(Point pos) { this.estimatedPos = pos; localized = true; }
	Point pos() { return estimatedPos; }
	double offset() { return estimatedPos.distance(actualPos); }
	boolean isLocalized() { return localized; }
	void addNeighbor(int neighbor, double distance) { neighbors.put(neighbor, distance); }
	void neighborLocalized(int neighbor) { LN.add(neighbor); }
	public double get(int neighbor) { return neighbors.get(neighbor); }
	public Set<Integer> neighbors() { return neighbors.keySet(); }
	public List<Integer> LN() { return LN; }

	public void reset() 
	{
		localized = false;
		LN = new ArrayList<>();
	}

	public Point actualPos() {
		return actualPos;
	}
	
}
