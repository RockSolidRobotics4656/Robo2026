// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
// this links to the Config file, where constants are stored.
import frc.robot.Config.*;

// WPI imports
import edu.wpi.first.hal.DIOJNI;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.cameraserver.CameraServer;
//import edu.wpi.first.cscore.CvSink;
//import edu.wpi.first.cscore.CvSource;
//import edu.wpi.first.cscore.UsbCamera;

// REVLIB specific imports. If errored, download REVLIB.
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.imgproc.Imgproc;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkLowLevel;

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
    private final SparkMax m_leftLeadSparkMax = 
      new SparkMax(Drivetrain.kDriveLeftLeadCANID, MotorType.kBrushless);
    private final SparkMax m_rightLeadSparkMax = 
      new SparkMax(Drivetrain.kDriveRightLeadCANID, MotorType.kBrushless);
    private final SparkMax m_leftFollowSparkMax = 
      new SparkMax(Drivetrain.kDriveLeftFollowCANID, MotorType.kBrushless);
    private final SparkMax m_rightFollowSparkMax = 
      new SparkMax(Drivetrain.kDriveRightFollowCANID, MotorType.kBrushless);

    // motor config declaration (not configured here) 
    private final SparkMaxConfig rightConfig = 
      new SparkMaxConfig();
    
    // motor group (followers grouped later)
    private final DifferentialDrive m_robotDrive = 
      new DifferentialDrive(m_leftLeadSparkMax::set, m_rightLeadSparkMax::set);

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
    SparkMax m_kickMotor = new SparkMax
      (Shoot.kBackKickMotorCANID, MotorType.kBrushed);
    
    //timer
    final Timer m_timeSave = new Timer();

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
    m_rightLeadSparkMax.configure(rightConfig, 
      ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // set up for followers in drivetrain
    SparkMaxConfig leftFollowConfig = new SparkMaxConfig();
    leftFollowConfig.follow(Drivetrain.kDriveLeftLeadCANID);
    //leftFollowConfig.inverted(true);
    m_leftFollowSparkMax.configure(leftFollowConfig, 
      ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig rightFollowConfig = new SparkMaxConfig();
    rightFollowConfig.inverted(true);
    rightFollowConfig.follow(Drivetrain.kDriveRightLeadCANID);
    m_rightFollowSparkMax.configure(rightFollowConfig, 
      ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //climb config
    //climbUpConfig.inverted(false);
    //climbDownConfig.inverted(true);

    //intake config
    downIntakeConfig.inverted(false);
    upIntakeConfig.inverted(true);
    runIntakeInConfig.inverted(false);
    runIntakeOutConfig.inverted(true);

    // camera config 
    //CameraServer.startAutomaticCapture();

    // We'll see camera code
    /*m_visionThread =new Thread(() -> {
      // Get the UsbCamera from CameraServer
      UsbCamera camera = CameraServer.startAutomaticCapture();

      // Set the resolution
      camera.setResolution(1000, 600);

      // Get a CvSink. This will capture Mats from the camera
      CvSink cvSink = CameraServer.getVideo();
              
      // Setup a CvSource. This will send images back to the Dashboard
      CvSource outputStream = CameraServer.putVideo("Rectangle", 1000, 600);

      // Mats are very memory expensive. Lets reuse this Mat.
      Mat mat = new Mat();

      // This cannot be 'true'. The program will never exit if it is. This
      // lets the robot stop this thread when restarting robot code or
      // deploying.
      while (!Thread.interrupted()) {

      // Tell the CvSink to grab a frame from the camera and put it
      // in the source mat.  If there is an error notify the output.
        if (cvSink.grabFrame(mat) == 0) {

        // Send the output the error.
          outputStream.notifyError(cvSink.getError());

          // skip the rest of the current iteration
          continue;
        }

        // Put a rectangle on the image
        Imgproc.rectangle(
          mat, new Point(100, 100), new Point(400, 400), new Scalar(255, 255, 255), 5);
        // Give the output stream a new image to display
        outputStream.putFrame(mat);
      }
    });
    m_visionThread.setDaemon(true);
    m_visionThread.start();*/
    // end of Robot class
  }


  @Override
  public void teleopInit() {
    //System.out.println("up");
    //System.out.println(m_intakeUpLimitSwitch.get());
    //System.out.println("down");
    //System.out.println(m_intakeDownLimitSwitch.get());
    // this runs once at the start of teleop
    
    //System.out.println("right lead canid");
    //System.out.println(m_rightLeadSparkMax.getDeviceId());
    
  }

  /* ---------------------------------------------- */
  @Override
  public void teleopPeriodic() {
    // runs every 20ms during teleop. 
    // returns error but continues rnning if it can't finish the loop in time.

    // teleoperated drive. involves buttons and joysticks.

    // drive command, using the left stick only.
    m_robotDrive.arcadeDrive(m_controller.getLeftY(), m_controller.getLeftX());
    double kSpeed = m_controller.getRightY();
    m_shootMotor.set(kSpeed);
    double m_shootTime = 0.0;
    /* ---------------------------------------------------------- */
    // button bindings
    // A and B are to deploy/bring back in the intake
    // X is shoot
    // bumpers run intake in and out
    // Y is climb
    /* ---------------------------------------------------------- */

    if (m_controller.getAButtonPressed() &
      !m_intakeDownLimitSwitch.get()) {
        m_deployIntakeMotor.configure
          (downIntakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_deployIntakeMotor.set(Intake.kDeployMotorSpeed);
        m_intakeIsMoving = true;
          System.out.println("deploy intake");
      // deploy intake
    } 

    if (m_intakeIsMoving & (m_controller.getAButtonReleased() | 
      m_intakeDownLimitSwitch.get())) {
        m_deployIntakeMotor.set(.0);
        m_intakeIsMoving = false;
        System.out.println("deploy intake stop");
    }
     
    if (m_controller.getBButtonPressed() &  
    !m_intakeUpLimitSwitch.get()) {
      m_intakeIsMoving = true;
      m_deployIntakeMotor.configure(upIntakeConfig, 
        ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
          m_deployIntakeMotor.set(Intake.kDeployMotorSpeed);
          System.out.println("retract intake");
      // bring in intake
    }

    if (m_intakeIsMoving & (m_controller.getBButtonReleased() | 
    m_intakeUpLimitSwitch.get())) {
      m_intakeIsMoving = false;
      m_deployIntakeMotor.set(0);
          System.out.println("retract intake stop");
    }

    if (m_controller.getLeftBumperButtonPressed()) {
      System.out.println("run intake");
      m_deployIntakeMotor.configure(runIntakeInConfig, 
        ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
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

    if (m_controller.getXButtonPressed()) {
      System.out.println("shoot");
      m_shootMotor.set(Shoot.kRunMotorSpeed);
      m_shootTime = m_timer.get();
      // shoot
    } 

    if ((m_controller.getLeftTriggerAxis() > 0) & 
      (m_timer.get() - m_shootTime > .25)) {
        m_kickMotor.set(Shoot.kKickMotorSpeed);
        System.out.println(m_shootTime);
    }

    if (m_controller.getXButtonReleased()) {
      System.out.println("stop shoot");
      m_shootMotor.set(0);
      m_kickMotor.set(0);
      // stop shoot
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

  @Override
  public void autonomousInit() {
    m_timer.restart();
    // CommandScheduler.getInstance();
  }

  @SuppressWarnings("unused")
  @Override
  public void autonomousPeriodic() {
    // most basic auto program
     /* ----------------------------------- */
     // AUTO #1
     /* ----------------------------------- */
     // move & shoot & move
     // do not uncomment this first section  
      /*if (m_controller.getXButtonPressed()) {
        System.out.println("shoot");
        m_shootMotor.set(Shoot.kRunMotorSpeed);
        m_shootTime = m_timer.get();
      // shoot
    } 

    if ((m_controller.getLeftTriggerAxis() > 0) & (m_timer.get() - m_shootTime > .25)) {
      m_kickMotor.set(Shoot.kKickMotorSpeed);
      System.out.println(m_shootTime);
    }

    if (m_controller.getXButtonReleased()) {
      System.out.println("stop shoot");
      m_shootMotor.set(0);
      m_kickMotor.set(0);
      // stop shoot
    }*/
     /* --------------------------------- */
     
    /*if (m_timer.get() < 3.0 & Auto.kautoVariable >= 1 & Auto.kautoVariable <= 1) {
      // arcadeDrive(speed, rotation) - rotation = 0 for driving straight
      m_robotDrive.arcadeDrive(0.1, 0, false);
      m_shootMotor.set(.3);
      System.out.println("auto drive forward");
    } */
    if (m_timer.get() > 3.0 & m_timer.get() < 5.0 & 
      Auto.kautoVariable >= 1 & Auto.kautoVariable <= 1) {
        m_shootMotor.set(Shoot.kRunMotorSpeed);
        System.out.println("auto shoot");
    }
    if (m_timer.get() > 5 & m_timer.get() < 8 & 
      Auto.kautoVariable >= 1 & Auto.kautoVariable <= 1) {
    m_robotDrive.arcadeDrive(0.1, 0, false);
    }
    if (m_timer.get() > 8 & m_timer.get() < 20 & 
      Auto.kautoVariable >= 1 & Auto.kautoVariable <= 1){
        m_robotDrive.arcadeDrive(0, 0.6);
    }
      
    /*--------------------------------------------------------- */
    //auto #2
    //RETRIEVAL
    /*-------------------------------------------------------- */
    if (m_timer.get() < 2.0 & Auto.kautoVariable 
      >= 2 & Auto.kautoVariable <= 2) {
        m_robotDrive.arcadeDrive(0, .5);
    }

    if (m_timer.get() < 5.0 & m_timer.get() > 2.0 
      & Auto.kautoVariable >= 2 & Auto.kautoVariable <= 2) {
        m_robotDrive.arcadeDrive(.8, 0);
        m_shootMotor.set(Shoot.kRunMotorSpeed);
    }

    if (m_timer.get() > 5 & m_timer.get() < 8 
      & Auto.kautoVariable >= 2 & Auto.kautoVariable <= 2) {
        m_robotDrive.arcadeDrive(.8, 0);
    }

    if (m_timer.get() > 8 & m_timer.get() < 10 
      & Auto.kautoVariable >= 2 & Auto.kautoVariable <= 2) {
        m_robotDrive.arcadeDrive(0, .5);
    }

    if (m_timer.get() > 10 & m_timer.get() < 15 
      & Auto.kautoVariable >= 2 & Auto.kautoVariable <= 2) {
        m_robotDrive.arcadeDrive(.7, .0);
    }

    if (!m_intakeIsMoving & (m_timer.get() > 11 & m_timer.get() < 20 & 
      !m_intakeDownLimitSwitch.get()) & Auto.kautoVariable >= 2 & Auto.kautoVariable <= 2) {
        m_deployIntakeMotor.set(Intake.kDeployMotorSpeed);

    }

    if(m_timer.get() > 11 & m_timer.get() < 20 
      & Auto.kautoVariable >= 2 & Auto.kautoVariable <= 2) {
        m_runIntakeMotor.set(Intake.kRunMotorSpeed);
    }
    /* ----------------------------------------------------------------------------------------------- */
    // end of auto #2
    /* ----------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------- */
    // auto # 3
    // climb
    /* ------------------------------------------------------------------------------------------ */

    if (m_timer.get() < 2.0 & Auto.kautoVariable >= 3 & Auto.kautoVariable <= 3) {
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
   //
   /* ---------------------------------------------------------------------------  */
  } 

}