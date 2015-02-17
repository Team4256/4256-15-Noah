package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Timer;

public class AutoDrive {

	////////////////TIME BASED////////////////
    public static void turnLeftTimeBased() {
    	goTimeBased(.5, -.5, .5);
    }
    
    public static void turnRightTimeBased() {
    	goTimeBased(.5, .5, -.5);
    }
    
    public static void goFowardTimeBased(double time) {
    	goTimeBased(.3, .3, .3);
		goTimeBased(time-.3, .6, .6);
    }
    
    public static void goTimeBased(double time, double lSpeed, double rSpeed) {
		Robot.drive.tankDrive(lSpeed, rSpeed);
		Timer.delay(time);
    }
    
    ////////////////ENCODER BASED////////////////
    static int ENC_ACCURACY_RANGE = 10;
    static int RIGHT_ANGLE_TURN_TICKS = 10000;
    
    public static void goFoward(int ticks, double speed) {
    	go(ticks, ticks, speed, speed, 1, 1);
    }
    
    public static void goReverse(int ticks, double speed) {
    	go(-Math.abs(ticks), -Math.abs(ticks), -Math.abs(speed), -Math.abs(speed), -1, -1);
    }
    
    public static void turnLeft(double speed) {
    	go(-RIGHT_ANGLE_TURN_TICKS, RIGHT_ANGLE_TURN_TICKS, -speed, speed, -1, 1);
    }
    
    public static void turnRight(double speed) {
    	go(RIGHT_ANGLE_TURN_TICKS, -RIGHT_ANGLE_TURN_TICKS, speed, -speed, 1, -1);
    }
    
    /**
     * 
     * @param lTicks
     * @param rTicks
     * @param lSpeed
     * @param rSpeed
     * @param leftDirection = direction of left motors represented as 1 or -1
     * @param rightDirection = direction of right motors represented as 1 or -1
     */
    private static void go(int lTicks, int rTicks, double lSpeed, double rSpeed,
    		int leftDirection, int rightDirection) {
    	resetEncodersToZero();
    	//start motors
    	setMotors(lSpeed, Robot.leftBack, Robot.leftFront);
    	setMotors(rSpeed, Robot.rightBack, Robot.rightFront);
    	//wait till distance is reached
    	boolean leftNotFinished = true;
    	boolean rightNotFinished = true;
    	while(leftNotFinished || rightNotFinished) {
    		if(leftNotFinished) {
    			int leftDistanceLeft = getAverageDistance(lTicks, Robot.leftBack, Robot.leftFront);
    			leftNotFinished = (leftDistanceLeft*leftDirection < ENC_ACCURACY_RANGE);
    		}
    		if(rightNotFinished) {
    			int rightDistanceLeft = getAverageDistance(rTicks, Robot.rightBack, Robot.rightFront);
    			rightNotFinished = (rightDistanceLeft*rightDirection < ENC_ACCURACY_RANGE);
    		}
    	}
    	//stop motors
    	setMotors(0, Robot.leftBack, Robot.leftFront);
    	setMotors(0, Robot.rightBack, Robot.rightFront);
    }

	private static int getAverageDistance(int ticks, ExtendedCANTalon m1, ExtendedCANTalon m2) {
    	return ticks-(m1.getEncPosition()+m2.getEncPosition())/2;
    }
    
    private static void setMotors(double speed, ExtendedCANTalon m1, ExtendedCANTalon m2) {
    	m1.set(speed);
    	m2.set(speed);
    }
    
    private static void resetEncodersToZero() {
		Robot.leftBack.resetEncPosition();
		Robot.leftFront.resetEncPosition();
		Robot.rightBack.resetEncPosition();
		Robot.rightFront.resetEncPosition();
	}
}
