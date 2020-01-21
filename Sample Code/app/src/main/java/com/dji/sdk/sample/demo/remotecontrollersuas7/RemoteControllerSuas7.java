/**
 * Controller for view 3
 * "Main screen"
 **/

// Package the controller belongs to
package com.dji.sdk.sample.demo.remotecontrollersuas7;

// Required imports
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.DialogUtils;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.VideoFeedView;
import com.dji.sdk.sample.internal.view.PresentableView;
import com.dji.sdk.sample.Mission.Mission;

import java.util.Locale;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.VisionSensorPosition;
import dji.common.util.CommonCallbacks;
import dji.sdk.battery.Battery;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.flightcontroller.Compass;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.timeline.actions.TakeOffAction;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.LiveStreamManager;


/*********************************************************************************************************
 * Name: RemoteControllerSuas7 Class
 * Purpose: This is the controller class for the main screen of the mobile application, view 3
 *********************************************************************************************************/
public class RemoteControllerSuas7 extends RelativeLayout
        implements View.OnClickListener,PresentableView
{
    // Button to make drone take off
    private Button btnTakeOff;
    // Button to make drone land
    private Button autoLand;

    private Button btnReturnHome;

    private boolean obstacleInFront;


    private TextView textViewLat;
    private TextView textViewLong;
    private TextView textViewAlt;
    private TextView textViewHeading;
    private TextView textViewBattery;

    private TextView textViewObstacleF;

    private Compass compass;

    // Mission to be flown
    private Mission mission;

    private VideoFeedView primaryVideoFeedView;
    private VideoFeedView fpvVideoFeedView;

    private LiveStreamManager.OnLiveChangeListener listener;
    private LiveStreamManager.LiveStreamVideoSource currentVideoSource = LiveStreamManager.LiveStreamVideoSource.Primary;






    /*********************************************************************************************************
     * Name: RemoteControllerSuas7()
     * Purpose: Class constructor
     * Input: Context ?
     * Returns: Nothing
     * Notes: TODO May need to add alot more here ...
     *********************************************************************************************************/
    public RemoteControllerSuas7(Context context)
    {
        super(context);
        init(context);
    }


    /*********************************************************************************************************
     * Name: getHint()
     * Purpose: Shows what java class this is in message window, top right
     * Input: None
     * Returns: Name of class to caller
     * Notes: Keep for now bc good example for messages later
     *********************************************************************************************************/
    @NonNull
    @Override
    public String getHint()
    {
        //return this.getClass().getSimpleName() + ".java";
        return "This is the control screen. Press [Start Mission] to execute flioght plan";
    }


    /*********************************************************************************************************
     * Name: onAttachedToWindow()
     * Purpose: When we transition to this page
     * Input: None
     * Returns: Nothing
     * Notes:
     *********************************************************************************************************/
    @Override
    protected void onAttachedToWindow()
    {
        // Super class constructor
        super.onAttachedToWindow();

        this.compass = ((Aircraft) DJISampleApplication.getProductInstance()).getFlightController().getCompass();

        if (ModuleVerificationUtil.isFlightControllerAvailable())
        {
            FlightController flightController =
                    ((Aircraft) DJISampleApplication.getProductInstance()).getFlightController();


            flightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(@NonNull FlightControllerState djiFlightControllerCurrentState) {
                    if (null != compass)
                    {
                        String s = String.format(Locale.ENGLISH, "HEADING :  %.4f", compass.getHeading());
                        changeDescriptionHeading(s);
                    }
                }
            });
            if (ModuleVerificationUtil.isCompassAvailable()) {
                compass = flightController.getCompass();
            }


            /** Obstacle detection **/
            FlightAssistant intelligentFlightAssistant = flightController.getFlightAssistant();

            if (intelligentFlightAssistant != null) {

                intelligentFlightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                    @Override
                    public void onUpdate(@NonNull VisionDetectionState visionDetectionState)
                    {
                        StringBuilder stringBuilder = new StringBuilder();

                        ObstacleDetectionSector[] visionDetectionSectorArray =
                                visionDetectionState.getDetectionSectors();

                        if(visionDetectionState.getPosition() == VisionSensorPosition.NOSE)
                        {
                            float distance_meters = visionDetectionSectorArray[2].getObstacleDistanceInMeters();

                            stringBuilder.append("FWD Obstacle distance: ")
                                    .append(visionDetectionSectorArray[2].getObstacleDistanceInMeters())
                                    .append("\n");

                            if(distance_meters < 0.60f)
                            {
                                stringBuilder.append("\n")
                                        .append("*WARNING OBSTACLE FRONT*")
                                        .append("\n")
                                        .append("Collision Avoidance Engaged")
                                        .append("\n");
                                obstacleInFront = true;
                            }
                            else
                            {
                                obstacleInFront = false;
                            }

                            changeDescriptionObstacleF(stringBuilder.toString());
                        }
                        /*
                        if(visionDetectionState.getPosition() == VisionSensorPosition.TAIL)
                        {
                            stringBuilder.append("AFT Obstacle distance: ")
                                    .append(visionDetectionSectorArray[1].getObstacleDistanceInMeters())
                                    .append("\n");

                            changeDescriptionObstacleA(stringBuilder.toString());
                        }
                        */


                    }
                });
            }
        }



        try
        {
            DJISampleApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
                @Override
                public void onUpdate(BatteryState djiBatteryState)
                {
                    String s = String.format(Locale.ENGLISH, "BATTERY : %d", djiBatteryState.getChargeRemainingInPercent());
                    changeDescriptionBattery(s);
                }
            });
        } catch (Exception ignored) {

        }



    }


    /*********************************************************************************************************
     * Name: onDetachedFromWindow()
     * Purpose: Clean up
     * Input: None
     * Returns: Nothing
     * Notes: TODO May need to add alot more here ...
     *********************************************************************************************************/
    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

    }


    /*********************************************************************************************************
     * Name: init()
     * Purpose: Intitialize the page view
     * Input: Context?
     * Returns: Nothing
     * Notes: Understand this more TODO
     *********************************************************************************************************/
    private void init(Context context)
    {
        mission = new Mission();
        setClickable(true);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_mobile_rc, this, true);
        initUI();

    }


    /*********************************************************************************************************
     * Name: initUI()
     * Purpose: Intitialize the GUI buttons, so that on action they run appropriate signal handler in switch
     * Input: None
     * Returns: Nothing
     * Notes:
     *********************************************************************************************************/
    private void initUI()
    {
        btnTakeOff = (Button) findViewById(R.id.btn_take_off);
        btnTakeOff.setOnClickListener(this);

        autoLand = (Button) findViewById(R.id.btn_auto_land);
        autoLand.setOnClickListener(this);

        btnReturnHome = (Button) findViewById(R.id.btn_return_home);
        btnTakeOff.setOnClickListener(this);

        textViewLat = (TextView) findViewById(R.id.textview_lat);
        textViewLong = (TextView) findViewById(R.id.textview_long);
        textViewAlt = (TextView) findViewById(R.id.textview_alt);
        textViewHeading = (TextView) findViewById(R.id.textview_heading);
        textViewBattery = (TextView) findViewById(R.id.textview_bat);
        textViewObstacleF = (TextView) findViewById(R.id.textview_obstacle_f);

        setClickable(true);

        primaryVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_primary_video_feed);
        primaryVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getPrimaryVideoFeed(), true);

        fpvVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_fpv_video_feed);
        fpvVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getSecondaryVideoFeed(), false);
        if (Helper.isMultiStreamPlatform())
        {
            fpvVideoFeedView.setVisibility(VISIBLE);
        }
    }


    /*********************************************************************************************************
     * Name: onClick()
     * Purpose: Listeners for the button press of Take-Off and Land
     * Input: Current view
     * Returns: Nothing
     * Notes: None
     *********************************************************************************************************/
    @Override
    public void onClick(View v)
    {
        // Switch off of the button pressed
        switch (v.getId())
        {
            // Take off button was pressed
            // Also considered the start mission.
            case R.id.btn_take_off:

                /**---------------------------------------**/
                /** Here execute the mission provided     **/
                /**---------------------------------------**/
                //mission.executeMission();

                mission.mission_control.unscheduleEverything();
                mission.mission_control.scheduleElement(new TakeOffAction());
                while(!mission.arrival())
                {
                    mission.navigationAlgorithm();
                    mission.mission_control.startTimeline();
                    while(mission.mission_control.isTimelineRunning())
                    {
                        if(obstacleInFront)
                        {
                            mission.mission_control.stopTimeline();
                            mission.mission_control.unscheduleEverything();
                            //Back up physically
                            //Update Grid Position
                            //Execute Avoidance
                        }
                    }
                }

                // Land button was pressed
            case R.id.btn_auto_land:
                break;

            case R.id.btn_return_home:
                break;

            // Default case currently does nothing
            default:
                break;
        }
    }


    /*********************************************************************************************************
     * Name: getDescription()
     * Purpose: I think this sets up the page layout
     * Input: None
     * Returns: ?
     * Notes: TODO Figure out what this does, something to do with the layout of the page
     *********************************************************************************************************/
    @Override
    public int getDescription()
    {
        return R.string.component_listview_SUAS7_mobile_remote_controller;

    }


    /*********************************************************************************************************
     * Name: takeoff Function
     * Purpose: Class is to call the flight controller take off function
     * Input: FlightController object
     * Returns: Nothing
     * Notes: None
     *********************************************************************************************************/
    public void takeOff(FlightController flightController)
    {
        flightController.startTakeoff(new CommonCallbacks.CompletionCallback()
        {
            @Override
            public void onResult(DJIError djiError)
            {
                DialogUtils.showDialogBasedOnError(getContext(), djiError);
            }
        });
    }


    /*********************************************************************************************************
     * Name: land Function
     * Purpose: Class is to call the flight controller auto land function
     * Input: FlightController object
     * Returns: Nothing
     * Notes: None
     *********************************************************************************************************/
    public void land(FlightController flightController)
    {
        flightController.startLanding(new CommonCallbacks.CompletionCallback()
        {
            @Override
            public void onResult(DJIError djiError)
            {
                DialogUtils.showDialogBasedOnError(getContext(), djiError);
            }
        });
    }

    protected void changeDescriptionHeading(final String newDescription)
    {
        post(new Runnable() {
            @Override
            public void run() {
                textViewHeading.setText(newDescription);
            }
        });
    }

    protected void changeDescriptionBattery(final String newDescription)
    {
        post(new Runnable() {
            @Override
            public void run() {
                textViewBattery.setText(newDescription);
            }
        });
    }

    protected void changeDescriptionObstacleF(final String newDescription)
    {
        post(new Runnable() {
            @Override
            public void run() {
                textViewObstacleF.setText(newDescription);
            }
        });
    }



}//Class

