package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory. 
 */
public class Robot extends IterativeRobot {
	//constants (these will change)
	static double WHEEL_INTAKE_SPEED = 1;
	static double TOTE_ROLLER_SPEED = 1;
	static double STACKER_TOTE_SPEED = 1;
	static double VERTICAL_LIFT_SPEED = 1;
	static int STACKER_TOTE_LIFT_MAX_HEIGHT = 2000;
	static int STACKER_TOTE_LIFT_MIN_HEIGHT = 0;
	
	DBJoystick xboxdrive = new DBJoystick(0);
	DBJoystick xboxgun = new DBJoystick(1);
	boolean sharedControlsMode = false;
	
	Relay light = new Relay(0);
	
	static DigitalInput upperLimitSwitch = new DigitalInput(0);
	static DigitalInput lowerLimitSwitch = new DigitalInput(1);
	static DigitalInput lowerStackerLimitSwitch = new DigitalInput(3);
	static DigitalInput upperStackerLimitSwitch = new DigitalInput(2);
	//static DigitalInput hallEffect = new DigitalInput(9);
	
	Compressor compressor = new Compressor();
	
	static DoubleSolenoid leftArm = new DoubleSolenoid(0, 0, 1);
	static DoubleSolenoid rightArm = new DoubleSolenoid(0, 2, 3);
	static DoubleSolenoid leftHook = new DoubleSolenoid(0, 4, 5);
	static DoubleSolenoid rightHook = new DoubleSolenoid(0, 6, 7);
/*	static DoubleSolenoid leftArm = new DoubleSolenoid(0, 4, 5);//disabled
	static DoubleSolenoid rightArm = new DoubleSolenoid(0, 2, 3);
	static DoubleSolenoid recycleBinGrabber = new DoubleSolenoid(0, 0, 1);
*/
	
	Servo servoX = new Servo(0);
	Servo servoY = new Servo(1);
	Camera cameraServos = new Camera(servoX, servoY, (int)6.9);
	
	static ExtendedCANTalon rightBack = new ExtendedCANTalon(10);
	static ExtendedCANTalon rightFront = new ExtendedCANTalon(11);
	static ExtendedCANTalon leftBack = new ExtendedCANTalon(12);
	static ExtendedCANTalon leftFront = new ExtendedCANTalon(13);
	static EncodedMotor verticalLift = new EncodedMotor(15);
	static EncodedMotor stackerToteLift = new EncodedMotor(14);
	static ExtendedVictorSP wheelIntake = new ExtendedVictorSP(9);
	static ExtendedVictorSP toteRoller = new ExtendedVictorSP(7);
	static RobotDrive drive = new RobotDrive(leftFront, leftBack, rightFront, rightBack);
	
	static Toggle armToggle = new Toggle(null, 6);
	static Toggle hookToggle = new Toggle(null, 5);
//	Toggle intakeToggle = new Toggle(xboxgun, 5);
	Toggle lightToggle = new Toggle(xboxgun, 10);
	Toggle switchToggle = new Toggle(xboxdrive, 4);
	Toggle atToggle = new Toggle(xboxdrive, 9);
	//Toggle liftToggle = new Toggle(xboxgun, 4);
	AnalogInput PressureGauge = new AnalogInput(0);
	
	TimedEvent rumbleAlert = new TimedEvent(10, 9, true) {
		@Override
		public void run() {
			xboxdrive.rumble(.5f);}
		@Override
		public void end() {
			xboxdrive.rumble(0);}
		};
	
	
			///////////////////////////////////////CODE START////////////////////////////////////////////
    public void robotInit() {
    	cameraServos.maxY = 42;
    	rightFront.setInversed(true);
    	rightBack.setInversed(true);
    	PressureGauge.setAverageBits(10);
    	
    	dashInit();
    }
    
