// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

// import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
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
  private final DifferentialDrive m_robotDrive2 = new DifferentialDrive(m_leftFollowSparkMax::set, m_rightFollowSparkMax::set);
  
  private final XboxController m_controller = new XboxController(0);

  private final Timer m_timer = new Timer();
  
  // DigitalInput m_toplimitSwitch = new DigitalInput(0);
  // DigitalInput m_bottomlimitSwitch = new DigitalInput(1);
  // SparkMax m_LimitTestMotor = new SparkMax(0, MotorType.kBrushless);
  // private final DigitalInput m_limitSwitch = new DigitalInput(0);

  /** Called once at the beginning of the robot program. */
  public Robot() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightLeadSparkMax.setInverted(true);
    m_rightFollowSparkMax.setInverted(true);
    // m_rightLeadSparkMax.configure(new SparkBaseConfig.inverted(true), ResetMode.kFollower, PersistMode.kPersistParameters);

    SendableRegistry.addChild(m_robotDrive, m_leftLeadSparkMax);
    SendableRegistry.addChild(m_robotDrive, m_rightLeadSparkMax);
    SendableRegistry.addChild(m_robotDrive, m_leftFollowSparkMax);
    SendableRegistry.addChild(m_robotDrive, m_rightFollowSparkMax);
      
    //DigitalInput topLimit = new DigitalInput(0);
    //SparkMax climbMotor = new SparkMax(2, MotorType.kBrushless);
  }

  
  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(m_controller.getLeftY(), m_controller.getLeftX());
    m_robotDrive2.arcadeDrive(m_controller.getLeftY(), m_controller.getLeftX());

    if (m_controller.getAButton()) {

      // setMotorSpeed(getAButton.getRawAxis(2));
      // m_LimitTestMotor
      /**
       * button motors dio limitSwitch actuator?
       */
    }
    else {
      // stop motor?
    }
     
    if (m_controller.getBButton()) {
      // PERIPHERAL FOR B HERE
    }
    else {}

    if (m_controller.getYButton()) {
      // PERIPHERAL FOR Y HERE
    }
    else {}

    if (m_controller.getXButton()) {
      // PERIPHERAL FOR X HERE
    }
    else {}

  }

  @Override
  public void autonomousInit() {
    m_timer.restart();
  }

  @Override
  public void autonomousPeriodic() {
    if(m_timer.get() < 2.0) {
      m_robotDrive.arcadeDrive(0.1, 0.1, false);
      m_robotDrive2.arcadeDrive(0.1, 0.1, false);
    }
  else {
    m_robotDrive.stopMotor();
    m_robotDrive2.stopMotor();
  }
  }


  /** private final limitClimbThing(double speed) {
       if (!topLimit.get() && speed > 0 {
          climbMotor.set(0);
        }
        else {
          climbMotor.set(speed);
        }
        );
      }
  */
}
