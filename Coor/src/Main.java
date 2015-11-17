import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Point2d;
import static choco.Choco.*;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.Line;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;


public class Main 
{
	//Git comment
	public static void testChoco()
	{
		Model m = new CPModel();
		Solver s = new CPSolver();
		IntegerVariable x1 = makeIntVar("x1",5,10);
		IntegerVariable x2 = makeIntVar("x2",5,10);
		IntegerVariable y1 = makeIntVar("y1",0,10);
		IntegerVariable y2 = makeIntVar("y2",0,10);
		m.addVariable(x1);
		m.addVariable(x2);
		m.addVariable(y1);
		m.addVariable(y2);
		double d = 5;
		m.addConstraint(
				eq(
						plus(
								power(minus(x1,x2), 2), 
								power(minus(y1,y2), 2)
								),

						(int)(d*d)
						)
				);
		s.read(m);
		s.solve();
		System.out.println(s.getVar(x1).getVal());
		System.out.println(s.getVar(x2).getVal());
		System.out.println(s.getVar(y1).getVal());
		System.out.println(s.getVar(y2).getVal());
	}

	public static void solve(double[][] adj, int[][] groups)
	{
		int N = adj.length;
		Model m = new CPModel();
		Solver s = new CPSolver();
		IntegerVariable[] x = makeIntVarArray("X",135,0,100);
		IntegerVariable[] y = makeIntVarArray("Y",135,0,100);
		for(int i=0; i<N; i++)
		{
			m.addVariable(x[i]);
			m.addVariable(y[i]);
		}

		//			minimize sum(i,j in N : adj[i][j] > 0) abs((x[i] - x[j])^2 + (y[i]-y[j])^2 - adj[i][j]^2);

		IntegerVariable[] dist = makeIntVarArray("dist", N*N,0,1000,Options.V_BOUND);
		IntegerVariable obj = makeIntVar("obj",0,135000,Options.V_BOUND, Options.V_OBJECTIVE);
		for(int i=0; i<N*N; i++)
			m.addVariable(dist[i]);

		m.addVariable(obj);
		m.addConstraint(eq(x[0],0));
		m.addConstraint(eq(y[0],90));
		m.addConstraint(eq(x[8],20));
		m.addConstraint(eq(y[8],90));
		m.addConstraint(eq(x[9],20));
		m.addConstraint(eq(y[9],20));

		//			x1: x[1] == 0;
		//			y1: y[1] == 90;
		//			x9: x[9] == 20;
		//			y9: y[9] == 90;
		//			x10: x[10] == 20;
		//			y10: y[10] == 20;


		for(int i=0; i<N; i++)
			for(int j=0; j<N; j++)
			{
//				if(i != j)
//					m.addConstraint(neq(minus( minus(x[i], x[j]), minus(y[i],y[j])),0));
				//					else if(adj[i][j] == -1)
				//						m.addConstraint(gt(plus(power(minus(x[i],x[j]), 2), power(Choco.minus(y[i],y[j]), 2)), 100));
				if(adj[i][j] > 0)
				{
					m.addConstraint(
							eq(
									plus(
											mult(minus(x[i],x[j]), minus(x[i],x[j])), 
											mult(minus(y[i],y[j]), minus(y[i],y[j]))
											),

									(int)(adj[i][j]*adj[i][j])
									)
							);
				}
				//					else if(adj[i][j] > 0)
				//						m.addConstraint(eq(dist[i*N+j], abs(minus(plus(power(minus(x[i],x[j]), 2), power(minus(y[i],y[j]), 2)), (int)(adj[i][j]*adj[i][j] + 0.5)))));
			}
		//			m.addConstraint(eq(obj,sum(dist)));
		s.read(m);
		s.solve();
		double[] X = new double[N];
		double[] Y = new double[N];

		for(int i=0; i<135; i++)
		{
			System.out.println("(" + s.getVar(x[i]).getVal() + ", " +  s.getVar(y[i]).getVal() + ")");
			X[i] = s.getVar(x[i]).getVal();
			Y[i] = s.getVar(y[i]).getVal();
		}
		//			for(int i=0; i<N; i++)
		//				for(int j=0; j<N; j++)
		//					System.out.println((i*N+j) + ": " + s.getVar(dist[i*135+j]).getVal() + " = " + (Math.pow(X[i] - X[j],2) + Math.pow(Y[i] - Y[j],2) - (int)(adj[i][j]*adj[i][j] + 0.5)));
		//			System.out.println(s.getVar(obj).getVal());
		double[][] points = new double[][]
				{{0,90},{2,90},{6,90},{8,90},{12,90},{14,90},{16,90},{18,90},{20,90},{20,20},{20,22},{20,25},{20,26},{20,28},{20,30},{20,32},{20,35},{20,40},{20,45},{20,50},{20,55},{20,60},{20,62},{20,65},{20,70},{20,75},{20,82},{20,86},{20,88},{20,92},{20,94},{20,98},{20,100},{22,100},{25,100},{30,100},{35,100},{40,100},{45,100},{50,100},{55,100},{60,100},{65,100},{68,100},{70,100},{72,96},{74,96},{76,96},{78,96},{80,96},{81,96},{82,96},{85,90},{85,89},{85,88},{80,80},{78,80},{75,80},{70,80},{65,80},{60,80},{95,75},{100,75},{85,75},{85,70},{85,65},{85,60},{86,60},{87,60},{88,60},{90,60},{94,60},{96,60},{98,60},{100,60},{80,57},{80,54},{80,49},{80,45},{80,40},{80,36},{80,35},{85,0},{85,2},{85,3},{85,4},{85,6},{85,7},{85,8},{85,10},{85,12},{85,13},{85,15},{85,16},{85,18},{85,19},{85,21},{85,22},{85,24},{85,25},{85,27},{85,27},{95,0},{95,1},{95,2},{95,3},{95,5},{95,6},{95,8},{95,9},{95,10},{95,12},{95,13},{95,15},{95,17},{95,18},{95,20},{100,0},{100,1},{100,2},{100,4},{100,5},{100,7},{100,8},{100,10},{100,11},{100,12},{100,14},{100,15},{100,16},{100,18},{100,19},{100,20},{100,25},{100,30}};
				Plot2DPanel plot = new Plot2DPanel();
				plot.addScatterPlot("Actual", Color.BLUE, points);

				plot.addScatterPlot("Estimated", Color.RED, X,Y);

				plot.setFixedBounds(new double[]{0,0}, new double[]{100,100});
				JFrame frame = new JFrame("a plot panel");
				frame.setContentPane(plot);

				frame.setSize(800, 800);
				frame.setVisible(true);
				//			System.out.println("(" + s.getVar(x[0]).getVal() + ", " +  s.getVar(y[0]).getVal() + ")");
				//			System.out.println("(" + s.getVar(x[8]).getVal() + ", " +  s.getVar(y[8]).getVal() + ")");
				//			System.out.println("(" + s.getVar(x[9]).getVal() + ", " +  s.getVar(y[9]).getVal() + ")");

				//			forall(i,j in N : adj[i][j] == -1)
				//			  sqrt((x[i] - x[j])^2 + (y[i]-y[j])^2) >= 10;
				//			  
				//		  forall(i,j in N : i!=j)
				//		    c2: (x[i] - x[j]) - (y[i] - y[j]) != 0;
				//		  forall(i in 1..13)
				//		  {
				//			forall(j,k in groups[i][1]..groups[i][2] : j!=k)
				//			{
				//				 x[j] == x[k] || y[j] == y[k];       
				//			}    
				//		  }


	}