    public void dashInit() {
    	//autonomous
    	SmartDashboard.getNumber("AUTONOMOUS MODE", 3);
    	SmartDashboard.putNumber("AutoLeftFrontEnc", 0);
		SmartDashboard.putNumber("AutoLeftBackEnc", 0);
		SmartDashboard.putNumber("AutoRightFrontEnc", 0);
		SmartDashboard.putNumber("AutoRightBackEnc", 0);
		
    	//teleop
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	SmartDashboard.putBoolean("Upper Tote Stacker Limit Switch", upperStackerLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Tote Stacker Limit Switch", lowerStackerLimitSwitch.get());
    	SmartDashboard.putNumber("Pressure", 0);
    	SmartDashboard.putNumber("PressureVoltage", 0);
    	SmartDashboard.putBoolean("PSI", false);
    	SmartDashboard.putString("Driver Mode", "");
    	SmartDashboard.putBoolean("Arm Intake", false);
    	SmartDashboard.putBoolean("Automatic Lift", false);
    	SmartDashboard.putString("Vertical Lift Direction", "");
    	SmartDashboard.putNumber("Stacker Encoder", stackerToteLift.getEncPosition());	
    	SmartDashboard.putNumber("Vertical Lift Encoder", verticalLift.getEncPosition());
    	SmartDashboard.putNumber("Vertical Tick Position", -1000);
    	//SmartDashboard.putBoolean("Hall Effect", !hallEffect.get());

    	cameraServos.displayInit();
        
    	//set up Smartboard labels - workaround since can't resize labels - creating text boxes
    	SmartDashboard.putString("","Upper Limit Switch");
    	SmartDashboard.putString(" ","Lower Limit Switch");
    	SmartDashboard.putString("  ","Upper Tote Stacker Limit Switch");
    	SmartDashboard.putString("   ","Lower Tote Stacker Limit Switch");
    	SmartDashboard.putString("    ","Pressure");
    	SmartDashboard.putString("     ","PressureVoltage");
    	SmartDashboard.putString("      ","Driver Mode");
    	SmartDashboard.putString("       ","Arm Intake");
    	SmartDashboard.putString("        ","Automatic Lift");
    	SmartDashboard.putString("         ","Vertical Lift Direction");
    	SmartDashboard.putString("          ","Stacker Encoder");
    	SmartDashboard.putString("           ","Vertical Lift Encoder");
    	SmartDashboard.putString("            ","Vertical Tick Position");
    	SmartDashboard.putString("             ","AUTONOMOUS MODE");
    	
    	SmartDashboard.getString("Drive Type", "Tank Mode");
    	
    	//test/configuration variables
    	SmartDashboard.putNumber("PORT", 0);
    	SmartDashboard.putNumber("S Module", 0);
    	SmartDashboard.putNumber("S Forward Channel", 0);
    	SmartDashboard.putNumber("S Reverse Channel", 0);
    	
    	SmartDashboard.putNumber("LeftFrontEnc", Robot.leftFront.getEncPosition());
		SmartDashboard.putNumber("LeftBackEnc", Robot.leftBack.getEncPosition());
		SmartDashboard.putNumber("RightFrontEnc", Robot.rightFront.getEncPosition());
		SmartDashboard.putNumber("RightBackEnc", Robot.rightBack.getEncPosition());
    	
    	
    	
    }
 
	
    public static double AUTO_DRIVE_SPEED = .5042;
    public static double AUTO_DRIVE_FAST_SPEED = .6;
    public static double AUTO_DRIVE_TURN = .5;
//    public static Thread autonomousThread;
    
    public void autonomousInit() {
    	Timer.delay(.1);
    	int mode = (int) SmartDashboard.getNumber("AUTONOMOUS MODE");
    	switch(mode) {
    	case 0: //lift recycle bin
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.95, -1);//2.42 before motor change
////    	AutoDrive.turnRight(90);
//    		AutoDrive.goFoward(600, AUTO_DRIVE_SPEED);
//    		AutoDrive.turnLeft(120);
    		break;
    	case 1: //single recycle bin and turn
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//1.9 before motor change
    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE, 27, AUTO_DRIVE_SPEED);
    		break;
    	case 2: //single tote + recyle bin
    		AutoDrive.syncRecycleBinAndToteIntake();
    		AutoDrive.goFoward(100, AUTO_DRIVE_SPEED);
    		Timer.delay(.5);
    		AutoDrive.turnRight(80);
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE*1.5, 55, AUTO_DRIVE_SPEED);
    		break;
    	case 3: //Recycle Bin (Drive Straight) 
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//1.9 before motor change
//    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE, 0, AUTO_DRIVE_SPEED);
    		AutoDrive.goFoward((int) (AutoDrive.AUTOZONE_DISTANCE*.95), AUTO_DRIVE_SPEED);
    		break;
    	case 4: //single recyle bin (over scoring platform)
//    		AutoDrive.syncRecycleBin(3.42);
//    		Timer.delay(.4);
//    		AutoDrive.goSidewaysLeft(800, AUTO_DRIVE_SPEED);
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.3, -1);//3.5 before motor change
    		AutoDrive.turnLeft(20);
    		Timer.delay(.2);
    		AutoDrive.goFoward((int) (AutoDrive.AUTOZONE_DISTANCE*1.6), AUTO_DRIVE_SPEED); // was 2 on robot #2 at wca
    		
