/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.shooting;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

public class Spin extends CommandBase {

  private final Shooter m_shooter;
  private final DoubleSupplier m_rpm;
  private final boolean m_stopAtEnd;

  /**
   * Creates a new Spin.
   */
  public Spin(Shooter shooter, DoubleSupplier rpm) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(shooter);

    m_shooter = shooter;
    m_rpm = rpm;
    m_stopAtEnd = false;
  }

  public Spin(Shooter shooter, DoubleSupplier rpm, boolean stopAtEnd) {

    addRequirements(shooter);

    m_shooter = shooter;
    m_rpm = rpm;
    m_stopAtEnd = stopAtEnd;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    m_shooter.setRPM(m_rpm.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    if(m_stopAtEnd) {
      m_shooter.setRPM(0);
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
