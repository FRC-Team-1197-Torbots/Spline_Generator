package org.usfirst.frc.team1197.TorTrajectoryLib;

public class PivotTrajectory extends TorTrajectory {
	
	public PivotTrajectory(double goal){
		super(0.0, goal * (Math.PI/180.0));
		build(goal_head, max_omg, max_alf, max_jeta, heading, omega, alpha);
	}
	
	public double lookUpHeading(long t){
		int i = time.indexOf(t);
		if(i == -1){
			return goal_pos;
		}
		return heading.elementAt(i);
	}
	public double lookUpOmega(long t){
		int i = time.indexOf(t);
		if(i == -1){
			return 0.0;
		}
		return omega.elementAt(i);
	}
	public double lookUpAlpha(long t){
		int i = time.indexOf(t);
		if(i == -1){
			return 0.0;
		}
		return alpha.elementAt(i);
	}
	
	public double lookUpPosition(long t){
		return 0;
	}
	public double lookUpVelocity(long t){
		return 0;
	}
	public double lookUpAcceleration(long t){
		return 0;
	}
}