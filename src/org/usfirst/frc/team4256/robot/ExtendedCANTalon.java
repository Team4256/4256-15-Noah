package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class ExtendedCANTalon extends CANTalon implements MotorInterface {
//	boolean isReversed = false;
	private int direction = 1;
	private int encZeroValue = 0;
	
	public ExtendedCANTalon(int deviceNumber) {
		super(deviceNumber);
	}
	public ExtendedCANTalon(int deviceNumber, int controlPeriodMs) {
		super(deviceNumber, controlPeriodMs);
	}
	
	public void setInversed(boolean isInversed) {
//		isReversed = isInversed;
		direction = (isInversed? -1 : 1);
	}

	public void set(double outputValue) {
		super.set(direction*outputValue);
	}
	
	public void resetEncPosition() {
		encZeroValue = super.getEncPosition();
	}
	
	public int getEncPosition() {
		return direction*(super.getEncPosition()-encZeroValue);
	}
	


}
