/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.subsystems.Climber;

public class DeployHook extends CommandBase {

  private final Climber m_climber;

  /**
   * Creates a new DeployHook.
   */
  public DeployHook(Climber climber) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(climber);

    m_climber = climber;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    m_climber.deployHook();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_climber.isHookDeployed();
  }
}
