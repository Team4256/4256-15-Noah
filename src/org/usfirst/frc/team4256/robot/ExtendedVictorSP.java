package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.VictorSP;

public class ExtendedVictorSP extends VictorSP implements MotorInterface {

	public ExtendedVictorSP(int channel) {
		super(channel);
	}

}
