/*package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp
public class touch_sensor extends OpMode {
    private DigitalChannel touchSensor;

    @Override
    public void init () {

        touchSensor = hardwareMap.get(DigitalChannel.class, "sensor_touch");
    }

    @Override
    public void loop(){
        boolean state = touchSensor.getState();

        if (state) {
            telemetry.addData("Touch Sensor", "Is Pressed");

        } else {
            telemetry.addData("Touch Sensor", "Is Not Pressed");
        }

        telemetry.update();
    }
}*/

package org.firstinspires.ftc.teamcode.Tests.SABINE;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Disabled
@TeleOp
public class touch_sensor extends OpMode {
    TouchSensor touchSensor;

    @Override
    public void init() {
        touchSensor = hardwareMap.get(TouchSensor.class, "touch_sensor");
    }

    public void loop(){
            if (touchSensor.isPressed()) {
                telemetry.addData("Touch Sensor", "Is Pressed");
            } else {
                telemetry.addData("Touch Sensor", "Is Not Pressed");
            }

            telemetry.update();
    }
}
