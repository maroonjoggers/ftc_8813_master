package org.firstinspires.ftc.teamcode.autonomous.test.opencv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.autonomous.BaseAutonomous;
import org.firstinspires.ftc.teamcode.autonomous.util.opencv.CameraStream;
import org.opencv.core.Mat;

@Autonomous(name="OpenCV Native Test")
public class OpenCVNative extends BaseAutonomous implements CameraStream.OutputModifier
{

    private native void test(long mat_addr);

    static
    {
        System.loadLibrary("native-lib");
    }

    @Override
    public Mat draw(Mat bgr)
    {
        test(bgr.nativeObj);

        return bgr;
    }

    @Override
    public void run() throws InterruptedException
    {
        CameraStream stream = getCameraStream();
        stream.addModifier(this);
    }
}
