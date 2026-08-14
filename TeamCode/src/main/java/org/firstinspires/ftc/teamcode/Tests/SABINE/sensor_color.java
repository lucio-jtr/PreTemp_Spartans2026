package org.firstinspires.ftc.teamcode.Tests.SABINE;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp
public class sensor_color extends OpMode {


    /*
     * This OpMode shows how to use a color sensor in a generic
     * way, regardless of which particular make or model of color sensor is used. The OpMode
     * assumes that the color sensor is configured with a name of "sensor_color".
     *
     * There will be some variation in the values measured depending on the specific sensor you are using.
     *
     * If the color sensor supports adjusting the gain, you can increase the gain (a multiplier to make
     * the sensor report higher values) by holding down the A button on the gamepad, and decrease the
     * gain by holding down the B button on the gamepad. The AndyMark Proximity & Color Sensor does not
     * support this.
     *
     * If the color sensor has a light which is controllable from software, you can use the X button on
     * the gamepad to toggle the light on and off. The REV sensors don't support this, but instead have
     * a physical switch on them to turn the light on and off, beginning with REV Color Sensor V2. The
     * AndyMark Proximity & Color Sensor does not support this.
     *
     * If the color sensor also supports short-range distance measurements (usually via an infrared
     * proximity sensor), the reported distance will be written to telemetry. As of September 2025,
     * the only color sensors that support this are the ones from REV Robotics and the AndyMark
     * Proximity & Color Sensor. These infrared proximity sensor measurements are only useful at very
     * small distances, and are sensitive to ambient light and surface reflectivity. You should use a
     * different sensor if you need precise distance measurements.
     *
     * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
     * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
     */
    NormalizedColorSensor colorSensor;

    float gain = 2;

    final float[] hsvValues = new float[3];


    @Override

    public void init() {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");


    }
    @Override
    public void loop() {
        telemetry.addLine("Hold the A button on gamepad 1 to increase gain, or B to decrease it.\n");
        telemetry.addLine("Higher gain values mean that the sensor will report larger numbers for Red, Green, and Blue, and Value\n");

        if (gamepad1.a) {
            gain += 0.005;
        } else if (gamepad1.b && gain > 1) { // A gain of less than 1 will make the values smaller, which is not helpful.
            gain -= 0.005;
        }

        telemetry.addData("Gain", gain);
        colorSensor.setGain(gain);


        // Get the normalized colors from the sensor
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
         * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
         * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
         * for an explanation of HSV color. */

        // Update the hsvValues array by passing it to Color.colorToHSV()

        Color.colorToHSV(colors.toColor(), hsvValues);

        telemetry.addLine()
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);

        /* If this color sensor also has a distance sensor, display the measured distance.
         * Note that the reported distance is only useful at very close range, and is impacted by
         * ambient light and surface reflectivity. */
        if (colorSensor instanceof DistanceSensor) {
            telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
        }

        telemetry.update();
    }
}