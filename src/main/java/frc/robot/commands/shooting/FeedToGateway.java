/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.shooting;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.indexing.SideBelt;

public class FeedToGateway extends CommandBase {
  
  private final SideBelt m_sideBelt;
  
  /**
   * Feeds balls into the stop-wheel
   */
  public FeedToGateway(SideBelt sideBelt) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(sideBelt);

    m_sideBelt = sideBelt;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    m_sideBelt.spin();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    m_sideBelt.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}