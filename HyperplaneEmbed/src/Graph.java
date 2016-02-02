import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.vecmath.Point3d;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.math.plot.Plot3DPanel;
import org.math.plot.plotObjects.Line;

import ProGAL.geom3d.Plane;
import ProGAL.geom3d.Point;


public class Graph 
{
	private class AdjComparator implements Comparator<Integer>
	{

		@Override
		public int compare(Integer v, Integer w) 
		{
			return adj[w].size() - adj[v].size();
		}
		
	}
	public static double R = 10;
	public static double tolerance = 1;
	public static double P = 0.01;
	public static double fR =  0.022*Math.log(1 + R) - 0.038;
	Random random = new Random();
	private ArrayList<Vertex> vertices;
	private int E = 0;
	private AdjList[] adj;
	private ArrayList<Hyperplane> hyperplanes;
	private HashSet<Integer> offPlane;
	private HashSet<Integer> onPlane;
	
	public Graph(String folderName, int id)
	{
		String source = "./graphs/" + folderName + "/" + Integer.valueOf(id).toString() + ".coor";
		List<String> coor = null;
		Path path = Paths.get(source);
		try {
			coor = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		coor.remove(coor.size()-1);
		coor.remove(coor.size()-1);
		String string = coor.toString();
		
		string = string.substring(1, string.length() - 1); // Get rid of braces.
		String[] parts = string.split("(?<=\\))(,\\s*)(?=\\()");
		double[][] coord = new double[parts.length][3];
		int i=0;
		for (String part : parts) 
		{
		    part = part.substring(1, part.length() - 1); // Get rid of parentheses.
		    String[] coords = part.split(",\\s*");
		    double x = Double.parseDouble(coords[0]);
		    double y = Double.parseDouble(coords[1]);
		    double z = Double.parseDouble(coords[2]);
		    coord[i++] = new double[]{x,y,z};
		}
		int size = coord.length;
		vertices = new ArrayList<Vertex>();
		this.hyperplanes = new ArrayList<>();
		this.vertices = new ArrayList<>();
		this.offPlane = new HashSet<>();
		this.onPlane = new HashSet<>();
		for(int c=0; c<size; c++)
		{
			vertices.add(new Vertex(c, 0, new Point3d(coord[c])));
			offPlane.add(c);
		}
		build();
	}
	
	public void toFile(String folderName, int id) throws IOException
	{
		BufferedWriter out = null;
		String destination = "./graphs/" + folderName + "/";
		new File(destination).mkdirs();
		FileWriter fstream = new FileWriter(destination + Integer.valueOf(id) + ".coor", true); //true tells to append data.
	    out = new BufferedWriter(fstream);
	    
	    StringBuilder str = new StringBuilder();
		for(int i=0; i<vertices.size(); i++)
		{
			str.append(vertices.get(i).toFile());
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
	
	public Graph(ArrayList<PointCluster> clusters)
	{
		this.hyperplanes = new ArrayList<>();
		this.vertices = new ArrayList<>();
		this.offPlane = new HashSet<>();
		this.onPlane = new HashSet<>();
		int cId = 0;
		int vId = 0;
		for(PointCluster pc : clusters)
		{
			for(Point3d p : pc.points())
			{
				Vertex v = new Vertex(vId,cId,p);
				vertices.add(v);
				offPlane.add(vId);
				vId++;
			}	
			cId++;
		}
		build();
		
	}
	
	private void build()
	{
		adj = new AdjList[vertices.size()];
		for (int i = 0; i < adj.length; i++) {
			adj[i] = new AdjList();
		}
		for(int i=0; i<vertices.size()-1; i++)
			for(int j=i+1; j<vertices.size(); j++)
			{
				double distance = vertices.get(i).distance(vertices.get(j));
				if(distance <= R)
				{
					if(P > 0)
					{
							distance = distance + fR + P*random.nextGaussian();
						if(random.nextDouble() < 0.05)
							distance = distance + random.nextDouble()*10;
					}
					addEdge(i,j,distance);
				}
					
			}
	}
	
	private void addEdge(int i, int j, double distance)
	{
		adj[i].add(j, distance);
		adj[j].add(i, distance);
		E++;
	}
	
	public Set<Integer> adj(int i)
	{
		return adj[i].get();
	}
	
	public Set<Integer> adj(int... sensors)
	{
		Set<Integer> set = new HashSet<>();
		set.addAll(adj(sensors[0]));
		for(int i=1; i<sensors.length; i++)
			set.retainAll(adj(sensors[i]));
		return set;
	}
	

	private Set<Integer> adj(Collection<Integer> collection)
	{
		Set<Integer> set = new HashSet<>();
		for(int c : collection)
			set.addAll(adj(c));
		return set;
	}
	
	public Set<Integer> adj(Collection<Integer> collection, Hyperplane h)
	{
		Set<Integer> set = new HashSet<>(adj(collection));
		set.retainAll(h.vertices());
		return set;
	}
	
	

	public Set<Integer> adj(Collection<Integer> others, int... vertices)
	{
		Set<Integer> set = new TreeSet<>();
		set.addAll(adj(vertices[0]));
		for(int i=1; i<vertices.length; i++)
			set.retainAll(adj(vertices[i]));
		set.retainAll(others);
		return set;
	}
	
	public void findHyperplanes()
	{
		while(findHyperplane())
		{
			System.out.println("Hyperplane found");
			extend(hyperplanes.get(hyperplanes.size()-1));
		}
		for(Hyperplane h : hyperplanes)
			System.out.println(h.vertices());
		
		System.out.println("Percentage: " + ((double)onPlane.size()/(double)vertices.size())*100);
		System.out.printf("Error: %.3f", + accuracy());
		System.out.printf("Worst: %.3f", + worst());
		
	}
	
	public double worst()
	{
		StandardDeviation st = new StandardDeviation();

		Plane p = new Plane(
				new Point(vertices.get(0).getPositionDouble()),
				new Point(vertices.get(1).getPositionDouble()),
				new Point(vertices.get(2).getPositionDouble()));
		double[] xyz = new double[]{p.getNormal().x(), p.getNormal().y(), p.getNormal().z()};
		double[] values = new double[vertices.size()];
		int i=0;
		for(Vertex v : vertices)
			values[i++] = v.plugIn(xyz);
		return st.evaluate(values)*vertices.size();
	}
	
	private double accuracy(Hyperplane h)
	{
		StandardDeviation st = new StandardDeviation();
		int[] r = h.getRandomThree();
		Plane p = new Plane(
				new Point(vertices.get(r[0]).getPositionDouble()),
				new Point(vertices.get(r[1]).getPositionDouble()),
				new Point(vertices.get(r[2]).getPositionDouble()));
		double[] xyz = new double[]{p.getNormal().x(), p.getNormal().y(), p.getNormal().z()};
		double[] values = new double[h.vertices().size()];
		int i=0;
		for(int v : h.vertices())
			values[i++] = vertices.get(v).plugIn(xyz);
		return st.evaluate(values);
	}
	
	public double accuracy()
	{
		double accuracy = 0;
		for(Hyperplane h : hyperplanes)
			accuracy += (accuracy(h)*h.vertices().size());
		return accuracy;
	}
	
	
	private boolean findHyperplane()
	{
		for(int i : offPlane)
			for(int j : adj(offPlane,i))
				for(int k : adj(offPlane, i, j))
					for(int l : adj(offPlane, i, j, k))
						if(coplanar(i,j,k,l))
						{
							hyperplanes.add(new Hyperplane(i, j, k, l));
							return true;
						}
		
		return false;
	}
	
	private int extend(Hyperplane h)
	{
		HashSet<Integer> newbies = new HashSet<>();
		int extended = 0;
		do
		{
		newbies.clear();
		for(int v : offPlane)
			if(belongsTo(v,h))
			{
				System.out.println(v + " belongs to " + h.id());
				h.addVertex(v);
				newbies.add(v);
			}
				
		offPlane.removeAll(newbies);
		onPlane.addAll(newbies);
		} while(!newbies.isEmpty());
		return extended;
	}
	
	private boolean belongsTo(int v, Hyperplane h)
	{
		Set<Integer> neighbors = adj(h.vertices(),v);
		int coplanar = 0;
		if(neighbors.size() < 3) return false;
		for(int i : neighbors)
			for(int j : adj(neighbors,i))
				for(int k : adj(neighbors, i, j))
				{
					if(coplanar(v,i,j,k))
//						coplanar++;
//					if((double)coplanar/(double)neighbors.size() >= 0.5)
						return true;
				}
		return false;
		
	}
	
	
	private boolean coplanar(int i, int j, int k, int l)
	{
		double dIJ2 = Math.pow(adj[i].get(j), 2);
		double dIK2 = Math.pow(adj[i].get(k), 2);
		double dIL2 = Math.pow(adj[i].get(l), 2);
		double dJK2 = Math.pow(adj[j].get(k), 2);
		double dJL2 = Math.pow(adj[j].get(l), 2);
		double dKL2 = Math.pow(adj[k].get(l), 2);

		Jama.Matrix matrix = new Jama.Matrix(new double[][]	
				{ 
				{0,	dIJ2, dIK2, dIL2, 1},
				{dIJ2, 0, dJK2, dJL2, 1},
				{dIK2, dJK2, 0, dKL2, 1},
				{dIL2, dJL2, dKL2, 0, 1},
				{	1,	  1, 	1, 1, 0}
				});
		
		double det = matrix.det();
		double V = Math.sqrt(det/288);
		if(Double.isNaN(V))	return false;
		return V <= tolerance;
	}
	
	private double avgDegree()
	{
		return (2*E)/vertices.size();
	}
	
	public List<Integer> sortByAdj()
	{
		ArrayList<Integer> V = new ArrayList<>();
		for(int i=0; i<vertices.size(); i++)
			V.add(i);
		V.sort(new AdjComparator());
		
		return V;
	}
	
	
	public List<Integer> getTop(double x)
	{
		double limit = avgDegree()*x;
		List<Integer> V = sortByAdj();
		for(int i=0; i<V.size(); i++)
			if(adj[V.get(i)].size() < limit)
				return V.subList(0, i);
		return null;
				
		
	}
	
	public List<Integer> getBottom(double x)
	{
		double limit = avgDegree()*x;
		List<Integer> V = sortByAdj();
		Collections.reverse(V);
		for(int i=0; i<V.size(); i++)
			if(adj[V.get(i)].size() > limit)
				return V.subList(0, i);
		return null;
				
		
	}
	
	public void plot()
	{
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout());
		Plot3DPanel plot = new Plot3DPanel();
		
		plot.addScatterPlot("Coord", getCoords());
		
		
		frame.add(plot);
		
		frame.setSize(800, 800);
		plot.setFixedBounds(new double[]{0, 0, 0}, new double[]{100,100,100});
		frame.setVisible(true);
	}
	
	public void plotWithEdges()
	{
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout());
		Plot3DPanel plot = new Plot3DPanel();
		
		plot.addScatterPlot("Coord", getCoords());
		for(int i=0; i<vertices.size()-1; i++)
			for(int j=i+1; j<vertices.size(); j++)
				if(adj[i].contains(j))
					plot.addPlotable(new Line(Color.BLACK, vertices.get(i).getPositionDouble(), vertices.get(j).getPositionDouble()));
			
		
		
		frame.add(plot);
		
		frame.setSize(800, 800);
		plot.setFixedBounds(new double[]{0, 0, 0}, new double[]{100,100,100});
		frame.setVisible(true);
	}
	
	public void plotHyperplanes()
	{

		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout());
		Plot3DPanel plot = new Plot3DPanel();
		int i=0;
		for(Hyperplane hyperplane : hyperplanes)
		{
			plot.addScatterPlot(Integer.toString(i), Colors.get(hyperplane.id()), getCoords(hyperplane.vertices()));
		}
		
		frame.add(plot);
		
		frame.setSize(800, 800);
		plot.setFixedBounds(new double[]{0, 0, 0}, new double[]{100,100,100});
		frame.setVisible(true);
	}

