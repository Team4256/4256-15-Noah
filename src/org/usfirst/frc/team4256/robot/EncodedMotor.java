package org.usfirst.frc.team4256.robot;


public class EncodedMotor {
	ExtendedCANTalon motor;
	int position;
	boolean goingUp;
	
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
	
	public void setPosition(int p) {
		position = p;
		goingUp = (position-motor.getEncPosition() > 0);
	}
}
