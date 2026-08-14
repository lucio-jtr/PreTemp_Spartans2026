package org.firstinspires.ftc.teamcode.Tests.SABINE;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp
public class banda extends OpMode {

    //Motor
    private DcMotor motor;
    boolean aAlreadyPressed = false;
    boolean motorOn = false;

    //Servo
    private Servo servo_pos;




    @Override
    public void init () {


    }

    @Override
    public void loop () {





// Toggle motor on and off witth same button
        if (gamepad1.b && !aAlreadyPressed) {
            motorOn = !motorOn;

            if (motorOn) {
                telemetry.addLine("Banda encendida");
                motor.setPower(0.5);
                servo_pos.setPosition(1.0);

            } else {
                motor.setPower(0.0);
                telemetry.addLine("Banda apagada");

            }
        }

        aAlreadyPressed = gamepad1.b;


    }
}