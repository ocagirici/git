package coverbylines;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.math.plot.Plot2DPanel;

import ProGAL.geom2d.Circle;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;



public class Maze {

	static Random rand = new Random();

	public static class Wall {
		private final int cell;
		private final int direction;

		public Wall(int cell, int direction) {
			this.cell = cell;
			this.direction = direction;
		}
	}

	private static int UP = 1;
	private static int RIGHT = 2;
	private static int DOWN = 4;
	private static int LEFT = 8;

	public static Maze createRandomMaze(int rows, int columns) {
		Maze maze = new Maze(rows, columns);

		// create all walls
		List<Wall> walls = new ArrayList<Wall>();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				if (row > 0) {
					walls.add(new Wall(row*columns + col, UP));
				}
				if (col > 0) {
					walls.add(new Wall(row*columns + col, LEFT));
				}
			}
		}

		// remove all the walls you can
		ArrayDisjointSet diset = new ArrayDisjointSet(rows*columns);

		while (diset.size() > 1) {
			int wallIndex = rand.nextInt(walls.size());
			int cell1 = walls.get(wallIndex).cell;
			int cell2 = (walls.get(wallIndex).direction == UP) ?
					cell1 - columns :
						cell1 - 1;

			if (diset.find(cell1) != diset.find(cell2)) {
				// we can remove the wall
				if (walls.get(wallIndex).direction == UP) {
					maze.grid[cell1] ^= UP;
					maze.grid[cell2] ^= DOWN;
				} else {
					maze.grid[cell1] ^= LEFT;
					maze.grid[cell2] ^= RIGHT;
				}
				diset.join(cell1, cell2);
			}
			walls.remove(wallIndex);
		}

		return maze;
	}

	private int[] grid;
	private int rows;
	private int columns;

	/**
	 * Creates a maze with ALL walls up.
	 * 
	 * @param rows number of rows.
	 * @param columns number of columns.
	 */
	private Maze(int rows, int columns) {
		this.grid = new int[rows*columns];
		for (int i = 0; i < rows*columns; i++) {
			this.grid[i] = UP | RIGHT | DOWN | LEFT;
		}
		this.rows = rows;
		this.columns = columns;
	}

	private int startX = 0;
	private int startY = 0;
	private int size = 10;
	private List<Point> points = new ArrayList<Point>();
	

	

	public List<Point> getOrthogonal(int numberOfPoints)
	{
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < columns; col++)
			{
				int cell = grid[col + row*columns];
				if ((cell & LEFT) != 0) {
					this.points.addAll(getOnLeftSide(numberOfPoints, row, col));
				}
				if ((cell & RIGHT) != 0) {
					this.points.addAll(getOnRightSide(numberOfPoints, row, col));
				}
				if ((cell & UP) != 0) {
					this.points.addAll(getOnUpperSide(numberOfPoints, row, col));
				}
				if ((cell & DOWN) != 0) {
					this.points.addAll(getOnLowerSide(numberOfPoints, row, col));
				}
			}
		}
		return this.points;
	}

	Point createRandomPoint(double x1, double y1, double x2, double y2)
	{
		
		double x = x1 + Math.abs(x2 - x1) * rand.nextDouble();
		double m = (x - x1) * (y1 - y2) / (x1 - x2);
		double y = 0;
		if(Double.isNaN(m))
			y = y1 + rand.nextDouble()*Math.abs(y2-y1);
		else
			y = y1 + m;
		return new Point(x,y);
	}

	public List<Point> getOnLeftSide(int numberOfPoints, int row, int col)
	{
		Point[] points = new Point[numberOfPoints];
		for(int i=0; i<numberOfPoints; i++)
			points[i] = createRandomPoint(startX + size*col, startY + size*row,
					startX + size*col, startY + size*(row+1));

		return Arrays.asList(points);
	}

	public List<Point> getOnRightSide(int numberOfPoints, int row, int col)
	{
		Point[] points = new Point[numberOfPoints];
		for(int i=0; i<numberOfPoints; i++)
			points[i] = createRandomPoint(startX + size*(col+1), startY + size*row,
					startX + size*(col+1), startY + size*(row+1));

		return Arrays.asList(points);
	}

	public List<Point> getOnUpperSide(int numberOfPoints, int row, int col)
	{
		Point[] points = new Point[numberOfPoints];
		for(int i=0; i<numberOfPoints; i++)
			points[i] = createRandomPoint(startX + size*col, startY + size*row,
					startX + size*(col+1), startY + size*row);

		return Arrays.asList(points);
	}

	public List<Point> getOnLowerSide(int numberOfPoints, int row, int col)
	{
		Point[] points = new Point[numberOfPoints];
		for(int i=0; i<numberOfPoints; i++)
			points[i] = createRandomPoint(startX + size*col, startY + size*(row+1),
					startX + size*(col+1), startY + size*(row+1));

		return Arrays.asList(points);
	}

	

	private void plot()
	{
		Plot2DPanel plot = new Plot2DPanel();
		double[][] scatter = new double[this.points.size()][2];
		int i=0;
		for(Point point : this.points)
		{
			scatter[i][0] = point.x();
			scatter[i][1] = point.y();
			i++;
		}
		
		plot.addScatterPlot("Actual", scatter);
		plot.setFixedBounds(new double[]{0,0}, new double[]{100,100});
		JFrame frame = new JFrame("a plot panel");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(plot);
		frame.setSize(800, 800);
		frame.setVisible(true);
	}
	
	private void plotClusters()
	{
		J2DScene scene = J2DScene.createJ2DSceneInFrame();
		for(Point p : points)
			scene.addShape(new Circle(p,0.5), Color.BLACK, 0.3, true);

		scene.autoZoom();
		scene.centerCamera();
		
	}

	public static void main(String[] args) {
		Maze m = createRandomMaze(10,10);
		m.getOrthogonal(2);
		m.plotClusters();
		m.plot();
	}

}
