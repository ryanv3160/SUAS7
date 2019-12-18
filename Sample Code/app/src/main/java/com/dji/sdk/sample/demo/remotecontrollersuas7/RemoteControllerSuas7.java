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
import com.dji.sdk.sample.internal.utils.DialogUtils;
import com.dji.sdk.sample.internal.view.PresentableView;
import com.dji.sdk.sample.Mission.Mission;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;




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

    // For pop-up window
    private TextView textView;

    // Mission to be flown
    private Mission mission;



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
        return this.getClass().getSimpleName() + ".java";
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
     * Notes: 2 buttons and message window
     *********************************************************************************************************/
    private void initUI()
    {
        btnTakeOff = (Button) findViewById(R.id.btn_take_off);
        btnTakeOff.setOnClickListener(this);
        autoLand = (Button) findViewById(R.id.btn_auto_land);
        autoLand.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textview_simulator);
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
                mission.executeMission();

                // Land button was pressed
            case R.id.btn_auto_land:
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
        return R.string.component_listview_mobile_remote_controller;
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


}//Class

