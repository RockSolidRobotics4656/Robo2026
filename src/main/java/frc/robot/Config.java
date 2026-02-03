package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DigitalInput;

public class Config {
    public class Drivetrain {
        public static final int kDriveLeftLeadCANID = 2;
        public static final int kDriveLeftFollowCANID = 3;
        public static final int kDriveRightLeadCANID = 1;
        public static final int kDriveRightFollowCANID = 4;
        public static final MotorType kDriveMotorType = MotorType.kBrushless;
    }

    public class Climb{ 
        public static final int kTopLimitSwitchDIOPort = 1;
        public static final int kBottomLimitSwitchDIOPort = 2;
        public static final int kMotorCANID = 5;
    }

    public class Shoot{
        public static final int kMotorCANID = 6;
    }

    public class Intake{
        public static final int kUpLimitSwitchDIOPort = 3;
        public static final int kDownLimitSwitchDIOPort = 4;
        public static final int kRunMotorCANID = 7;
        public static final int kDeployMotorCANID = 8;
    }
    
    

}
