/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.constants.IntakeConstants.*;

public class Intake extends SubsystemBase {

  private final WPI_TalonSRX m_wheels = new WPI_TalonSRX(kTalonPort);
  private final DoubleSolenoid m_solenoid 
    = new DoubleSolenoid(kSolenoidForwardChannel, kSolenoidReverseChannel);

  /**
   * Creates a new Intake.
   */
  public Intake() {

    m_wheels.configFactoryDefault();

    m_wheels.setInverted(InvertType.InvertMotorOutput);

    m_wheels.configPeakCurrentLimit(30);
    m_wheels.enableCurrentLimit(true);

    m_wheels.setNeutralMode(NeutralMode.Brake);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    SmartDashboard.putNumber("Intake draw", m_wheels.getSupplyCurrent());
  }

  public void dropBar() {

    m_solenoid.set(Value.kForward);
  }

  public void liftBar() {

    m_solenoid.set(Value.kReverse);
  }

  /**
   * Could be innacurate as it only accounts for what the solenoid is set to be...
   * @return
   */
  public boolean isBarDown() {

    return m_solenoid.get() == Value.kForward;
  }

  public void toggleBar() {

    if(isBarDown())
      liftBar();
    else
      dropBar();
  }
  
  public void spin(double speed) {

    m_wheels.set(ControlMode.PercentOutput, speed);
  }

  public void stopSpinning() {

    m_wheels.set(ControlMode.PercentOutput, 0.0);
  }
}
