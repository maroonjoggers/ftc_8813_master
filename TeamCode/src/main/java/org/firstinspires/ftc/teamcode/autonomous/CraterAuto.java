package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.autonomous.util.MotorController;
import org.firstinspires.ftc.teamcode.util.Config;

@Autonomous(name="Crater Autonomous")
public class CraterAuto extends BaseAutonomous
{
    @Override
    public void run() throws InterruptedException
    {
        DcMotor left = hardwareMap.dcMotor.get("left");
        DcMotor right = hardwareMap.dcMotor.get("right");
        DcMotor lifter = hardwareMap.dcMotor.get("lifter");
        /*
        lifter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lifter.setPower(-1);
        Thread.sleep(50);
        lifter.setPower(0);
        */

        // Forward
        left.setPower(-0.9);
        right.setPower(0.9);

        Thread.sleep(2000);

        // STOP
        left.setPower(0);
        right.setPower(0);
    }
}
