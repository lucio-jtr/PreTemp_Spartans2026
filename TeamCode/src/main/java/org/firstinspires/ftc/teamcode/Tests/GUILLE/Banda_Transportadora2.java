package org.firstinspires.ftc.teamcode.Tests;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.firstinspires.ftc.vision.opencv.PredominantColorProcessor;

import java.util.List;

@TeleOp
public class Banda_Transportadora2 extends OpMode {

    // Change these after testing the physical mechanism.
    private static final double CONVEYOR_POWER = 0.65;
    private static final double GREEN_LEFT_POS = 0.00;   // servo's 0 degree end
    private static final double PURPLE_RIGHT_POS = 1.00; // servo's 180 degree end

    // Camera detection settings.  Tune MIN_BLOB_AREA with the camera preview open.
    private static final double MIN_BLOB_AREA = 300;
    private static final double MAX_BLOB_AREA = 100000;
    private static final long MIN_SERVO_CHANGE_MS = 250;

    private DcMotor conveyorMotor;
    private Servo sorterServo;
    private VisionPortal visionPortal;
    private ColorBlobLocatorProcessor greenLocator;
    private ColorBlobLocatorProcessor purpleLocator;

    private int lastDirection = -1; // 0 = left, 1 = right
    private long lastServoChangeMs = 0;

    private static final double COUNT_LINE_Y = 120; // middle of a 240-pixel-tall image
    private static final double LINE_MARGIN_PX = 12;
    private static final int EMPTY_FRAMES_TO_RESET = 8;

    private static final int NO_COLOR = -1;
    private static final int GREEN = 0;
    private static final int PURPLE = 1;

    private int trackedColor = NO_COLOR;
    private boolean sawBallAboveLine = false;
    private boolean countedThisBall = false;
    private int emptyFrames = 0;
    private int greenBallCount = 0;
    private int purpleBallCount = 0;


    @Override
    public void init() {
        conveyorMotor = hardwareMap.get(DcMotor.class, "motor");
        sorterServo = hardwareMap.get(Servo.class, "servo");

        telemetry.setMsTransmissionInterval(150);

        conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyorMotor.setPower(0);
        sorterServo.setPosition(GREEN_LEFT_POS);

        // The ROI is only the area above the diverter.  This prevents colors elsewhere
        // in the camera image from sorting a ball at the wrong time.
        ImageRegion sortingZone = ImageRegion.asUnityCenterCoordinates(
                -0.50, 0.45, 0.50, -0.45);

        //greenLocator = makeLocator(ColorRange.ARTIFACT_GREEN, sortingZone);
        //purpleLocator = makeLocator(ColorRange.ARTIFACT_PURPLE, sortingZone);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(320, 240))
                .addProcessor(greenLocator)
                .addProcessor(purpleLocator)
                .addProcessor(colorSensor)
                .build();

        telemetry.addLine("Ready. Aim Webcam 1 at the sorting zone.");
        telemetry.addLine("Use the camera preview to tune ROI and MIN_BLOB_AREA.");
    }

    PredominantColorProcessor colorSensor = new PredominantColorProcessor.Builder()
            .setRoi(ImageRegion.asUnityCenterCoordinates(-0.1, 0.1, 0.1, -0.1))
            .setSwatches(
                    PredominantColorProcessor.Swatch.ARTIFACT_GREEN,
                    PredominantColorProcessor.Swatch.ARTIFACT_PURPLE)
            .build();

    @Override
    public void start() {
        // Allow the first detected ball to command the servo immediately.
        lastServoChangeMs = 1;
        conveyorMotor.setPower(CONVEYOR_POWER);
    }

    @Override
    public void loop() {
        // This is deliberately in loop(): the belt
        // remains powered while vision sorts.
        conveyorMotor.setPower(CONVEYOR_POWER);

        double greenY = getUsableBallCenterY(greenLocator);
        double purpleY = getUsableBallCenterY(purpleLocator);

        boolean greenBall = !Double.isNaN(greenY);
        boolean purpleBall = !Double.isNaN(purpleY);

        countBallCrossing(greenBall, greenY, purpleBall, purpleY);

        String decision = "No ball";
        if (greenBall && !purpleBall) {
            commandSorter(0);
            decision = "GREEN -> LEFT";
        } else if (purpleBall && !greenBall) {
            commandSorter(1);
            decision = "PURPLE -> RIGHT";
        } else if (greenBall) {
            // Do not guess when two balls overlap in the camera ROI.
            decision = "Ambiguous: wait for one ball";
        }

        telemetry.addData("Sorter", decision);
        telemetry.addData("Servo position", "%.2f", sorterServo.getPosition());
        telemetry.addData("Conveyor power", "%.2f", conveyorMotor.getPower());
        telemetry.addData("Green blobs", greenLocator.getBlobs().size());
        telemetry.addData("Purple blobs", purpleLocator.getBlobs().size());
        telemetry.addData("Green balls counted", greenBallCount);
        telemetry.addData("Purple balls counted", purpleBallCount);
        telemetry.update();
    }

    /**
     * Counts only the first frame in which a new ball enters the ROI.  Without this
     * edge detection, one ball would be counted many times while it stays in view.
     */
    private double getUsableBallCenterY(ColorBlobLocatorProcessor locator) {
        List<ColorBlobLocatorProcessor.Blob> blobs = locator.getBlobs();

        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                MIN_BLOB_AREA, MAX_BLOB_AREA, blobs);

        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                0.7, 1.0, blobs);

        if (blobs.isEmpty()) {
            return Double.NaN;
        }

        // This assumes one ball at a time in the sorting zone.
        return blobs.get(0).getBoxFit().center.y;
    }

    private void countBallCrossing(
            boolean greenBall, double greenY,
            boolean purpleBall, double purpleY) {

        // Ignore ambiguous frames.
        if (greenBall && purpleBall) {
            return;
        }

        // Reset only after the zone has really been empty for a while.
        if (!greenBall && !purpleBall) {
            emptyFrames++;

            if (emptyFrames >= EMPTY_FRAMES_TO_RESET) {
                trackedColor = NO_COLOR;
                sawBallAboveLine = false;
                countedThisBall = false;
            }
            return;
        }

        emptyFrames = 0;

        int color = greenBall ? GREEN : PURPLE;
        double ballY = greenBall ? greenY : purpleY;

        if (trackedColor == NO_COLOR) {
            trackedColor = color;
        }

        // A different color during this pass is not trusted.
        if (color != trackedColor) {
            return;
        }

        // Ball must first be seen above the line.
        if (ballY <= COUNT_LINE_Y - LINE_MARGIN_PX) {
            sawBallAboveLine = true;
        }

        // Count only when it later moves below the line.
        if (!countedThisBall){
                //&& sawBallAboveLine
                //&& ballY >= COUNT_LINE_Y + LINE_MARGIN_PX) {

            if (trackedColor == GREEN) {
                greenBallCount++;
            } else {
                purpleBallCount++;
            }

            countedThisBall = true;
        }
    }

    private void commandSorter(int direction) {
        long now = System.currentTimeMillis();
        if (direction == lastDirection || now - lastServoChangeMs < MIN_SERVO_CHANGE_MS) {
            return;
        }

        sorterServo.setPosition(direction == 0 ? GREEN_LEFT_POS : PURPLE_RIGHT_POS);
        lastDirection = direction;
        lastServoChangeMs = now;
    }

    @Override
    public void stop() {
        conveyorMotor.setPower(0);
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}
