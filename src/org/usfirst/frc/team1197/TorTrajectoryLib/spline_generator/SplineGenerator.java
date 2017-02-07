package org.usfirst.frc.team1197.TorTrajectoryLib.spline_generator;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.usfirst.frc.team1197.TorTrajectoryLib.velocity_plotter.VelocityGraph;
import org.usfirst.frc.team1197.TorTrajectoryLib.velocity_plotter.VelocityGraph.motionType;

public class SplineGenerator {

	private static final double ds = 0.005;
	private static TorSpline inputSpline;
	private static SplineTrajectory trajectory;
	private static VelocityGraph translationGraph;
	private static VelocityGraph rotationGraph;
//	private static TrajectoryWriter writer;

	public static void main(final String[] args) {

		final PathGraph graph = new PathGraph();
		graph.display();
		TorSpline move1Left = new TorSpline(0.375, 6.070, 0.0);
		move1Left.add(new LineSegment(1.697, 0.0));
		move1Left.add(new ArcSegment(0.943, 60*(Math.PI/180.0)));
		move1Left.add(new LineSegment(0.5, 0.0));
		
//		TorSpline move2Left = new TorSpline(3.139, 5.165, 2.0*Math.PI/3.0);
//		move2Left.add(new LineSegment(0.5, 0.0));
//		move2Left.add(new ArcSegment(0.75, 150.0*(Math.PI/180.0)));
//		move2Left.add(new LineSegment(3.025, 0.0));
//		move2Left.add(new ArcSegment(1.654, 40.0*(Math.PI/180.0)));
//		move2Left.add(new LineSegment(0.5, 0.0));
		
		inputSpline = new TorSpline(0.375, 6.070, 0.0);
		inputSpline.add(new LineSegment(1.697, 0.0));
		inputSpline.add(new ArcSegment(0.943, -60*(Math.PI/180.0)));
		inputSpline.add(new LineSegment(0.5, 0.0));
		System.out.println(inputSpline.length());
//		inputSpline = new TorSpline(0.0, 0.0, 0.0);
//		inputSpline.add(move1Left);

		RealVector P = new ArrayRealVector(new double[] { 0.0, 0.0 });
		for (double s = 0.0; s <= inputSpline.length(); s += ds) {
			P = inputSpline.positionAt(s);
//			System.out.println(P);
			graph.inputPath.add(P.getEntry(0), P.getEntry(1));
		}

		trajectory = new SplineTrajectory(inputSpline);
		translationGraph = new VelocityGraph(trajectory, motionType.Translation);
		rotationGraph = new VelocityGraph(trajectory, motionType.Rotation);
		translationGraph.display();
		rotationGraph.display();
		translationGraph.plotData();
		rotationGraph.plotData();

//		writer = new TrajectoryWriter(trajectory, new String("testSpline"));
//		try {
//			writer.writeDotJava(new String("org.usfirst.frc.team1197.robot"));
////			writer.writeDotCSV();
//		} catch (IOException x) {
//			System.err.format("IOException: %s%n", x);
//		}
	}

}
