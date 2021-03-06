package org.usfirst.frc.team1197.TorTrajectoryLib.spline_generator;

import java.util.List;

public class SmoothSpline extends TorSpline {

	private TorSpline inputSpline;
	private HalfSpiralSpline optimizingSpline = new HalfSpiralSpline();
	private double computedPivotX;
	private double computedPivotY;

	public SmoothSpline(TorSpline s) {
		super(s.externalTranslation().getEntry(0), s.externalTranslation().getEntry(1), s.externalRotation());
		inputSpline = s.clone();
		int input_index, output_index = 1;
		if (!SplineErrMsg.pathIllegalAlert(inputSpline.path)) {
			this.add(inputSpline.path.get(0).clone());
			for (input_index = 1; input_index < inputSpline.path.size(); input_index++) {
				if (inputSpline.path.get(input_index).type() == SegmentType.ARC) {
					input_index++; // Can't just add an arc, so skip to the next
									// segment.
					output_index++;
				}
				if (inputSpline.path.get(input_index).type() == SegmentType.LINE
						&& inputSpline.path.get(input_index - 1).type() == SegmentType.ARC
						&& inputSpline.path.get(input_index - 2).type() == SegmentType.LINE) {
					replaceArc(inputSpline.path, this.path, input_index, output_index);
					output_index++;
				} else if (inputSpline.path.get(input_index).type() == SegmentType.LINE
						&& inputSpline.path.get(input_index - 1).type() == SegmentType.LINE) {
					spliceLines(inputSpline.path, this.path, input_index, output_index);
					output_index += 2; // since we added an extra path segment
										// on the output path, but not the input
				} else {
					SplineErrMsg.unexpectedSegmentAlert(inputSpline.path.get(input_index).type());
					// (If this happens, it's 100% my (Joe's) fault and I'm very
					// sorry.)
					break;
				}
			}
		}
		else{
			System.exit(1);
		}
	}

	private void replaceArc(List<PathSegment> inputPath, List<PathSegment> outputPath, int inputIndex,
			int outputIndex) {
		int inputLine1 = inputIndex - 2;
		int inputArc = inputIndex - 1;
		int inputLine2 = inputIndex;
		int outputLine1 = outputIndex - 2;
		// int outputSpiralSpline = outputIndex - 1;
		// int outputLine2 = outputIndex;
		if (!SplineErrMsg.arcTooTightAlert(inputPath, inputArc)) {
			double angle = inputPath.get(inputArc).totalAngle();
			double curvature = inputPath.get(inputArc).curvatureAt(0.0);
			double radius = secantMethod(Math.abs(angle), Math.abs(1.0 / curvature));
			if (!SplineErrMsg.tooShortAlert(outputPath, outputLine1, inputLine1, computedPivotX)
					&& !SplineErrMsg.tooShortAlert(inputPath, inputLine2, inputLine2, computedPivotX)
					&& !SplineErrMsg.arcTooTightAlert(inputPath, inputArc)
					&& !SplineErrMsg.tangencyViolatedAlert(inputPath, inputLine2)) {
				outputPath.get(outputLine1).addToLength(-computedPivotX);
				this.addToLength(-computedPivotX);
				this.add(new SpiralSpline(angle, radius));
				this.add(inputPath.get(inputLine2).cloneTrimmedBy(computedPivotX));
			}
			else{
				System.exit(1);
			}
		}
		else{
			System.exit(1);
		}
	}

	private void spliceLines(List<PathSegment> inputPath, List<PathSegment> outputPath, int inputIndex,
			int outputIndex) {
		int inputLine1 = inputIndex - 2;
		int inputLine2 = inputIndex;
		int outputLine1 = outputIndex - 1;
		// int outputSpiralSpline = outputIndex;
		// int outputLine2 = outputIndex + 1;
		double angle = inputPath.get(inputLine2).internalRotation();
		SpiralSpline newSpline = new SpiralSpline(angle);
		double length_to_cut = Math.abs(newSpline.pivot_x())
				+ Math.abs(newSpline.pivot_y()) / Math.tan(0.5 * (Math.PI - Math.abs(angle)));
		// TODO: Handle segments with 0 internal
		// rotation--------------------------^^^
		// Probably add a trivial spiral spline, i.e. a short line segment
		if (!SplineErrMsg.tooShortAlert(outputPath, outputLine1, inputLine1, length_to_cut)
				&& !SplineErrMsg.tooShortAlert(inputPath, inputLine2, inputLine2, length_to_cut)) {
			outputPath.get(outputLine1).addToLength(-length_to_cut);
			this.addToLength(-length_to_cut);
			this.add(newSpline);
			this.add(new LineSegment((inputPath.get(inputLine2).length() - length_to_cut), 0.0));
		}
		else{
			System.exit(1);
		}
	}

	private double secantMethod(double angle, double radius) {
		int max_iterations = 100;
		double x = radius;
		double x_prev = radius * 1.001;
		double f = rootFunction(angle, x, radius);
		double f_prev = rootFunction(angle, x_prev, radius);
		double q;
		int i;
		for (i = 0; i < max_iterations; i++) {
			f = rootFunction(angle, x, radius);
			if (Math.abs(f) <= absoluteAccuracy)
				break;
			q = (f - f_prev) / (x - x_prev);
			x_prev = x;
			f_prev = f;
			x = x - f / q;
		}
		return x;
	}

	private double rootFunction(double angle, double radius, double targetY) {
		optimizingSpline.buildFirstHalfOnly(angle, radius);
		computedPivotX = optimizingSpline.pivot_x();
		computedPivotY = optimizingSpline.pivot_y();
		return targetY - computedPivotY;
	}

}
