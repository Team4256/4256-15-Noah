package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Timer;

public class AutoDrive {

    
    public static void turnLeft() {
    	go(.5, -.5, .5);
    }
    
    public static void turnRight() {
    	go(.5, .5, -.5);
    }
    
    public static void goFoward(double time) {
		go(.3, .3, .3);
		go(time-.3, .6, .6);
    }
    
    public static void go(double time, double lSpeed, double rSpeed) {
		Robot.drive.tankDrive(lSpeed, rSpeed);
		Timer.delay(time);
    }
}
