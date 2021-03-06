package org.firstinspires.ftc.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.autonomous.BaseAutonomous;
import org.firstinspires.ftc.teamcode.common.Robot;

@Autonomous(name="IMU Test")
public class IMUTest extends BaseAutonomous
{
    @Override
    public void run() throws InterruptedException
    {
        Robot robot = Robot.instance();
        robot.imu.initialize(telemetry);
        robot.imu.start();

        while (opModeIsActive())
        {
            robot.imu.update();
            Thread.sleep(50);
        }
    }
}