//    		Timer.delay(1.0);
//    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE*2, 90, AUTO_DRIVE_SPEED); // was +800.
    		break;
    	case 5: //single tote + recyle bin (over scoring platform)
    		AutoDrive.syncRecycleBinAndToteIntake();
    		AutoDrive.goFoward(100, AUTO_DRIVE_SPEED);
    		Timer.delay(.7);
    		AutoDrive.turnRight(65);
    		Timer.delay(.3);
//    		AutoDrive.goFoward(5750, AUTO_DRIVE_SPEED); //go on bump
//    		Timer.delay(1.25);
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE+2300, 50, AUTO_DRIVE_SPEED); //+700 for being in line with totes, +100 for bump
//    		AutoDrive.syncRecycleBinAndToteIntake();
//    		AutoDrive.turnLeft(90);
//    		AutoDrive.goBackwardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE+1800, 90, AUTO_DRIVE_SPEED);
    		break;
    	case 6: //(right side) single recycle bin to right side auto
    		enableBreakMode(true);
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//1.9 before motor change
//    		AutoDrive.goSidewaysLeft((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*0.5), AUTO_DRIVE_FAST_SPEED); encoder based
    		AutoDrive.goSidewaysLeftTimeBased(.5, 0, AUTO_DRIVE_FAST_SPEED); //time based for 15.5 inch side shift
    		Timer.delay(0.2);
    		AutoDrive.turnRight(25);
    		Timer.delay(.2);
    		AutoDrive.goFoward((int) (AutoDrive.AUTOZONE_DISTANCE*1.5), AUTO_DRIVE_FAST_SPEED); //was 2.2 for robot #2 at wca
    		AutoDrive.turnRight(70);
    		break;
    	case 7: //two totes + recycle bin
    		AutoDrive.recycleBinAndTwoTote();
    		Timer.delay(.5);
    		AutoDrive.goReverse((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*0.8), AUTO_DRIVE_SPEED);// 3/20 changed from 1.1 to 0.8 (1.1 backs up into RBin)
    		AutoDrive.turnLeft(95, .5042);
    		AutoDrive.sycnToteSpewAlign();
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE*2, 80, AUTO_DRIVE_SPEED);
    		break;
    	case 8: //single totes + recycle bin (same section)
    		AutoDrive.intakeTote();
    		AutoDrive.turnRight(180);
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//1.9 before motor change
    		AutoDrive.turnLeft(90);
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE+1800, 90, AUTO_DRIVE_SPEED);
    		break;
    	case 9: //3 grey tote + recyle bin (over close bump)
    		//does 1 tote so far
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//1.9 before motor change
    		AutoDrive.goFoward(200, AUTO_DRIVE_SPEED);
    		Timer.delay(.5);
    		AutoDrive.turnRight(80);
    		AutoDrive.goFoward(4000, AUTO_DRIVE_SPEED); //go on bump
    		Timer.delay(1.25);
    		AutoDrive.goFoward(9000, AUTO_DRIVE_SPEED); //go to grey totes
    		AutoDrive.syncToteIntake();
    		AutoDrive.turnRight(90, .1);
    		AutoDrive.goFoward(200, .3);
    		break;
    	case 10: //lift recycle bin and go to feed
