package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Joystick;

public class DBJoystick extends Joystick {
	public double DEADBAND = .2;
	
	public DBJoystick(int port) {
		super(port);
	}
	
	protected DBJoystick(int port, int numAxisTypes, int numButtonTypes) {
		super(port, numAxisTypes, numButtonTypes);
	}

	public double getRawAxis(int axis) {
		return deadband(super.getRawAxis(axis));
	}
	
	public double deadband(double input) {
		return (Math.abs(input) <= DEADBAND ? 0 : input);
	}

	public void runSharedFunctions(DBJoystick xboxgun, int liftbutton, int otherButton) {
		DBJoystick xboxdrive = null;
		xboxgun = xboxdrive;
		xboxdrive = xboxgun;
		// TODO Auto-generated method stub
		
	}

	
		
		
	}


