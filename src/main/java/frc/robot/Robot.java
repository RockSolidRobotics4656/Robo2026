//GITHUB TEST

// hello github!
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.AnalogInput;
// import edu.wpi.first.wpilibj.AnalogTrigger;
// import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.Joystick;
// import edu.wpi.first.net.PortForwarder;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

// import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
// import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
// import com.revrobotics.spark.config.SparkBaseConfig;
// import com.revrobotics.spark.SparkBase;

/**
 * This is a demo program showing the use of the ArcadeDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {

  private final SparkMax m_leftLeadSparkMax = new SparkMax(2, MotorType.kBrushless);
  private final SparkMax m_rightLeadSparkMax = new SparkMax(1, MotorType.kBrushless);
  private final SparkMax m_leftFollowSparkMax = new SparkMax(3, MotorType.kBrushless);
  private final SparkMax m_rightFollowSparkMax = new SparkMax(4, MotorType.kBrushless);
  
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftLeadSparkMax::set, m_rightLeadSparkMax::set);
  // private final DifferentialDrive m_robotDrive2 = new DifferentialDrive(m_leftFollowSparkMax::set, m_rightFollowSparkMax::set);

  DigitalInput m_TestlimitSwitch = new DigitalInput(1);
  SparkMax m_LimitTestMotor = new SparkMax(9, MotorType.kBrushless);


  private final XboxController m_controller = new XboxController(0);

  private final Timer m_timer = new Timer();
  
  // private final DigitalInput m_limitSwitch = new DigitalInput(0);

  /** Called once at the beginning of the robot program. */
  public Robot() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    // m_rightLeadSparkMax.setInverted(true);
    // m_rightFollowSparkMax.setInverted(true);
    // m_rightLeadSparkMax.configure(new SparkBaseConfig.inverted(true), ResetMode.kFollower, PersistMode.kPersistParameters);

    // SendableRegistry.addChild(m_robotDrive, m_leftLeadSparkMax);
    // SendableRegistry.addChild(m_robotDrive, m_rightLeadSparkMax);
    // SendableRegistry.addChild(m_robotDrive, m_leftFollowSparkMax);
    // SendableRegistry.addChild(m_robotDrive, m_rightFollowSparkMax);
    
    // DigitalInput m_input = new DigitalInput(0);
    
    // AnalogTrigger m_Trigger0 = new AnalogTrigger(0);
    // AnalogInput m_input = new AnalogInput(1);
    // AnalogTrigger m_Trigger1 = new AnalogTrigger(m_input);

    SparkMaxConfig leftFollowConfig = new SparkMaxConfig();
    leftFollowConfig.follow(2);
    m_leftFollowSparkMax.configure(leftFollowConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig rightFollowConfig = new SparkMaxConfig();
    rightFollowConfig.follow(1);
    m_rightFollowSparkMax.configure(rightFollowConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(m_controller.getLeftY(), m_controller.getLeftX());

    if (m_controller.getAButtonPressed() & !m_TestlimitSwitch.get()) {
      m_LimitTestMotor.set(.4);
    } 

    if (m_controller.getAButtonReleased() | m_TestlimitSwitch.get()) {
      m_LimitTestMotor.set(.0);
    }
     
    if (m_controller.getBButtonPressed() & !m_TestlimitSwitch.get()) {
      m_LimitTestMotor.setInverted(true);
      m_LimitTestMotor.set(.4);
    }
    if (m_controller.getBButtonReleased() | m_TestlimitSwitch.get()) {
      m_LimitTestMotor.setInverted(false);
      m_LimitTestMotor.set(.0);
    }


    if (m_controller.getYButtonPressed()) {
      System.out.println("run intake");
      // run intake
    }
    if (m_controller.getYButtonReleased()) {
      System.out.println("stop intake");
      // stop intake
    }

    if (m_controller.getXButtonPressed()) {
      System.out.println("shoot");
      // shoot
    }
    if (m_controller.getXButtonReleased()) {
      System.out.println("stop shoot");
      // stop shoot
    }

    if (m_controller.getLeftBumperButtonPressed()) {
      System.out.println("climb");
      // climb
    }

    if (m_controller.getLeftBumperButtonReleased()) {
      System.out.println("stop climb");
      // stop climb
    }

  }

  @Override
  public void autonomousInit() {
    m_timer.restart();
  }

  @Override
  public void autonomousPeriodic() {
    if (m_timer.get() < 2.0) {
      // arcadeDrive(speed, rotation) - rotation = 0 for driving straight
      m_robotDrive.arcadeDrive(0.1, 0, false);
    } 
    else {
      m_robotDrive.stopMotor();
    }
  }


  /** private void limitClimbThing(double speed) {
       if !topLimit.get() && speed > 0 {
          climbMotor.set(0);
        }
        else {
          climbMotor.set(speed);
        }
        );
      }
  */
}