	////	public static void jcop()
	//	{
	//		Store store = new Store(); 
	//		int [][]adj = new int[][] {
	//			{0, 5, 5, 5, 5},
	//			{5, 0, 7, 3, 4},
	//			{5, 7, 0, 4, 3},
	//			{5, 3, 4, 0, 1},
	//			{5, 4, 3, 1, 0},
	//		};
	//		double[][] adjF = new double[][]{
	//			{0.00, 5.00, 5.00, 5.00, 5.00},
	//			{5.00, 0.00, 7.07, 3.16, 4.47},
	//			{5.00, 7.07, 0.00, 4.47, 3.16},
	//			{5.00, 3.16, 4.47, 0.00, 1.41},
	//			{5.00, 4.47, 3.16, 1.41, 0.00},
	//		};
	//
	//		FloatVar[][] adjFsq = new FloatVar[5][5];
	//		IntVar[] x = new IntVar[5];
	//		IntVar[] y = new IntVar[5];
	//		IntVar[][] ox = new IntVar[5][5];
	//		IntVar[][] oy = new IntVar[5][5];
	//		IntVar[][] absox = new IntVar[5][5];
	//		IntVar[][] absoy = new IntVar[5][5];
	//		for(int i=0; i<5; i++)
	//		{
	//			x[i] = new IntVar(store, "x" + i, 0, 100);
	//			y[i] = new IntVar(store, "y" + i, 0, 100);
	//
	//		}
	//
	//		for(int i=0; i<5; i++)
	//			for(int j=0; j<5; j++)
	//			{
	//				adj[i][j] *=10;
	//				ox[i][j] = new IntVar(store, "ox"+i+","+j, -100, 100);
	//				oy[i][j] = new IntVar(store, "oy"+i+","+j, -100, 100);
	//				absox[i][j] = new IntVar(store, "ox"+i+","+j, 0, 100);
	//				absoy[i][j] = new IntVar(store, "oy"+i+","+j, 0, 100);
	//				store.impose(new AbsXeqY(ox[i][j], absox[i][j]));
	//				store.impose(new AbsXeqY(oy[i][j], absoy[i][j]));
	//			}
	//
	//		for(int i=0; i<4; i++)
	//			for(int j=i+1; j<5; j++)
	//			{
	//				store.impose(new IfThen(new XeqY(y[i],y[j]), new XneqY(x[i],x[j])));
	//				store.impose(new IfThen(new XeqY(x[i],x[j]), new XneqY(y[i],y[j])));
	//				store.impose(new XplusYeqC(absox[i][j], absoy[i][j], adj[i][j]));
	//				store.impose(new XplusYeqC(absox[j][i], absoy[j][i], adj[j][i]));
	//				store.impose(new XplusYeqZ(x[j], ox[i][j], x[i]));
	//				store.impose(new XplusYeqZ(y[j], oy[i][j], y[i]));
	//				store.impose(new XplusYeqZ(x[i], ox[j][i], x[j]));
	//				store.impose(new XplusYeqZ(y[i], oy[j][i], y[j]));
	//
	//
	//			}
	//
	//		Search<IntVar> search = new DepthFirstSearch<IntVar>(); 
	//		SelectChoicePoint<IntVar> select = 
	//				new InputOrderSelect<IntVar>(store,x, 
	//						new IndomainMin<IntVar>());  
	//		boolean result = search.labeling(store, select); 
	//
	//
	//	}



