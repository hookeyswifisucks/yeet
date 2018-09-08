package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.sun.tools.javac.jvm.ClassWriter;
import android.graphics.Color;


/**
 * Created by Phoebe Taylor on 12/4/2017.
 * This code is for parking in the triangle zones with a mecanum drive train using encoders. Hopefully,
 * it will also put one glyph in the cryptobox
 * TODO: It turns a little too much now toward the cryptobox, so it goes on crooked, that can be fixed with
 * TODO: some fine tuning and trial and error. Also, bring the lift up a little bit so the block doesn't
 * TODO: get hit or crooked on the start. Then, adopt the code for red side.
 */

@Autonomous(name = "AutoBlueStraightColor", group = "Autonomous Mecanum")
public class driftbotauto extends LinearOpMode {

    //make object of mecanum hardware class
    driftbothardware robot = new driftbothardware();

    //declare an object of the ElapsedTime class to allow you to calculate how long you've been driving
    private ElapsedTime runtime = new ElapsedTime();

    static final double DRIVE_SPEED = 0.6;
    static final double TURN_SPEED = 0.4;


    /*the following variables are arguments for the encoderDrive method, each for a different action.
    RIGHT variables denote values that are applicable for the right wheels, LEFT for left wheels.
    Rotating on the Z axis means all wheels are spinning in the same direction, so only one TURN variable is necessary.
    We will not be going diagonally in this code, so you will only need to use one axis
    (just y axis for forward, just z for rotation, just x for laterally) */

    /**For forward/backward motion with our wheel configuration, the right side needs to be negative, and the left
     needs to be positive (opposite of the driver class's logic because we reverse Y in the driver but not here)
     All measurements are in inches. 23.5 inches is about 90 DEGREES!!!!
     */

    static final double FULL_LEFT = 0.5;
    static final double FULL_RIGHT = -0.5;
    static final double HALF_LEFT = 0.25;
    static final double HALF_RIGHT = -0.25;
    static final double LOW_LEFT = 0.1;
    static final double LOW_RIGHT = -0.1;
    static final double STOP = 0;
    static final double FULL_SPEED = 1;
    static final double HALF_SPEED = .5;
    static final double LOW_SPEED = .15;

    //say which alliance color so the jewel code will know which one to knock off. Keep this lower case
    //(don't put Blue or Red or BLUE or RED, because the loops on the colorSense method don't account for those.
    //I made the method account for both alliance colors because it was easier to do that and then copy/paste
    //into the other autonomous classes. It doesn't have to be done that way... actually:
    static final String ALLIANCE_COLOR = "blue";

    @Override
    public void runOpMode(){
        //start the init method in the hardware class
        robot.init(hardwareMap);

        //send some telemetry messages to tell you that it's running
        telemetry.addData("IT'S RUNNING WTF", "autonomous");
        telemetry.update();

        //tell the encoders to reset for a hot sec
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        idle();

        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        //the LEFT and RIGHT in the variables denotes the wheel or side to which it is assigned, not
        //the direction it aims to move the robot
        glyphPlacement(PICK_RIGHT, PICK_LEFT, "Close grabber");

        //make the robot lift the glyph up a little so it's not resting on the ground while you're moving
        runtime.reset();
        while (runtime.seconds() < 1.8) {
            robot.lift.setPower(-0.2);
        }
        robot.lift.setPower(0);

        colorSense(ALLIANCE_COLOR, "Should be reading");

        encoderDrive(DRIVE_SPEED, s1_LEFT, s1_RIGHT, 10.0, "backward");  // S1: backward 23.5'' off board
        encoderDrive(TURN_SPEED, s2_TURN, s2_TURN, 10.0, "turn 90 degrees");  // S2: Turn left 90 degrees
        encoderDrive(DRIVE_SPEED, s3_LEFT, s3_RIGHT, 10.0, "forward a little"); // s3: Forward a little
        encoderDrive(TURN_SPEED, s4_TURN, s4_TURN, 10.0, "turn 90 degrees"); //s4: turn another 90 left

        glyphPlacement(DROP_RIGHT, DROP_LEFT, "open grabber"); //open grabber to drop the glyph

        encoderDrive(DRIVE_SPEED, s5_LEFT, s5_RIGHT, 10.0, "into the cryptobox"); // forward into the box
        encoderDrive(DRIVE_SPEED, s6_LEFT, s6_RIGHT, 10.0, "Retreat");  // S3: retreat like 2 inches so we're not touching the glyph


    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS, String msg) {
        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newFrontLeftTarget = robot.frontLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newFrontRightTarget = robot.frontRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newBackLeftTarget = robot.backLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newBackRightTarget = robot.backRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

            robot.frontLeft.setTargetPosition(newFrontLeftTarget);
            robot.frontRight.setTargetPosition(newFrontRightTarget);
            robot.backLeft.setTargetPosition(newBackLeftTarget);
            robot.backRight.setTargetPosition(newBackRightTarget);



            // Turn On RUN_TO_POSITION
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);



