
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
    
	DBJoystick xboxdrive = new DBJoystick(0);
	DBJoystick xboxgun = new DBJoystick(1);
	boolean sharedControlsMode = false;
	
	
	Relay light = new Relay(0);
	
	
	DigitalInput upperLimitSwitch = new DigitalInput(0);
	DigitalInput lowerLimitSwitch = new DigitalInput(1);
	
	Compressor compressor = new Compressor();
	
	DoubleSolenoid leftArm = new DoubleSolenoid(0, 0, 1);
	DoubleSolenoid rightArm = new DoubleSolenoid(0, 2, 3);
	
	Servo servoX = new Servo(0);
	Servo servoY = new Servo(1);
	Camera cameraServos = new Camera(servoX, servoY, (int)6.9);
	
	ExtendedCANTalon rightBack = new ExtendedCANTalon(10);
	ExtendedCANTalon rightFront = new ExtendedCANTalon(11);
	ExtendedCANTalon leftBack = new ExtendedCANTalon(12);
	ExtendedCANTalon leftFront = new ExtendedCANTalon(13);
	ExtendedCANTalon verticalLift = new ExtendedCANTalon(15);
	EncodedMotor stackerToteLift = new EncodedMotor(14);
	ExtendedVictorSP wheelIntake = new ExtendedVictorSP(9);
	ExtendedVictorSP toteRoller = new ExtendedVictorSP(7);
	RobotDrive drive = new RobotDrive(leftFront, leftBack, rightFront, rightBack);
	
	Toggle armToggle = new Toggle(null, 5);
