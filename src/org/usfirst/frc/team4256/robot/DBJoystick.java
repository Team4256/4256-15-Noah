package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.RumbleType;

public class DBJoystick extends Joystick {
	public static double DEADBAND = .2;
	public static double DETENT = 0.7;
	public static int NORTH = 0;
	public static int NORTH_EAST = 45;
	public static int EAST = 90;
	public static int SOUTH_EAST = 135;
	public static int SOUTH = 180;
	public static int SOUTH_WEST = 225;
	public static int WEST = 270;
	public static int NORTH_WEST = 315;
	
	public DBJoystick(int port) {
		super(port);
	}
	
	protected DBJoystick(int port, int numAxisTypes, int numButtonTypes) {
		super(port, numAxisTypes, numButtonTypes);
	}

	public double getRawAxis(int axis) {
		return deadband(super.getRawAxis(axis));
	}
	
	public boolean axisPressed(int axis) {
		return super.getRawAxis(axis) > DETENT;
	}
	
	public double deadband(double input) {
		return (Math.abs(input) <= DEADBAND ? 0 : input);
	}
	
	public void rumble(float amount) {
		setRumble(RumbleType.kLeftRumble, amount);
    	setRumble(RumbleType.kRightRumble, amount);
	}
}


