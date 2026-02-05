// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
// import com.revrobotics.spark.config.SparkBaseConfig;
// import com.revrobotics.spark.SparkBase;
import frc.robot.Config.*;

/**
 * This is a demo program showing the use of the ArcadeDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  // definitions and objects. see config for ids

  // general objects (not specific to a subsystem)
  private final XboxController m_controller = new XboxController(0);
  private final Timer m_timer = new Timer();

  // drivetrain objects
  private final SparkMax m_leftLeadSparkMax = 
    new SparkMax(Drivetrain.kDriveLeftLeadCANID, MotorType.kBrushless);
  private final SparkMax m_rightLeadSparkMax = 
    new SparkMax(Drivetrain.kDriveRightLeadCANID, MotorType.kBrushless);
  private final SparkMax m_leftFollowSparkMax = 
   new SparkMax(Drivetrain.kDriveLeftFollowCANID, MotorType.kBrushless);
  private final SparkMax m_rightFollowSparkMax = 
    new SparkMax(Drivetrain.kDriveRightFollowCANID, MotorType.kBrushless);
  
  private final DifferentialDrive m_robotDrive = 
    new DifferentialDrive(m_leftLeadSparkMax::set, m_rightLeadSparkMax::set);

  // climb objects
  DigitalInput m_climbTopLimitSwitch = new DigitalInput(Climb.kTopLimitSwitchDIOPort);
  DigitalInput m_climbBottomLimitSwitch = new DigitalInput(Climb.kBottomLimitSwitchDIOPort);
  SparkMax m_climbMotor = new SparkMax(Climb.kMotorCANID, MotorType.kBrushless);

  // shooter objects
  SparkMax m_shootMotor = new SparkMax(Shoot.kMotorCANID, MotorType.kBrushless);

  // intake objects
  SparkMax m_runIntakeMotor = new SparkMax(Intake.kRunMotorCANID, MotorType.kBrushless);
  SparkMax m_deployIntakeMotor = new SparkMax(Intake.kDeployMotorCANID, MotorType.kBrushed);
  DigitalInput m_intakeUpLimitSwitch = new DigitalInput(Intake.kUpLimitSwitchDIOPort);
  DigitalInput m_intakeDownLimitSwitch = new DigitalInput(Intake.kDownLimitSwitchDIOPort);
  Boolean m_intakeIsMoving = false;
  

  /** Called once at the beginning of the robot program. */
  public Robot() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightLeadSparkMax.setInverted(true);

    // set up for followers in drivetrain
    SparkMaxConfig leftFollowConfig = new SparkMaxConfig();
    leftFollowConfig.follow(2);
    m_leftFollowSparkMax.configure
      (leftFollowConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig rightFollowConfig = new SparkMaxConfig();
    rightFollowConfig.follow(1);
    m_rightFollowSparkMax.configure
      (rightFollowConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      // end of Robot class
  }

  @Override
  public void teleopInit() {
    CommandScheduler.getInstance().disable();
  }

  @Override
  public void teleopPeriodic() {
    // drive command, using the left stick only
    m_robotDrive.arcadeDrive(m_controller.getLeftY(), m_controller.getLeftX());

    // button bindings
    // A and B are to deploy/bring back in the intake
    // X is shoot
    // Y is run intake
    // left bumper is climb
    // System.out.println(m_intakeUpLimitSwitch.get()); 
    // System.out.println(m_controller.getAButtonPressed());

    if (m_controller.getAButtonPressed() &
      !m_intakeDownLimitSwitch.get()) {
        m_deployIntakeMotor.set(Intake.kDeployMotorSpeed);
        m_intakeIsMoving = true;
          System.out.println("deploy intake");
      // deploy intake
    } 


    if (m_intakeIsMoving & (m_controller.getAButtonReleased() | 
      m_intakeDownLimitSwitch.get())) {
        m_deployIntakeMotor.set(.0);
        m_intakeIsMoving = false;
        //while (m_intakeUpLimitSwitch.get()) {
          
        //}
          System.out.println("deploy intake stop");
    }
     
    if (m_controller.getBButtonPressed() &  
    !m_intakeUpLimitSwitch.get()) {
      m_intakeIsMoving = true;
      m_deployIntakeMotor.setInverted(true);
      m_deployIntakeMotor.set(Intake.kDeployMotorSpeed);
      System.out.println("retract intake");
      // bring in intake
    }

    if (m_intakeIsMoving & (m_controller.getBButtonReleased() | 
    m_intakeUpLimitSwitch.get())) {
      m_intakeIsMoving = false;
      m_deployIntakeMotor.setInverted(false);
      m_deployIntakeMotor.set(0);
          System.out.println("retract intake stop");
    }

    if (m_controller.getYButtonPressed()) {
      System.out.println("run intake");
      m_runIntakeMotor.set(Intake.kRunMotorSpeed);
      // run intake
    }

    if (m_controller.getYButtonReleased()) {
      System.out.println("stop intake");
      m_runIntakeMotor.set(0);
      // stop intake
    }

    if (m_controller.getXButtonPressed()) {
      System.out.println("shoot");
      m_shootMotor.set(Shoot.kMotorSpeed);
      // shoot
    }

    if (m_controller.getXButtonReleased()) {
      System.out.println("stop shoot");
      m_shootMotor.set(0);
      // stop shoot
    }

    if (m_controller.getLeftBumperButtonPressed() & !m_climbTopLimitSwitch.get()) {
      System.out.println("climb");
      m_climbMotor.set(Climb.kMotorSpeed);
      // climb
    }

    if (m_controller.getLeftBumperButtonReleased() | m_climbTopLimitSwitch.get()) {
      m_climbMotor.set(0);
      // stop climb
    }

  }

  @Override
  public void autonomousInit() {
    m_timer.restart();
    CommandScheduler.getInstance();
  }

  @Override
  public void autonomousPeriodic() {
    // most basic auto program
    if (m_timer.get() < 3.0) {
      CommandScheduler.getInstance().run();
      // arcadeDrive(speed, rotation) - rotation = 0 for driving straight
      m_robotDrive.arcadeDrive(0.1, 0, false);
      m_shootMotor.set(.3);
      System.out.println("auto drive forward");
    } 
    if (m_timer.get() > 3.0 & m_timer.get() < 5.0);
    m_shootMotor.set(Shoot.kMotorSpeed);
    System.out.println("auto shoot");
    //m_shootMotor.set(0);
    //m_shootMotor.set(Shoot.kMotorSpeed);
  }

}