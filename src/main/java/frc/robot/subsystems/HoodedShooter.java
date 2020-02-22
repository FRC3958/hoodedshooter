/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ShooterConstants;

public class HoodedShooter extends SubsystemBase {

  private final WPI_TalonSRX m_master = new WPI_TalonSRX(ShooterConstants.kTalonPortRight);
  private final WPI_TalonSRX m_slave = new WPI_TalonSRX(ShooterConstants.kTalonPortLeft); 

  /**
   * Creates a new SideShooter.
   */
  public HoodedShooter() {

    m_master.configFactoryDefault();
    m_slave.configFactoryDefault();

    // coast for faster subsequent rev ups and less wear and tear
    m_master.setNeutralMode(NeutralMode.Coast);
    m_slave.setNeutralMode(NeutralMode.Coast);

    // config encoder for use in loop
    m_master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, ShooterConstants.kPIDLoopIdx, Constants.kTimeout);

    // phase sensor
    m_master.setSensorPhase(false);

    // config nominal and peak outputs
    m_master.configNominalOutputForward(0);
    m_master.configNominalOutputReverse(0); // we never want to reverse, could cause mechanical issues!
    m_master.configPeakOutputForward(+1);
    m_master.configPeakOutputReverse(-1);

    // config gains
    // running the loop on the talon
    m_master.config_kF(ShooterConstants.kPIDLoopIdx, ShooterConstants.kGains.kF);
    m_master.config_kP(ShooterConstants.kPIDLoopIdx, ShooterConstants.kGains.kP);
    m_master.config_kI(ShooterConstants.kPIDLoopIdx, ShooterConstants.kGains.kI);
    m_master.config_kD(ShooterConstants.kPIDLoopIdx, ShooterConstants.kGains.kD);
    
    // slavery
    m_slave.follow(m_master);
    m_slave.setInverted(InvertType.OpposeMaster);
  }

  @Override
  public void periodic() {
    
    SmartDashboard.putNumber("enc native", m_master.getSelectedSensorVelocity());
    SmartDashboard.putNumber("enc rpm", ShooterConstants.getRPMFromNativeVelocity(m_master.getSelectedSensorVelocity()));
  }

  public void setNative(int targetVelocity) {

    m_master.set(ControlMode.Velocity, targetVelocity);
  }

  public void setRPM(double rpm) {

    setNative(ShooterConstants.getVelocityNativeFromRPM(rpm));
  }

  /**
   * NOT an absolute value. Negative indicated measurement is lower than setpoint!
   * @return
   */
  public double getClosedLoopErrorPercent() {
    
    return m_master.getClosedLoopError() / m_master.getClosedLoopTarget();
  }

  public boolean isDippedPastShotThreshold() {

    // error percent is not absolute!!!
    return getClosedLoopErrorPercent() <= -ShooterConstants.kShootDipPercent;
  }
}
