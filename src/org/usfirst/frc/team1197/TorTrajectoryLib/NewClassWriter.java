package org.usfirst.frc.team1197.TorTrajectoryLib;

import java.io.IOException;
import java.text.DecimalFormat;

public class NewClassWriter extends StringWriter{
	private String packageName;
	private String className;
	private final DecimalFormat df = new DecimalFormat(".####");
	
	public NewClassWriter(TorTrajectory t, String p, String c) {
		super(t);
		packageName = p;
		className = c;
		
	}

	@Override
	public void write() throws IOException {
		s = new String("package ").concat(packageName).concat(";\n\n");
		s = s.concat("public class ").concat(className).concat(" extends TorTrajectory {\n\n");
		fileWriter.write(s, 0, s.length());
		s = new String("\tpublic ").concat(className).concat("() {\n");
		s = s.concat("\t\tsuper(").concat(df.format(trajectory.goal_pos()))
			 .concat(", ").concat(df.format(trajectory.goal_head())).concat(");\n");
		s = s.concat("\t\ttime.clear();\n\t\ttranslation.clear();\n\t\trotation.clear();\n");
		fileWriter.write(s, 0, s.length());
		
		for (long time = 0; time <= trajectory.totalTime(); time += dt) {
			lookUpData(time);
			s = new String("\n\t\ttime.add((long) ").concat(String.valueOf(time)).concat(");\n");
			
			s = s.concat("\t\ttranslation.add(new MotionState1D(");
			s = s.concat(df.format(pos)).concat(", ");
			s = s.concat(df.format(vel)).concat(", ");
			s = s.concat(df.format(acc)).concat("));\n");
			
			s = s.concat("\t\trotation.add(new MotionState1D(");
			s = s.concat(df.format(head)).concat(", ");
			s = s.concat(df.format(omg)).concat(", ");
			s = s.concat(df.format(alf)).concat("));\n");
			fileWriter.write(s, 0, s.length());
		}
		s = new String("\t}\n\n}");
		fileWriter.write(s, 0, s.length());
	}
}
