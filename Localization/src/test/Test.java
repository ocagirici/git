package test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.Line;

import ProGAL.geom2d.Point;
import entity.WSN;
import environment.Plane;

public class Test
{
	
	public static void plot(Map<Integer,Point> pf,  Map<Integer, Point> actualPf)
	{
		double[][] estimated = new double[pf.size()][2];
		Plot2DPanel plot = new Plot2DPanel();
		int i=0;
		for(Map.Entry<Integer, Point> p : actualPf.entrySet())
		{
			plot.addPlotable(new Line(Color.BLACK, p.getValue().getCoords(), pf.get(p.getKey()).getCoords()));
			estimated[i++] = p.getValue().getCoords();
			
		}
		plot.addScatterPlot("Actual", estimated);
		plot.setFixedBounds(new double[]{0,0}, new double[]{100,100});
		JFrame frame = new JFrame("a plot panel");
		frame.setContentPane(plot);
		
		frame.setSize(800, 800);
		frame.setVisible(true);
	}
	
	
	
	public static void main(String[] args)
	{
//		for(int i=0; i<10; i++)
//		{
//			Random rand = new Random();
//			double dist = 4;
//			double fR = 0.022*Math.log(1+dist) - 0.038;
//			dist = dist + rand.nextGaussian()*0.03 + fR;
//			System.out.println(dist);
//		}
		double[][] coor = new double[2500][2];
		
		int c=0;
		for(int i=1; i<=100; i+=2)
		{
			for(int j=(i%4)/2+1; j<=100; j+=2)
				coor[c++] = new double[]{j,i};
		}
		
		WSN.R = 2.24;
		WSN wsn = new WSN(coor);
		wsn.plotGraph();
		plot(wsn.localize(), wsn.pointFormation());
//		double[][] coor = new double[][]{
//			{1,3},
//			{5,0},
//			{7,4},
//			{6,8},
//			{1,8},
//			{4,4}};
//		
//		WSN wsn = new WSN(coor);
//		wsn.plotGraph();
//		Map<Integer, Point> actualPf = wsn.pointFormation();
//		Map<Integer, Point> pf = wsn.trilatOnly();
//		System.out.println(actualPf.size() + " " + pf.size());
		
	}

}
