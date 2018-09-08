package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;

public class driftbothardware {

    public DcMotor motorTurn = null;
    public DcMotor frontRight = null;
    public DcMotor frontLeft = null;

    HardwareMap hwMap = null;

    public void init (HardwareMap ahwMap)
    {
        hwMap = ahwMap;

        //assigns variable to config name
        motorTurn = hwMap.dcMotor.get("motor_1");
        frontRight = hwMap.dcMotor.get("front_right");
        frontLeft = hwMap.dcMotor.get("front_left");

        //sets power to 0
        motorTurn.setPower(0);
        frontRight.setPower(0);
        frontLeft.setPower(0);

        //sets direction
        motorTurn.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);

        //needed
        motorTurn.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
