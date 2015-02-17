package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class ExtendedCANTalon extends CANTalon implements MotorInterface {
	boolean isReversed = false;
	private int encZeroValue = 0;
	
	public ExtendedCANTalon(int deviceNumber) {
		super(deviceNumber);
	}
	public ExtendedCANTalon(int deviceNumber, int controlPeriodMs) {
		super(deviceNumber, controlPeriodMs);
	}
	
	public void setInversed(boolean isInversed) {
		isReversed = isInversed;
	}

	public void set(double outputValue) {
		super.set(isReversed? -outputValue : outputValue);
	}
	
	public void resetEncPosition() {
		encZeroValue = super.getEncPosition();
	}
	
	public int getEncPosition() {
		return super.getEncPosition()-encZeroValue;
	}
	


}
