package com.dji.sdk.sample.Mission;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dji.sdk.sample.Map.Grid;
import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.Triggerable;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.TimelineEvent;
import dji.sdk.mission.timeline.actions.LandAction;
import dji.sdk.mission.timeline.triggers.AircraftLandedTrigger;

import dji.sdk.mission.timeline.actions.TakeOffAction;
import dji.sdk.mission.timeline.triggers.TriggerEvent;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import com.dji.sdk.sample.FlightControls.FlightDirection;

public class Mission
{

    public MissionControl mission_control;
   // public ;
    private FlightController flight_controller;
    private FlightAssistant flight_assistant;

    private Grid grid;
    public MissionLoader mission_loader;

    public boolean obstacle;

    private int forward      = 0;
    private int backward     = 1;
    private int slide_Left   = 2;
    private int slide_right  = 3;
    private int down         = 4;
    private int up           = 5;
    private int rotate_left  = 6;
    private int rotate_right = 7;
    private int hover        = 8;

    private int north = 0;
    private int south = 1;
    private int west  = 2;
    private int east  = 3;

    // True = Left Avoidance / False = Right Avoidance
    private boolean avoidanceDirection = true;

    private void addObstacleAvoidanceTrigger(Triggerable triggerTarget) {
        float value = 20f;//distance threshold value
        //ObstacleAvoidanceTrigger Trigger = new ObstacleAvoidanceTrigger();
    }
    public Mission()
    {
        this.grid = new Grid(10,10);
        this.mission_loader = new MissionLoader();

        // Disconnect failsafe
        flight_controller = ModuleVerificationUtil.getFlightController();
        flight_controller.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.LANDING,
                null);

        mission_control = DJISDKManager.getInstance().getMissionControl();

        // Disable automatic avoidance systems, so they don't interfere with
        // our logic
        flight_assistant = flight_controller.getFlightAssistant();
        flight_assistant.setCollisionAvoidanceEnabled(false, null);
        flight_assistant.setActiveObstacleAvoidanceEnabled(false, null);
        flight_assistant.setAdvancedPilotAssistanceSystemEnabled(false, null);

