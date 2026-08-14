package org.firstinspires.ftc.teamcode.Tests.SABINE;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class sensor_distancia extends OpMode {
    private DistanceSensor distanceSensor;

    @Override
    public void init(){
        distanceSensor = hardwareMap.get(DistanceSensor.class, "sensor_distance");



    }


    @Override
    public void loop (){
        telemetry.addData("deviceName", distanceSensor.getDeviceName() );
        telemetry.addData("range", String.format("%.01f mm", distanceSensor.getDistance(DistanceUnit.MM)));
        telemetry.addData("range", String.format("%.01f cm", distanceSensor.getDistance(DistanceUnit.CM)));
        telemetry.addData("range", String.format("%.01f m", distanceSensor.getDistance(DistanceUnit.METER)));
        telemetry.addData("range", String.format("%.01f in", distanceSensor.getDistance(DistanceUnit.INCH)));

        telemetry.update();

    }
//en el bus 0 del 12c debe tener el imu, es el controltrolador principal.
//@disable to disable the whole op mode

}
