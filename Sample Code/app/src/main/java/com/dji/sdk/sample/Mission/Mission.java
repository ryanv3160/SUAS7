package com.dji.sdk.sample.Mission;

import android.util.Log;

import androidx.annotation.Nullable;

import com.dji.sdk.sample.Map.Grid;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.TimelineEvent;
import dji.sdk.mission.timeline.actions.LandAction;
import dji.sdk.mission.timeline.actions.TakeOffAction;
import dji.sdk.sdkmanager.DJISDKManager;
import com.dji.sdk.sample.FlightControls.FlightDirection;

public class Mission
{

        private MissionControl mission_control;
        private FlightController flight_controller;
        private FlightAssistant flight_assistant;

        private Grid grid;
        private MissionLoader mission_loader;

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
             //TestPlans test_plan = new TestPlans(mission_control);
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

        public boolean arrival()
        {
            if(this.mission_loader.end_location.getX() == this.mission_loader.current_location.getX() &&
               this.mission_loader.end_location.getY() == this.mission_loader.current_location.getY())
            {
                return true;
            }
            return false;
        }


        private void forceStopMissionTimeline()
        {
            TimelineElement running = mission_control.getRunningElement();
            mission_control.stopTimeline();
            if (running != null) { running.stop(); }
            mission_control.unscheduleEverything();
        }

        // Clear MissionControl and call the FlightController directly for these -
        // they mean the user wants flight to stop immediately
        public void autoLand()
        {
            forceStopMissionTimeline();
            flight_controller.startLanding(null);
        }

        public void confirmLand()
        {
            forceStopMissionTimeline();
            flight_controller.confirmLanding(null);
        }

}























/*

        public void test1()
        {
            mission_control.unscheduleEverything();
            mission_control.scheduleElement(new TakeOffAction());
            mission_control.scheduleElement(new AircraftYawAction(-90.0f, false));
            mission_control.scheduleElement(new FlightDirection(2, 0));
            mission_control.scheduleElement(new AircraftYawAction(90.0f, false));
            mission_control.scheduleElement(new FlightDirection(2, 0 ));
            mission_control.scheduleElement(new AircraftYawAction(90.0f, false));
            mission_control.scheduleElement(new FlightDirection(2, 0 ));
            mission_control.scheduleElement(new AircraftYawAction(90.0f, false));
            mission_control.scheduleElement(new FlightDirection(2,  0));
            mission_control.scheduleElement(new LandAction());
            mission_control.startTimeline();
        }

*/


        /*
        public void test4()
        {

            mission_control.unscheduleEverything();
            mission_control.scheduleElement(new TakeOffAction());
            mission_control.scheduleElement(new FlightDirection(1, 8));

            mission_control.scheduleElement(new FlightDirection(1, 6));
            mission_control.scheduleElement(new FlightDirection(1, 8));

            mission_control.scheduleElement(new FlightDirection(1, 7));
            mission_control.scheduleElement(new FlightDirection(1, 8));

            mission_control.scheduleElement(new FlightDirection(1, 7));
            mission_control.scheduleElement(new FlightDirection(1, 8));

            mission_control.scheduleElement(new FlightDirection(1, 6));

            mission_control.scheduleElement(new FlightDirection(1, 8));
            mission_control.scheduleElement(new LandAction());
            mission_control.startTimeline();
        }*/