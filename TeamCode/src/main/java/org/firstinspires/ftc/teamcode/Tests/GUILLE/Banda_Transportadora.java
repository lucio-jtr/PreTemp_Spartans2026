package org.firstinspires.ftc.teamcode.Tests;
import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;

import java.util.List;

@TeleOp
public class Banda_Transportadora extends OpMode {

    public enum Estado_Banda {
        CALIBRANDO, MOVIENDO, CLASIFICANDO
    }

    Estado_Banda Status_Banda = Estado_Banda.CALIBRANDO;
    double tiempoInicio = 0;

    DcMotor motor;
    Servo servo;

    VisionPortal portal;
    ColorBlobLocatorProcessor purple_ball;
    ColorBlobLocatorProcessor green_ball;

    int count_purple = 0;
    int count_green = 0;

    boolean yaDetectado = false;
    boolean xPresionadoAnteriormente = false;

    // Constantes para servo
    private static final double POS_NEUTRO = 0.0;
    private static final double POS_MORADO = 0.5;
    private static final double POS_VERDE = 1.0;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        servo = hardwareMap.get(Servo.class, "servo");

        telemetry.setMsTransmissionInterval(150);
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

        purple_ball = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_PURPLE)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setDrawContours(true).setBoxFitColor(0)
                .setCircleFitColor(Color.rgb(255, 255, 0))
                .setBlurSize(10).setDilateSize(15).setErodeSize(10)
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)
                .build();

        green_ball = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_GREEN)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setDrawContours(true).setBoxFitColor(0)
                .setCircleFitColor(Color.rgb(255, 255, 0))
                .setBlurSize(10).setDilateSize(15).setErodeSize(10)
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)
                .build();

        portal = new VisionPortal.Builder()
                .addProcessor(purple_ball)
                .addProcessor(green_ball)
                .setCameraResolution(new Size(640, 480)) // resolución mejorada
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();
    }

    @Override
    public void loop() {
        boolean xPresionado = gamepad1.x;

        switch (Status_Banda) {
            case CALIBRANDO:
                telemetry.addData("STATUS", "La Cámara se está Calibrando");
                if (tiempoInicio == 0) tiempoInicio = getRuntime();

                if (getRuntime() - tiempoInicio > 2.0) {
                    Status_Banda = Estado_Banda.MOVIENDO;
                }
                break;

            case MOVIENDO:
                telemetry.addData("STATUS", "Moviendo banda");
                motor.setPower(0.8);

                // Detección de flanco en botón X
                if (xPresionado && !xPresionadoAnteriormente) {
                    motor.setPower(0.0);
                    tiempoInicio = getRuntime();
                    yaDetectado = false; // reinicia detección
                    Status_Banda = Estado_Banda.CLASIFICANDO;
                }
                break;

            case CLASIFICANDO:
                motor.setPower(0.0);
                telemetry.addData("STATUS", "Clasificando Pelota");

                if (!yaDetectado && getRuntime() - tiempoInicio > 0.5) {
                    procesarVision();
                    yaDetectado = true;
                    Status_Banda = Estado_Banda.MOVIENDO; // regresa al ciclo
                }
                break;
        }

        xPresionadoAnteriormente = xPresionado;
        telemetry.update();
    }

    private void filtrarBlobs(List<ColorBlobLocatorProcessor.Blob> blobs) {
        ColorBlobLocatorProcessor.Util.filterByCriteria(ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA, 800, 80000, blobs);
        ColorBlobLocatorProcessor.Util.filterByCriteria(ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY, 0.6, 1.0, blobs);
        ColorBlobLocatorProcessor.Util.filterByCriteria(ColorBlobLocatorProcessor.BlobCriteria.BY_DENSITY, 0.5, 1.0, blobs);
        ColorBlobLocatorProcessor.Util.sortByArea(SortOrder.DESCENDING, blobs);
    }

    private void procesarVision() {
        List<ColorBlobLocatorProcessor.Blob> blobsMorados = purple_ball.getBlobs();
        List<ColorBlobLocatorProcessor.Blob> blobsVerdes = green_ball.getBlobs();

        filtrarBlobs(blobsMorados);
        filtrarBlobs(blobsVerdes);

        if (!blobsMorados.isEmpty()) {
            ColorBlobLocatorProcessor.Blob pelota = blobsMorados.get(0);
            telemetry.addData("DETECTADO", "PELOTA MORADA");
            telemetry.addData("Área", pelota.getContourArea());
            telemetry.addData("Circularidad", pelota.getCircularity());
            telemetry.addData("Densidad", pelota.getDensity());

            servo.setPosition(POS_MORADO);
            count_purple++;
            telemetry.addData("Total Moradas", count_purple);
        }
        else if (!blobsVerdes.isEmpty()) {
            ColorBlobLocatorProcessor.Blob pelota = blobsVerdes.get(0);
            telemetry.addData("DETECTADO", "PELOTA VERDE");
            telemetry.addData("Área", pelota.getContourArea());
            telemetry.addData("Circularidad", pelota.getCircularity());
            telemetry.addData("Densidad", pelota.getDensity());

            servo.setPosition(POS_VERDE);
            count_green++;
            telemetry.addData("Total Verdes", count_green);
        }
        else {
            telemetry.addData("DETECTADO", "NO HAY PELOTA");
            servo.setPosition(POS_NEUTRO);
        }
    }
}