	private double[][] getCoords(Collection<Integer> vertices) {
		double[][] coords = new double[vertices.size()][3];
		int i=0;
		for(int v : vertices)
			coords[i++] = this.vertices.get(v).getPositionDouble();
		return coords;
	}
	
	private double[][] getCoords() {
		double[][] coords = new double[vertices.size()][3];
		int i=0;
		for(Vertex v : vertices)
			coords[i++] = v.getPositionDouble();
		return coords;
	}

	public void plotDense() 
	{
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout());
		Plot3DPanel plot = new Plot3DPanel();
		ArrayList<Integer> all = new ArrayList<>();
		for(int i=0; i<vertices.size(); i++)
			all.add(i);
		
		List<Integer> V = getTop(1.2);
		plot.addScatterPlot("Dense", Color.RED, getCoords(V));
		all.removeAll(V);
		V = getBottom(0.8);
		plot.addScatterPlot("Sparse", Color.GREEN, getCoords(V));
		all.removeAll(V);
		if(!all.isEmpty())
			plot.addScatterPlot("Rem", Color.BLUE, getCoords(all));

		frame.add(plot);
		
		frame.setSize(800, 800);
		plot.setFixedBounds(new double[]{0, 0, 0}, new double[]{100,100,100});
		frame.setVisible(true);
	}

}