//    		AutoDrive.moveMotorTimeBased(verticalLift, 0.95, -1);//2.42 before motor change
//    		AutoDrive.turnRight(90);
//    		AutoDrive.goFoward(600, AUTO_DRIVE_SPEED);
//    		AutoDrive.turnLeft(120);
    		break;
    	case 11: //center recycle bin to far left side
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//1.9 before motor change
//    		AutoDrive.goSidewaysLeft(AutoDrive.TOTE_TO_TOTE_DISTANCE, 0.8);
    		AutoDrive.goSidewaysLeftTimeBased(1, 0, AUTO_DRIVE_FAST_SPEED); //time based for 31 inch side shift (tote to tote distance)
    		AutoDrive.turnLeft(35);
    		Timer.delay(0.5);
    		AutoDrive.goFoward((int) (AutoDrive.AUTOZONE_DISTANCE*1.86), AUTO_DRIVE_SPEED); //AutoZone distance + 1900 (Arkansas)
    		Timer.delay(0.2);
    		AutoDrive.turnLeft(45);
    		AutoDrive.goFoward(200, AUTO_DRIVE_SPEED);
    		//Timer.delay(0.5);
    		//AutoDrive.goFoward(150, AUTO_DRIVE_SPEED*0.2);
    		
    		break;
    	case 12: //center recyle bin to center field
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.8, -1);//was 1.9 before vlift motor change
//    		AutoDrive.goSidewaysLeft(AutoDrive.TOTE_TO_TOTE_DISTANCE, 0.8);
    		AutoDrive.goSidewaysLeftTimeBased(1.25, 0, AUTO_DRIVE_FAST_SPEED); //time based for 31 inch side shift (tote to tote distance)
    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE*1.4, 27, AUTO_DRIVE_SPEED);//was 1.2, 74deg but short robot #1
    		AutoDrive.goFoward((int) (AutoDrive.AUTOZONE_DISTANCE*.6), AUTO_DRIVE_SPEED);
    		break;
    	case 14: //left side, recyle bin, turn right
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.95, -1);//2.42 before motor change
    		AutoDrive.turnRight(60);
    		break;
    	case 15: //right side, recyle bin, turn left (we want to rotate opposite direction to face bin towards the feeder station -Alex)
    		AutoDrive.moveMotorTimeBased(verticalLift, 0.95, -1);//2.42 before motor change
    		AutoDrive.turnLeft(60);
    		break;
//    	case 16:
//    		enableBreakMode(true);
//    		AutoDrive.turnLeft(170, .57);
//    		break;
//    	case 17: //tote lift, left sideways
//    		AutoDrive.moveMotorTimeBased(verticalLift, 0.95, -1);//2.42 before motor change
//    		AutoDrive.goSidewaysLeftTimeBased(5, 0, 0.5);
//    		break;
    	case 18:
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.25, -1);//3.42 before motor change
    		AutoDrive.goSidewaysLeft((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*0.5), AUTO_DRIVE_FAST_SPEED);
    		AutoDrive.turnLeft(145);
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*2), AUTO_DRIVE_SPEED);
    		break;
    	case 20: //3 tote + 1 bin (over bump)
    		//first two totes
    		AutoDrive.recycleBinAndTwoTote();
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.4), AUTO_DRIVE_SPEED);
//    		AutoDrive.sycnToteSpewAlign();
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.4), AUTO_DRIVE_SPEED);
    		
    		//3rd tote
    		AutoDrive.syncToteStackerLiftDownAndTo(1.08);
    		AutoDrive.steerToNextTote(AUTO_DRIVE_SPEED, true);
//    		Timer.delay(.2);
    		AutoDrive.syncToteIntake();
    		Timer.delay(.3);
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.7), AUTO_DRIVE_SPEED);
    		Timer.delay(.2);
    		
    		//to autozone
    		AutoDrive.turnRight(70, .8);
    		Timer.delay(.1);
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.4), AUTO_DRIVE_FAST_SPEED);
//    		AutoDrive.sycnToteSpewAlign();
    		AutoDrive.syncToteStackerLiftDown();
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*.6), AUTO_DRIVE_FAST_SPEED);
    		AutoDrive.exeSrvc.execute(new Runnable() {
				public void run() {
		    		AutoDrive.spitTote();
				}});
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*.7), AUTO_DRIVE_FAST_SPEED);
    		break;
    	case 21: //3 tote + 1 bin
    		//two tote
    		AutoDrive.recycleBinAndTwoTote();
    		Timer.delay(.4);
    		AutoDrive.goFoward(100, AUTO_DRIVE_SPEED);
    		//third tote
    		AutoDrive.goReverse((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.7+80), AUTO_DRIVE_SPEED);
    		Timer.delay(.3);
    		AutoDrive.turnLeft(169, .6);
    		Timer.delay(.3);
    		AutoDrive.goSidewaysRight(AutoDrive.STEER_DISTANCE, .9);
    		AutoDrive.syncToteStackerLiftDownAndTo(1);
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*1.8), AUTO_DRIVE_SPEED);
    		Timer.delay(.2);
    		AutoDrive.goSidewaysLeft((int) (AutoDrive.STEER_DISTANCE*.95), AUTO_DRIVE_SPEED);
    		Timer.delay(.2);
    		AutoDrive.openArms();
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.8), AUTO_DRIVE_SPEED);
    		AutoDrive.syncToteIntake();
    		Timer.delay(.5);
    		AutoDrive.goFoward(120, AUTO_DRIVE_SPEED);
