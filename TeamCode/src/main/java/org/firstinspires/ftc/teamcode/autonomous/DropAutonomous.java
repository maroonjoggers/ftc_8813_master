package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.autonomous.util.MotorController;
import org.firstinspires.ftc.teamcode.util.Config;

@Autonomous(name="Drop Autonomous")
public class DropAutonomous extends BaseAutonomous
{
    @Override
    public void run() throws InterruptedException
    {
        DcMotor left = hardwareMap.dcMotor.get("left");
        DcMotor right = hardwareMap.dcMotor.get("right");
        DcMotor lifter = hardwareMap.dcMotor.get("lifter");

        lifter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lifter.setPower(-1);
        Thread.sleep(1000);
        lifter.setPower(0);
        Thread.sleep(1000);
        lifter.setPower(-1);
        Thread.sleep(700);
        lifter.setPower(0);
        // Get off the hook
        left.setPower(-1);
        right.setPower(1);
        Thread.sleep(800);

        // Forward
        left.setPower(-0.75);
        right.setPower(0.75);

        Thread.sleep(1000);

        //Backward
        left.setPower(0.5);
        right.setPower(-0.5);

        Thread.sleep(500);

        // Turn
        left.setPower(-0.6);
        right.setPower(-0.6);

        Thread.sleep(850);

        // Forward
        left.setPower(-0.75);
        right.setPower(0.75);

        Thread.sleep(3000);

        // STOP
        left.setPower(0);
        right.setPower(0);
    }
}
