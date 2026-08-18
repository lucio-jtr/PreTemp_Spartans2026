package org.firstinspires.ftc.teamcode.Tests.SABINE;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;
@Disabled
@TeleOp
public class magnetic_Limit_Switch extends OpMode {

    private DigitalChannel limitMagnet;



    @Override
    public void init(){
        limitMagnet = hardwareMap.get(DigitalChannel.class, "limit_swtich");
    }

    @Override
    public void loop(){
        boolean state = !limitMagnet.getState();

        telemetry.addData("Magnetic limit switch state", state);


    }

}