        mission_control.addListener(new MissionControl.Listener()
        {
            @Override
            public void onEvent(@Nullable TimelineElement timelineElement, TimelineEvent timelineEvent, @Nullable DJIError djiError)
            {
                String element = (timelineElement != null) ? timelineElement.getClass().getSimpleName() : "null";
                Log.d("suas.timeline", String.format("Element: %s - Event: %s", element, timelineEvent.name()));
            }
        });
    }

    public void executeMission()
    {
        //test4();
        //test10();

        // Build the test queue to be scheduled to fly to target
        // TestPlans test_plan = new TestPlans(mission_control);
        //test_plan.test1();
        //test_plan.test2();
        //test_plan.test3();
        //test_plan.test4();
        //test_plan.test5();
        //test_plan.test6();
        //test_plan.test7();
        //test_plan.test8();
        //test_plan.testRelease2Demo();

        this.flyToTarget();
        //this.navigationAlgorithm();
        //this.betaNavigation();
        mission_control.scheduleElement(new LandAction());
        mission_control.startTimeline();
    }

    // 0=N,1=S,2=W,3=E
    public void betaNavigation()
    {
        //## DRONE PHASE FLAG : Ground  ##//

        // -------------------- Drone Take-Off -------------------- //
        mission_control.scheduleElement(new TakeOffAction());
        //mission_control.startTimeline();
        //waitForManuever();
        // -------------------------------------------------------- //
       // mission_control.addListener();
        //## DRONE PHASE FLAG : Hover  ##//

        facePositiveY();
        mission_control.scheduleElement(new FlightDirection(2, this.forward));
        mission_control.startTimeline();
        waitForManuever();
        mission_loader.current_location.setY(mission_loader.current_location.getY() + 1);
        //## DRONE PHASE FLAG : Hover & Facing POS Y  ##//

        // We have not reached the target Yet! Keep making moves
        while (!arrival())
        {
            // We detected an obstacle
            if(obstacle)
            {
                mission_control.scheduleElement(new LandAction());
                mission_control.startTimeline();
                waitForManuever();
               /* if(leftAvoidance())
                {

                }
                else
                {
                    rightAvoidance();
                }*/
            }
            else
            {
                facePositiveY();
                mission_control.scheduleElement(new FlightDirection(2, this.forward));
                mission_control.startTimeline();
                waitForManuever();
                mission_loader.current_location.setY(mission_loader.current_location.getY() + 1);
            }
        }
    }

    public void detourPath()
    {
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
        mission_control.startTimeline();
        waitForManuever();
    }





    public void navigationAlgorithm()
    {
        mission_control.unscheduleEverything();
        mission_control.scheduleElement(new TakeOffAction());

        while (!arrival())
        {
            /** Move Left on the grid **/ /** West : W **/
            if (this.mission_loader.current_location.getX() > this.mission_loader.end_location.getX())
            {
                if(this.mission_loader.facing != this.west)
                {
                    switch (this.mission_loader.facing)
                    {
                        //Facing North
                        case 0:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                        //Facing South
                        case 1:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                            break;
                        //Facing East
                        case 3:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            mission_control.scheduleElement(new FlightDirection(1, this.hover));
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                    }
                    this.mission_loader.facing = this.west;
                }
                mission_control.scheduleElement(new FlightDirection(2, this.forward));
                mission_loader.current_location.setX(mission_loader.current_location.getX() - 1);
            }
            /** Move Right on the grid **/ /** East : E **/
            else if(this.mission_loader.end_location.getX() > this.mission_loader.current_location.getX())
            {
                if(this.mission_loader.facing != this.east)
                {
                    switch (this.mission_loader.facing)
                    {
                        //Facing North
                        case 0:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                            break;
                        //Facing South
                        case 1:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                        //Facing West
                        case 3:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            mission_control.scheduleElement(new FlightDirection(1, this.hover));
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                    }
                    this.mission_loader.facing = this.east;
                }
                mission_control.scheduleElement(new FlightDirection(2, this.forward));
                mission_loader.current_location.setX(mission_loader.current_location.getX() + 1);
            }
            /** Move Up and Down on the grid **/ /** North : N and South : S **/
            else if(this.mission_loader.end_location.getX() == this.mission_loader.current_location.getX())
            {
                /** Move Up on the grid **/ /** North : N **/
                if (this.mission_loader.current_location.getY() < this.mission_loader.end_location.getY())
                {
                    if(this.mission_loader.facing != this.north)
                    {
                        switch (this.mission_loader.facing)
                        {
                            //Facing South
                            case 1:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                mission_control.scheduleElement(new FlightDirection(1, this.hover));
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                            //Facing West
                            case 2:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                                break;
                            //Facing East
                            case 3:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                        }
                        this.mission_loader.facing = this.north;
                    }
                    mission_control.scheduleElement(new FlightDirection(2, this.forward));
                    mission_loader.current_location.setY(mission_loader.current_location.getY() + 1);
                }
                /** Move Down on the grid **/ /** South : S **/
                else if(this.mission_loader.current_location.getY() > this.mission_loader.end_location.getY())
                {
                    if(this.mission_loader.facing != this.south)
                    {
                        switch (this.mission_loader.facing)
                        {
                            //Facing North
                            case 0:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                mission_control.scheduleElement(new FlightDirection(1, this.hover));
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                            //Facing West
                            case 2:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                            //Facing East
                            case 3:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                                break;
                        }
                        this.mission_loader.facing = this.south;
                    }
                    mission_control.scheduleElement(new FlightDirection(2, this.forward));
                    mission_loader.current_location.setY(mission_loader.current_location.getY() - 1);
                }


                //mission_control.startTimeline();
                //waitForManuever();

            }

            // After every Movement, Reset the controls
            mission_control.scheduleElement(new FlightDirection(1, this.hover));
            mission_control.startTimeline();
            waitForManuever();
        }

        //mission_control.scheduleElement(new LandAction());
        //mission_control.startTimeline();
    }




