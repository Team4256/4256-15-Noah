package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDrive {
	public static int VERTICAL_LIFT_UP_POSITION = 2000;//value needs testing
	public static int VERTICAL_LIFT_DOWN_POSITION = 0;//value needs testing
	public static int STACKER_TOTE_LIFT_LEVEL_DISTANCE = 1000;//value needs testing
	public static int STACKER_TOTE_BOTTOM_POSITION = -1000;//value needs testing
	public static double TOTE_INTAKE_TIME = 5;
	public static double TOTE_SPIT_TIME = 1;
	
	//private static int TOTE_TO_TOTE_DISTANCE = 2750; //2ft 9 inches	-calculated by mr ies's expertise
//	private static int TOTE_TO_TOTE_DISTANCE = 1400; //comp arkansas robot #1 omni wheels
	public static int TOTE_TO_TOTE_DISTANCE = 1650;//1850

//	public static int AUTOZONE_DISTANCE = 4200; //pre comp old don't use me
//	public static int AUTOZONE_DISTANCE = 2200; //comp arkansas robot #1 omni wheels
	public static int AUTOZONE_DISTANCE = 3200; //comp st louis mecanum wheels
	
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
				moveMotorTimeBased(Robot.verticalLift, 1.9, -1);
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
    	exeSrvc.execute(new Runnable() {
			public void run() {
//				if(level >= 2) {
				stackerToteLiftDown();
//				}
//				stackerToteLiftUp(level);
				moveMotorTimeBased(Robot.stackerToteLift, 1.1*level, -Robot.STACKER_TOTE_SPEED);
			}});
    }
    
	////////////////COMBO MOVES////////////////
    public static void recycleBinAndTwoTote() {
    	Robot.enableBreakMode(true);
		AutoDrive.syncRecycleBinAndToteIntake();
//		Timer.delay(.2);
		AutoDrive.goFoward(120, Robot.AUTO_DRIVE_SPEED);
		Timer.delay(.8);
		AutoDrive.syncToteStackerLiftDownAndTo(1);
		AutoDrive.turnLeft(173, .57);//145
		Timer.delay(.4);
		AutoDrive.goToNextTote((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.8), Robot.AUTO_DRIVE_SPEED);
		AutoDrive.syncToteIntake();
    }
    
    public static void liftAndGoToNextTote(int distance, double speed) {
//    	syncToteIntake();
    	intakeTote();
    	goToNextTote(distance, speed);
    }
    
	public static void goToNextTote(int distance, double speed) {
		openArms();
		goFoward(distance-100, speed);
		syncToteIntake();
		Timer.delay(.7);
		goFoward(100, speed);
	}
	
	static int STEER_DISTANCE = 1600;
	public static void steerToNextTote(double speed, boolean steerLeft) {
//		turnRight(45);
//		turnLeft(45);
		if(steerLeft) {
			goSidewaysLeft(STEER_DISTANCE, .7);
//			Timer.delay(.2);
			goFoward(800, speed);
			openArms();
			Timer.delay(.2);
//			exeSrvc.execute(new Runnable() {
//				public void run() {
//
//					Robot.wheelIntake.set(Robot.WHEEL_INTAKE_SPEED);
//					Timer.delay(.5);
//					Robot.wheelIntake.set(Robot.WHEEL_INTAKE_SPEED);
//				}});
			Robot.wheelIntake.set(Robot.WHEEL_INTAKE_SPEED);
			goSidewaysRight(STEER_DISTANCE+700, .7);
//			turnLeft(5);
			Robot.wheelIntake.set(0);
		}else{
			goSidewaysRight(STEER_DISTANCE, speed);
			goFoward(200, speed);
			goSidewaysLeft(STEER_DISTANCE, speed);
		}
		Timer.delay(.1);
		goFoward(600, speed);
	}
	
	public static void goFowardToAutozoneAndDeploy(boolean deployTotes, double distance, double turnAngle, double speed) {
		goFoward((int) (distance), speed);
		AutoDrive.syncToteStackerLiftDown();
		Timer.delay(.5);
		turnRight(turnAngle);
		if(deployTotes) {
			deployTotes(distance, speed);
			goReverse(100, speed);
		}
	}
	
	public static void goBackwardToAutozoneAndDeploy(boolean deployTotes, double distance, double turnAngle, double speed) {
		goReverse((int) (distance), speed);
		turnRight(turnAngle);
		if(deployTotes) {
			deployTotes(distance, speed);
			goFoward(1000, speed);
		}
	}
	
	//private
	private static void deployTotes(double distance, double speed) {
		goFoward((int) (TOTE_TO_TOTE_DISTANCE*.15), speed);
		spitTote();
	}
	
	////////////////TOTE INTAKE////////////////
	public static void intakeTote() {
		closeArms();
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
	
	public static void closeArms() {
		Robot.leftArm.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
		Robot.rightArm.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
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
		moveEncodedMotorUp(Robot.stackerToteLift, STACKER_TOTE_BOTTOM_POSITION + STACKER_TOTE_LIFT_LEVEL_DISTANCE*level, Robot.upperStackerLimitSwitch,
				-Robot.STACKER_TOTE_SPEED);
    }
	
	public static void stackerToteLiftDown(int level) {
		moveEncodedMotorDown(Robot.stackerToteLift, STACKER_TOTE_BOTTOM_POSITION + STACKER_TOTE_LIFT_LEVEL_DISTANCE*level, Robot.lowerStackerLimitSwitch,
				Robot.STACKER_TOTE_SPEED);
    }
	
	public static void stackerToteLiftDown() {
		moveMotorToLimitSwitch(Robot.stackerToteLift, Robot.lowerStackerLimitSwitch, Robot.STACKER_TOTE_SPEED);
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
	
	private static void moveEncodedMotorDown(EncodedMotor m, int position, DigitalInput lowerLimitSwitch, double speed) {
		while(m.getEncPosition() > position && lowerLimitSwitch.get()) {
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
//    static int RIGHT_ANGLE_TURN_TICKS = 1396;//omni wheels arkansas
    static int RIGHT_ANGLE_TURN_TICKS = 2370;//mecanum wheels
    
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
    
    public void goSideways(int ticks, int speed) {
//    	boolean notFinished = true;
    	while(getAverageDistance(ticks, Robot.leftFront, Robot.leftFront) > ENC_ACCURACY_RANGE) {
    		Robot.drive.mecanumDrive_Cartesian(speed, 0, 0, 0);
    	}
    }
    
    public static void goSidewaysRight(int ticks, double speed) {
    	goSideways(ticks, speed, -1, 1);
    }
    
    public static void goSidewaysLeft(int ticks, double speed) {
    	goSideways(ticks, speed, 1, -1);
    }
    
    private static void goSideways(int ticks, double speed, double frontRightDirection, double frontLeftDirection) {
    	resetEncodersToZero();
    	//wait till distance is reached
    	boolean set1NotFinished = true;
    	boolean set2NotFinished = true;
    	while(set1NotFinished && set2NotFinished) {
    		//start motors
        	setMotors(speed*frontRightDirection, Robot.rightFront, Robot.leftBack);
        	setMotors(speed*frontLeftDirection, Robot.leftFront, Robot.rightBack);
        	//check if distance is reached
    		if(set1NotFinished) {
    			int set1DistanceLeft = getAverageDistance(ticks, Robot.rightFront, Robot.leftBack);
    			set1NotFinished = (set1DistanceLeft > ENC_ACCURACY_RANGE);
    		}
    		if(set2NotFinished) {
    			int set2DistanceLeft = getAverageDistance(ticks, Robot.leftFront, Robot.rightBack);
    			set2NotFinished = (set2DistanceLeft > ENC_ACCURACY_RANGE);
    		}
    		SmartDashboard.putNumber("AutoLeftFrontEnc", Robot.leftFront.getEncPosition());
    		SmartDashboard.putNumber("AutoLeftBackEnc", Robot.leftBack.getEncPosition());
    		SmartDashboard.putNumber("AutoRightFrontEnc", Robot.rightFront.getEncPosition());
    		SmartDashboard.putNumber("AutoRightBackEnc", Robot.rightBack.getEncPosition());
    	}
    	//stop motors
    	setMotors(0, Robot.rightFront, Robot.leftBack);
    	setMotors(0, Robot.leftFront, Robot.rightBack);
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
    	while(leftNotFinished && rightNotFinished) {
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
		int workingEncoders = 2;
		int encPos1 = m1.getEncPosition();
		int encPos2 = m2.getEncPosition();
		int total = 0;
		if(Math.abs(encPos1) > 40) {
			total += encPos1;
		}else{
			workingEncoders = 1;
		}
		if(Math.abs(encPos2) > 40) {
			total += encPos2;
		}else{
			workingEncoders = 1;
		}
    	return ticks-(total/workingEncoders);
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
