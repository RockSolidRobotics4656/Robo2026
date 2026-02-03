package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;

public class Config {
    public class Drivetrain {
        public static final int kDriveLeftLeadCANID = 2;
        public static final int kDriveLeftFollowCANID = 3;
        public static final int kDriveRightLeadCANID = 1;
        public static final int kCriveRightFollowCANID = 4;
        public static final MotorType kDriveMotorType = MotorType.kBrushless;
    }
}
