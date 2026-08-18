package org.firstinspires.ftc.teamcode.Tests.GUILLE;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

//El sensor de toque es una conexión digital
@Disabled
@TeleOp
public class Sensor_toque extends OpMode {
    TouchSensor touchSensor;

    @Override
    public void init(){
        touchSensor = hardwareMap.get(TouchSensor.class, "touch_sensor");
    }

    @Override
    public void loop(){
        //Verifica que el sensor de toque se haya presionado
        //La entrada 0 no sirve
        //El sensor de toque sólo sirve con entradas impares
        if (touchSensor.isPressed()){
            telemetry.addData("Touch Sensor", "Is Pressed");
        }else{
            telemetry.addData("Touch Sensor", "Isn't Pressed");
        }

        telemetry.update();
    }
}
