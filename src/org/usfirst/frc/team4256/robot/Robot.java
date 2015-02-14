
package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
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
	
	DigitalInput limitSwitch = new DigitalInput(0);
	
	Compressor Compressor = new Compressor();
	
	Servo servoX = new Servo(0);
	Servo servoY = new Servo(1);
	Camera cameraServos = new Camera(servoX, servoY, (int)6.9);
	
	ExtendedCANTalon rightBack = new ExtendedCANTalon(13);
	ExtendedCANTalon rightFront = new ExtendedCANTalon(12);
	ExtendedCANTalon leftBack = new ExtendedCANTalon(11);
	ExtendedCANTalon leftFront = new ExtendedCANTalon(10);
	ExtendedCANTalon verticalLift = new ExtendedCANTalon(15);
	ExtendedCANTalon stackerToteLift = new ExtendedCANTalon(14);
	ExtendedVictorSP wheelIntake = new ExtendedVictorSP(9);
	EncodedMotor intakeArms = new EncodedMotor(0, .4);
	ExtendedVictorSP toteRoller = new ExtendedVictorSP(7);
	RobotDrive drive = new RobotDrive(leftFront, leftBack, rightFront, rightBack);
	
	Toggle intakeToggle = new Toggle(xboxgun, 5);
	Toggle lightToggle = new Toggle(xboxdrive, 2);
	Toggle switchToggle = new Toggle(xboxdrive, 4);
	
	AnalogInput PressureGauge = new AnalogInput(0);
	
	
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
	double INTAKE_SPEED = Math.PI/10;
	int maxHeight = 2000;
	int minHeight = 0;
	int maxWidth = 1000;
	int minWidth = 0;
			
    public void robotInit() {
    	//camera.setRange(-100, -2000, 2000, 10000);
    	rightFront.setInversed(true);
    	rightBack.setInversed(true);
    	PressureGauge.setAverageBits(10);
    	dashInit();
    	
    }
    
    public void dashInit() {
    	//SmartDashboard.getNumber("POV", -1);
    	SmartDashboard.putNumber("POV", -1);
    	SmartDashboard.putNumber("PORT", 0);
    	SmartDashboard.putNumber("VICTOR_PORT", 0);
    	SmartDashboard.putBoolean("Limit Switch", limitSwitch.get());
    	SmartDashboard.putNumber("Pressure", 0);
    	SmartDashboard.putNumber("PressureVoltage", 0);
    	
    }
 
	
    
    
    public void autonomousInit() {
//    	xboxdrive.setRumble(RumbleType.kLeftRumble, 1);
//    	xboxdrive.setRumble(RumbleType.kRightRumble, 1);
    }
    
    public void autonomousPeriodic() {
//    	OJ.configMotorPorts(.5);
    }
    
    
    public void teleopInit() {
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	SmartDashboard.putNumber("POV", xboxdrive.getPOV());
    	if(switchToggle.getState()) {
    		runSharedFunctions(xboxdrive);
    	}else{
    		runSharedFunctions(xboxgun);
    	}
    }
    
    
    /**
     * Controls the main functions in teleop. The joystick input is the joystick that is currently controlling the gunner functions
     */
    public void runSharedFunctions(DBJoystick joystick) {
    	double driveSpeedScale = (xboxdrive.getRawButton(6)? .5 : 1); // scaling factor reduced to 0.5
    	drive.arcadeDrive(xboxdrive.getRawAxis(4)*driveSpeedScale, xboxdrive.getRawAxis(1)*driveSpeedScale, true); // left stick on Xbox controls forward and backward direction. right sticks controls rotation.
        
    	Utility.runMotor(xboxgun, 3, 1, wheelIntake, INTAKE_SPEED); // button 3 on xboxgun (X) will run motor in forward direction, button 1 will reverse. wheelIntake represents motor type and INTAKE_SPEED represents the motor's speed
    	Utility.runLED(lightToggle, light);
    	
    	verticalLift(joystick);
    	stackerToteLift(joystick);
    	intake(joystick);
    	moveCamera();
    	
    	rumbleAlert.check();
    	
    	//camera.moveCamera(xboxgun.getRawAxis(4),xboxgun.getRawAxis(5));
    	SmartDashboard.getBoolean("Limit Switch", limitSwitch.get());
    	SmartDashboard.getNumber("Pressure", roundTo(PressureGauge.getAverageVoltage()*40.81-54.89, 0));
    	SmartDashboard.getNumber("PressureVoltage", roundTo(PressureGauge.getAverageVoltage(), 1));
    }
    
    
    public double roundTo(double n, int places) {
        	double amount = Math.pow(10, places);
        	return Math.round(n*amount)/amount;
	}

	/**
     * Moves the vertical lift up or down
     */
    public void verticalLift(DBJoystick joystick) {
    	if(joystick.getPOV() == DBJoystick.NORTH) { // xboxgun dpad (haven't found button (or in this case POV) inputs) will control direction of vertical lift. Up ascends the lift to the maximum height, down descends the lift to the minimum height.
    		verticalLift.setPosition(maxHeight);//change
    	}else if(joystick.getPOV() == DBJoystick.SOUTH) {
    		verticalLift.setPosition(minHeight);
    	}
    }
    
    
    /**
     * Moves the stacker tote lift up or down
     */
    public void stackerToteLift(DBJoystick joystick) {
    	if(joystick.axisPressed(3)) { //axis 3 (RT) and axis 2 will control direction of stackerToteLift. RT will send tote stacker to maxheight. LT will send tote stacker to minheight. 
    		stackerToteLift.setPosition(maxHeight);
    	}else if(joystick.axisPressed(2)) {
    		stackerToteLift.setPosition(minHeight);
    	}
    }
    
    
    /**
     * Toggles the intake arms
     */
    public void intake(DBJoystick joystick) {
    	if(intakeToggle.getState(joystick)) { 
    		intakeArms.setPosition(maxWidth);
    	}else{
    		intakeArms.setPosition(minWidth);
    	}
    }
    
    /**
     * Moves the camera based on joystick axis 0 and 1
     * Also jumps to the home positions if buttons x and x
     */
    public void moveCamera() {
    	cameraServos.moveCamera(xboxgun.getRawAxis(0), xboxgun.getRawAxis(1));
    	if(xboxgun.getRawButton(10)) {
    		cameraServos.setPosition(10, 10);
    	}else if(xboxgun.getRawButton(9)) {
    		cameraServos.setPosition(10, 10);
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
