// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
// this links to the Config file, where constants are stored.
import frc.robot.Config.*;

// WPI imports
// import edu.wpi.first.hal.DIOJNI;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.cameraserver.CameraServer;

// REVLIB specific imports. If errored, download REVLIB.
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

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

  // climb objects
    // limit switches 
    //DigitalInput m_climbTopLimitSwitch = 
      //new DigitalInput(Climb.kTopLimitSwitchDIOPort);
    //DigitalInput m_climbBottomLimitSwitch = 
      //new DigitalInput(Climb.kBottomLimitSwitchDIOPort);
/* 
    // motor
    SparkMax m_climbMotor = 
      new SparkMax(Climb.kMotorCANID, MotorType.kBrushless);

    // variable
    Boolean m_climbIsMoving = false;

    // motor config declarations (not declared here)
    SparkMaxConfig climbUpConfig = new SparkMaxConfig();
    SparkMaxConfig climbDownConfig = new SparkMaxConfig();*/

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
      ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
    m_leftLeadSparkMax.configure(rightConfig, 
      ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // set up for followers in drivetrain
    //SparkMaxConfig leftFollowConfig = new SparkMaxConfig();
    //leftFollowConfig.follow(Drivetrain.kDriveLeftLeadCANID);
    //leftFollowConfig.inverted(true);
    //m_rightFollowSparkMax.configure(leftFollowConfig, 
      //ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //SparkMaxConfig rightFollowConfig = new SparkMaxConfig();
    //rightFollowConfig.inverted(false);
    //rightFollowConfig.follow(Drivetrain.kDriveRightLeadCANID);
    //m_leftFollowSparkMax.configure(rightFollowConfig, 
    //  ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //climb config
    //climbUpConfig.inverted(false);
    //climbDownConfig.inverted(true);

    //intake config
    downIntakeConfig.inverted(false);
    upIntakeConfig.inverted(true);
    runIntakeInConfig.inverted(false);
    runIntakeOutConfig.inverted(true);

    // camera config 
    CameraServer.startAutomaticCapture();

    // end of Robot class
  }


  @Override
  public void teleopInit() {
    System.out.println("up");
    System.out.println(m_intakeUpLimitSwitch.get());
    System.out.println("down");
    System.out.println(m_intakeDownLimitSwitch.get());
    // this runs once at the start of teleop
    
    //System.out.println("right lead canid");
    //System.out.println(m_leftLeadSparkMax.getDeviceId());
    // kill auto
    m_deployIntakeMotor.set(0);
    // m_climbMotor.set(0);
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

    // teleoperated drive. involves buttons and joysticks.

    // drive command, using the left stick only.
    m_robotDrive.arcadeDrive(m_controller.getLeftY(), -m_controller.getLeftX());
    m_robotDrive2.arcadeDrive(m_controller.getLeftY(), -m_controller.getLeftX());
    /* ---------------------------------------------------------- */
    // button bindings
    // A - inward and B - outward intake tilt
    // X is shoot
    // bumpers run intake in and out
    // Y is climb
    // right stick button is climb down
    /* ---------------------------------------------------------- */

    // if (m_controller.getAButton()) {
    //   System.out.println("abutton" + m_intakeDownLimitSwitch.get());
    // }

    if (!m_controller.getBButton() &
      m_intakeDownLimitSwitch.get() & (m_controller.getLeftTriggerAxis() > .5)) {
        m_deployIntakeMotor.configure(downIntakeConfig, 
          ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_deployIntakeMotor.set(Intake.kDeployMotorDownSpeed);
        System.out.println("deploy intake");
      // deploy intake
    } else if (/*m_controller.getAButtonReleased() |*/ !m_intakeDownLimitSwitch.get()) {
      m_deployIntakeMotor.set(0);
    } 

    /*if (m_intakeIsMoving & !m_intakeDownLimitSwitch.get()) {
      // double count = (m_intakeStartTime - Timer.getMatchTime());
      double intakeMotorSpeed = Intake.kDeployMotorMaxSpeed - (Intake.kDeployMotorMinSpeed * count * Intake.kIntakeSpeedMultiplier);
      m_deployIntakeMotor.set(intakeMotorSpeed);

      System.out.println("intakeaaaaaaaaaaaaaaaaaaeeeeeeeeeeeeeeeeee");
      System.out.println(Timer.getMatchTime());
      System.out.println(m_intakeStartTime);
      System.out.println(count);
      System.out.println(intakeMotorSpeed);
      System.out.println(m_deployIntakeMotor.get());

      if (intakeMotorSpeed <= Intake.kDeployMotorMinSpeed) {
        m_deployIntakeMotor.set(0);
        m_intakeIsMoving = false;
        System.out.println("deploy intake stop");
      }

      count += 1;
    }*/
     
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

    /*if (m_intakeIsMoving & !m_intakeUpLimitSwitch.get()) {
      // double count = (m_intakeStartTime - Timer.getMatchTime());
      double intakeMotorSpeed = Intake.kDeployMotorMaxSpeed - (Intake.kDeployMotorMinSpeed * count * Intake.kIntakeSpeedMultiplier);
      m_deployIntakeMotor.set(intakeMotorSpeed);

      System.out.println("outakeaaaaaaaaaaaaaaaaaaeeeeeeeeeeeeeeeeee2");
      System.out.println(Timer.getMatchTime());
      System.out.println(m_intakeStartTime);
      System.out.println(count);
      System.out.println(intakeMotorSpeed);
      System.out.println(m_deployIntakeMotor.get());

      if (intakeMotorSpeed <= Intake.kDeployMotorMinSpeed) {
        m_deployIntakeMotor.set(0);
        m_intakeIsMoving = false;
        System.out.println("deploy intake stop");
      }

      count += 1;
    }*/
    
    // if (m_intakeIsMoving & m_intakeUpLimitSwitch.get()) {
    //   m_deployIntakeMotor.set(0);
    //   m_intakeIsMoving = false;
    // }

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
        ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
          m_runIntakeMotor.set(Intake.kRunMotorSpeed);
      // run intake
    }

    if (m_controller.getRightBumperButtonReleased()) {
      System.out.println("stop outtake");
      m_runIntakeMotor.set(0);
      // stop outtake
    }

    // if (Timer.getMatchTime()
    /*if (m_controller.getXButtonPressed() & !shootIsMoving) {
      shootIsMoving = true;
      shootStartTime = Timer.getMatchTime();
      m_backKickMotor.set(-Shoot.kKickMotorSpeed);
    }*/

    if (m_controller.getXButton()) {
      m_shootMotor.set(Shoot.kRunMotorSpeed);
      m_backKickMotor.set(Shoot.kKickMotorSpeed);
      /*if (Timer.getMatchTime() - shootStartTime > Shoot.kKickDelay) {
        m_backKickMotor.set(Shoot.kKickMotorSpeed);
      }*/
      shootIsMoving = true;
      System.out.println("shoot moving");
    } 
    else if (m_controller.getXButtonReleased() & shootIsMoving) {
        System.out.println("Shoot Stopper");
        m_shootMotor.set(Shoot.kStoppedMotor);
        m_backKickMotor.set(Shoot.kStoppedMotor);
        shootIsMoving = false;
    }


/* 
    if (m_controller.getYButtonPressed() & !m_climbTopLimitSwitch.get()) {
      System.out.println("climb");
      m_climbMotor.configure(climbUpConfig,
        ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
          m_climbMotor.set(Climb.kMotorSpeed);
      // climb
    }

    if (m_controller.getYButtonReleased() | m_climbTopLimitSwitch.get()) {
      m_climbMotor.set(0);
      // stop climb
    }

    if ((m_controller.getRightStickButtonPressed() & !m_climbBottomLimitSwitch.get())) {
      System.out.println("un-climb");
      m_climbIsMoving = true;
      m_climbMotor.configure
        (climbDownConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      m_climbMotor.set(Climb.kMotorSpeed);
      // climb
    }

    if (m_climbIsMoving & (m_controller.getRightStickButtonReleased() 
      | m_climbBottomLimitSwitch.get())) {
        m_climbMotor.set(0);
        m_climbIsMoving = false;
        System.out.println("un-climb stop");
        // stop climb
    }*/

  }

  /* ------------------------------------------------------- */
  // AUTO START
  /* ------------------------------------------------------- */

  @Override
  public void autonomousInit() {
    m_timer.restart();
    // CommandScheduler.getInstance();
  }

  @SuppressWarnings("unused")
  @Override 
  public void autonomousPeriodic() {
    // most basic auto program
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
   // Move for 4 sec at 60% simple
   /* ------------------------------------------------------- */

   if (Timer.getMatchTime() > 0 & Timer.getMatchTime() < 4 & Auto.kautoVariable == 5) {
    m_robotDrive.arcadeDrive(0.6, 0);
    m_robotDrive2.arcadeDrive(0.6, 0);
   }

   /* ------------------------------------------------------------------------------------ */
   // Auto #6
   // Right position
   //
   /* ------------------------------------------------------------------------------------ */
   if (Auto.kautoVariable == 6 & Timer.getMatchTime() < 4) {
    m_robotDrive2.arcadeDrive(.7,1);
    m_robotDrive.arcadeDrive(1, 0);
   }

   if (Auto.kautoVariable == 6 & Timer.getMatchTime() > 4 & Timer.getMatchTime() < 6) {
    m_shootMotor.set(Shoot.kRunMotorSpeed);
   }

  } 

}