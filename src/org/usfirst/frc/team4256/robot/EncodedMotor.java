package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class EncodedMotor {
	CANTalon motor;
	double speed;
	int position;
	boolean goingUp;
	
	public EncodedMotor(int port, double speed) {
		this.motor = new CANTalon(port);
		this.speed = speed;
	}
	
	public void update() {
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
