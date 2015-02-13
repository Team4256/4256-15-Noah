package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class OJ {
	
	////////////////MOTOR////////////////
	
	public static void runMotor(boolean condition, OJ_Motor motor, double speed) {
    	if(condition) {
    		motor.set(speed);
    	}else{
    		motor.set(0);
    	}
    }
	
	public static void runMotor(boolean fwdCondition, boolean reverseCondition, OJ_Motor motor, double speed) {
    	if(fwdCondition) {
    		motor.set(speed);
    	}else if(reverseCondition) {
    		motor.set(-speed);
    	}else{
    		motor.set(0);
    	}
    }
	
	public static void runMotor(Joystick j, int button, OJ_Motor motor, double speed) {
		runMotor(j.getRawButton(button), motor, speed);
    }
	
	public static void runMotor(Joystick j, int fwdButton, int reverseButton, OJ_Motor motor, double speed) {
		runMotor(j.getRawButton(fwdButton), j.getRawButton(reverseButton), motor, speed);
    }
    
    public static void runMotor(Toggle toggle, OJ_Motor motor, double speed) {
    	runMotor(toggle.getState(), motor, speed);
    }
    
    //config
    public static void configMotorPorts(double testSpeed) {
    	new CANTalon((int) SmartDashboard.getNumber("PORT")).set(testSpeed);
    }
    
    ////////////////SOLENOID////////////////
    
    public static void runSolenoid(boolean fwdCondition, boolean reverseCondition, DoubleSolenoid solenoid) {
    	if(fwdCondition) {
        	solenoid.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
    	}else if(reverseCondition) {
    		solenoid.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse);
    	}
    }
    
    public static void runSolenoid(Joystick j, int fwdButton, int reverseButton, DoubleSolenoid solenoid) {
    	runSolenoid(j.getRawButton(fwdButton), j.getRawButton(reverseButton), solenoid);
    }
    
    public static void runSolenoid(Toggle toggle, DoubleSolenoid solenoid) {
    	if(toggle.getState()) {
        	solenoid.set(DoubleSolenoid.Value.kForward);
    	}else{
    		solenoid.set(DoubleSolenoid.Value.kReverse);
    	}
    }
    
    ////////////////LED////////////////
    
    public static void runLED(Toggle toggle, Relay led) {
    	if(toggle.getState()) {
        	led.set(Relay.Value.kForward);
    	}else{
    		led.set(Relay.Value.kReverse);
    	}
    }
}
