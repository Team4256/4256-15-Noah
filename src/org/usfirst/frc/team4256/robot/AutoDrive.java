package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDrive {
	public static int VERTICAL_LIFT_UP_POSITION = 2000;//value needs testing
	public static int VERTICAL_LIFT_DOWN_POSITION = 0;//value needs testing
	public static int STACKER_TOTE_LIFT_LEVEL_POSITION = 2000;//value needs testing
	public static int STACKER_TOTE_BOTTOM_POSITION = 0;//value needs testing
	public static double TOTE_INTAKE_TIME = 2.5;
	public static double TOTE_SPIT_TIME = 1;
	
	private static int TOTE_TO_TOTE_DISTANCE = 2750; //2ft 9 inches	-calculated by mr ies's expertise
	public static int AUTOZONE_DISTANCE = 4200;
	
    public static ExecutorService exeSrvc = Executors.newCachedThreadPool();
	
    ////////////////SYNCHRONIZED MOVES////////////////
    public static void syncRecycleBinAndToteIntake() {
    	syncRecycleBin();
		syncToteIntake();
		Timer.delay(1); //1.5 seconds less than tote intake time!
    }
    
    public static void syncRecycleBin() {
		exeSrvc.execute(new Runnable() {
			public void run() {
				moveMotorTimeBased(Robot.verticalLift, 1.5, -1);
			}});
    }
    
    public static void syncToteIntake() {
		exeSrvc.execute(new Runnable() {
			public void run() {
				intakeTote();
			}});
    }
    
    public static void syncToteStackerLiftDown() {
		exeSrvc.execute(new Runnable() {
			public void run() {
				stackerToteLiftDown();
			}});
    }
    
    public static void syncToteStackerLiftDownAndTo(final int level) {
//    	exeSrvc.execute(new Runnable() {
//			public void run() {
				//if(level >= 2) {
					stackerToteLiftDown();
				//}
				stackerToteLiftUp(level);
//			}});
    }
    
	////////////////COMBO MOVES////////////////
    public static void liftAndGoToNextTote(int level, double speed) {
//    	syncToteIntake();
    	intakeTote();
    	goToNextTote(level, speed);
    }
	public static void goToNextTote(int level, double speed) {
		goFoward(TOTE_TO_TOTE_DISTANCE-500, speed);
		openArms();
		syncToteStackerLiftDownAndTo(level);
		goFoward(500, speed);
	}
	
	public static void goFowardToAutozoneAndDeploy(boolean deployTotes, double distance, double turnAngle, double speed) {
		goFoward((int) (distance), speed);
		turnRight(turnAngle);
		if(deployTotes) {
			deployTotes(distance, turnAngle, speed);
			goReverse(200, speed);
		}
	}
	
	public static void goBackwardToAutozoneAndDeploy(boolean deployTotes, double distance, double turnAngle, double speed) {
		goReverse((int) (distance), speed);
		turnRight(turnAngle);
		if(deployTotes) {
			deployTotes(distance, turnAngle, speed);
			goFoward(1000, speed);
		}
	}
	
	//private
	private static void deployTotes(double distance, double turnAngle, double speed) {
		goFoward(1200, speed);
		spitTote();
	}
	
	////////////////TOTE INTAKE////////////////
	public static void intakeTote() {
		Robot.leftArm.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
		Robot.rightArm.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
		Robot.wheelIntake.set(-Robot.WHEEL_INTAKE_SPEED);
		Robot.toteRoller.set(Robot.TOTE_ROLLER_SPEED);
		Timer.delay(TOTE_INTAKE_TIME);
		Robot.wheelIntake.set(0);
		Robot.toteRoller.set(0);
    }
	
	public static void openArms() {
		Robot.leftArm.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse);
		Robot.rightArm.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse);
	}
	
	public static void spitTote() {
		openArms();
		Robot.wheelIntake.set(Robot.WHEEL_INTAKE_SPEED);
		Robot.toteRoller.set(-Robot.TOTE_ROLLER_SPEED);
		Timer.delay(TOTE_SPIT_TIME);
		Robot.wheelIntake.set(0);
		Robot.toteRoller.set(0);
    }
	
	////////////////ENCODED/LIMIT SWITCH LIFTS////////////////
	public static void verticalLiftUp() {
		moveEncodedMotorUp(Robot.verticalLift, VERTICAL_LIFT_UP_POSITION, Robot.upperLimitSwitch, Robot.VERTICAL_LIFT_SPEED);
    }
	
	public static void verticalLiftDown() {
//		moveEncodedMotorDown(Robot.verticalLift, VERTICAL_LIFT_DOWN_POSITION, -Robot.VERTICAL_LIFT_SPEED);
		moveMotorToLimitSwitch(Robot.verticalLift, Robot.lowerLimitSwitch, -Robot.VERTICAL_LIFT_SPEED);
    }
	
	public static void stackerToteLiftUp(int level) {
		moveEncodedMotorUp(Robot.stackerToteLift, STACKER_TOTE_BOTTOM_POSITION + STACKER_TOTE_LIFT_LEVEL_POSITION*level, Robot.upperStackerLimitSwitch,
				-Robot.STACKER_TOTE_SPEED/5); //SLO MO
    }
	
	public static void stackerToteLiftDown() {
//		moveEncodedMotorDown(Robot.stackerToteLift, STACKER_TOTE_BOTTOM_POSITION - STACKER_TOTE_LIFT_LEVEL_POSITION*level, Robot.STACKER_TOTE_SPEED);
		moveMotorToLimitSwitch(Robot.stackerToteLift, Robot.lowerStackerLimitSwitch, Robot.STACKER_TOTE_SPEED/5); //SLO MO
    }
	
	public static void moveMotorTimeBased(EncodedMotor m, double time, double speed) {
    	m.set(speed);
    	Timer.delay(time);
    	m.set(0);
	}
	
	//private
	private static void moveEncodedMotorUp(EncodedMotor verticalLift, int position, DigitalInput upperLimitSwitch, double speed) {
		while(verticalLift.getEncPosition() < position && upperLimitSwitch.get()) {
    		verticalLift.set(speed);
    	}
    	verticalLift.set(0);
	}
	
	private static void moveEncodedMotorDown(EncodedMotor m, int position, DigitalInput upperLimitSwitch, DigitalInput lowerLimitSwitch, double speed) {
		while(m.getEncPosition() > position && upperLimitSwitch.get() && lowerLimitSwitch.get()) {
    		m.set(speed);
    	}
    	m.set(0);
	}
	
	private static void moveMotorToLimitSwitch(EncodedMotor m, DigitalInput limitSwitch, double speed) {
		while(limitSwitch.get()) {
    		m.set(speed);
    	}
    	m.set(0);
	}
	
	////////////////TIME BASED DRIVE////////////////
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
    
    //private
    private static void goTimeBased(double time, double lSpeed, double rSpeed) {
		Robot.drive.tankDrive(lSpeed, rSpeed);
		Timer.delay(time);
    }
    
    ////////////////ENCODER BASED DRIVE////////////////
    static int ENC_ACCURACY_RANGE = 10;
    static int RIGHT_ANGLE_TURN_TICKS = 1396;//1466
    static double TURN_SPEED = .342;
    
    public static void goFoward(int ticks, double speed) {
    	go(ticks, ticks, speed, speed, 1, 1);
    }
    
    public static void goReverse(int ticks, double speed) {
    	go(-Math.abs(ticks), -Math.abs(ticks), -Math.abs(speed), -Math.abs(speed), -1, -1);
    }
    
    public static void turnLeft(double degrees) {
    	turnLeft(degrees, TURN_SPEED);
    }
    
    public static void turnLeft(double degrees, double speed) {
    	int turnTicks = (int) (degrees*RIGHT_ANGLE_TURN_TICKS/90);
    	go(-turnTicks, turnTicks, -speed, speed, -1, 1);
    }
    
    public static void turnRight(double degrees) {
    	turnRight(degrees, TURN_SPEED);
    }
    
    public static void turnRight(double degrees, double speed) {
    	int turnTicks = (int) (degrees*RIGHT_ANGLE_TURN_TICKS/90);
    	go(turnTicks, -turnTicks, speed, -speed, 1, -1);
    }
    
    //private
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
//    	//start motors
//    	setMotors(lSpeed, Robot.leftBack, Robot.leftFront);
//    	setMotors(rSpeed, Robot.rightBack, Robot.rightFront);
    	//wait till distance is reached
    	boolean leftNotFinished = true;
    	boolean rightNotFinished = true;
    	while(leftNotFinished || rightNotFinished) {
    		//start motors
        	setMotors(lSpeed, Robot.leftBack, Robot.leftFront);
        	setMotors(rSpeed, Robot.rightBack, Robot.rightFront);
        	//check if distance is reached
    		if(leftNotFinished) {
    			int leftDistanceLeft = getAverageDistance(lTicks, Robot.leftBack, Robot.leftFront);
    			leftNotFinished = (leftDistanceLeft*leftDirection > ENC_ACCURACY_RANGE);
    		}
    		if(rightNotFinished) {
    			int rightDistanceLeft = getAverageDistance(rTicks, Robot.rightBack, Robot.rightFront);
    			rightNotFinished = (rightDistanceLeft*rightDirection > ENC_ACCURACY_RANGE);
    		}
    		SmartDashboard.putNumber("AutoLeftFrontEnc", Robot.leftFront.getEncPosition());
    		SmartDashboard.putNumber("AutoLeftBackEnc", Robot.leftBack.getEncPosition());
    		SmartDashboard.putNumber("AutoRightFrontEnc", Robot.rightFront.getEncPosition());
    		SmartDashboard.putNumber("AutoRightBackEnc", Robot.rightBack.getEncPosition());
    	}
    	//stop motors
    	setMotors(0, Robot.leftBack, Robot.leftFront);
    	setMotors(0, Robot.rightBack, Robot.rightFront);
    }

	private static int getAverageDistance(int ticks, ExtendedCANTalon m1, ExtendedCANTalon m2) {
    	return ticks-((m1.getEncPosition()+m2.getEncPosition())/2);
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
