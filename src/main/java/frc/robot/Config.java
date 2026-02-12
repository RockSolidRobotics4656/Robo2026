package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
// import com.revrobotics.spark.SparkMax;
// import com.revrobotics.spark.config.SparkMaxConfig;

// import edu.wpi.first.wpilibj.DigitalInput;

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
        public static final int kMotorCANID = 14;
        public static final double kMotorSpeed = 1;
    }

    public class Intake{
        public static final int kUpLimitSwitchDIOPort = 0;
        public static final int kDownLimitSwitchDIOPort = 1;
        public static final int kRunMotorCANID = 6;
        public static final int kDeployMotorCANID = 8;
        public static final double kDeployMotorSpeed = .2;
        public static final double kRunMotorSpeed = 1;
    }
    
}