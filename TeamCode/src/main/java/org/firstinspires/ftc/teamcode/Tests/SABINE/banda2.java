
package org.firstinspires.ftc.teamcode.Tests.SABINE;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp
public class banda2 extends OpMode {

    //Motor
    private DcMotor motor;
    boolean aAlreadyPressed = false;
    boolean motorOn = false;

    //Servo
    private Servo servo_pos;

    //Sensor de color
    NormalizedColorSensor colorSensor;

    public enum detectedColor {
        PURPLE,
        GREEN,
        UNKNOWN}


    public detectedColor getdetectedColor() {
            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            colorSensor.setGain(6);

            float normRed, normGreen, normBlue;
            normRed = colors.red / colors.alpha;
            normGreen = colors.green / colors.alpha;
            normBlue = colors.blue / colors.alpha;


        /*purple =     >.1353,     <.176,  >.1721
        green =      <.1,    >.23,      < 15,*/


            if (normRed > .1353 && normGreen > .176 && normBlue > .1721) {
                return detectedColor.PURPLE;
            } else if (normRed < .1 && normGreen > .23 && normBlue < .15) {
                return detectedColor.GREEN;
            } else {
                return detectedColor.UNKNOWN;
            }

    }

    @Override
    public void init () {
            motor = hardwareMap.get(DcMotor.class, "motor");
            colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
            servo_pos = hardwareMap.get(Servo.class, "servo");
    }

    @Override
    public void loop () {

        // Toggle motor on and off witth same button
        if (gamepad1.b && !aAlreadyPressed) {
                motorOn = !motorOn;

                if (motorOn) {

                    telemetry.addLine("Banda encendida");

                    motor.setPower(0.5);
                } else {
                    motor.setPower(0.0);
                    telemetry.addLine("Banda apagada");
                }
            }
            aAlreadyPressed = gamepad1.b;

            //detects color and moves servo
    }
}

