package org.firstinspires.ftc.teamcode.Tests.SABINE;

import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.Circle;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;


import java.util.List;

@TeleOp
public class circular_color_detection extends LinearOpMode {

    @Override
    public void runOpMode() {

            ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                    .setTargetColorRange(ColorRange.ARTIFACT_PURPLE)
                    .setTargetColorRange(ColorRange.ARTIFACT_GREEN)// Use a predefined color match
                    .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                    .setRoi(ImageRegion.entireFrame())

                    //.setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                    .setDrawContours(false)   // Show contours on the Stream Preview
                    .setBoxFitColor(0)       // Disable the drawing of rectangles
                    .setCircleFitColor(Color.rgb(235, 167, 204)) // Draw a circle 235, 167, 204
                    .setBlurSize(8)          // Smooth the transitions between different colors in image, originally set to 5

                    // the following options have been added to fill in perimeter holes.
                    .setDilateSize(20)       // Expand blobs to fill any divots on the edges
                    .setErodeSize(20)        // Shrink blobs back to original size
                    .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)



                    .build();

            VisionPortal portal = new VisionPortal.Builder()
                    .addProcessor(colorLocator)
                    .setCameraResolution(new Size(320, 240))
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .build();

            telemetry.setMsTransmissionInterval(100);   // Speed up telemetry updates for debugging.
            telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

            // WARNING:  To view the stream preview on the Driver Station, this code runs in INIT mode.
       while (opModeIsActive() || opModeInInit()) {
           telemetry.addData("preview on/off", "... Camera Stream\n");

           List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();


           ColorBlobLocatorProcessor.Util.filterByCriteria(
                   ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                   50, 20000, blobs);  // filter out very small blobs.

           ColorBlobLocatorProcessor.Util.filterByCriteria(
                   ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                   0.6, 1, blobs);

           ColorBlobLocatorProcessor.Util.sortByCriteria(ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA, SortOrder.DESCENDING, blobs);

           if (!blobs.isEmpty()) {

               ColorBlobLocatorProcessor.Blob largestBlob = blobs.get(0);

               Circle circleFit = largestBlob.getCircle();
               telemetry.addData("Largest blob area", largestBlob.getContourArea());
               telemetry.addData("Circle fit x: ", circleFit.getX());
               telemetry.addData("Circle Fit y", circleFit.getY());


               //✿ Here is where we let the user know in what direction to move the ball✿

               int ball_area = 100;

                if (circleFit.getX() > 180){
                    telemetry.addLine("Mover objeto a la derecha");
                }
                if (circleFit.getX() < 160) {
                    telemetry.addLine("Mover objeto a la izquierda");
                }
                if (circleFit.getY() > 130) {
                    telemetry.addLine("Mover objeto hacia arriba");
                }
                if (circleFit.getY() < 110) {
                    telemetry.addLine("Mover objeto hacia abajo");
                }
                if (largestBlob.getContourArea() < 13000){
                    telemetry.addLine("Mover objeto hacia adelante");
                }
                if (ColorRange.ARTIFACT_PURPLE == colorLocator.getBlobs()){

                }

           } else {
               telemetry.addLine("Ningun objeto detectado");
           }



           telemetry.update();
           sleep(100);

       }
    }
}

