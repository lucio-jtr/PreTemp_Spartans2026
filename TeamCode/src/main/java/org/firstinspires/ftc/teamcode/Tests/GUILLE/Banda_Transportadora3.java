package org.firstinspires.ftc.teamcode.Tests;


import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.firstinspires.ftc.vision.opencv.PredominantColorProcessor;

@TeleOp
public class Banda_Transportadora3 extends OpMode {

    private static final double CONVEYOR_POWER = 0.55;
    private static final double GREEN_LEFT_POS = 0.00;
    private static final double PURPLE_RIGHT_POS = 1.00;

    private static final ImageRegion COLOR_ZONE =
            ImageRegion.asUnityCenterCoordinates(-0.12, 0.12, 0.12, -0.12);

    private static final int REQUIRED_STABLE_FRAMES = 4;

    private static final int EMPTY_FRAMES_TO_REARM = 8;

    private static final int MIN_SATURATION = 70;
    private static final int MIN_BRIGHTNESS = 35;

    private static final int NO_BALL_COLOR = -1;
    private static final int GREEN = 0;
    private static final int PURPLE = 1;

    private DcMotor conveyorMotor;
    private Servo sorterServo;
    private VisionPortal visionPortal;
    private PredominantColorProcessor colorSensor;

    private int colorBeingConfirmed = NO_BALL_COLOR;
    private int stableFrames = 0;
    private int emptyFrames = 0;
    private boolean counterArmed = false;
    private int greenBallCount = 0;
    private int purpleBallCount = 0;

    @Override
    public void init() {
        conveyorMotor = hardwareMap.get(DcMotor.class, "motor");
        sorterServo = hardwareMap.get(Servo.class, "servo");

        conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyorMotor.setPower(0);
        sorterServo.setPosition(GREEN_LEFT_POS);

        colorSensor = new PredominantColorProcessor.Builder()
                .setRoi(COLOR_ZONE)
                .setSwatches(
                        PredominantColorProcessor.Swatch.ARTIFACT_GREEN,
                        PredominantColorProcessor.Swatch.ARTIFACT_PURPLE,
                        PredominantColorProcessor.Swatch.RED,
                        PredominantColorProcessor.Swatch.ORANGE,
                        PredominantColorProcessor.Swatch.YELLOW,
                        PredominantColorProcessor.Swatch.CYAN,
                        PredominantColorProcessor.Swatch.BLUE,
                        PredominantColorProcessor.Swatch.GREEN,
                        PredominantColorProcessor.Swatch.PURPLE,
                        PredominantColorProcessor.Swatch.MAGENTA,
                        PredominantColorProcessor.Swatch.BLACK,
                        PredominantColorProcessor.Swatch.WHITE)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(320, 240))
                .addProcessor(colorSensor)
                .build();

        telemetry.addLine("Ready: first show the ROI the neutral belt/background.");
        telemetry.update();
    }

    @Override
    public void start() {
        conveyorMotor.setPower(CONVEYOR_POWER);
    }

    @Override
    public void loop() {
        conveyorMotor.setPower(CONVEYOR_POWER);

        PredominantColorProcessor.Result result = colorSensor.getAnalysis();
        int detectedColor = getTrustedBallColor(result);

        if (detectedColor == NO_BALL_COLOR) {
            handleEmptyColorZone();
        } else {
            confirmAndHandleColor(detectedColor);
        }

        telemetry.addData("Closest swatch", result.closestSwatch);
        telemetry.addData("HSV", "(%d, %d, %d)",
                result.HSV[0], result.HSV[1], result.HSV[2]);
        telemetry.addData("Confirmed color", colorName(colorBeingConfirmed));
        telemetry.addData("Stable frames", stableFrames);
        telemetry.addData("Counter armed", counterArmed);
        telemetry.addData("Empty frames", emptyFrames);
        telemetry.addData("Green balls", greenBallCount);
        telemetry.addData("Purple balls", purpleBallCount);
        telemetry.update();
    }

    private int getTrustedBallColor(PredominantColorProcessor.Result result) {
        if (result.HSV[1] < MIN_SATURATION || result.HSV[2] < MIN_BRIGHTNESS) {
            return NO_BALL_COLOR;
        }

        if (result.closestSwatch == PredominantColorProcessor.Swatch.ARTIFACT_GREEN) {
            return GREEN;
        }
        if (result.closestSwatch == PredominantColorProcessor.Swatch.ARTIFACT_PURPLE) {
            return PURPLE;
        }
        return NO_BALL_COLOR;
    }

    private void confirmAndHandleColor(int detectedColor) {
        emptyFrames = 0;

        if (detectedColor == colorBeingConfirmed) {
            stableFrames++;
        } else {
            colorBeingConfirmed = detectedColor;
            stableFrames = 1;
        }

        if (!counterArmed || stableFrames < REQUIRED_STABLE_FRAMES) {
            return;
        }

        if (detectedColor == GREEN) {
            sorterServo.setPosition(GREEN_LEFT_POS);
            greenBallCount++;
        } else {
            sorterServo.setPosition(PURPLE_RIGHT_POS);
            purpleBallCount++;
        }

        counterArmed = false;
    }

    private void handleEmptyColorZone() {
        colorBeingConfirmed = NO_BALL_COLOR;
        stableFrames = 0;

        if (!counterArmed) {
            emptyFrames++;
            if (emptyFrames >= EMPTY_FRAMES_TO_REARM) {
                counterArmed = true;
                emptyFrames = 0;
            }
        }
    }

    private String colorName(int color) {
        if (color == GREEN) return "GREEN";
        if (color == PURPLE) return "PURPLE";
        return "NONE";
    }

    @Override
    public void stop() {
        conveyorMotor.setPower(0);
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}
