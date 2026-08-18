package org.firstinspires.ftc.teamcode.Tests.GUILLE;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@TeleOp(name="Mover Motor", group="Tests")
public class Motor2 extends OpMode {
    DcMotor motor;
    double velocity = 0;

    boolean prevRightBumper = false;
    boolean prevLeftBumper = false;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
    }

    @Override
    public void loop() {
        if (gamepad1.right_bumper && !prevRightBumper) {
            velocity += 0.1;
            if (velocity > 1) velocity = 1;
        }

        if (gamepad1.left_bumper && !prevLeftBumper) {
            velocity -= 0.1;
        }

        if (gamepad1.a) {
            velocity = 0;
        }

        prevRightBumper = gamepad1.right_bumper;
        prevLeftBumper = gamepad1.left_bumper;

        motor.setPower(velocity);

        telemetry.addData("Velocidad actual", velocity);
        telemetry.update();
    }
}
