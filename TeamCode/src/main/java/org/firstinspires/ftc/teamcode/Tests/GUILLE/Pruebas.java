package org.firstinspires.ftc.teamcode.Tests.GUILLE;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@TeleOp
public class Pruebas extends OpMode{
    DcMotor Motor;

    @Override
    public void init(){
        Motor = hardwareMap.get(DcMotor.class, "Motor");
    }

    @Override
    public void loop(){

    }
}
