package entity;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.Line;

import ProGAL.geom2d.Circle;
import ProGAL.geom2d.Point;
public class WSN 
{
	public static double R = 1;
	public static double P = 3;
	Sensor[] sensors;
	private boolean[][] adj;
	int size;
	List<Integer> localized = new ArrayList<>();
	public WSN(double[][] coor)
	{
		size = coor.length;
		adj = new boolean[size][size];
		sensors = new Sensor[size];
		for(int i=0; i<size; i++)
			sensors[i] = new Sensor(i, coor[i]);
			
		build();
	}
	
	public WSN(WSN wsn) 
	{
		this.size = wsn.size;
		this.sensors = new Sensor[size];
		adj = wsn.adj;
		for(int i=0; i<wsn.size; i++)
			this.sensors[i] = new Sensor(wsn.sensors[i]);
		
		build();
	}
	
	public WSN(String folderName, int id)
	{
		String source = "./WSNs/" + folderName + "/" + Integer.valueOf(id).toString() + ".wsn";
		List<String> coor = null;
		Path path = Paths.get(source);
		try {
			coor = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		coor.remove(coor.size()-1);
		coor.remove(coor.size()-1);
		String string = coor.toString();
		
		string = string.substring(1, string.length() - 1); // Get rid of braces.
		String[] parts = string.split("(?<=\\))(,\\s*)(?=\\()");
		double[][] coord = new double[parts.length][2];
		int i=0;
		for (String part : parts) 
		{
		    part = part.substring(1, part.length() - 1); // Get rid of parentheses.
		    String[] coords = part.split(",\\s*");
		    double x = Double.parseDouble(coords[0]);
		    double y = Double.parseDouble(coords[1]);
		    coord[i++] = new double[]{x,y};
		}
		size = coord.length;
		adj = new boolean[size][size];
		sensors = new Sensor[size];
		for(int c=0; c<size; c++)
			sensors[c] = new Sensor(c, coord[c]);
		build();
	}
	
	private void build()
	{
		
		Random rand = new Random();
		for(int i=0; i<size-1; i++)
			for(int j=i+1; j<size; j++)
			{
				double dist = sensors[i].actualDistanceTo(sensors[j]);
				if(dist <= R)
				{
					double fR = 0.022*Math.log(1+dist) - 0.038;
					dist = dist + rand.nextGaussian()*0.03 + fR;
//					if(rand.nextDouble() < 0.05)
//						dist += rand.nextDouble()*10*R;
					
					sensors[i].addNeighbor(j, dist);
					sensors[j].addNeighbor(i, dist);
					adj[i][j] = true;
				}
			}
	}
	
	public Set<Integer> neighbors(int... n)
	{
		Set<Integer> set = new HashSet<>();
		set.addAll(sensors[n[0]].neighbors());
		for(int i=1; i<n.length; i++)
			set.retainAll(sensors[n[i]].neighbors());
		return set;
	}
	
	public Map<Integer, Point> localize()
	{
		Map<Integer, Point> current = new HashMap<>();
		Map<Integer, Point> best = new HashMap<>();
		
		Queue<Integer> process = new LinkedBlockingQueue<>();
		for(int i=0; i<size; i++)
		{
			for(int j : neighbors(i))
			{
				for(int k : neighbors(i,j))
				{
					seed(i,j,k);
					process.add(i);
					process.add(j);
					process.add(k);
					while(!process.isEmpty())
					{
						int n = process.remove();
						localized.add(n);
						for(int m : neighbors(n))
						{
							if(!sensors[m].isLocalized())
							{
								sensors[m].neighborLocalized(n);
								if(localize(m))
								{
									current.put(m, sensors[m].pos());
									process.add(m);
								}
							}
						}
					}
					if(current.size() > best.size())
					{
						best = new HashMap<Integer, Point>(current);
						current.clear();
					}
					if(current.size() == this.size)
					{
						best = new HashMap<Integer, Point>(current);
						return best;
					}
					localized.clear();
					for(Sensor s : sensors)
						s.reset();
				}
			}
		}
		return best;
	}
	
	private boolean localize(int n)
	{
		List<Integer> list = sensors[n].LN();
		Integer[] LN = new Integer[list.size()];
		list.toArray(LN);
		if(LN.length < 2) return false;
		if(LN.length >= 2)
			if(bilaterate(n, LN[0], LN[1]))
			{
				return true;
			}
		
		if(LN.length >= 3)
			if(trilaterate(n, LN[0], LN[1], LN[LN.length-1]))
			{
				return true;
			}
		return false;
	}
	
	private boolean bilaterate(int n, int i, int j)
	{
		double rI = sensors[n].get(i);
		double rJ = sensors[n].get(j);
		Circle cI = new Circle(sensors[i].pos(), rI);
		Circle cJ = new Circle(sensors[j].pos(), rJ);
		Point[] is = cI.intersections(cJ);
		if(is == null) return false;
		if(Double.isNaN(is[0].x())) return false;
		boolean iOK = true;
		boolean jOK = true;
		Set<Integer> localizedSet = new HashSet<Integer>(localized);
		Set<Integer> neighbors = neighbors(n);
		localizedSet.removeAll(neighbors);
		for(int l : localizedSet)
		{
			if(is[0].distance(sensors[l].pos()) <= R)
				iOK = false;
			if(is[1].distance(sensors[l].pos()) <= R)
				jOK = false;
			if(!iOK && !jOK)
				return false;
		}
		if(iOK && jOK) return false;
		if(iOK)
			sensors[n].setPos(is[0]);
		else if(jOK)
			sensors[n].setPos(is[1]);
		return true;
		
	}
	
	private boolean trilaterate(int n, int i, int j, int k)
	{
		if(Point.collinear(sensors[i].pos(), sensors[j].pos(), sensors[k].pos())) return false;
		double rI = sensors[n].get(i);
		double rJ = sensors[n].get(j);
		double rK = sensors[n].get(k);
		Circle cI = new Circle(sensors[i].pos(), rI);
		Circle cJ = new Circle(sensors[j].pos(), rJ);
		Point[] is = cI.intersections(cJ);
		if(is == null) return false;
		if(Double.isNaN(is[0].x())) return false;
		boolean iOK = true;
		boolean jOK = true;
		Set<Integer> localizedSet = new HashSet<Integer>(localized);
		localizedSet.removeAll(neighbors(n));
		for(int l : localizedSet)
		{
			Point lPos = sensors[l].pos();
			if(is[0].distance(lPos) <= 1)
				iOK = false;
			if(is[1].distance(lPos) <= 1)
				jOK = false;
			if(!iOK && !jOK) return false;
		}
		if(iOK && jOK)
		{
			Point kPos = sensors[k].pos();
			if(Math.abs(is[0].distance(kPos) - rK) <= R*P)
			{
				sensors[n].setPos(is[0]);
				return true;
			}
			else if(Math.abs(is[1].distance(kPos) - rK) <= R*P)
			{
				sensors[n].setPos(is[1]);
				return true;
			}
		}
		
		if(iOK)
			sensors[n].setPos(is[0]);
		else if(jOK)
			sensors[n].setPos(is[1]);
		return true;
	}

	

	private void seed(int i, int j, int k) 
	{
		sensors[i].seed();
		sensors[j].seed();
		sensors[k].seed();
	}

	public Map<Integer, Point> pointFormation() {
		Map<Integer, Point> pf = new HashMap<>();
		for(Sensor s : sensors)
			pf.put(s.id(), s.actualPos());
			
		
		return pf;
	}
	
	public void plotGraph()
	{
		double[][] points = new double[size][2];
		
		Plot2DPanel plot = new Plot2DPanel();
		for(int i = 0; i<size-1; i++)
		{
			points[i] = sensors[i].actualPos().getCoords();
			for(int j = i+1; j<size; j++)
			{
				if(adj[i][j])
					plot.addPlotable(new Line(Color.BLACK, sensors[i].actualPos().getCoords(), sensors[j].actualPos().getCoords()));
			}
		}
		
		points[size-1] = sensors[size-1].actualPos().getCoords();
			
		plot.addScatterPlot("Actual", points);
		plot.setFixedBounds(new double[]{0,0}, new double[]{50,50});
		JFrame frame = new JFrame("a plot panel");
		frame.setContentPane(plot);
		
		frame.setSize(800, 800);
		frame.setVisible(true);
	}

	public Map<Integer, Point> trilatOnly()
	{
		Map<Integer, Point> current = new HashMap<>();
		Map<Integer, Point> best = new HashMap<>();
		
		Queue<Integer> process = new LinkedBlockingQueue<>();
		for(int i=0; i<size; i++)
		{
			for(int j : neighbors(i))
			{
				for(int k : neighbors(i,j))
				{
					seed(i,j,k);
					process.add(i);
					process.add(j);
					process.add(k);
					while(!process.isEmpty())
					{
						int n = process.remove();
						localized.add(n);
						for(int m : neighbors(n))
						{
							if(!sensors[m].isLocalized())
							{
								sensors[m].neighborLocalized(n);
								if(localizeT(m))
								{
									current.put(m, sensors[m].pos());
									process.add(m);
								}
							}
						}
					}
					if(current.size() > best.size())
					{
						best = new HashMap<Integer, Point>(current);
						current.clear();
					}
					if(current.size() == this.size)
					{
						best = new HashMap<Integer, Point>(current);
						return best;
					}
					localized.clear();
					for(Sensor s : sensors)
						s.reset();
				}
			}
		}
		return best;
	}

	private boolean localizeT(int n) 
	{
		List<Integer> list = sensors[n].LN();
		Integer[] LN = new Integer[list.size()];
		list.toArray(LN);
		if(LN.length < 3) return false;
		else return mereTrilaterate(n, LN[0], LN[1], LN[LN.length-1]);
	}
	
	private boolean mereTrilaterate(int n, int i, int j, int k)
	{
		if(Point.collinear(sensors[i].pos(), sensors[j].pos(), sensors[k].pos())) return false;
		double rI = sensors[n].get(i);
		double rJ = sensors[n].get(j);
		double rK = sensors[n].get(k);
		Circle cI = new Circle(sensors[i].pos(), rI);
		Circle cJ = new Circle(sensors[j].pos(), rJ);
		Point[] is = cI.intersections(cJ);
		if(is == null) return false;
		if(Double.isNaN(is[0].x())) return false;
		Point kPos = sensors[k].pos();
		if(Math.abs(is[0].distance(kPos) - rK) <= R*P && Math.abs(is[1].distance(kPos) - rK) <= R*P) return false;
		
		if(Math.abs(is[0].distance(kPos) - rK) <= R*P)
		{
			sensors[n].setPos(is[0]);
			return true;
		}
		else if(Math.abs(is[1].distance(kPos) - rK) <= R*P)
		{
			sensors[n].setPos(is[1]);
			return true;
		}
		
		return false;
	}
	
	public boolean isConnected()
	{
		List<Integer> queue = new ArrayList<>();
		int totalVisited = 0;
		boolean[] visited = new boolean[size];
		visited[0] = true;
		
		queue.add(0);
		while(queue.size() > 0)
		{
			
			visited[queue.get(0)] = true;
			totalVisited++;
			for(Integer i : neighbors(queue.get(0)))
			{

				if(!visited[i] && !queue.contains(i))
				{
					queue.add(i);
				}
			}
			queue.remove(0);
		}
		return totalVisited == size;
	}
	
	public void toFile(String folderName, int id) throws IOException
	{
		BufferedWriter out = null;
		String destination = "./WSNs/" + folderName + "/";
		new File(destination).mkdirs();
		FileWriter fstream = new FileWriter(destination + Integer.valueOf(id) + ".wsn", true); //true tells to append data.
	    out = new BufferedWriter(fstream);
	    
	    StringBuilder str = new StringBuilder();
		for(int i=0; i<size; i++)
		{
			str.append(sensors[i].actualPos().toString());
			str.append(System.getProperty("line.separator"));
		}
		String write = str.toString();
		out.write(write);
	    out.newLine();
	    try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    System.out.println(write);
	}
	

		

	
}
