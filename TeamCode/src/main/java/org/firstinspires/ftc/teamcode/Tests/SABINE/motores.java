package org.firstinspires.ftc.teamcode.Tests.SABINE;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@TeleOp
public class motores extends OpMode {
    double incremento = 0.1;
    double velocidad = 0;
    boolean lbutton = false;
    boolean rbutton = false;

    private DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");


    }

    @Override
    public void loop() {

        if (gamepad1.a) {
            motor.setPower(velocidad);
        }

        if (gamepad1.b) {
            motor.setPower(0.0);

        }

        if (gamepad1.right_bumper && !rbutton) {
            velocidad += incremento;
            if (velocidad > 1) velocidad = 1;
        }


        if (gamepad1.left_bumper && !lbutton) {
            velocidad -= incremento;
            if (velocidad < -1) velocidad = -1;

        }

        rbutton = gamepad1.right_bumper;
        lbutton = gamepad1.left_bumper;
        motor.setPower(velocidad);
        telemetry.addData("velocidad", motor.getPower());
        telemetry.update();




    }



    private void While(boolean b) {
    }
}