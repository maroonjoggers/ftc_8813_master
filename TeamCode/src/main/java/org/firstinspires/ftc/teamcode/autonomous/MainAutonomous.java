package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.autonomous.tasks.TaskDetectGold;
import org.firstinspires.ftc.teamcode.autonomous.tasks.TaskDrop;
import org.firstinspires.ftc.teamcode.autonomous.tasks.TaskIntakeMineral;
import org.firstinspires.ftc.teamcode.autonomous.tasks.TaskSample;
import org.firstinspires.ftc.teamcode.autonomous.util.opencv.CameraStream;
import org.firstinspires.ftc.teamcode.autonomous.util.opencv.WebcamStream;
import org.firstinspires.ftc.teamcode.common.Robot;
import org.firstinspires.ftc.teamcode.common.util.Config;
import org.firstinspires.ftc.teamcode.common.util.Logger;
import org.firstinspires.ftc.teamcode.common.util.Profiler;
import org.firstinspires.ftc.teamcode.common.util.Utils;
import org.firstinspires.ftc.teamcode.common.util.Vlogger;
import org.firstinspires.ftc.teamcode.common.sensors.vision.ShapeGoldDetector;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;

@Autonomous(name="Crater Autonomous")
public class MainAutonomous extends BaseAutonomous implements CameraStream.OutputModifier
{

    private Vlogger video;
    private Logger log;
    private volatile String state;
    private volatile long start;

    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;
    private static final String[] sides = {"Left", "Center", "Right"};

    private int side;

    private Profiler profiler = new Profiler();

    public static final boolean DROP = true;

    @Override
    public void initialize() throws InterruptedException
    {
        Robot robot = Robot.instance();
        if (DROP) robot.hook.setPosition(Robot.HOOK_CLOSED);
        else robot.hook.setPosition(Robot.HOOK_OPEN);
        robot.imu.initialize(telemetry);
        robot.imu.start();
        CameraStream stream = getCameraStream();
        video = new Vlogger(getVlogName(),
                (int)stream.getSize().width, (int)stream.getSize().height, 10.0);
        log = new Logger("Crater Autonomous");
        robot.initPivotAuto();
        robot.mark.setPosition(0.91);
    }

    private String getVlogName()
    {
        int i;
        for (i = 0; new File(Config.storageDir + "autonomous_capture" + i + ".avi").exists(); i++);
        return "autonomous_capture" + i + ".avi";
    }

    @Override
    public void run() throws InterruptedException
    {
        profiler.start("run");
        start = System.currentTimeMillis();
        state = "Initializing";

        Robot robot = Robot.instance();
        robot.leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        robot.rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Initialize camera
        profiler.start("init camera");
        CameraStream stream = getCameraStream();
        ShapeGoldDetector detector = new ShapeGoldDetector();
        profiler.end();

        profiler.start("drop");
        if (DROP) new TaskDrop().runTask();
        else Thread.sleep(4000);
        robot.imu.resetHeading();
        profiler.end();

        DcMotor left = robot.leftFront;
        DcMotor right = robot.rightFront;
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Start detecting after the camera has warmed up
        stream.addModifier(detector);
        stream.addListener(detector);
        stream.addModifier(this);

        robot.imu.resetHeading();

        state = "Detecting Mineral";
        profiler.start("detect");
        new TaskDetectGold(detector, profiler).runTask();
        profiler.end();

        state = "Sampling Mineral";
        profiler.start("sample");
        new TaskSample(detector).runTask();
        profiler.end();
        telemetry.clearAll();
        telemetry.update();

        robot.imu.update();
        if (robot.imu.getHeading() >= 25) side = LEFT;
        else if (robot.imu.getHeading() <= -25) side = RIGHT;
        else side = CENTER;
        telemetry.addData("Side", sides[side+1]).setRetained(true);

        state = "Intake mineral";
        profiler.start("intake");
        new TaskIntakeMineral(profiler).runTask();
        profiler.end();

        state = "Drive to depot";
        profiler.start("depot");
        profiler.start("back up");
        robot.reverse(10, 0.3);
        profiler.end();
        profiler.start("turn");
        if (side == RIGHT) turnTo(70);
        else turnTo(70);
        profiler.end();

        profiler.start("drive");
        if (side == LEFT) robot.leftFront.setPower(0.45);
        else if (side == RIGHT) robot.leftFront.setPower(0.55);
        else robot.leftFront.setPower(0.5);
        robot.rightFront.setPower(0.7);

        if (side == LEFT) Thread.sleep(1500);
        else if (side == RIGHT) Thread.sleep(1500);
        else Thread.sleep(2000);

        if (side == LEFT) robot.forward(45, 0.4);
        else robot.forward(45, 0.4);
        Thread.sleep(500); // Allow it to coast
        profiler.end();

        profiler.start("drop");
        robot.mark.setPosition(0);
        Thread.sleep(500);
        profiler.end();
        profiler.end(); // depot

        state = "Park in crater";
        profiler.start("park");
        robot.reverse(150, 0.6);
        profiler.end();
        profiler.end(); // run

    }

    private void turnTo(int offset) throws InterruptedException
    {
        log.d("Turning to %d degrees", offset);
        Robot robot = Robot.instance();
        DcMotor left = robot.leftFront;
        DcMotor right = robot.rightFront;
        double speed = 0.18;
        double kP = 0.15;
        int deadband = 7;
        for (int i = 0; (Math.abs(robot.imu.getHeading() - offset) > deadband || i < 20) && opModeIsActive(); )
        {
            double error = (robot.imu.getHeading() - offset);
            if (Math.abs(error) >= deadband)
            {
                left.setPower(speed * Math.min(1, error * kP));
                right.setPower(-speed * Math.min(1, error * kP));
                i = 0;
            }
            else
            {
                left.setPower(0);
                right.setPower(0);
                i++;
            }
            Thread.sleep(5);
            robot.imu.update();
            log.v("Heading: %.4f", robot.imu.getHeading());
        }
        left.setPower(0);
        right.setPower(0);
    }

    @Override
    public synchronized void finish()
    {
        video.close();
        log.d("Crater autonomous finished -- mineral=%s, drop=%s", sides[side+1], Boolean.toString(DROP));
        profiler.finish();
    }

    @Override
    public synchronized Mat draw(Mat bgr)
    {
        Mat frame = new Mat();
        if (getCameraStream() instanceof WebcamStream)
            bgr.copyTo(frame);
        else
            Core.rotate(bgr, frame, Core.ROTATE_90_COUNTERCLOCKWISE);
        int y = text(frame, state, 0, 10);
        text(frame, Utils.elapsedTime(System.currentTimeMillis() - start), 0, y);
        video.put(frame);
        frame.release();
        return bgr;
    }

    private int text(Mat m, String text, int x, int y)
    {
        int[] base = new int[1];
        Size textSize = Imgproc.getTextSize(text, Imgproc.FONT_HERSHEY_PLAIN, 1, 1, base);
        Rect r = new Rect(x, y, (int)textSize.width, (int)textSize.height);
        Imgproc.rectangle(m, r, new Scalar(0, 0, 0), -1);
        Imgproc.putText(m, text, new Point(x, y + base[0]), Imgproc.FONT_HERSHEY_PLAIN, 1, new Scalar(255, 255, 255));
        return base[0] + y + 12;
    }

}
