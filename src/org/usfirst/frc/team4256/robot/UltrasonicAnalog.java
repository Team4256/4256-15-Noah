package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;

public class UltrasonicAnalog extends SensorBase implements LiveWindowSendable {
	public boolean isOn;
	private int dioPort;
	private int analogPort;
	private DigitalOutput dio;
	private AnalogInput analogIn;
	
	private int samplesToAverage;
	
	
	private ITable m_table;
	
	public UltrasonicAnalog(int port) {
		this(port, port);
	}
	
	public UltrasonicAnalog(int dioPort, int analogPort) {
		this.dioPort = dioPort;
		this.analogPort = analogPort;
		dio = new DigitalOutput(dioPort);
		analogIn = new AnalogInput(analogPort);
		setAverageBits(5);
		on();
	}
	
	public void on() {
		isOn = true;
		dio.set(true);
	}
	
	public void off() {
		isOn = false;
		dio.set(false);
	}
	
	public void setAverageBits(int bits) {
		analogIn.setAverageBits(bits);
	}
	
	public double getVoltage() {
		if(isOn)
			return analogIn.getVoltage();
		else
			return -1;
	}
	
	public double getAverageVoltage() {
		if(isOn)
			return analogIn.getAverageVoltage();
		else
			return -1;
	}
	
	public double getMeters() {
		return getAverageVoltage()*1.0246;
	}
	
	public static double getAngle(UltrasonicAnalog u1, UltrasonicAnalog u2, double distBetweenSensors) {
		return (u1.getAverageVoltage()-u2.getAverageVoltage())/distBetweenSensors;
	}
	
	@Override
	public String getSmartDashboardType() {
		return "Ultrasonic Analog";
	}

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
	public void updateTable() {
		if (m_table != null) {
			m_table.putNumber("Value (m)", getMeters());
		}
	}

	@Override
	public void startLiveWindowMode() {}

	@Override
	public void stopLiveWindowMode() {}
}
