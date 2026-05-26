package org.firstinspires.ftc.teamcode.Tests;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Mover extends OpMode {

    private DcMotor motor;

    @Override
    public void init (){
        motor = hardwareMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


    }
    @Override
    public  void  loop(){
        if (gamepad1.a){
            motor.setPower(0.0);
        }
        if (gamepad1.b){
            motor.setPower(1.0);
        }

    }

}
