
package org.usfirst.frc.team4256.robot;

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
    	SmartDashboard.putNumber("AutoLeftFrontEnc", Robot.leftBack.getEncPosition());
		SmartDashboard.putNumber("AutoLeftBackEnc", Robot.leftBack.getEncPosition());
		SmartDashboard.putNumber("AutoRightFrontEnc", Robot.leftBack.getEncPosition());
		SmartDashboard.putNumber("AutoRightBackEnc", Robot.leftBack.getEncPosition());
		
    	//teleop
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	SmartDashboard.putBoolean("Upper Tote Stacker Limit Switch", upperStackerLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Tote Stacker Limit Switch", lowerStackerLimitSwitch.get());
    	SmartDashboard.putNumber("Pressure", 0);
    	SmartDashboard.putNumber("PressureVoltage", 0);
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
    	
    	
    	//test/configuration variables
    	SmartDashboard.putNumber("PORT", 0);
    	SmartDashboard.putNumber("S Module", 0);
    	SmartDashboard.putNumber("S Forward Channel", 0);
    	SmartDashboard.putNumber("S Reverse Channel", 0);
    }
 
	
    
    
    public void autonomousInit() {
    	int mode = (int) SmartDashboard.getNumber("AUTONOMOUS MODE");
    	switch(mode) {
    	case 1:
    		AutoDrive.verticalLiftUp();
    		AutoDrive.intakeTote();
    		AutoDrive.turnRight(.6);
    		AutoDrive.goFoward(2000, .5);
    		AutoDrive.spitTote();
    		break;
    	case 2:
    		AutoDrive.turnRight(.6);
    		break;
    	case 3:
    		AutoDrive.turnLeft(.6);
    		break;
    	case 4:
    		AutoDrive.intakeTote();
    		Timer.delay(1);
    		AutoDrive.spitTote();
    		Timer.delay(1);
    		AutoDrive.verticalLiftUp();
    		Timer.delay(1);
    		AutoDrive.verticalLiftDown();
    		break;
    	case 5:
    		AutoDrive.verticalLiftDown();
    		break;
    	case 6:
    		AutoDrive.goFoward(2000, .5);
    		break;
    	default:
    		break;
    	}
    }
    
    public void autonomousPeriodic() {
//    	Utility.configSolenoidPorts(.2);
    	//Utility.configMotorPorts(.5);

		
    }
    
    
    public void teleopInit() {
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
    	double driveSpeedScale = (xboxdrive.getRawButton(5)? .5 : .75); // scaling factor reduced to 0.5
    	//drive.arcadeDrive(xboxdrive.getRawAxis(4)*driveSpeedScale, xboxdrive.getRawAxis(1)*driveSpeedScale, true); // left stick on Xbox controls forward and backward direction. right sticks controls rotation.
        drive.tankDrive(-xboxdrive.getRawAxis(1)*driveSpeedScale, xboxdrive.getRawAxis(5)*driveSpeedScale, true);
    	//    	Utility.runMotor(xboxgun, 3, 1, wheelIntake, INTAKE_SPEED); // button 3 on xboxgun (X) will run motor in forward direction, button 1 will reverse. wheelIntake represents motor type and INTAKE_SPEED represents the motor's speed
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
//    	intake(joystick);
    	moveCamera();
    	
    	rumbleAlert.check();
    	
    	//camera.moveCamera(xboxgun.getRawAxis(4),xboxgun.getRawAxis(5));
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	SmartDashboard.putNumber("Pressure", roundTo(PressureGauge.getAverageVoltage()*43.14-55.39, 5));
    	SmartDashboard.putNumber("PressureVoltage", roundTo(PressureGauge.getAverageVoltage(), 4));
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
    public void stackerToteLift(DBJoystick joystick) {
//    	stackerToteLift.update(-STACKER_TOTE_SPEED);
    	boolean lwrLimitSwitch = lowerStackerLimitSwitch.get();
    	boolean upperLimitSwitch = upperStackerLimitSwitch.get();
    	Utility.runMotor(joystick.axisPressed(2) && lwrLimitSwitch, joystick.axisPressed(3) && upperLimitSwitch, stackerToteLift, STACKER_TOTE_SPEED);
//    	if(joystick.axisPressed(3)) {
//    		//stackerToteLift.setEncPosition(STACKER_TOTE_LIFT_MAX_HEIGHT);
//    	}else if(joystick.axisPressed(2) && StackerToteLiftCurrentLimitSwitch.get()) {
//    		//stackerToteLift.setEncPosition(STACKER_TOTE_LIFT_MIN_HEIGHT);
//    	}else{
//    		stackerToteLift.set(0);
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
    	}else if(xboxgun.getRawButton(7)) {//back
    		cameraServos.setPosition(131.18, 152.58);
    	}else if(xboxgun.getRawButton(9)) {//feed position
    		cameraServos.setPosition(12.65, 132.69);
    	}else{
        	cameraServos.moveCamera(-xboxgun.getRawAxis(0), xboxgun.getRawAxis(1));//move normally
    	}
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