//    		Timer.delay(.2);
//    		AutoDrive.goFoward(120, AUTO_DRIVE_SPEED);
//    		//deploy
    		AutoDrive.syncToteStackerLiftDown();
    		AutoDrive.goSidewaysRight((int) (AutoDrive.AUTOZONE_DISTANCE*2.5), 1);
    		AutoDrive.spitTote();
    		AutoDrive.goReverse(100, AUTO_DRIVE_SPEED);
    		break;
    		
    	case 22: //3 tote + 1 bin (needs center bin removed)
    		//two tote
    		AutoDrive.recycleBinAndTwoTote();
    		Timer.delay(.4);
    		AutoDrive.goFoward(100, AUTO_DRIVE_SPEED);
    		//third tote
    		AutoDrive.goReverse((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.7), AUTO_DRIVE_SPEED);
    		Timer.delay(.3);
    		AutoDrive.turnLeft(169, .6);
    		Timer.delay(.3);
    		AutoDrive.openArms();
    		AutoDrive.syncToteStackerLiftDownAndTo(1);
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*3), AUTO_DRIVE_SPEED);
    		AutoDrive.syncToteIntake();
    		Timer.delay(.5);
    		AutoDrive.goFoward(120, .4);//slow tote pick up speed
//    		//deploy
    		AutoDrive.syncToteStackerLiftDown();
    		AutoDrive.goSidewaysRight((int) (AutoDrive.AUTOZONE_DISTANCE*2.5), 1);
    		AutoDrive.spitTote();
    		AutoDrive.goReverse(100, AUTO_DRIVE_SPEED);
    		break;
    	case 23: //3 tote + 1 bin (needs right bin removed)
    		//first two totes
    		AutoDrive.recycleBinAndTwoTote();
//    		AutoDrive.goFowardAndAlignToTote((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.4), AUTO_DRIVE_SPEED);//align
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.4), AUTO_DRIVE_SPEED);
//    		AutoDrive.sycnToteSpewAlign();
    		AutoDrive.syncToteStackerLiftDownAndTo(1.08);
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.4), AUTO_DRIVE_SPEED);
    		
    		//3rd tote
    		AutoDrive.openArms();
    		Timer.delay(.2);
    		AutoDrive.goSidewaysLeftTimeBased(0.2, 0, .5);
    		Timer.delay(.1);
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*1.2), AUTO_DRIVE_SPEED);//2.2
//    		AutoDrive.goFowardAndAlignToTote((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.4), AUTO_DRIVE_SPEED);//2.2
//    		Timer.delay(.2);
    		AutoDrive.syncToteIntake();
    		Timer.delay(.3);
    		AutoDrive.goFoward((int) (AutoDrive.TOTE_TO_TOTE_DISTANCE*.1), AUTO_DRIVE_SPEED);//.7
    		Timer.delay(.2);
    		
    		//to autozone
    		AutoDrive.turnRight(70, .8);
    		Timer.delay(.1);
    		AutoDrive.sycnToteSpewAlign();
//    		Timer.delay(.5);
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.4), AUTO_DRIVE_FAST_SPEED);
//    		AutoDrive.sycnToteSpewAlign();
    		AutoDrive.syncToteStackerLiftDown();
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*.6), AUTO_DRIVE_FAST_SPEED);
    		AutoDrive.exeSrvc.execute(new Runnable() {
				public void run() {
		    		AutoDrive.spitTote();
				}});
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*.7), AUTO_DRIVE_FAST_SPEED);
    		break;
    	case 50: //2 Trash cans from middle (fast)
    		enableBreakMode(true);
    		AutoDrive.openArms();
    		AutoDrive.goSidewaysLeftTimeBased(.2, AUTO_DRIVE_SPEED, 0);
//    		AutoDrive.goFoward(150, AUTO_DRIVE_SPEED); //moved 3.25 inches avg (3.5 l, 3 r)
    		Timer.delay(1);
//    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.1), AUTO_DRIVE_FAST_SPEED);
   // 		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.6), 0.8);
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.4), 0.5);
 //   		AutoDrive.closeArms();
    		
 //   		Timer.delay(.4);
 //   		AutoDrive.goSidewaysLeftTimeBased(.2, 0, .25);
 //   		AutoDrive.goSidewaysLeftTimeBased(5, 0, .5);
 //   		AutoDrive.goSidewaysLeftTimeBased(.2, 0, .25);
    		break;
    	case 51: 
    		enableBreakMode(true);
    		AutoDrive.openArms();
    		Timer.delay(.3);
    		AutoDrive.goFoward(150, AUTO_DRIVE_SPEED);
    		Timer.delay(.2);
    		AutoDrive.turnLeft(3, .3);
    		Timer.delay(.2);
    		AutoDrive.turnRight(6, .3);
    		Timer.delay(.2);
    		AutoDrive.turnLeft(3, .3);
    		Timer.delay(.5);