            // reset the timeout time and start motion.
            runtime.reset();
            double power = Math.abs(speed);

            robot.frontLeft.setPower(power);
            robot.backLeft.setPower(power);
            robot.frontRight.setPower(power);
            robot.backRight.setPower(power);

            telemetry.addData("Current Stage: ", msg);
            telemetry.update();


            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.frontLeft.isBusy() && robot.frontRight.isBusy())) {

                // Display it for the driver.
                //telemetry.addData("Path1",  "Running to %7d :%7d", newFrontLeftTarget,  newFrontRightTarget);
                //telemetry.addData("Path2",  "Running at %7d :%7d",
                //        robot.frontLeft.getCurrentPosition(),
                //        robot.frontRight.getCurrentPosition());
                //telemetry.addData(msg, "autonomous");
                int whereFrontRightThinks = robot.frontRight.getCurrentPosition();
                int whereFrontLeftThinks = robot.frontLeft.getCurrentPosition();
                int whereBackRightThinks = robot.backRight.getCurrentPosition();
                int whereBackLeftThinks = robot.backLeft.getCurrentPosition();

                /*telemetry.addData("front right position: ", whereFrontRightThinks);
                telemetry.addData("front left position: ", whereFrontLeftThinks);
                telemetry.addData("back right position: ", whereBackRightThinks);
                telemetry.addData("back left position: ", whereBackLeftThinks);*/
                telemetry.addData("current stage: ", msg);
                telemetry.update();
            }

            // Stop all motion;
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backRight.setPower(0);
            robot.backLeft.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



            //  sleep(250);   // optional pause after each move
        }
    }

    public void glyphPlacement (double PositionRight, double PositionLeft, String msg){
        //control is set to 0 when you want the lift to open
        robot.left_grab.setPosition(PositionLeft);
        robot.right_grab.setPosition(PositionRight);
        //telemetry.addData(msg,"something");
        //telemetry.update();
    }


    /* the following method only really exists because it needs to set up the sensor, read in and process
    the data from it, and THEN actually read the color of the glyph. The code responsible for actually
    deciding what color it's seeing and what to do with what color is relatively small in and of itself
     */
    public void colorSense (String allianceColor, String msg) {
        //put the arm down (1 is up), 0.43 is how far down i want it to get
        double maxArm = 0.95;
        // decrementing the position of the arm so that the arm doesn't slam into the ground
        while (maxArm > 0.37) {
            maxArm -= 0.002;
            robot.jewel_arm.setPosition(maxArm);
        }


        // values is a reference to the hsvValues array.
        float[] hsvValues = new float[3];
        final float values[] = hsvValues;

        //this just makes sure the light on the sensor is on... it should be by default but just in case
        if (robot.colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)robot.colorSensor).enableLight(true);
        }


        runtime.reset();
        while (runtime.seconds() < 2) {
            //this should read in the color values in RGB from the sensor
            NormalizedRGBA colors = robot.colorSensor.getNormalizedColors();

            telemetry.addData("Should be down","arm");
            telemetry.addLine()
                    .addData("Hue", "%.2f", hsvValues[0]);
            telemetry.update();

            float max = Math.max(Math.max(Math.max(colors.red, colors.green), colors.blue), colors.alpha);
            colors.red /= max;
            colors.green /= max;
            colors.blue /= max;
            int color = colors.toColor();

            /*change the color values from RGB to HSV, because HSV is a more reliable spectrum of color data.
            If you aren't sure why, google it for a full description, but basically HSV is Hue, Saturation,
            and Vibrancy. The Hue is the "color" you're seeing, and the other two values essentially measure
            the light in the room. This is good because if you are reading in values in two rooms with different
            lighting, the RGB values will be drastically different, but the Hue value will remain similar
            because HSV can correct for different lighting. This means that, when we later decide what color
            the sensor is looking at, we will use just the H value.
            */
            Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsvValues);
        }

        /* the following code will actually execute and move the robot. First it will put the arm down,
        then read the color, and based upon the color it reads and what alliance color it is given,
        it will knock one of the jewels off and then pull the arm back up. Then it will rotate back so
        as not to disrupt the rest of the code for placing the glyph. After that, it will exit
        this method. I will use the encoderDrive method to turn the robot the appropriate way, because
        there's no point in rewriting all that code in here when it already exists in that method
         */

        //sleep(1000);

        telemetry.addData("Should be down","arm");
        telemetry.addLine()
                .addData("Hue", "%.2f", hsvValues[0]);
        telemetry.update();

        //I use sleeps to troubleshoot so I can stop all the movement for a time and see what the robot is doing
        //sleep(3000);

        if (allianceColor == "red") {
            //1-3 is probably red (I'm making the range really big just in case, so I'll use 50 instead),
            //so if this is true, then the robot should turn right to
            //knock the opposite jewel off (blue jewel). Positive values for the direction of movement
            //will make the robot turn right, and negative values will make it go left
            if (hsvValues[0] < 60) {
                encoderDrive(DRIVE_SPEED, 2.5, 2.5, 10.0, "knock jewel off right"); //turns 2 inches right
                robot.jewel_arm.setPosition(0.94);
                encoderDrive(DRIVE_SPEED, -2.5, -2.5, 10.0, "reset position"); //turns 2 inches right

            } else if (hsvValues[0] >= 60 && hsvValues[0] <= 300){
                encoderDrive(DRIVE_SPEED, -2.5, -2.5, 10.0, "knock jewel off left"); //turns 2 inches left
                robot.jewel_arm.setPosition(0.94);
                encoderDrive(DRIVE_SPEED, 2.5, 2.5, 10.0, "reset position"); //turns 2 inches right
            }
            else {
                robot.jewel_arm.setPosition(0.94);
            }
        }

        if (allianceColor == "blue") {
            //if the first loop (greater than 60) is true, then the sensor is looking at the blue jewel
            //while in the blue alliance, so it should rotate right to knock off the red jewel
            if (hsvValues[0] >= 60 && hsvValues[0] <= 300) {
                encoderDrive(DRIVE_SPEED, 2.5, 2.5, 10.0, "knock jewel off right"); //turns 2 inches left
                robot.jewel_arm.setPosition(0.94);
                encoderDrive(DRIVE_SPEED, -2.5, -2.5, 10.0, "reset position"); //turns 2 inches right

            } else if (hsvValues[0] < 60){
                encoderDrive(DRIVE_SPEED, -2.5, -2.5, 10.0, "knock jewel off left"); //turns 2 inches right
                robot.jewel_arm.setPosition(0.94);
                encoderDrive(DRIVE_SPEED, 2.5, 2.5, 10.0, "reset position"); //turns 2 inches right
            }
            else {
                robot.jewel_arm.setPosition(0.94);
            }
        }
    }
}
