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
	
	Compressor compressor = new Compressor();
	
	static DoubleSolenoid leftArm = new DoubleSolenoid(0, 0, 1);
//	static DoubleSolenoid rightArm = new DoubleSolenoid(0, 3, 2); //Robot 1
	static DoubleSolenoid rightArm = new DoubleSolenoid(0, 2, 3);

	
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
	
	Toggle armToggle = new Toggle(null, 6);
//	Toggle intakeToggle = new Toggle(xboxgun, 5);
	Toggle lightToggle = new Toggle(xboxgun, 10);
	Toggle switchToggle = new Toggle(xboxdrive, 4);
	Toggle atToggle = new Toggle(xboxdrive, 9);
	
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
    public static double AUTO_DRIVE_TURN = .5;
//    public static Thread autonomousThread;
    
    public void autonomousInit() {
    	int mode = (int) SmartDashboard.getNumber("AUTONOMOUS MODE");
    	switch(mode) {
    	case 0: //lift recycle bin
    		AutoDrive.moveMotorTimeBased(verticalLift, 2.42, -1);
////    	AutoDrive.turnRight(90);
//    		AutoDrive.goFoward(600, AUTO_DRIVE_SPEED);
//    		AutoDrive.turnLeft(120);
    		break;
    	case 1: //single recycle bin and turn
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.9, -1);
    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE, 90, AUTO_DRIVE_SPEED);
    		break;
    	case 2: //single tote + recyle bin
    		AutoDrive.syncRecycleBinAndToteIntake();
    		AutoDrive.goFoward(100, AUTO_DRIVE_SPEED);
    		Timer.delay(.5);
    		AutoDrive.turnRight(80);
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE+700, 90, AUTO_DRIVE_SPEED);
    		break;
    	case 3: //Recycle Bin (Drive Straight) 
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.9, -1);
//    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE, 0, AUTO_DRIVE_SPEED);
    		AutoDrive.goFoward(AutoDrive.AUTOZONE_DISTANCE, AUTO_DRIVE_SPEED);
    		break; 
    		//old trit-toter don't use
//    		AutoDrive.liftAndGoToNextTote(1, AUTO_DRIVE_SPEED);
//    		AutoDrive.liftAndGoToNextTote(2, AUTO_DRIVE_SPEED);
//    		AutoDrive.stackerToteLiftDown();
//    		AutoDrive.stackerToteLiftUp(3);
//    		AutoDrive.intakeTote();
//    		AutoDrive.syncToteStackerLiftDown();
//    		AutoDrive.turnRight(90);
//    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE, 90, AUTO_DRIVE_SPEED);
//    		break;
    	case 4: //single recyle bin (over scoring platform)
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.9, -1);
//    		AutoDrive.goFoward(1500, AUTO_DRIVE_SPEED);
//    		Timer.delay(1.0);
    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE*2, 90, AUTO_DRIVE_SPEED); // was +800.
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
    	case 6: //short single cycle bin
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.5, -1);
    		AutoDrive.goFowardToAutozoneAndDeploy(false, AutoDrive.AUTOZONE_DISTANCE-2500, 0, AUTO_DRIVE_SPEED);
    		break;
    	case 7: //two totes + recycle bin
    		AutoDrive.syncRecycleBinAndToteIntake();
    		AutoDrive.turnLeft(160, AutoDrive.TURN_SPEED);
    		AutoDrive.turnLeft(20, .1);
    		AutoDrive.goToNextTote(AUTO_DRIVE_SPEED);
    		AutoDrive.intakeTote();
    		AutoDrive.turnLeft(90);
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE+1800, 90, AUTO_DRIVE_SPEED);
    		break;
    	case 8: //single totes + recycle bin (same section)
    		AutoDrive.intakeTote();
    		AutoDrive.turnRight(180);
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.9, -1);
    		AutoDrive.turnLeft(90);
    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE+1800, 90, AUTO_DRIVE_SPEED);
    		break;
    	case 9: //3 grey tote + recyle bin (over close bump)
    		//does 1 tote so far
    		AutoDrive.moveMotorTimeBased(verticalLift, 1.9, -1);
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
    		AutoDrive.moveMotorTimeBased(verticalLift, 2.42, -1);
    		AutoDrive.turnRight(90);
    		AutoDrive.goFoward(600, AUTO_DRIVE_SPEED);
    		AutoDrive.turnLeft(120);
    		break;
    	case 11:
    		AutoDrive.moveMotorTimeBased(verticalLift, 3.6, -1);
    		AutoDrive.turnLeft(55);
    		Timer.delay(0.5);
    		AutoDrive.goFoward((int) (AutoDrive.AUTOZONE_DISTANCE+1900), AUTO_DRIVE_SPEED);
    		Timer.delay(0.2);
    		AutoDrive.turnLeft(45);
    		AutoDrive.goFoward(400, AUTO_DRIVE_SPEED);
    		Timer.delay(0.5);
    		AutoDrive.goFoward(150, AUTO_DRIVE_SPEED*0.2);
    		
    		break;
    	case 15:
    		AutoDrive.turnRight(3600);
    		break;
    	case 20:
    		AutoDrive.liftAndGoToNextTote(AUTO_DRIVE_SPEED);
