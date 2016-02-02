import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


public class Hyperplane 
{
	static int numberOfHyperplanes = 0;
	private int id;
	private HashSet<Integer> vertices;
	
	public Hyperplane(int i, int j, int k, int l)
	{
		this.id = numberOfHyperplanes++;
		this.vertices = new HashSet<>();
		vertices.add(i);
		vertices.add(j);
		vertices.add(k);
		vertices.add(l);
	}
	
	public void addVertex(int id)
	{
		vertices.add(id);
	}
	
	public void addVertices(Collection<Integer> vertices)
	{
		this.vertices.addAll(vertices);
	}

	public HashSet<Integer> vertices() {
		return vertices;
	}
	
	public int[] getRandomThree()
	{
		ArrayList<Integer> list = new ArrayList<Integer>(vertices);
		Collections.shuffle(list);
		int[] v = new int[]{list.get(0), list.get(1), list.get(2)};
		return v;
	}
	
	public int id()
	{
		return id;
	}

}