//    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.1), AUTO_DRIVE_FAST_SPEED);
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*1.4), 0.5);
 //   		AutoDrive.closeArms();
    		break;
    	case 52:
    		enableBreakMode(true);
    		AutoDrive.openArms();
    		AutoDrive.goSidewaysLeftTimeBased(.2, AUTO_DRIVE_SPEED, 0);
    		Timer.delay(1);
    		AutoDrive.goReverse((int) (AutoDrive.AUTOZONE_DISTANCE*.1), 0.5);
    		break;
    	default:
//    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE*1.4, 27, AUTO_DRIVE_SPEED);//was 1.2, 74deg but short robot #1
//    		AutoDrive.goFoward(500, 0.5*AUTO_DRIVE_SPEED);
//    		AutoDrive.goFoward(1000, AUTO_DRIVE_SPEED);
//    		AutoDrive.goSidewaysLeft(5000, AUTO_DRIVE_SPEED);
//    		AutoDrive.syncToteStackerLiftDownAndTo(1);
//    		AutoDrive.goFowardAndAlignToTote(AutoDrive.TOTE_TO_TOTE_DISTANCE, .2);
    		
    	}
//    	autonomousThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
				
//			}});
//    	autonomousThread.run();
    	
    }
    
    public static void enableBreakMode(boolean brake) {
    	leftBack.enableBrakeMode(brake);
    	leftFront.enableBrakeMode(brake);
    	rightBack.enableBrakeMode(brake);
    	rightFront.enableBrakeMode(brake);
    }
    
    public void autonomousPeriodic() {
    
//    	Utility.configSolenoidPorts(.2);
    	//Utility.configMotorPorts(.5);

		
    }
    
    
    public void teleopInit() {
//    	autonomousThread.suspend();
//    	cameraFeedPosition();
    	enableBreakMode(false);
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	cameraServos.display();
    	if(switchToggle.getState()) {
    		SmartDashboard.putString("Driver Mode", "Solo Mode");
    		runSharedFunctions(xboxdrive);
    	}else{
    		SmartDashboard.putString("Driver Mode", "Dual Mode");
    		runSharedFunctions(xboxgun);
    	}
    	
		
    }
    
    
    /**
     * Controls the main functions in teleop. The joystick input is the joystick that is currently controlling the gunner functions
     */
    public void runSharedFunctions(DBJoystick joystick) {
    	double driveSpeedScale = (xboxdrive.getRawButton(5)? .5 : .75); // scaling factor
    	drive.mecanumDrive_Cartesian(xboxdrive.getRawAxis(0)*driveSpeedScale, xboxdrive.getRawAxis(1)*driveSpeedScale, xboxdrive.getRawAxis(4)*driveSpeedScale, 0);
//    	if(atToggle.getState()) {
//        	drive.arcadeDrive(xboxdrive.getRawAxis(4)*driveSpeedScale, xboxdrive.getRawAxis(1)*driveSpeedScale, true); // left stick on Xbox controls forward and backward direction. right sticks controls rotation.
//        	SmartDashboard.putString("Drive Type", "Arcade Drive");
//    	}else{
//    		drive.tankDrive(-xboxdrive.getRawAxis(1)*driveSpeedScale, xboxdrive.getRawAxis(5)*driveSpeedScale, true);
//    	    SmartDashboard.putString("Drive Type", "Tank Drive");
//    	}
    	Utility.runMotor((joystick.getRawButton(1) || joystick.getRawButton(2)), joystick.getRawButton(3), toteRoller, TOTE_ROLLER_SPEED);
    	Utility.runMotor(joystick, 3, 1, wheelIntake, WHEEL_INTAKE_SPEED);
    	Utility.runLED(lightToggle, light);
    	//Utility.runSolenoid(xboxdrive, 7, 8, recycleBinGrabber);
    	
    	
    	
    	if(SmartDashboard.getNumber("Vertical Tick Position") == -1000){
        	verticalLift(joystick);
    	}else{
    		verticalEncodeMode(joystick);
    	}
    	stackerToteLift(joystick);
    	moveArms(joystick);
    	moveCamera();
    	
    	rumbleAlert.check();
    	
    	//update dashboard
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	//SmartDashboard.putBoolean("Hall Effect", !hallEffect.get());
    	double pressureGauge = PressureGauge.getAverageVoltage()*43.14-55.39;
    	SmartDashboard.putNumber("Pressure", roundTo(pressureGauge, 4));
    	double pressureVoltage = PressureGauge.getAverageVoltage();
    	SmartDashboard.putNumber("PressureVoltage", roundTo(pressureVoltage, 2));
    	if (pressureGauge < 60){
    		SmartDashboard.putBoolean("PSI", false);
    	}else{
    		SmartDashboard.putBoolean("PSI", true);
    	}
    	SmartDashboard.putNumber("Vertical Lift Encoder", verticalLift.getEncPosition());
    }
    
    
    public double roundTo(double n, int places) {
        	double amount = Math.pow(10, places);
        	return Math.round(n*amount)/amount;
	}
    
    public void verticalEncodeMode(DBJoystick joystick) {
    	verticalLift.setEncPosition((int) SmartDashboard.getNumber("Vertical Tick Position"));
    	verticalLift.update(VERTICAL_LIFT_SPEED);
    }
    
	/**
     * Moves the vertical lift up or down
     */
    double vertLiftCurrentSpeed = 0;
    double stackerToteCurrentSpeed = 0;
    DigitalInput vertLiftCurrentLimitSwitch = lowerLimitSwitch;
    boolean automaticLift = false;
    
    public void verticalLift(DBJoystick joystick) {
    	//sets speed and limit switch check variables based on what button is pressed
    	int joystickPOV = joystick.getPOV();
    	double verticalLiftSpeedScale = (xboxgun.getRawButton(5)? 0.5 : 1.0);
    	if(joystickPOV == DBJoystick.NORTH || joystickPOV == DBJoystick.NORTH_WEST || joystickPOV == DBJoystick.NORTH_EAST ) { // xboxgun dpad (haven't found button (or in this case POV) inputs) will control direction of vertical lift. Up ascends the lift to the maximum height, down descends the lift to the minimum height.
    		automaticLift = false;
    		raiseVertOutput();
        	SmartDashboard.putString("Vertical Lift Direction", "Up");
    	}else if(joystickPOV == DBJoystick.SOUTH || joystickPOV == DBJoystick.SOUTH_WEST || joystickPOV == DBJoystick.SOUTH_EAST ) {
    		automaticLift = false;
    		lowerVertOutput();
        	SmartDashboard.putString("Vertical Lift Direction", "Down");
    	}else if(joystickPOV == DBJoystick.EAST) {
    		automaticLift = true;
    		raiseVertOutput();
        	SmartDashboard.putString("Vertical Lift Direction", "Auto Up");
    	}else if(joystickPOV == DBJoystick.WEST) {
    		automaticLift = true;
    		lowerVertOutput();
        	SmartDashboard.putString("Vertical Lift Direction", "Auto Down");
    	}else{
    		//sets the speed value to 0 if not in automatic mode
    		if(!automaticLift) {
    			vertLiftCurrentSpeed = 0;
            	SmartDashboard.putString("Vertical Lift Direction", "0");
        	}
    	}
    	
    	//stops if the limit switch is pressed
    	if(vertLiftCurrentLimitSwitch.get()) {
    		verticalLift.set(vertLiftCurrentSpeed*verticalLiftSpeedScale);
    	}else{
    		vertLiftCurrentSpeed = 0;
    		verticalLift.set(0);
    		automaticLift = false;
    		SmartDashboard.putString("Vertical Lift Direction", "0");
    	}
   
    	
    	
    	//writes values to the dashboard
    	SmartDashboard.putBoolean("Automatic lift", automaticLift);
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	}
    
    
    public void raiseVertOutput() {
    	vertLiftCurrentSpeed = -VERTICAL_LIFT_SPEED;
    	vertLiftCurrentLimitSwitch = upperLimitSwitch;
    }
    
    public void lowerVertOutput() {
    	vertLiftCurrentSpeed = VERTICAL_LIFT_SPEED;
    	vertLiftCurrentLimitSwitch = lowerLimitSwitch;
    }
    
    
    /**
     * Moves the stacker tote lift up or down
     */
    int FEED_UP_STACKER_POSITION = (int) (AutoDrive.STACKER_TOTE_BOTTOM_POSITION + AutoDrive.STACKER_TOTE_LIFT_LEVEL_DISTANCE*2.8);
    int FEED_DOWN_STACKER_POSITION = (int) (AutoDrive.STACKER_TOTE_BOTTOM_POSITION + AutoDrive.STACKER_TOTE_LIFT_LEVEL_DISTANCE*1.8);
    boolean stackerGoingToLevel = false;
    int stackerAutomaticFeedStage = 1;
    public void stackerToteLift(DBJoystick joystick) {
//    	stackerToteLift.update(-STACKER_TOTE_SPEED);
    	boolean lwrLimitSwitch = lowerStackerLimitSwitch.get();
    	boolean upperLimitSwitch = upperStackerLimitSwitch.get();
    	//boolean hallEffectSwitch = !hallEffect.get();
//    	if(xboxgun.getRawButton(7)) {
//    		stackerGoingToLevel = true;
//    		//reset feed stage if not currently running
//    		if(stackerAutomaticFeedStage == 3) {
//    			stackerAutomaticFeedStage = 1;
//    		}
//    	}
//    	if(stackerGoingToLevel) {
//    		//move stacker down to 2nd tote
//    		if(stackerAutomaticFeedStage == 1) {
//    			if(stackerToteLift.getEncPosition() > FEED_DOWN_STACKER_POSITION && lwrLimitSwitch) {
//    				stackerToteLift.set(-STACKER_TOTE_SPEED);
//        		}
//    			stackerAutomaticFeedStage = 2;
//    		//move stacker up to lift totes
//    		}else if(stackerAutomaticFeedStage == 2) {
//    			if(stackerToteLift.getEncPosition() < FEED_UP_STACKER_POSITION && upperLimitSwitch) {
//    				stackerToteLift.set(STACKER_TOTE_SPEED);
//        		}
//    			stackerAutomaticFeedStage = 3;
//    		}else{
//    			stackerToteLift.set(0);
//    		}
//    		stackerGoingToLevel = false;
//    	}else{
    		Utility.runMotor(joystick.axisPressed(2) && lwrLimitSwitch, joystick.axisPressed(3) && (upperLimitSwitch), stackerToteLift, STACKER_TOTE_SPEED);
//    	}

        SmartDashboard.putBoolean("Upper Tote Stacker Limit Switch", upperLimitSwitch);
        SmartDashboard.putBoolean("Lower Tote Stacker Limit Switch", lwrLimitSwitch);
    	SmartDashboard.putNumber("Stacker Encoder", stackerToteLift.getEncPosition());
    	//SmartDashboard.putBoolean("Hall Effect", !hallEffect.get());
    }
    
    /**
     * Toggles the intake arms and moves the intake wheels
     */
    public void moveArms(DBJoystick joystick) {
    	//moves arms out or in
    	boolean armToggleState = armToggle.getState(joystick);
    	Utility.runSolenoid(armToggleState, leftArm);
    	Utility.runSolenoid(armToggleState, rightArm);
    	
    	boolean hookToggleState = hookToggle.getState(joystick);
    	Utility.runSolenoid(hookToggleState, leftHook);
    	Utility.runSolenoid(hookToggleState, rightHook);

    	SmartDashboard.putBoolean("Arm Intake", armToggleState);
    	
    	//moves wheels
//    	if(armToggleState) {
//    		Utility.runMotor(joystick, armToggleButton, wheelIntake, INTAKE_SPEED, 0);
//    	}else{
//    		Utility.runMotor(joystick, armToggleButton, wheelIntake, -INTAKE_SPEED, 0);
//    	}
    }
    
    
    /**
     * Moves the camera based on joystick axis 0 and 1
     * Also jumps to the home positions if buttons x and x
     */
    public void moveCamera() {
    	if(xboxgun.getRawButton(8)) {//front
    		cameraServos.setPosition(-86, 75);
    	}else if(xboxgun.getRawButton(7)) {//back (vertical lift)
    		cameraServos.setPosition(131.18, 152.58);
    	}else if(xboxgun.getRawButton(9)) {//feed position
    		cameraServos.setPosition(12.65, 132.69);
    	}else{
    		cameraFeedPosition();
    	}
    }
    
    public void cameraFeedPosition() {
    	cameraServos.moveCamera(-xboxgun.getRawAxis(0), xboxgun.getRawAxis(1));//move normally
    }
    
    
    public void disabledPeriodic() {
//    	Timer.delay(new Random().nextInt(10));
//    	xboxdrive.rumble(1);
//    	Timer.delay(.5);
//    	xboxdrive.rumble(0);
    }
    
     
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}
