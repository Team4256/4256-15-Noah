
package org.usfirst.frc.team4256.robot;

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
	
	OJ_CANTalon rightBack = new OJ_CANTalon(6); //insert correct CAN motor port for all
	OJ_CANTalon rightFront = new OJ_CANTalon(4);
	OJ_CANTalon leftBack = new OJ_CANTalon(3);
	OJ_CANTalon leftFront = new OJ_CANTalon(2);
	OJ_CANTalon verticalLift = new OJ_CANTalon(0);
	OJ_CANTalon stackerToteLift = new OJ_CANTalon(5);
	OJ_VictorSP daMouth = new OJ_VictorSP(2);
	EPMotor daMouthArms = new EPMotor(0, .4);//check port
	OJ_VictorSP toteRoller = new OJ_VictorSP(0);
	RobotDrive drive = new RobotDrive(leftFront, leftBack, rightFront, rightBack);
	
	Toggle toggle5 = new Toggle(xboxgun, 5);
	Toggle toggle2 = new Toggle(xboxdrive, 2);
	
	double MOUTH_SPEED = Math.PI/10;
    
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
    }
    
    
    public void autonomousInit() {
//    	xboxdrive.setRumble(RumbleType.kLeftRumble, 1);
//    	xboxdrive.setRumble(RumbleType.kRightRumble, 1);
    }
    
    public void autonomousPeriodic() {
    	OJ.configMotorPorts(.5);
    }
    
    
    public void teleopInit() {
    	xboxdrive.setRumble(RumbleType.kLeftRumble, 0);
    	xboxdrive.setRumble(RumbleType.kRightRumble, 0);
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	
    	SmartDashboard.putNumber("POV", xboxgun.getPOV());
    	
    	double scale = (xboxdrive.getRawButton(6)? .5 : 1);
    	drive.arcadeDrive(xboxdrive.getRawAxis(4)*scale, xboxdrive.getRawAxis(1)*scale, true);
        
    	OJ.runMotor(xboxgun, 3, 1, daMouth, MOUTH_SPEED);
    	
    	if(xboxgun.getPOV() == 90) {
    		verticalLift.setPosition(2000);//change
    	}else if(xboxgun.getPOV() == 270) {
    		verticalLift.setPosition(0);
    	}
    	if(xboxgun.getRawAxis(3) > .7) {
    		stackerToteLift.setPosition(2000);
    	}else if(xboxgun.getRawAxis(2) < .7) {
    		stackerToteLift.setPosition(0);
    	}
    	
    	if(xboxdrive.getRawButton(4)) {
    		DBJoystick newDriver = xboxdrive;
    		xboxgun = xboxdrive;
    		xboxdrive = xboxgun;
    		
    	}
    	
    	if(toggle5.getState()) {
    		daMouthArms.setPosition(1000);
    	}else{
    		daMouthArms.setPosition(0);
    	}
    	
    	if(xboxgun.getRawButton(2)) {
    		//camera.setPosition(90, 1000);
    	}
    	if(toggle2.getState()) {
    		light.set(Value.kOn);
    	}else{
    		light.set(Value.kOff);
    	}
    	//camera.moveCamera(xboxgun.getRawAxis(4),xboxgun.getRawAxis(5));
    }
    
    
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}
