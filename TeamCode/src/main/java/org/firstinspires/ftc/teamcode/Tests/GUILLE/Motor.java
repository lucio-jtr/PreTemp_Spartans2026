package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Mover Motor", group="Tests")
public class Motor extends OpMode {
    DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
    }

    @Override
    public void loop() {
        double velocity = 0;
        while (velocity <= 1){
            if (gamepad1.right_bumper == true){
                motor.setPower(0);
                velocity += 0.1;
                telemetry.addData("Velocidad actual", velocity);
                telemetry.update();
            }
        }
        while (velocity < 0) {
            if (gamepad1.left_bumper == true) {
                motor.setPower(0);
                velocity -= 0.1;
                telemetry.addData("Velocidad actual", velocity);
                telemetry.update();
            }
        }
        if (gamepad1.a == true){
            motor.setPower(0.0);
        }
    }
}
