import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



class AdjList
{
	DecimalFormat df;
	DecimalFormatSymbols dfs;
	Map<Integer, Double> visible;
	Map<Integer, Double> hidden;
	AdjList()
	{
		df = new DecimalFormat("#####0.0000");
	    dfs = df.getDecimalFormatSymbols();
	    dfs.setDecimalSeparator('.');
	    df.setDecimalFormatSymbols(dfs);
		visible = new HashMap<Integer, Double>();
		hidden = new HashMap<Integer, Double>();
	}
	
	int size()
	{
		return visible.size();
	}
	
	void add(int adj, double distance)
	{
		this.visible.put(adj, distance);
	}
	
	double get(int adj)
	{
		return visible.get(adj);
	}
	
	boolean contains(int adj)
	{
		return visible.containsKey(adj);
	}
	
	public Set<Integer> get()
	{
		return visible.keySet();
	}
	
	double averageWeight()
	{
		double sum = 0.0;
		for(double d : visible.values())
			sum += d;
		return sum/(visible.size());
		
	}
	
	void hide(int adj)
	{
		hidden.put(adj, this.visible.get(adj));
	}
	
	void show(int adj)
	{
		visible.put(adj, hidden.get(adj)); 
	}
	
			
	void addAll(AdjList adjList)
	{
		visible.putAll(adjList.visible);
	}
	
	int hideAll(double hopDistance)
	{
		int hidden = 0;
		Iterator<Integer> it = visible.keySet().iterator();
		while(it.hasNext())
		{
			int adj = it.next();
			if(visible.get(adj) > hopDistance)
			{
				this.hidden.put(adj, this.visible.get(adj));
				it.remove();
				hidden++;
			}
		}
		return hidden;
	}
	
	int show(double hopDistance)
	{
		int shown = 0;
		Iterator<Integer> it = hidden.keySet().iterator();
		while(it.hasNext())
		{
			int adj = it.next();
			if(hidden.get(adj) < hopDistance)
			{
				show(adj);
				it.remove();
				shown++;
			}
		}
		return shown;
	}
	
	void unhideAll()
	{
		visible.putAll(hidden);
		hidden.clear();
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		for(int adj : visible.keySet())
		{
			str.append("(");
			str.append(Integer.valueOf(adj).toString());
			str.append(",");
			str.append(df.format(Double.valueOf(visible.get(adj))));
			str.append(") ");
		}
		return str.toString();
		
	}

	
	
}