//	Toggle intakeToggle = new Toggle(xboxgun, 5);
	Toggle lightToggle = new Toggle(xboxgun, 2);
	Toggle switchToggle = new Toggle(xboxdrive, 4);
	
	AnalogInput PressureGauge = new AnalogInput(0);
	

    boolean automaticLift = false;
	
	
	TimedEvent rumbleAlert = new TimedEvent(10, 9, true) {
		@Override
		public void run() {
			xboxdrive.rumble(.5f);
			
		}

		@Override
		public void end() {
			xboxdrive.rumble(0);
		}};
	
	//constants (these will change)
	double INTAKE_SPEED = 1;
	double TOTE_ROLLER_SPEED = 1;
	double STACKER_TOTE_SPEED = .2;
    double VERTICAL_LIFT_SPEED = .1;
	int maxHeight = 2000;
	int minHeight = 0;
			
    public void robotInit() {
    	//camera.setRange(-100, -2000, 2000, 10000);
    	cameraServos.maxY = 42;
    	rightFront.setInversed(true);
    	rightBack.setInversed(true);
    	PressureGauge.setAverageBits(10);
    	dashInit();
    	
    }
    
    public void dashInit() {
    	SmartDashboard.putNumber("AUTONOMOUS MODE", 0);
    	SmartDashboard.putNumber("POV", -1);
    	SmartDashboard.putNumber("PORT", 0);
    	SmartDashboard.putNumber("VICTOR_PORT", 0);
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	SmartDashboard.putNumber("Pressure", 0);
    	SmartDashboard.putNumber("PressureVoltage", 0);
    	SmartDashboard.putString("Driver Mode", "");
    	SmartDashboard.putBoolean("Wheel Intake", false);
    	SmartDashboard.putBoolean("Automatic Lift", false);
    	SmartDashboard.putString("Vertical Lift Direction", "");
    	cameraServos.displayInit();
    }
 
	
    
    
    public void autonomousInit() {
    }
    
    public void autonomousPeriodic() {
    	//Utility.configMotorPorts(.5);
    	//Utility.configVictorPorts(.5);

//    	int mode = (int) SmartDashboard.getNumber("AUTONOMOUS MODE");
//    	if(mode == 0) {
//    		stackerToteLift.setPosition(maxHeight);
//    		Timer.delay(3);
//    		drive.arcadeDrive(.8, 0);
//    		Timer.delay(5);
//    	}
    }
    
    
    public void teleopInit() {
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	cameraServos.display();
    	SmartDashboard.putNumber("POV", xboxdrive.getPOV());
    	if(switchToggle.getState()) {
    		SmartDashboard.putString("Driver Mode", "Solo Mode");
    		runSharedFunctions(xboxdrive, 6);
    	}else{
    		SmartDashboard.putString("Driver Mode", "Dual Mode");
    		runSharedFunctions(xboxgun, 5);
    	}
    }
    
    
    /**
     * Controls the main functions in teleop. The joystick input is the joystick that is currently controlling the gunner functions
     */
    public void runSharedFunctions(DBJoystick joystick, int armToggleButton) {
    	double driveSpeedScale = (xboxdrive.getRawButton(6)? .5 : 1); // scaling factor reduced to 0.5
    	drive.arcadeDrive(xboxdrive.getRawAxis(4)*driveSpeedScale, xboxdrive.getRawAxis(1)*driveSpeedScale, true); // left stick on Xbox controls forward and backward direction. right sticks controls rotation.
        
//    	Utility.runMotor(xboxgun, 3, 1, wheelIntake, INTAKE_SPEED); // button 3 on xboxgun (X) will run motor in forward direction, button 1 will reverse. wheelIntake represents motor type and INTAKE_SPEED represents the motor's speed
    	Utility.runMotor(joystick, 1, 3, toteRoller, TOTE_ROLLER_SPEED);
    	Utility.runMotor(joystick, 3, 1, wheelIntake, INTAKE_SPEED);
    	Utility.runLED(lightToggle, light);
    	
    	verticalLift(joystick);
    	stackerToteLift(joystick);
    	moveArms(joystick, armToggleButton);
//    	intake(joystick);
    	moveCamera();
    	
    	rumbleAlert.check();
    	
    	//camera.moveCamera(xboxgun.getRawAxis(4),xboxgun.getRawAxis(5));
    	SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
    	SmartDashboard.putNumber("Pressure", roundTo(PressureGauge.getAverageVoltage()*40.81-54.89, 5));
    	SmartDashboard.putNumber("PressureVoltage", roundTo(PressureGauge.getAverageVoltage(), 1));
    }
    
    
    public double roundTo(double n, int places) {
        	double amount = Math.pow(10, places);
        	return Math.round(n*amount)/amount;
	}

	/**
     * Moves the vertical lift up or down
     */
    public void verticalLift(DBJoystick joystick) {
    	String verticalLiftDirection;
    	if(joystick.getPOV() == DBJoystick.NORTH) { // xboxgun dpad (haven't found button (or in this case POV) inputs) will control direction of vertical lift. Up ascends the lift to the maximum height, down descends the lift to the minimum height.
    		automaticLift = false;
    		raiseVertOutput();
    		verticalLiftDirection = "Up";
    	}else if(joystick.getPOV() == DBJoystick.SOUTH) {
    		automaticLift = false;
    		lowerVertOutput();
    		verticalLiftDirection = "Down";
    	}else if(joystick.getPOV() == DBJoystick.EAST) {
    		automaticLift = true;
    		lowerVertOutput();
    		verticalLiftDirection = "Auto Down";
    	}else if(joystick.getPOV() == DBJoystick.WEST) {
    		automaticLift = true;
    		raiseVertOutput();
    		verticalLiftDirection = "Auto Up";
    	}else{
    		if(!automaticLift) {
        		verticalLift.set(0);
        	}
    		verticalLiftDirection = "";
    	}
    	
    	SmartDashboard.putBoolean("Automatic lift", automaticLift);
    	SmartDashboard.putString("Vertical Lift Direction", verticalLiftDirection);
    	SmartDashboard.putBoolean("Upper Limit Switch", !upperLimitSwitch.get());
    	SmartDashboard.putBoolean("Lower Limit Switch", !lowerLimitSwitch.get());
    }
    
    public void setVertOutput(DigitalInput limitSwitch, double speed) {
    	if(!limitSwitch.get()) {
    		verticalLift.set(0);
    	}else{
    		verticalLift.set(speed);
    	}
    }
    
    public void raiseVertOutput() {
    	setVertOutput(upperLimitSwitch, -VERTICAL_LIFT_SPEED);
    }
    
    public void lowerVertOutput() {
    	setVertOutput(lowerLimitSwitch, VERTICAL_LIFT_SPEED);
    }
    
    
    /**
     * Moves the stacker tote lift up or down
     */
    public void stackerToteLift(DBJoystick joystick) {
    	stackerToteLift.update(STACKER_TOTE_SPEED);
//    	Utility.runMotor(joystick.axisPressed(3), joystick.axisPressed(2), stackerToteLift, -STACKER_TOTE_SPEED);
    	if(joystick.axisPressed(3)) { //axis 3 (RT) and axis 2 will control direction of stackerToteLift. RT will send tote stacker to maxheight. LT will send tote stacker to minheight. 
    		stackerToteLift.setPosition(maxHeight);
    	}else if(joystick.axisPressed(2)) {
    		stackerToteLift.setPosition(minHeight);
    	}
    }
    
    /**
     * Toggles the intake arms and moves the intake wheels
     */
    public void moveArms(DBJoystick joystick, int armToggleButton) {
    	//moves arms out or in
    	armToggle.setButton(armToggleButton);
    	boolean armToggleState = armToggle.getState(joystick);
    	Utility.runSolenoid(armToggleState, leftArm);
    	Utility.runSolenoid(armToggleState, rightArm);

    	SmartDashboard.putBoolean("Wheel Intake", armToggleState);
    	
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
    	cameraServos.moveCamera(-xboxgun.getRawAxis(0), xboxgun.getRawAxis(1));
    	if(xboxgun.getRawButton(10)) {
    		cameraServos.setPosition(-86, 75);
    	}else if(xboxgun.getRawButton(9)) {
    		cameraServos.setPosition(253, 73);
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
