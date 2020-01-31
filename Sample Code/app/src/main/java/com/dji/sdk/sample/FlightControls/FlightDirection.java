/**
 * Class FlightDirection
 * Description : Class serves as the schedule flightcontrols orientation manipulation
 * The class determines which direction the flight controls need to be moved in order
 * achieve the desired direction of flight. This class has the capabiltities
 * grid coordinate (x,y)
 *
 * Author : Ryan Vacca
 **/

package com.dji.sdk.sample.FlightControls;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import dji.common.error.DJIError;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mobilerc.MobileRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;


public class FlightDirection extends TimelineElement
{
    private MissionControl mission_control;
    private MobileRemoteController mobile_remote_controller;
    private double rate;
    private int direction;

    /**  Velocity input values for all flight orientations  **/
    /**  Range for all is ( -1.0f , 1.0f )                  **/
    /**  These values worked well for correct 90 Degree Turns and Fwd movements of 1 foot **/
    private float yaw_left_velocity        = -0.9f;
    private float yaw_right_velocity       =  0.9f;
    private float pitch_forward_velocity   =  0.1f;
    private float pitch_backward_velocity  = -0.1f;
    private float throttle_up_velocity     =  0.1f;
    private float throttle_down_velocity   = -0.1f;
    private float roll_left_velocity       = -0.05f;
    private float roll_right_velocity      =  0.05f;

    /**  Delay in milliseconds for each specific orientation  **/
    /**  Tried to stay below 2 seconds for now for each move  **/
    private long DirectionDelay = 0;
    private long yaw_delay = 1500;
    private long hover_delay = 2000;
    private long pitch_delay = 2000;
    private long throttle_delay = 1500;
    private long roll_delay = 1000;

    /** Custom Constructor **/
    public FlightDirection(double rate, int direction)
    {
        this.mission_control = DJISDKManager.getInstance().getMissionControl();
        this.mobile_remote_controller = DJISampleApplication.getAircraftInstance().getMobileRemoteController();
        this.rate = rate;
        this.direction = direction;
    }

    /*********************************************************************************************************
     * Name: run()
     * Purpose: This function serves as the entry point for the runnable class that extends the timelineevent.
     * This function is what determines which way to move the drone based on the direction value given.
     * Can Move -->  Forward, Backward, Left, Right, Up, down, Rotate Left, Rotate Right, Hover in Place.
     * Each Move above can be held for a determined amount of time delay. the longer the time delay the longer
     * the controls are held in that position, thus executing the manuver. When the time delay has expired
     * the controls are set back to neutral (0,0,0,0) for both horizontal and vertical stick X and Y values
     *********************************************************************************************************/
    @Override
    public void run()
    {
        mission_control.onStart(this);

        // Switch off of the button pressed
        switch (this.direction)
        {
            /** -------------------------------------------------------------------- **/
            /******************************** Fly Forward *****************************/
            /** -------------------------------------------------------------------- **/
            case 0:
                mobile_remote_controller.setRightStickVertical(this.pitch_forward_velocity);
                this.DirectionDelay = this.pitch_delay;
                break;

            /** -------------------------------------------------------------------- **/
            /******************************* Fly Backward *****************************/
            /** -------------------------------------------------------------------- **/
            case 1:
                mobile_remote_controller.setRightStickVertical(this.pitch_backward_velocity);
                this.DirectionDelay = this.pitch_delay;
                break;

            /** -------------------------------------------------------------------- **/
            /******************************** Slide Left ******************************/
            /** -------------------------------------------------------------------- **/
            case 2:
                mobile_remote_controller.setRightStickHorizontal(this.roll_left_velocity);
                this.DirectionDelay = this.roll_delay;
                break;

            /** -------------------------------------------------------------------- **/
            /******************************** Slide Right *****************************/
            /** -------------------------------------------------------------------- **/
            case 3:
                mobile_remote_controller.setRightStickHorizontal(this.roll_right_velocity);
                this.DirectionDelay = this.pitch_delay;
                break;

            /** -------------------------------------------------------------------- **/
            /******************************** Fly Down ********************************/
            /** -------------------------------------------------------------------- **/
            case 4:
                mobile_remote_controller.setLeftStickVertical(this.throttle_down_velocity);
                this.DirectionDelay = this.throttle_delay;
                break;

            /** -------------------------------------------------------------------- **/
            /********************************* Fly Up *********************************/
            /** -------------------------------------------------------------------- **/
            case 5:
                mobile_remote_controller.setLeftStickVertical(this.throttle_up_velocity);
                this.DirectionDelay = this.throttle_delay;
                break;

            /** -------------------------------------------------------------------- **/
            /******************************* Rotate Left ******************************/
            /** -------------------------------------------------------------------- **/
            case 6:
                this.DirectionDelay = this.yaw_delay;
                mobile_remote_controller.setLeftStickHorizontal(this.yaw_left_velocity);
                break;

            /** -------------------------------------------------------------------- **/
            /******************************* Rotate Right *****************************/
            /** -------------------------------------------------------------------- **/
            case 7:
                this.DirectionDelay = this.yaw_delay;
                mobile_remote_controller.setLeftStickHorizontal(this.yaw_right_velocity);
                break;

            /** -------------------------------------------------------------------- **/
            /****************************** Hover in Place ****************************/
            /** -------------------------------------------------------------------- **/
            case 8:
                this.DirectionDelay = this.hover_delay;
                break;

            // Default case currently does nothing
            default:
                break;
        }

        // Hold the flight controls in this position for the set delay
        SystemClock.sleep(this.DirectionDelay);
        // Set the controls to neutral, This will cause the brief hover in place
        FlightDirection.this.stop();

    }

    /*********************************************************************************************************
     * Name: stop()
     * Purpose: This function is the intermidiary command inbetween drone direction movements.
     * The drone currently moves in a grid like pattern traveling along the X and Y axis of the plane.
     * This is neccessary inbetween commands 
     *********************************************************************************************************/
    @Override
    public void stop()
    {
        mobile_remote_controller.setRightStickVertical(0.0f);
        mobile_remote_controller.setRightStickHorizontal(0.0f);
        mobile_remote_controller.setLeftStickVertical(0.0f);
        mobile_remote_controller.setLeftStickHorizontal(0.0f);
        mission_control.onFinishWithError(this, null);
    }

    @Override
    public void finishRun(@Nullable DJIError djiError) { }

    @Override
    public DJIError checkValidity() { return null; }

    @Override
    public boolean isPausable() { return false; }

}
