package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Utility {
	
	////////////////MOTOR////////////////
	
	public static void runMotor(boolean condition, MotorInterface motor, double speed, double revSpeed) {
    	if(condition) {
    		motor.set(speed);
    	}else{
    		motor.set(revSpeed);
    	}
    }
	
	public static void runMotor(boolean fwdCondition, boolean reverseCondition, MotorInterface motor, double speed) {
    	if(fwdCondition) {
    		motor.set(speed);
    	}else if(reverseCondition) {
    		motor.set(-speed);
    	}else{
    		motor.set(0);
    	}
    }
	
	public static void runMotor(Joystick j, int button, MotorInterface motor, double speed, double revSpeed) {
		runMotor(j.getRawButton(button), motor, speed, revSpeed);
    }
	
	public static void runMotor(Joystick j, int fwdButton, int reverseButton, MotorInterface motor, double speed) {
		runMotor(j.getRawButton(fwdButton), j.getRawButton(reverseButton), motor, speed);
    }
    
    public static void runMotor(Toggle toggle, MotorInterface motor, double speed, double revSpeed) {
    	runMotor(toggle.getState(), motor, speed, revSpeed);
    }
    
    //config
    public static void configMotorPorts(double testSpeed) {
    	new CANTalon((int) SmartDashboard.getNumber("PORT")).set(testSpeed);
    }
    
    public static void configVictorPorts(double testSpeed) {
    	new VictorSP ((int) SmartDashboard.getNumber("VICTOR_PORT")).set(testSpeed);
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
    
    public static void runSolenoid(boolean condition, DoubleSolenoid solenoid) {
    	if(condition) {
        	solenoid.set(DoubleSolenoid.Value.kForward);
    	}else{
    		solenoid.set(DoubleSolenoid.Value.kReverse);
    	}
    }
    
    //config
    public static void configSolenoidPorts(double delay) {
    	for(int x=0; x<10; x++) {
    		for(int y=0; y<5; y+=2) {
    			for(int z=1; z<5; z+=2) {
    				DoubleSolenoid s = new DoubleSolenoid(x, y, z);
    				s.set(DoubleSolenoid.Value.kForward);
    				Timer.delay(delay);
    				s.set(DoubleSolenoid.Value.kReverse);
    	        	Timer.delay(delay);
    	    		SmartDashboard.putNumber("S Module", x);
    	        	SmartDashboard.putNumber("S Forward Channel", y);
    	        	SmartDashboard.putNumber("S Reverse Channel", z);
    			}
    		}
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
