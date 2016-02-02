public class Neighbor implements Comparable<Neighbor>
{
	int i;
	double distance;
	public Neighbor(int i, double distance)
	{
		this.i = i;
		this.distance = distance;
	}
	@Override
	public int compareTo(Neighbor that) {
		if(this.distance < that.distance) return -1;
		else if(this.distance > that.distance) return 1;
		else return 0;
	}
}