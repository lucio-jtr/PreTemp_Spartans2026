package org.firstinspires.ftc.teamcode.Tests.GUILLE;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
@Disabled
@TeleOp(name="Mover Servomotor", group="Tests")
public class Servomotor extends OpMode{
    Servo Servomotor;

    @Override
    public void init(){
        Servomotor = hardwareMap.get(Servo.class, "Servo");
    }

    @Override
    public void loop(){
        if (gamepad1.a == true){
            Servomotor.setPosition(1.0);
        }
        if (gamepad1.x == true){
            Servomotor.setPosition(0.0);
        }
        if (gamepad1.y == true){
            Servomotor.setPosition(-1.0);
        }
    }
}