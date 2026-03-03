// This program stores contants in classes within the Config class. 
// Call these in Robot.java by inputting the class name (not 
// counting the Config class) and then the constant name in the
// format ClassName.ConstantName in place of the variable.
// This works because the Config file is imported into Robot.java.

// When altering the constants, keep in mind that changing them here 
// changes them for every instance they are called in the program.

package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;

public class Config {

    // public static final double kStoppedMotor = 0;

    public class Drivetrain {
        public static final int kDriveLeftLeadCANID = 1;
        public static final int kDriveLeftFollowCANID = 4;
        public static final int kDriveRightLeadCANID = 2;
        public static final int kDriveRightFollowCANID = 3;
        public static final MotorType kDriveMotorType = MotorType.kBrushless;
        public static final double kSpeedFactor = .75; // .7 for Tryg, 1 for everyone else
    }

    public class Climb{ 
        public static final int kTopLimitSwitchDIOPort = 6;
        public static final int kBottomLimitSwitchDIOPort = 8;
        public static final int kMotorCANID = 10;
        public static final double kMotorSpeed = .2;
    }

    public class Shoot{
        public static final int kRunMotorCANID = 8;
        public static final double kRunMotorSpeed = .60;
        public static final double kBackMotorSpeed = .7;
        public static final double k2BackMotorSpeed = .8;
        public static final int kBackKickMotorCANID = 7;
        public static final double kKickMotorSpeed = 1;
        public double m_savedTime = 0.0; 
        public static final double kStoppedMotor = 0;
        public static final double kKickDelay = 0.7;
        public static final double kInitSpeed = 1;
    }

    public class Intake{
        public static final int kUpLimitSwitchDIOPort = 8;
        public static final int kDownLimitSwitchDIOPort = 9;
        public static final int kRunMotorCANID = 6;
        public static final int kDeployMotorCANID = 5;
        public static final double kDeployMotorUpSpeed = 0.25;
        public static final double kDeployMotorDownSpeed = 0.55;
        public static final double kRunMotorSpeed = .5;
    }

    public class Auto {
        public static final int kautoVariable = 6;
    }
    
}