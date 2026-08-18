package org.firstinspires.ftc.teamcode.Tests.GUILLE;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
@Disabled
public class Sensor_distancia extends OpMode{
    DistanceSensor Sensor_Distancia;
    Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor) Sensor_Distancia;


    @Override
    public void init() {
        Sensor_Distancia = hardwareMap.get(DistanceSensor.class, "sensor_distance");
    }

    @Override
    public void loop(){
        //El 0 siempre lo debe tener por default el Drive Station (el IMU)
        //En el sensor de distancia el cuadrito va hacia arriba
        telemetry.addData("deviceName", Sensor_Distancia.getDeviceName());
        telemetry.addData("range", String.format("%.01f mm", Sensor_Distancia.getDistance(DistanceUnit.MM)));
        telemetry.addData("range", String.format("%.01f cm", Sensor_Distancia.getDistance(DistanceUnit.CM)));
        telemetry.addData("range", String.format("%.01f m", Sensor_Distancia.getDistance(DistanceUnit.METER)));
        telemetry.addData("range", String.format("%.01f in", Sensor_Distancia.getDistance(DistanceUnit.INCH)));

        telemetry.update();
    }
}
