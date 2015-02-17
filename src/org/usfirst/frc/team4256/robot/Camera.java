package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;

public class Camera implements LiveWindowSendable {
	Servo x;
	Servo y;
	int sensitivity;
	double positionX;
	double positionY;
	
	private int minX = -1000;
	private int maxX = 1000;
	public int minY = -1000;
	public int maxY = 1000;
	
	public Camera(Servo x, Servo y, int sensitivity) {
		this.x = x;
		this.y = y;
		this.sensitivity = sensitivity;
	}
	
	public void moveCamera(double axisX, double axisY) {
		setX(positionX+sensitivity*axisX);
		setY(positionY+sensitivity*axisY);
	}
	
	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public void setX(double positionX) {
		//if(positionX < maxX && positionX > minX) {
			this.positionX = positionX;
		//}
		x.setAngle(positionX);
	}
	
	public void setY(double positionY) {
		//if(positionY < maxY && positionY > minY) {
		if(positionY > maxY) {
			this.positionY = positionY;
		}else{
			this.positionY = 40;
		}
		y.setAngle(positionY);
	}
	
	public void setRange(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void displayInit() {
		SmartDashboard.getNumber("Camera Position X", positionX);
		SmartDashboard.getNumber("Camera Position Y", positionY);
	}
	
	public void display() {
		SmartDashboard.putNumber("Camera Position X", positionX);
		SmartDashboard.putNumber("Camera Position Y", positionY);
	}
	
	
	private ITable m_table;
	
	@Override
	public void initTable(ITable subtable) {
		m_table = subtable;
		updateTable();
	}

	@Override
	public ITable getTable() {
		return m_table;
	}

	@Override
	public String getSmartDashboardType() {
		return "Camera";
	}

	@Override
	public void updateTable() {
		if (m_table != null) {
			m_table.putNumber("Position X", positionX);
			m_table.putNumber("Position Y", positionY);
		}
	}

	@Override
	public void startLiveWindowMode() {
	}

	@Override
	public void stopLiveWindowMode() {
	}
}
