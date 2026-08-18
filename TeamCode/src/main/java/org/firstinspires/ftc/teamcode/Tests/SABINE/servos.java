package org.firstinspires.ftc.teamcode.Tests.SABINE;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp
public class servos extends OpMode {
    double incremento = 0.1;
    double position = 0.5;

    boolean lbutton = false;
    boolean rbutton = false;
    private Servo servo_pos;

    @Override
    public void init () {
        servo_pos = hardwareMap.get(Servo.class, "servo");

    }

    @Override
    public void loop(){

        if (gamepad1.right_bumper && !rbutton) {
            position += incremento;

        }


        if (gamepad1.left_bumper && !lbutton) {
            position -= incremento;


        }

        rbutton = gamepad1.right_bumper;
        lbutton = gamepad1.left_bumper;
        servo_pos.setPosition(position);
        telemetry.addData("Position", servo_pos.getPosition());
        telemetry.update();
    }
}
