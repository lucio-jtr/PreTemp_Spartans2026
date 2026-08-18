package org.firstinspires.ftc.teamcode.Tests.SABINE;

import android.util.Size;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.firstinspires.ftc.vision.opencv.PredominantColorProcessor;

import java.util.ArrayList;
import java.util.List;

@Disabled
@TeleOp
public class color_detector extends LinearOpMode {

    //Motor
    private DcMotor motor;
    boolean bAlreadyPressed = false;
    boolean motorOn = false;

    //Servo
    private Servo servo_pos;

    //Registro

    boolean purpleDetected = false;
    boolean greenDetected = false;

    int cantPurple;
    int cantGreen;
    boolean aAlreadyPressed = false;
    boolean registroOn = false;
    List<String> colors = new ArrayList<>();



    PredominantColorProcessor colorSensor = new PredominantColorProcessor.Builder()
            .setRoi(ImageRegion.asUnityCenterCoordinates(-.75, 0.75, 0.75, -0.75))
            .setSwatches(
                    PredominantColorProcessor.Swatch.ARTIFACT_GREEN,
                    PredominantColorProcessor.Swatch.ARTIFACT_PURPLE,
                    PredominantColorProcessor.Swatch.BLUE,
                    PredominantColorProcessor.Swatch.YELLOW,
                    PredominantColorProcessor.Swatch.BLACK,
                    PredominantColorProcessor.Swatch.WHITE)
            .build();



    @Override
    public void runOpMode() {



        //Camara web
        VisionPortal portal = new VisionPortal.Builder()
                .addProcessor(colorSensor)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();



        telemetry.setMsTransmissionInterval(100);
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

        motor = hardwareMap.get(DcMotor.class, "motor");
        servo_pos = hardwareMap.get(Servo.class, "servo");




        while (opModeIsActive() || opModeInInit()) {

            PredominantColorProcessor.Result result = colorSensor.getAnalysis();

            if (gamepad1.b && !bAlreadyPressed) {
                motorOn = !motorOn;

                if (motorOn) {
                    telemetry.addLine("Banda encendida");
                    motor.setPower(0.5);


                } else {
                    motor.setPower(0.0);
                    telemetry.addLine("Banda apagada");
                }
            }

            if (result.closestSwatch != PredominantColorProcessor.Swatch.ARTIFACT_GREEN){
                greenDetected = false;
            }

            if (result.closestSwatch != PredominantColorProcessor.Swatch.ARTIFACT_PURPLE){
                purpleDetected = false;
            }

            if (result.closestSwatch == PredominantColorProcessor.Swatch.ARTIFACT_PURPLE) {
                if (!purpleDetected) {
                    telemetry.addData("Detected color", result.closestSwatch);
                    servo_pos.setPosition(1.0);
                    colors.add("ARTIFACT_PURPLE");
                    cantPurple = cantPurple + 1;
                    purpleDetected= true;
                }


            } else if (result.closestSwatch == PredominantColorProcessor.Swatch.ARTIFACT_GREEN) {

                if (!greenDetected) {
                    telemetry.addData("Detected color", result.closestSwatch);
                    servo_pos.setPosition(0.0);
                    colors.add("ARTIFACT_GREEN");
                    cantGreen = cantGreen + 1;
                    greenDetected = true;
                }


            } else {
                if (servo_pos.getPosition() == 1.0 || servo_pos.getPosition() == 0.0) {
                    telemetry.addLine("Ningun objeto detectado o color desconocido");
                    servo_pos.setPosition(0.5);

                }
            }

            if (gamepad1.a && !aAlreadyPressed) {
                registroOn = !registroOn;

                if (registroOn) {
                    for ( String element : colors){
                        telemetry.addLine(element);
                    }

                } else {
                    telemetry.addLine("Presiona ( a ) para mostrar el registro");
                }
            }

            if (gamepad1.x){
                cantGreen = 0;
                cantPurple = 0;
                colors.clear();
            }

            aAlreadyPressed = gamepad1.a;
            bAlreadyPressed = gamepad1.b;


            telemetry.addData("Cantidad de pelotas moradas", cantPurple);
            telemetry.addData("Cantidad de pelotas verdes", cantGreen);

            sleep(20);

            telemetry.update();
        }
    }
}
