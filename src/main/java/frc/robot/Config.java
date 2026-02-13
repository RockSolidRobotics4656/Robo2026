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
    public class Drivetrain {
        public static final int kDriveLeftLeadCANID = 2;
        public static final int kDriveLeftFollowCANID = 3;
        public static final int kDriveRightLeadCANID = 1;
        public static final int kDriveRightFollowCANID = 4;
        public static final MotorType kDriveMotorType = MotorType.kBrushless;
    }

    public class Climb{ 
        public static final int kTopLimitSwitchDIOPort = 3;
        public static final int kBottomLimitSwitchDIOPort = 9;
        public static final int kMotorCANID = 5;
        public static final double kMotorSpeed = .2;
    }

    public class Shoot{
        public static final int kRunMotorCANID = 14;
        public static final double kRunMotorSpeed = 1;
        public static final int kKickMotorCANID = 12;
        public static final double kKickMotorSpeed = 1;
        public static double m_savedTime = 0.0; 

    }

    public class Intake{
        public static final int kUpLimitSwitchDIOPort = 0;
        public static final int kDownLimitSwitchDIOPort = 1;
        public static final int kRunMotorCANID = 6;
        public static final int kDeployMotorCANID = 8;
        public static final double kDeployMotorSpeed = .2;
        public static final double kRunMotorSpeed = 1;
    }

    public class Auto {
        public static final int kautoVariable = 3;
    }
    
}