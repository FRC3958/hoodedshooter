/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.ArcadeDrive;
import frc.robot.commands.DropIntake;
import frc.robot.commands.EatBalls;
import frc.robot.commands.SwitchToDriverMode;
import frc.robot.commands.EjectBalls;
import frc.robot.commands.LiftDropIntake;
import frc.robot.commands.LiftIntake;
import frc.robot.commands.climbing.ExtendShaft;
import frc.robot.commands.climbing.LiftBot;
import frc.robot.commands.climbing.LowerShaft;
import frc.robot.commands.climbing.RaiseLowerShaft;
import frc.robot.commands.climbing.RaiseShaft;
import frc.robot.commands.climbing.RetractShaft;
import frc.robot.commands.shooting.AlignToTarget;
import frc.robot.commands.shooting.FeedToGateway;
import frc.robot.commands.shooting.FeedToShooter;
import frc.robot.commands.shooting.FullShootRoutine;
import frc.robot.commands.shooting.LoadToConveyor;
import frc.robot.commands.shooting.SpinUpToSpeed;
import frc.robot.constants.ControlConstants;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Limelight.LedMode;
import frc.robot.subsystems.indexing.ConveyorBelt;
import frc.robot.subsystems.indexing.Gateway;
import frc.robot.subsystems.indexing.SideBelt;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {

  // OI
  private final XboxController m_driverController = new XboxController(ControlConstants.kDriverControllerPort);
  private final XboxController m_operatorController = new XboxController(ControlConstants.kOperatorControllerPort);

  // The robot's subsystems
  private final Drivetrain m_drive = new Drivetrain();
  
  private final Intake m_intake = new Intake();
  
  private final Shooter m_shooter = new Shooter();
  
  private final Limelight m_limelight = new Limelight();
  
  private final Climber m_climber = new Climber();

  private final SideBelt m_sideBelt = new SideBelt();
  private final ConveyorBelt m_conveyor = new ConveyorBelt();
  private final Gateway m_gateway = new Gateway();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    setupSmartDashboard();
    configureButtonBindings();
  }

  /**
   * Puts all information to SmartDashboard. Putting anything general
   */
  private void setupSmartDashboard() {

    /**
     * Intake testing
     */

    SmartDashboard.putData("Drop intake", new DropIntake(m_intake));
    SmartDashboard.putData("Lift intake", new LiftIntake(m_intake));

    SmartDashboard.putData("Eat balls", new EatBalls(m_intake));
    SmartDashboard.putData("Eject balls", new EjectBalls(m_intake));

    // /**
    //  * Indexing testing
    //  */
    
    SmartDashboard.putData("Feed to gateway (from sidebelt)", 
      new FeedToGateway(m_sideBelt));
    SmartDashboard.putData("Load to conveyor (from gateway)", 
      new LoadToConveyor(m_gateway));
    SmartDashboard.putData("Feed to shooter (from conveyor)",
      new FeedToShooter(m_conveyor));

    /*
     * Shooter testing
     */

    SmartDashboard.putNumber("Test RPM setpoint", 0);
    SmartDashboard.putData("Set RPM setpoint", 
      new InstantCommand(
        () -> m_shooter.setRPM(SmartDashboard.getNumber("Test RPM setpoint", 0)),
        m_shooter
      )
    );

    SmartDashboard.putData("Zero shooter setpoint", 
      new InstantCommand(() -> m_shooter.setNative(0), m_shooter)
    );

    /**
     * Climber testing
     */

    SmartDashboard.putData("Raise shaft", new RaiseShaft(m_climber));
    SmartDashboard.putData("Lower shaft", new LowerShaft(m_climber));

    SmartDashboard.putData("Extend hook shaft", new ExtendShaft(m_climber));
    SmartDashboard.putData("Retract hook shaft", new RetractShaft(m_climber));

    SmartDashboard.putData("Lift bot (hook must be deployed)", new LiftBot(m_climber, () -> 0.3));

    /**
     * Limelight
     */

    SmartDashboard.putData("Turn lights on", new InstantCommand(() -> m_limelight.setLedMode(LedMode.kForceOff), m_limelight));
    SmartDashboard.putData("Turn lights auto", new InstantCommand(() -> m_limelight.setLedMode(LedMode.kAuto), m_limelight));   

     /**
      * Music
      */

    SmartDashboard.putData("Play music", new InstantCommand(() -> m_climber.playMusic()));
    SmartDashboard.putData("Pause music", new InstantCommand(() -> m_climber.stopMusic()));
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {

    // config controls
    configureDriverControls(m_driverController, m_operatorController);
    configureOperatorControls(m_operatorController, m_driverController);
  }

  /**
   * Configure all driver controls on the given controller
   * @param controller
   */
  private void configureDriverControls(XboxController controller, XboxController operatorController) {
    
    // Drive with stick (note: it is automatically linearly limited)
    m_drive.setDefaultCommand(new ArcadeDrive(m_drive, 
      () -> Util.deadband(controller.getY(Hand.kRight), 0.1),
      () -> Util.deadband(controller.getX(Hand.kLeft), 0.1))
    );

    // Auto align
    new JoystickButton(controller, ControlConstants.Driver.kAutoAlign)
      .whenHeld(new AlignToTarget(
        m_limelight,
        m_drive, 
        true,
        () -> Util.deadband(controller.getY(Hand.kRight), 0.1))
    );
  }

  /**
   * Configure all operator controls on the given controller
   * @param controller
   */
  private void configureOperatorControls(XboxController controller, XboxController driverController) {

    // Eat balls
    new JoystickButton(controller, ControlConstants.Operator.kEat)
      .whenHeld(new EatBalls(m_intake)
    );

    // Clear intake
    new JoystickButton(controller, ControlConstants.Operator.kEjectBalls)
      .whenHeld(new EjectBalls(m_intake)
    );

    // Lift lower intake
    new JoystickButton(controller, ControlConstants.Operator.kLiftLowerIntake)
      .whenPressed(new LiftDropIntake(m_intake)
    );

    // Manual ramp up
    new Button(() -> controller.getRawAxis(ControlConstants.Operator.kManualRampUp) >= 0.5)
      .whenHeld(new SpinUpToSpeed(
        m_shooter,
        () -> Util.calculateRPM(m_limelight.getApproximateDistanceMeters()),
        true
      )
    );

    // Run entire shooting routine, maintaining alignment when held, and shoot balls
    // over and over again at the same time
    new Button(
      () -> controller.getRawAxis(ControlConstants.Operator.kShoot) >= 0.5
      && driverController.getRawButton(ControlConstants.Driver.kAutoAlign)  // driver must also be autoaligning
    ).whileHeld(
      new FullShootRoutine(m_shooter, m_sideBelt, m_conveyor, m_gateway,
        () -> {
          // manual ramp up rpm logic
          if(m_limelight.isValidTargetPresent()) {
            return 2000;
          }

          return Util.calculateRPM(m_limelight.getApproximateDistanceMeters());
        }
      )
    );

    // Switch driver mode limelight
    new JoystickButton(controller, ControlConstants.Operator.kLimelightModeSwitch)
      .toggleWhenPressed(new SwitchToDriverMode(m_limelight)
    );

    // Toggle raising / lowering shaft
    new JoystickButton(controller, ControlConstants.Operator.kToggleClimberPiston)
      .whenPressed(new RaiseLowerShaft(m_climber)
    );

    // Extend shaft
    new JoystickButton(controller, ControlConstants.Operator.kExtendShaft)
      .whenPressed(new ExtendShaft(m_climber)
    );

    // Retract shaft
    new JoystickButton(controller, ControlConstants.Operator.kRetractShaft)
      .whenPressed(new RetractShaft(m_climber)
    );

    // Lift (with winch)
    new Button(() -> -controller.getRawAxis(ControlConstants.Operator.kLift) >= 0.2)
      .whenPressed(new LiftBot(m_climber, () -> -controller.getRawAxis(ControlConstants.Operator.kLift))
    );
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    
    // var voltageConstraint = new DifferentialDriveVoltageConstraint(
    //   new SimpleMotorFeedforward(
    //     DriveConstants.ksVolts, 
    //     DriveConstants.kvVoltSecondsPerMeter, 
    //     DriveConstants.kaVoltSecondsSquaredPerMeter
    //   ),
    //   DriveConstants.kKinematics,
    //   10 // not 12, account for voltage sag
    // );

    // var config = new TrajectoryConfig(
    //   DriveConstants.kMaxVelocityMPS * .6, // 60% of max physical speed
    //   DriveConstants.kMaxVelocityMPS / 3.0 // 3 seconds to reach max speed, deemed safe for auton
    // ).setKinematics(DriveConstants.kKinematics)
    // .addConstraint(voltageConstraint);


    // var trajectory = TrajectoryGenerator.generateTrajectory(
    //   new Pose2d(0, 0, new Rotation2d(0)),
    //   List.of(
    //     // internal waypoints
    //   ),
    //   new Pose2d(3, 0, new Rotation2d(0)),
    //   config
    // );

    // var ramsete = new RamseteCommand(
    //   trajectory, 
    //   m_drive::getPose, 
    //   new RamseteController(AutoConstants.kRameseteB, AutoConstants.kRameseteZeta),
    //   new SimpleMotorFeedforward(
    //     DriveConstants.ksVolts, 
    //     DriveConstants.kvVoltSecondsPerMeter, 
    //     DriveConstants.kaVoltSecondsSquaredPerMeter
    //   ), 
    //   DriveConstants.kKinematics, 
    //   m_drive::getWheelSpeeds, 
    //   new PIDController(DriveConstants.kPDriveVel, 0.0, 0.0),
    //   new PIDController(DriveConstants.kPDriveVel, 0.0, 0.0),
    //   m_drive::tankDriveVolts, 
    //   m_drive
    // );

    // return ramsete.andThen(() -> m_drive.tankDriveVolts(0.0, 0.0));

    return null;
  } 
}
