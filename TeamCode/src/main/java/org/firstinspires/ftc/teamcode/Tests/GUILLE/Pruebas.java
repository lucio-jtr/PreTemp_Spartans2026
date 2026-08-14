package org.firstinspires.ftc.teamcode.Tests;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

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