	public static void main(String[] args)
	{


		//		jcop1();
		double[][] points = new double[][]
				//{{0,0}, {0,50}, {50,0}, {30,40}, {40,30}};
				{{0,90},{2,90},{6,90},{8,90},{12,90},{14,90},{16,90},{18,90},{20,90},{20,20},{20,22},{20,25},{20,26},{20,28},{20,30},{20,32},{20,35},{20,40},{20,45},{20,50},{20,55},{20,60},{20,62},{20,65},{20,70},{20,75},{20,82},{20,86},{20,88},{20,92},{20,94},{20,98},{20,100},{22,100},{25,100},{30,100},{35,100},{40,100},{45,100},{50,100},{55,100},{60,100},{65,100},{68,100},{70,100},{72,96},{74,96},{76,96},{78,96},{80,96},{81,96},{82,96},{85,90},{85,89},{85,88},{80,80},{78,80},{75,80},{70,80},{65,80},{60,80},{95,75},{100,75},{85,75},{85,70},{85,65},{85,60},{86,60},{87,60},{88,60},{90,60},{94,60},{96,60},{98,60},{100,60},{80,57},{80,54},{80,49},{80,45},{80,40},{80,36},{80,35},{85,0},{85,2},{85,3},{85,4},{85,6},{85,7},{85,8},{85,10},{85,12},{85,13},{85,15},{85,16},{85,18},{85,19},{85,21},{85,22},{85,24},{85,25},{85,27},{85,27},{95,0},{95,1},{95,2},{95,3},{95,5},{95,6},{95,8},{95,9},{95,10},{95,12},{95,13},{95,15},{95,17},{95,18},{95,20},{100,0},{100,1},{100,2},{100,4},{100,5},{100,7},{100,8},{100,10},{100,11},{100,12},{100,14},{100,15},{100,16},{100,18},{100,19},{100,20},{100,25},{100,30}};
				int[][] groups = new int[][]{{1,9},{10,32},{33,45},{46,52},{53,55},{56,61},{62,64},{65,67},{68,75},{76,82},{83,102},{103,117},{118,135}};
				double[] x = new double[]{0,1,2,3,4,5,6,7,20,20,41,5,6,7,8,9,10,11,12,13,14,15,16,21,22,23,3,0,1,19,2,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,7,8,9,10,11,12,13,14,15,16,17,18,74,19,20,22,23,4,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,24,25,26,27,28,29,30,56,31,32,33,34,35,36,37,38,39,40,};
				double[] y = new double[]{90,90,90,90,90,90,90,90,90,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,3,21,22,23,24,25,26,27,28,29,30,31,32,33,34,2,35,36,37,38,39,40,41,42,43,44,45,47,48,49,50,46,51,52,53,54,55,56,57,59,61,62,63,58,60,64,65,66,67,68,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,69,80,71,72,81,73,74,82,75,76,77,78,91,79,92,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
				System.out.println(points.length);
				Point2d[] P = new Point2d[points.length];
				Point2d[] Q = new Point2d[points.length];

				//			ArrayList<Double> W= new ArrayList<Double>();
				for(int i=0; i<points.length; i++)
				{
					P[i] = new Point2d(points[i][0], points[i][1]);
					Q[i] = new Point2d(x[i], y[i]);
					//					System.out.println(P[i].distance(Q[i]));
				}
				//

				////
				int E = 0;
				double [][]adj = new double[points.length][points.length]; 
				for(int i=0; i<points.length-1; i++)
					for(int j=i+1; j<points.length; j++)
					{
						double d = P[i].distance(P[j]); 
						if(i == j)
						{
							adj[i][j] = 0.0;
							adj[j][i] = 0.0;
						}
						else if(d <= 10)
						{

							adj[i][j] = d;
							adj[j][i] = d;
							E++;
						}

						else
						{
							adj[i][j] = -1.0;
							adj[j][i] = -1.0;
						}

						//										if(E%5 == 0)
						//											System.out.println();
						//										System.out.printf("<%d, %d, %.2f>, ", i, j, adj[i][j]);
					}




				//									for(int i=0; i<points.length; i++)
				//									{
				//										System.out.print("[");
				//										for(int j=0; j<points.length; j++)
				//										{
				//												System.out.printf("%.2f, ", adj[i][j]);
				//										}
				//										System.out.print("]\n");
				//									}
				//				
				//									System.out.println(E);
													solve(adj,groups);
//				testChoco();


	}


}
