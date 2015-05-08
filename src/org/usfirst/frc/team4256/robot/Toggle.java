package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Toggle {
	private Joystick joystick;
	private int button;
	private boolean value = false;
	private boolean lastValue = false;
	boolean state = false;
	
	
	public Toggle(Joystick joystick, int button) {
		this.joystick = joystick;
		this.button = button;
	}
	
	/**
	 * Updates and returns the button's toggle state
	 */
	public boolean getState() {
		return getState(joystick);
	}
	
	public boolean getState(Joystick j) {
		value = j.getRawButton(button);
		if(value && value!=lastValue)
			state = !state;
		lastValue = value;
		return state;
	}
	
	public void setButton(int button) {
		this.button = button;
	}
}
