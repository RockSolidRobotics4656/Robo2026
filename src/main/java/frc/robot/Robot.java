// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// this links to the Config file, where constants are stored.
import frc.robot.Config.*;

// WPI imports
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.cameraserver.CameraServer;

// REVLIB specific imports. If errored, download REVLIB.
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import java.util.Optional;
import java.util.OptionalInt;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
// import com.revrobotics.spark.SparkLowLevel;

/* ----------------------------------------------------------------- */
// Arcade drive robot, using an xbox controller and CANsparkmax motor controllers.
public class Robot extends TimedRobot {
  // definitions and objects. see config for ids

  // general objects (not specific to a subsystem)
  private final XboxController m_controller = 
    new XboxController(0);
  private final Timer m_timer = 
   new Timer();
   Thread m_visionThread; 

  // drivetrain objects
    // motors
    private final SparkMax m_rightLeadSparkMax = 
      new SparkMax(Drivetrain.kDriveRightLeadCANID, MotorType.kBrushless);
    private final SparkMax m_leftLeadSparkMax = 
      new SparkMax(Drivetrain.kDriveLeftLeadCANID, MotorType.kBrushless);
    private final SparkMax m_rightFollowSparkMax = 
      new SparkMax(Drivetrain.kDriveRightFollowCANID, MotorType.kBrushless);
    private final SparkMax m_leftFollowSparkMax = 
      new SparkMax(Drivetrain.kDriveLeftFollowCANID, MotorType.kBrushless);

    // motor config declaration (not configured here) 
    private final SparkMaxConfig rightConfig = 
      new SparkMaxConfig();
    
    // motor group (followers grouped later)
    private final DifferentialDrive m_robotDrive = 
      new DifferentialDrive(m_rightLeadSparkMax::set, m_leftLeadSparkMax::set);
    private final DifferentialDrive m_robotDrive2 = 
      new DifferentialDrive(m_rightFollowSparkMax::set, m_leftFollowSparkMax::set);

  // shooter objects
    //motors
    SparkMax m_shootMotor = new SparkMax
      (Shoot.kRunMotorCANID, MotorType.kBrushless);
    SparkMax m_backKickMotor = new SparkMax
      (Shoot.kBackKickMotorCANID, MotorType.kBrushed);
    
    // variable
    Boolean shootIsMoving = false;
    Double shootStartTime = 0.0;
    
    //timer
    final Timer m_timeSave = new Timer();

    //config
    SparkMaxConfig frontKickConfig = 
      new SparkMaxConfig();

  // intake objects
    // motors
    SparkMax m_runIntakeMotor = 
      new SparkMax(Intake.kRunMotorCANID, MotorType.kBrushed);
    SparkMax m_deployIntakeMotor = 
      new SparkMax(Intake.kDeployMotorCANID, MotorType.kBrushed);

    // limit switches
    DigitalInput m_intakeUpLimitSwitch = 
      new DigitalInput(Intake.kUpLimitSwitchDIOPort);
    DigitalInput m_intakeDownLimitSwitch = 
      new DigitalInput(Intake.kDownLimitSwitchDIOPort);

    // variable
    Boolean m_intakeIsMoving = false;
    double m_intakeStartTime;
    boolean m_intakeUp = true;
    double count = 0;
    double deployIntakeTime = 0.0;

    // motor config declaration (not configured here)
    SparkMaxConfig upIntakeConfig = 
      new SparkMaxConfig();
    SparkMaxConfig downIntakeConfig = 
      new SparkMaxConfig();
    SparkMaxConfig runIntakeInConfig = 
      new SparkMaxConfig();
    SparkMaxConfig runIntakeOutConfig = 
      new SparkMaxConfig();

  /* Called once at the beginning of the robot program. */
  /* ---------------------------------------------------------------------------------------- */

