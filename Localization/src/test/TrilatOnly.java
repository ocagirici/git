package test;

import java.util.Map;

import ProGAL.geom2d.Point;
import entity.WSN;
import environment.Plane;

public class TrilatOnly 
{
	public static void main(String[] args)
	{
		Plane plane = new Plane(1);
		WSN wsn = new WSN(plane.createRandom(100));
		Map<Integer, Point> actualPf = wsn.pointFormation();
		Map<Integer, Point> pf = wsn.trilatOnly();
		WSN wsn2 = new WSN(wsn);
		Map<Integer, Point> actualPf2 = wsn2.pointFormation();
		Map<Integer, Point> pf2 = wsn2.localize();
		System.out.println(actualPf.size() + " " + pf.size());
		System.out.println(actualPf2.size() + " " + pf2.size());
	}

}
