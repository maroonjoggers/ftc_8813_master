package org.firstinspires.ftc.teamcode.autonomous.util.opencv;

import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

public class UVCCameraStream extends CameraStream
{

    private USBMonitor monitor;

    public UVCCameraStream()
    {
        monitor = new USBMonitor(AppUtil.getDefContext(), null);
    }

    @Override
    public void addListener(CameraListener l)
    {
        super.addListener(l);
    }

    @Override
    public void removeListener(CameraListener l)
    {
        super.removeListener(l);
    }

    @Override
    public void addModifier(OutputModifier m)
    {
        super.addModifier(m);
    }

    @Override
    public void removeModifier(OutputModifier m)
    {
        super.removeModifier(m);
    }

    @Override
    public void stop()
    {
        super.stop();
    }

}
