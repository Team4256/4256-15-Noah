
package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.RobotDrive;
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
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	DBJoystick xboxdrive = new DBJoystick(0);
	DBJoystick xboxgun = new DBJoystick(1);
	
	//OJ_Camera camera = new OJ_Camera(0, 0);
	
	Relay light = new Relay(0);
	
	DigitalInput limitswitch = new DigitalInput(0);
	
	OJ_CANTalon rightBack = new OJ_CANTalon(6); //insert correct CAN motor port for all
	OJ_CANTalon rightFront = new OJ_CANTalon(4);
	OJ_CANTalon leftBack = new OJ_CANTalon(3);
	OJ_CANTalon leftFront = new OJ_CANTalon(2);
	OJ_CANTalon verticalLift = new OJ_CANTalon(0);
	OJ_CANTalon stackerToteLift = new OJ_CANTalon(5);
	OJ_VictorSP wheelIntake = new OJ_VictorSP(2);
	EPMotor intakeArms = new EPMotor(0, .4);//check port, speed
	OJ_VictorSP toteRoller = new OJ_VictorSP(0);
	RobotDrive drive = new RobotDrive(leftFront, leftBack, rightFront, rightBack);
	
	Toggle Intaketoggle = new Toggle(xboxgun, 5);
	Toggle lighttoggle = new Toggle(xboxdrive, 2);
	
	//constants (these will change)
	double INTAKE_SPEED = Math.PI/10;
	int maxHeight = 2000;
	double depressionAmount = 0.7;
	int minHeight = 0;
	int maxWidth = 1000;
	int minWidth = 0;
			
    public void robotInit() {
    	//camera.setRange(-100, -2000, 2000, 10000);
    	rightFront.setInversed(true);
    	rightBack.setInversed(true);
    	dashInit();
    }
    public void dashInit() {
    	//SmartDashboard.getNumber("POV", -1);
    	SmartDashboard.putNumber("POV", -1);
    	SmartDashboard.putNumber("PORT", 0);
    	SmartDashboard.putBoolean("Limit Switch", limitswitch.get());
    	SmartDashboard.putNumber("POV", xboxgun.getPOV());
    }
 
	
    
    
    public void autonomousInit() {
//    	xboxdrive.setRumble(RumbleType.kLeftRumble, 1);
//    	xboxdrive.setRumble(RumbleType.kRightRumble, 1);
    }
    
    public void autonomousPeriodic() {
//    	OJ.configMotorPorts(.5);
    }
    
    
    public void teleopInit() {
    	xboxdrive.setRumble(RumbleType.kLeftRumble, 0); //controller vibration left side. 0 is float type to represent the "intensity" of the vibration.
    	xboxdrive.setRumble(RumbleType.kRightRumble, 0); //controller vibration right side
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	
    	
    	double driveSpeedScale = (xboxdrive.getRawButton(6)? .5 : 1); // scaling factor reduced to 0.5
    	drive.arcadeDrive(xboxdrive.getRawAxis(4)*driveSpeedScale, xboxdrive.getRawAxis(1)*driveSpeedScale, true); // left stick on Xbox controls forward and backward direction. right sticks controls rotation.
        
    	OJ.runMotor(xboxgun, 3, 1, wheelIntake, INTAKE_SPEED); // button 3 on xboxgun (X) will run motor in forward direction, button 1 will reverse. wheelIntake represents motor type and INTAKE_SPEED represents the motor's speed
    	
    	if(xboxgun.getPOV() == 90) { // xboxgun dpad (haven't found button (or in this case POV) inputs) will control direction of vertical lift. Up ascends the lift to the maximum height, down descends the lift to the minimum height.
    		verticalLift.setPosition(maxHeight);//change
    	}else if(xboxgun.getPOV() == 270) {
    		verticalLift.setPosition(minHeight);
    	}
    	if(xboxgun.getRawAxis(3) > depressionAmount) { //axis 3 (RT) and axis 2 will control direction of stackerToteLift. RT will send tote stacker to maxheight. LT will send tote stacker to minheight. 
    		stackerToteLift.setPosition(maxHeight);
    	}else if(xboxgun.getRawAxis(2) < depressionAmount) {
    		stackerToteLift.setPosition(minHeight);
    	}
    	
    	if(xboxdrive.getRawButton(4)) { //button 4 (Y) will control the switch on the gunners controller where all of the gunner functions go to the driver. Most likely will be set as a toggle.
    		DBJoystick newDriver = xboxdrive;
//    		xboxgun = xboxdrive;
//    		xboxdrive = xboxgun;
    		xboxgun.runSharedFunctions(xboxgun, 3, 5);
    		xboxdrive.runSharedFunctions(xboxgun, 2, 5);
    	}
    	
    	if(Intaketoggle.getState()) { 
    		intakeArms.setPosition(maxWidth);
    	}else{
    		intakeArms.setPosition(minWidth);
    	}
    	
    	if(xboxgun.getRawButton(2)) { // xboxgun button 2 (B). potential camera position
    		//camera.setPosition(90, 1000);
    	}
    	if(lighttoggle.getState()) {
    		light.set(Value.kOn);
    	}else{
    		light.set(Value.kOff);
    	}
    	//camera.moveCamera(xboxgun.getRawAxis(4),xboxgun.getRawAxis(5));
    	SmartDashboard.getBoolean("Limit Switch", limitswitch.get());
    }
    
    public void runSharedFunctions(DBJoystick joystick, int liftButton, int otherButton) {
    	
    }

    
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}