// 0=N,1=S,2=W,3=E
// Forward = 0 : Backward = 1 : Slide Left  = 2 : Slide Right  = 3
// Down    = 4 : UP       = 5 : Rotate Left = 6 : Rotate Right = 7
// Hover   = 8

    public void flyToTarget()
    {
        mission_control.unscheduleEverything();
        mission_control.scheduleElement(new TakeOffAction());

        while (!arrival())
        {
            /** Move Left on the grid **/ /** West : W **/
            if (this.mission_loader.current_location.getX() > this.mission_loader.end_location.getX())
            {
                if(this.mission_loader.facing != this.west)
                {
                    switch (this.mission_loader.facing)
                    {
                        //Facing North
                        case 0:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                        //Facing South
                        case 1:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                            break;
                        //Facing East
                        case 3:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            mission_control.scheduleElement(new FlightDirection(1, this.hover));
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                    }
                    this.mission_loader.facing = this.west;
                }
                mission_control.scheduleElement(new FlightDirection(2, this.forward));
                mission_loader.current_location.setX(mission_loader.current_location.getX() - 1);
            }
            /** Move Right on the grid **/ /** East : E **/
            else if(this.mission_loader.end_location.getX() > this.mission_loader.current_location.getX())
            {
                if(this.mission_loader.facing != this.east)
                {
                    switch (this.mission_loader.facing)
                    {
                        //Facing North
                        case 0:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                            break;
                        //Facing South
                        case 1:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                        //Facing West
                        case 3:
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            mission_control.scheduleElement(new FlightDirection(1, this.hover));
                            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                            break;
                    }
                    this.mission_loader.facing = this.east;
                }
                mission_control.scheduleElement(new FlightDirection(2, this.forward));
                mission_loader.current_location.setX(mission_loader.current_location.getX() + 1);
            }
            /** Move Up and Down on the grid **/ /** North : N and South : S **/
            else if(this.mission_loader.end_location.getX() == this.mission_loader.current_location.getX())
            {
                /** Move Up on the grid **/ /** North : N **/
                if (this.mission_loader.current_location.getY() < this.mission_loader.end_location.getY())
                {
                    if(this.mission_loader.facing != this.north)
                    {
                        switch (this.mission_loader.facing)
                        {
                            //Facing South
                            case 1:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                mission_control.scheduleElement(new FlightDirection(1, this.hover));
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                            //Facing West
                            case 2:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                                break;
                            //Facing East
                            case 3:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                        }
                        this.mission_loader.facing = this.north;
                    }
                    mission_control.scheduleElement(new FlightDirection(2, this.forward));
                    mission_loader.current_location.setY(mission_loader.current_location.getY() + 1);
                }
                /** Move Down on the grid **/ /** South : S **/
                else if(this.mission_loader.current_location.getY() > this.mission_loader.end_location.getY())
                {
                    if(this.mission_loader.facing != this.south)
                    {
                        switch (this.mission_loader.facing)
                        {
                            //Facing North
                            case 0:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                mission_control.scheduleElement(new FlightDirection(1, this.hover));
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                            //Facing West
                            case 2:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
                                break;
                            //Facing East
                            case 3:
                                mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
                                break;
                        }
                        this.mission_loader.facing = this.south;
                    }
                    mission_control.scheduleElement(new FlightDirection(2, this.forward));
                    mission_loader.current_location.setY(mission_loader.current_location.getY() - 1);
                }

            }

            mission_control.scheduleElement(new FlightDirection(1, this.hover));
        }

        mission_control.scheduleElement(new LandAction());
        mission_control.startTimeline();
    }

    public void waitForManuever()
    {
        //Wait for manuever to finish
        while(!mission_control.isTimelineRunning())
        {

        }
    }

    public void facePositiveY()
    {
        // ----------------- Face The long axis of the grid for takeoff ---------------- //
        // Turn Facing the positive Y axis of the grid.
        // Condition Facing Negative Y, Turn to Positive Y
        if(mission_loader.facing == 1)
        {
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
            mission_control.startTimeline();
            waitForManuever();
            mission_control.scheduleElement(new FlightDirection(1, this.hover));
            mission_control.startTimeline();
            waitForManuever();
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
            mission_control.startTimeline();
            waitForManuever();
        }
        // Condition Facing Negative X, Turn to Positive Y
        if(mission_loader.facing == 2)
        {
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
            mission_control.startTimeline();
            waitForManuever();
        }
        // Condition Facing Negative X, Turn to Positive Y
        if(mission_loader.facing == 3)
        {
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
            mission_control.startTimeline();
            waitForManuever();
        }
        mission_loader.facing = 0;
        // ----------------------------------------------------------------------------- //
    }

    public boolean arrival()
    {
        if(this.mission_loader.end_location.getX() == this.mission_loader.current_location.getX() &&
                this.mission_loader.end_location.getY() == this.mission_loader.current_location.getY())
        {
            return true;
        }
        return false;
    }
    public void Avoidance()
    {
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(2, this.forward));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(2, this.forward));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(2, this.forward));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(2, this.forward));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        mission_control.scheduleElement(new FlightDirection(2, this.forward));
        mission_control.scheduleElement(new FlightDirection(1, this.hover));
        //update grid position
        //call navigation algorithm again
        //this.flyToTarget();
        //possibly exclude the last two lines
        mission_control.scheduleElement(new LandAction());
        mission_control.startTimeline();

    }
    public boolean leftAvoidance()
    {
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
        mission_control.startTimeline();
        waitForManuever();
        updateGridFacing(false); // false = left, true = right


        if(obstacle)
        {
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
            mission_control.startTimeline();
            waitForManuever();
            updateGridFacing(true); // false = left, true = right
            return false;
        }

        if(checkBoundary())
        {
            mission_control.scheduleElement(new FlightDirection(2, this.forward));
            mission_loader.current_location.setX(mission_loader.current_location.getX() - 1);

            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
            mission_control.startTimeline();
            waitForManuever();
            updateGridFacing(true); // false = left, true = right
        }
        else
        {
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
            mission_control.startTimeline();
            waitForManuever();
            updateGridFacing(true); // false = left, true = right
            return false;
        }

        return true;
    }


    // -------------------------------------------------------------------------------------- //
    // Function: rightAvoidance()
    //
    // Description: This function performs the right avoidance flight maneuver
    //
    // Input:   Nothing
    // Returns: True if maneuver succeeded, False if maneuver failed
    // -------------------------------------------------------------------------------------- //
    public boolean rightAvoidance()
    {
        // -------------- ROTATION RIGHT -------------------------------------------- //
        mission_control.scheduleElement(new FlightDirection(1, this.rotate_right));
        mission_control.startTimeline();
        waitForManuever();
        updateGridFacing(true); // false = left, true = right

        // Check if we are now facing an obstacle
        if(obstacle)
        {
            // -------------- ROTATION LEFT -------------------------------------------- //
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
            mission_control.startTimeline();
            waitForManuever();
            updateGridFacing(false); // false = left, true = right

            // Avoidance maneuver failed : New obstacle detected
            return false;
        }

        // Ensure we are not about to cross a boundary
        if(checkBoundary())
        {
            // -------------- FLY FORWARD ------------------------------------------ //
            mission_control.scheduleElement(new FlightDirection(2, this.forward));
            mission_loader.current_location.setX(mission_loader.current_location.getX() + 1);

            // -------------- ROTATION LEFT -------------------------------------------- //
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
            mission_control.startTimeline();
            waitForManuever();
            updateGridFacing(false); // false = left, true = right

        }
        // Boundary crossing would occur if moved fwd
        else
        {
            // Reset to direction when
            // -------------- ROTATION LEFT -------------------------------------------- //
            mission_control.scheduleElement(new FlightDirection(1, this.rotate_left));
            mission_control.startTimeline();
            waitForManuever();
            updateGridFacing(false); // false = left, true = right

            // Avoidance maneuver failed : Grid Boundary Violation
            return false;
        }

        // Avoidance maneuver success
        return true;
    }
    // -------------------------------------------------------------------------------------- //


    // Left = false , Right = true
    public void updateGridFacing(boolean leftRight)
    {
        if(leftRight)
        {
            switch (this.mission_loader.facing)
            {
                // Facing North
                case 0:
                    this.mission_loader.facing = 2;
                    break;
                // Facing South
                case 1:
                    this.mission_loader.facing = 3;
                    break;
                // Facing West
                case 2:
                    this.mission_loader.facing = 1;
                    break;
                // Facing East
                case 3:
                    this.mission_loader.facing = 0;
                    break;

            }
        }
        if(!leftRight)
        {
            switch (this.mission_loader.facing)
            {
                // Facing North
                case 0:
                    this.mission_loader.facing = 3;
                    break;
                // Facing South
                case 1:
                    this.mission_loader.facing = 2;
                    break;
                // Facing West
                case 2:
                    this.mission_loader.facing = 0;
                    break;
                // Facing East
                case 3:
                    this.mission_loader.facing = 1;
                    break;

            }
        }
    }


    // 0=N,1=S,2=W,3=E
    public boolean checkBoundary()
    {
        if(mission_loader.facing == 0)
        {
            if(this.mission_loader.current_location.getY() == this.mission_loader.grid_length_y)
            {
                return false;
            }
        }
        if(mission_loader.facing == 1)
        {
            if(this.mission_loader.current_location.getY() == 0)
            {
                return false;
            }
        }
        if(mission_loader.facing == 2)
        {
            if(this.mission_loader.current_location.getX() == 0)
            {
                return false;
            }
        }
        if(mission_loader.facing == 3)
        {
            if(this.mission_loader.current_location.getX() == this.mission_loader.grid_length_x)
            {
                return false;
            }
        }
        return true;
    }

    public void emergencyLand()
    {
        mission_control.stopTimeline();
        mission_control.unscheduleEverything();
        mission_control.scheduleElement(new LandAction());
        mission_control.startTimeline();
    }

}