  /* ---------------------------------------------------------------------------------------- */
  public Robot() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    // setinverted is deprecated, this is the new invert method.
    rightConfig.inverted(true);
    m_leftFollowSparkMax.configure(rightConfig, 
      ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
    
    m_leftLeadSparkMax.configure(rightConfig, 
      ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

    //intake config
    downIntakeConfig.inverted(false);
    upIntakeConfig.inverted(true);
    runIntakeInConfig.inverted(false);
    runIntakeOutConfig.inverted(true);

    // camera config if camera
    // CameraServer.startAutomaticCapture();

    // end of Robot class
  }


  @Override
  public void teleopInit() {
    // this runs once at the start of teleop
    
    // kill auto
    m_deployIntakeMotor.set(0);
    m_backKickMotor.set(0);
    m_runIntakeMotor.set(0);
    m_shootMotor.set(0);
    m_robotDrive.arcadeDrive(0, 0);
    m_robotDrive2.arcadeDrive(0, 0);
    System.out.println("Teleop ready...");
  }

  /* ---------------------------------------------- */
  @Override
  public void teleopPeriodic() {
    // runs every 20ms during teleop. 
    // returns error but continues running if it can't finish the loop in time.

    // stop everything. emergency button
    if (m_controller.getRightStickButton() & m_controller.getLeftStickButton()) {
      System.out.println("E-STOP");
      m_deployIntakeMotor.set(0);
      m_backKickMotor.set(0);
      m_runIntakeMotor.set(0);
      m_shootMotor.set(0);
      m_robotDrive.arcadeDrive(0, 0);
      m_robotDrive2.arcadeDrive(0, 0);
    } else {

      // teleoperated drive. involves buttons and joysticks.

      // drive command, using the left stick only.
      m_robotDrive.arcadeDrive(m_controller.getLeftY() * Drivetrain.kSpeedFactor, -m_controller.getLeftX() * Drivetrain.kTurnFactor);
      m_robotDrive2.arcadeDrive(m_controller.getLeftY() * Drivetrain.kSpeedFactor, -m_controller.getLeftX() * Drivetrain.kTurnFactor);
      /* ---------------------------------------------------------- */
      // button bindings
      // A - inward and B - outward intake tilt
      // X is shoot
      // bumpers run intake in and out
      // right stick button is climb down
      /* ---------------------------------------------------------- */

      if (!m_controller.getBButton() &
        m_intakeDownLimitSwitch.get() & (m_controller.getLeftTriggerAxis() < .5) & (Timer.getTimestamp() - deployIntakeTime) > 0.2) {
          m_deployIntakeMotor.configure(downIntakeConfig, 
            ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
          m_deployIntakeMotor.set(Intake.kDeployIntakeHomeSpeed);
          System.out.println("deploy intake");
        // deploy intake
      } 
      else if (!m_intakeDownLimitSwitch.get()) {
        m_deployIntakeMotor.set(0);
      } 
      else if ((Timer.getTimestamp() - deployIntakeTime) < 0.2) {
        m_deployIntakeMotor.set(Intake.kDeployMotorUpSpeed);
      }

      if (!m_controller.getBButton() & 
        (m_controller.getLeftTriggerAxis() < .5) & (Timer.getTimestamp() - deployIntakeTime) > 0.2) {
          deployIntakeTime = Timer.getTimestamp();
      }
      
      if (m_controller.getBButton() & 
        m_intakeUpLimitSwitch.get()) {
          m_intakeIsMoving = true;
          m_deployIntakeMotor.configure(upIntakeConfig, 
            ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
          m_deployIntakeMotor.set(Intake.kDeployMotorUpSpeed);
          System.out.println("retract intake");
          // bring in intake
      } 
      else if (/*m_controller.getBButtonReleased() |*/ 
        (!m_intakeUpLimitSwitch.get() & m_intakeIsMoving)) {
          m_intakeIsMoving = false;
          m_deployIntakeMotor.set(0);
          System.out.println("retract intake stop");
      }

      /* run intake to deploy 
      * hold b to run intake
      */

      if (m_controller.getLeftBumperButtonPressed()) {
        System.out.println("run intake");
        m_runIntakeMotor.configure(runIntakeInConfig, 
          ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
            m_runIntakeMotor.set(Intake.kRunMotorSpeed);
        // run intake
      }

      if (m_controller.getLeftBumperButtonReleased()) {
        System.out.println("stop intake");
        m_runIntakeMotor.set(0);
        // stop intake
      }

      if (m_controller.getRightBumperButtonPressed()) {
        System.out.println("run outtake");
        m_runIntakeMotor.configure(runIntakeOutConfig,
          ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
            m_runIntakeMotor.set(Intake.kRunMotorSpeed);
        // run intake
      }

      if (m_controller.getRightBumperButtonReleased()) {
        System.out.println("stop outtake");
        m_runIntakeMotor.set(0);
        // stop outtake
      }

      if (m_controller.getXButtonPressed() & !shootIsMoving) {
        System.out.println("Shoot init");
        shootIsMoving = true;
        shootStartTime = Timer.getTimestamp();
        System.out.println(shootStartTime);
        m_backKickMotor.set(-1 * Shoot.kKickMotorSpeed);
        m_shootMotor.set(Shoot.kInitSpeed);
      }

      if (m_controller.getXButton() & (Timer.getTimestamp() -shootStartTime) > (Shoot.kKickDelay - 0.1) &
        (Timer.getTimestamp() -shootStartTime) < Shoot.kKickDelay) {
          m_backKickMotor.set(0);
      }

      if (m_controller.getXButton() & (Timer.getTimestamp() -shootStartTime) > Shoot.kKickDelay) {
        System.out.println("start kick");
        m_shootMotor.set(Shoot.kRunMotorSpeed);
        m_backKickMotor.set(Shoot.kKickMotorSpeed);
      }

      if (m_controller.getXButtonReleased() & shootIsMoving) {
          System.out.println("Shoot Stopper");
          m_shootMotor.set(Shoot.kStoppedMotor);
          m_backKickMotor.set(Shoot.kStoppedMotor);
          shootIsMoving = false;
      }

    }

  }

  /* ------------------------------------------------------- */
  // AUTO START
  /* ------------------------------------------------------- */

  @Override
  public void autonomousInit() {
    m_timer.restart();
    // CommandScheduler.getInstance();
    OptionalInt station = DriverStation.getLocation();
    Optional<Alliance> ally = DriverStation.getAlliance();
    System.out.println("Driver Station Location");
    System.out.println(station);
    
    System.out.println("Driver Station Alliance");
    System.out.println(ally);
  }

  @SuppressWarnings("unused")
  @Override 
  public void autonomousPeriodic() {

     /* -------------------------------------------------------- */
     // AUTO #1
     /* -------------------------------------------------------- */
     // move & shoot & move


    if (m_timer.get() > 3.0 & m_timer.get() < 5.0 & 
      Auto.kautoVariable == 1) {
        m_shootMotor.set(Shoot.kRunMotorSpeed);
        System.out.println("auto shoot");
    }

    if (m_timer.get() > 5 & m_timer.get() < 8 & 
      Auto.kautoVariable == 1) {
    m_robotDrive.arcadeDrive(-0.1, 0, false);
    }

    if (m_timer.get() > 8 & m_timer.get() < 20 & 
      Auto.kautoVariable == 1){
        m_robotDrive.arcadeDrive(0, 0.6);
    }
      
    /*--------------------------------------------------------- */
    //auto #2
    //RETRIEVAL
    /*-------------------------------------------------------- */
    if (m_timer.get() < 2.0 & Auto.kautoVariable == 2) {
        m_robotDrive.arcadeDrive(0, .5);
    }

    if (m_timer.get() < 5.0 & m_timer.get() > 2.0 
      & Auto.kautoVariable == 2) {
        m_robotDrive.arcadeDrive(.8, 0);
        m_shootMotor.set(Shoot.kRunMotorSpeed);
    }

    if (m_timer.get() > 5 & m_timer.get() < 8 
      & Auto.kautoVariable == 2) {
        m_robotDrive.arcadeDrive(.8, 0);
    }

    if (m_timer.get() > 8 & m_timer.get() < 10 
      & Auto.kautoVariable == 2) {
        m_robotDrive.arcadeDrive(0, .5);
    }

    if (m_timer.get() > 10 & m_timer.get() < 15 
      & Auto.kautoVariable == 2) {
        m_robotDrive.arcadeDrive(.7, .0);
    }
 
    if (!m_intakeIsMoving & (m_timer.get() > 11 & m_timer.get() < 20 & 
      !m_intakeDownLimitSwitch.get()) & Auto.kautoVariable == 2) {
        m_deployIntakeMotor.set(Intake.kDeployMotorUpSpeed);

    }

    if(m_timer.get() > 11 & m_timer.get() < 20 
      & Auto.kautoVariable == 2) {
        m_runIntakeMotor.set(Intake.kRunMotorSpeed);
    }
    
    /* ---------------------------------------------------------------------------------------------- */
    // auto # 3
    // climb
    /* ------------------------------------------------------------------------------------------ */

    if (m_timer.get() < 2.0 & Auto.kautoVariable == 3) {
      m_robotDrive.arcadeDrive(0, .5);
      m_robotDrive2.arcadeDrive(0,.5);
    }

    /*if (m_timer.get() >  2.0  & !m_climbIsMoving & !m_climbTopLimitSwitch.get() 
      & Auto.kautoVariable >= 3 & Auto.kautoVariable <= 3) {
        m_robotDrive.arcadeDrive(.8, 0);
        m_climbMotor.set(.6);
        m_climbIsMoving = true;
    }   */
   /* ---------------------------------------------------------------------------  */
   // AUTO # 4
   // LEFT
   /* ---------------------------------------------------------------------------  */

   if (Timer.getMatchTime() > 0 & Auto.kautoVariable == 4) {
    m_shootMotor.set(Shoot.kRunMotorSpeed);
   }

   if (Timer.getMatchTime() < 4 & Timer.getMatchTime() > 6) {
    m_robotDrive.arcadeDrive(.6, 0);
    m_robotDrive2.arcadeDrive(.6, 0);
   }

   /* ------------------------------------------------------- */
   // Auto #5
   // Move for 4 sec at 40% simple
   /* ------------------------------------------------------- */

   if (Timer.getMatchTime() > 18 & Timer.getMatchTime() < 20 & Auto.kautoVariable == 5) {
    m_robotDrive.arcadeDrive(0.4, 0);
    m_robotDrive2.arcadeDrive(0.4, 0);
   } 

   if (Timer.getMatchTime() > 0 & Timer.getMatchTime() < 18 & Auto.kautoVariable == 5) {
    m_robotDrive.arcadeDrive(0, 0.01);
    m_robotDrive.arcadeDrive(0, 0.01);
   }

   /* ------------------------------------------------------------------------------------ */
   // Auto #6
   // middle
   // drive & shoot
   /* ------------------------------------------------------------------------------------ */
   if (Auto.kautoVariable == 6 & Timer.getMatchTime() > 16) {
    m_shootMotor.set(1);
    // shoot motor
   }

   if (Auto.kautoVariable == 6 & Timer.getMatchTime() < 19.3 & Timer.getMatchTime() > 16) {
    // kick motor
    m_backKickMotor.set(Shoot.kKickMotorSpeed);
    m_shootMotor.set(Shoot.kRunMotorSpeed);
   }

   if (Auto.kautoVariable == 6 & Timer.getMatchTime() > 14.5 & Timer.getMatchTime() < 16) {
    m_robotDrive2.arcadeDrive(0.5,0);
    m_robotDrive.arcadeDrive(0.5, 0);
    m_shootMotor.set(0);
    m_backKickMotor.set(0);
   }

   if (Auto.kautoVariable == 6 & Timer.getMatchTime() < 16 & 
    Timer.getMatchTime() > 15.5 & m_intakeDownLimitSwitch.get()) {
      m_deployIntakeMotor.set(Intake.kDeployMotorUpSpeed);
   }

   if (Auto.kautoVariable == 6 & Timer.getMatchTime() < 15.5 & 
    Timer.getMatchTime() > 15 & m_intakeUpLimitSwitch.get()) {
      m_deployIntakeMotor.set(Intake.kDeployMotorDownSpeed);
    }

   if (Auto.kautoVariable == 6 & Timer.getMatchTime() < 14.5) {
    m_robotDrive.arcadeDrive(0, 0);
    m_robotDrive2.arcadeDrive(0, 0);
   }

  } 

}