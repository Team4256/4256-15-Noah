package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class OJ_CANTalon extends CANTalon implements OJ_Motor {
	boolean isReversed = false;
	
	public OJ_CANTalon(int deviceNumber) {
		super(deviceNumber);
	}
	public OJ_CANTalon(int deviceNumber, int controlPeriodMs) {
		super(deviceNumber, controlPeriodMs);
	}
	
	public void setInversed(boolean isInversed) {
		isReversed = isInversed;
	}

	public void set(double outputValue) {
		super.set(isReversed? -outputValue : outputValue);
	}

}
