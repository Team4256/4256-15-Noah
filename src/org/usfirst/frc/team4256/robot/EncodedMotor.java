package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class EncodedMotor {
	ExtendedCANTalon motor;
	int position;
	boolean goingUp;
	int minEncPosition;
	int maxEncPosition;
	int encRange;
	
	public EncodedMotor(int port) {
		this.motor = new ExtendedCANTalon(port);
	}
	
	public void update(double speed) {
    	int dist = position-motor.getEncPosition();
    	if(dist < 0 && !goingUp) {
    		motor.set(-speed);
    	}else if(dist > 0 && goingUp) {
    		motor.set(speed);
    	}else{
    		motor.set(0);
    	}
	}
	
	public void setEncRange(int min, int max) {
		minEncPosition = min;
		maxEncPosition = max;
		encRange = max-min;
	}
	
	public void setEncPosition(int p) {
		position = p;
		goingUp = (position-motor.getEncPosition() > 0);
	}
	
	public int getEncPosition() {
		return motor.getEncPosition();
	}
	
	public int getPositionAsPercent() {
		return 100*(getEncPosition()-minEncPosition)/encRange;
	}
	
	public void displayPositionAsPercent(String smartDashboardVarName) {
		SmartDashboard.putString(smartDashboardVarName, getPositionAsPercent()+"%");
	}
}