//    		AutoDrive.liftAndGoToNextTote(AUTO_DRIVE_SPEED);
//    		AutoDrive.syncToteStackerLiftDownAndTo(1);
//    		AutoDrive.intakeTote();
//    		AutoDrive.syncToteStackerLiftDown();
//    		AutoDrive.turnRight(90);
//    		AutoDrive.goFowardToAutozoneAndDeploy(true, AutoDrive.AUTOZONE_DISTANCE, 90, AUTO_DRIVE_SPEED);
    		break;
    	default:
    		AutoDrive.goFoward(1000, AUTO_DRIVE_SPEED);
    		break;
    	}
//    	autonomousThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
				
//			}});
//    	autonomousThread.run();
    	
    }
    
    public void autonomousPeriodic() {
    
//    	Utility.configSolenoidPorts(.2);
    	//Utility.configMotorPorts(.5);

		
    }
    
    
    public void teleopInit() {
//    	autonomousThread.suspend();
    	cameraFeedPosition();
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
    		lowerVertOutput();
        	SmartDashboard.putString("Vertical Lift Direction", "Auto Down");
    	}else if(joystickPOV == DBJoystick.WEST) {
    		automaticLift = true;
    		raiseVertOutput();
        	SmartDashboard.putString("Vertical Lift Direction", "Auto Up");
    	}else{
    		//sets the speed value to 0 if not in automatic mode
    		if(!automaticLift) {
    			vertLiftCurrentSpeed = 0;
            	SmartDashboard.putString("Vertical Lift Direction", "0");
        	}
    	}
    	
    	//stops if the limit switch is pressed
    	if(vertLiftCurrentLimitSwitch.get()) {
    		verticalLift.set(vertLiftCurrentSpeed);
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
    		Utility.runMotor(joystick.axisPressed(2) && lwrLimitSwitch, joystick.axisPressed(3) && upperLimitSwitch, stackerToteLift, STACKER_TOTE_SPEED);
//    	}

        SmartDashboard.putBoolean("Upper Tote Stacker Limit Switch", upperLimitSwitch);
        SmartDashboard.putBoolean("Lower Tote Stacker Limit Switch", lwrLimitSwitch);
    	SmartDashboard.putNumber("Stacker Encoder", stackerToteLift.getEncPosition());
    }
    
    /**
     * Toggles the intake arms and moves the intake wheels
     */
    public void moveArms(DBJoystick joystick) {
    	//moves arms out or in
    	boolean armToggleState = armToggle.getState(joystick);
    	Utility.runSolenoid(armToggleState, leftArm);
    	Utility.runSolenoid(armToggleState, rightArm);

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
