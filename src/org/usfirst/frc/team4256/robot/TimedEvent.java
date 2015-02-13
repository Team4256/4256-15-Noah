package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Timer;

public abstract class TimedEvent {
	double startTime;
	double endTime;
	
	public TimedEvent(double startTime, double duration, boolean fromEnd) {
		if(fromEnd) {
			setTimeRange(230-startTime, duration);
		}else{
			setTimeRange(startTime, duration);
		}
	}
	
	private void setTimeRange(double startTime, double duration) {
		this.startTime = startTime;
		endTime = startTime+duration;
	}
	
	/**
	 * MUST be called in teleop to update event
	 */
	public void check() {
		double time = Timer.getMatchTime();
		//if started
		if(startTime <= time) {
			if(endTime <= time) {
				run();
			}else{
				end();
			}
		}
	}
	
	public abstract void run();
	public abstract void end();
}
