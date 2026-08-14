package org.firstinspires.ftc.teamcode.Tests;
import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.RotatedRect;

import java.util.List;

@TeleOp
public class Camera_Detected extends LinearOpMode {

    @Override
    public void runOpMode() {
        ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_PURPLE)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                .setDrawContours(true)
                .setBlurSize(10)
                .build();

        VisionPortal portal = new VisionPortal.Builder()
                .addProcessor(colorLocator)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();

        telemetry.setMsTransmissionInterval(100);
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

        waitForStart();

        while (opModeIsActive() || opModeInInit()) {
            telemetry.addData("Status", "Camara Corriendo...\n");

            List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                    1000, 20000, blobs);

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                    0.7, 1.0, blobs);

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_DENSITY,
                    0.6, 1.0, blobs);

            ColorBlobLocatorProcessor.Util.sortByArea(SortOrder.DESCENDING, blobs);

            if (blobs.isEmpty()) {
                telemetry.addLine("NO HAY PELOTA DETECTADA");
            }
            if (!blobs.isEmpty()) {
                ColorBlobLocatorProcessor.Blob blobPrincipal = blobs.get(0);
                RotatedRect boxFit = blobPrincipal.getBoxFit();
                int centerx = (int) boxFit.center.x;

                telemetry.addLine("Ctr:(X,Y)  Area Dens Aspect Arc  Circ");
                telemetry.addLine(String.format("(%3d,%3d) %5d %4.2f  %5.2f %3d %5.3f ",
                        (int) boxFit.center.x, (int) boxFit.center.y,
                        blobPrincipal.getContourArea(), blobPrincipal.getDensity(),
                        blobPrincipal.getAspectRatio(), (int) blobPrincipal.getArcLength(),
                        blobPrincipal.getCircularity()));

                if (centerx < 130) {
                    telemetry.addData("Pelota", "ESTÁ A LA IZQUIERDA");
                } else if (centerx > 190) {
                    telemetry.addData("Pelota", "ESTÁ A LA DERECHA");
                } else {
                    telemetry.addData("Pelota", "¡ESTÁ CENTRADA!");
                }
            }

            telemetry.update();
            sleep(50);
        }
    }
}
