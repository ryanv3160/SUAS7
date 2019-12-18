package com.dji.sdk.sample.Mission;

import com.dji.sdk.sample.FlightControls.FlightDirection;

import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.actions.LandAction;
import dji.sdk.mission.timeline.actions.TakeOffAction;

public class TestPlans
{
    public MissionControl scheduler;

    public TestPlans(MissionControl mission_control)
    {
        this.scheduler = mission_control;
    }

    /*********************************************************************************************************
     * ------------------------------------------ T E S T 1 ------------------------------------------------ *
     * Description : Take off and Land Test
     * Take off , hover 2 seconds, land
     * Status : PASS
     *********************************************************************************************************/
    public void test1()
    {
        this.scheduler.unscheduleEverything();
        this.scheduler.scheduleElement(new TakeOffAction());
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new LandAction());
        this.scheduler.startTimeline();
    }

    /*********************************************************************************************************
     * ------------------------------------------ T E S T 2 ------------------------------------------------ *
     * Description : Turn left 90 degree test
     * Take off , hover 2 seconds, turn left 90 degree, hover 2 seconds, land
     * Status : PASS
     *********************************************************************************************************/
    public void test2()
    {
        this.scheduler.unscheduleEverything();
        this.scheduler.scheduleElement(new TakeOffAction());
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new FlightDirection(1, 6));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new LandAction());
        this.scheduler.startTimeline();
    }


    /*********************************************************************************************************
     * ------------------------------------------ T E S T 3 ------------------------------------------------ *
     * Description : Turn right 90 degree test
     * Take off , hover 2 seconds, turn right 90 degree, hover 2 seconds, land
     * Status : PASS
     *********************************************************************************************************/
    public void test3()
    {
        this.scheduler.unscheduleEverything();
        this.scheduler.scheduleElement(new TakeOffAction());
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new FlightDirection(1, 7));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new LandAction());
        this.scheduler.startTimeline();
    }

    /*********************************************************************************************************
     * ------------------------------------------ T E S T 4 ------------------------------------------------ *
     * Description : Turn left 90 degree then right 90 degree test
     * Take off , hover 2 seconds, turn left 90 degree, hover 2 seconds, turn right 90 degree, hover 2 seconds, land
     * Status : PASS
     *********************************************************************************************************/
    public void test4()
    {
        this.scheduler.unscheduleEverything();
        this.scheduler.scheduleElement(new TakeOffAction());
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new FlightDirection(1, 7));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new LandAction());
        this.scheduler.startTimeline();
    }

    /*********************************************************************************************************
     * ------------------------------------------ T E S T 7 ------------------------------------------------ *
     * Description : Multiple coupled turns Test
     * Take off , hover 2 seconds, turn left 90, hover 2 seconds, turn right 90, hover 2 seconds,
     * turn right 90, hover 2 seconds, turn left 90, hover 2 seconds, land
     *********************************************************************************************************/
    public void test7()
    {
        this.scheduler.unscheduleEverything();
        this.scheduler.scheduleElement(new TakeOffAction());
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        this.scheduler.scheduleElement(new FlightDirection(1, 6));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        this.scheduler.scheduleElement(new FlightDirection(1, 7));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        this.scheduler.scheduleElement(new FlightDirection(1, 7));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        this.scheduler.scheduleElement(new FlightDirection(1, 6));

        this.scheduler.scheduleElement(new FlightDirection(1, 8));
        this.scheduler.scheduleElement(new LandAction());
        this.scheduler.startTimeline();
    }

    public void testRelease2Demo()
    {
        this.scheduler.unscheduleEverything();
        this.scheduler.scheduleElement(new TakeOffAction());
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //FWD
        this.scheduler.scheduleElement(new FlightDirection(1, 0));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //LFT TRN
        this.scheduler.scheduleElement(new FlightDirection(1, 6));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //FWD
        this.scheduler.scheduleElement(new FlightDirection(1, 0));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //RHT TRN
        this.scheduler.scheduleElement(new FlightDirection(1, 7));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //FWD
        this.scheduler.scheduleElement(new FlightDirection(1, 0));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //RHT TRN
        this.scheduler.scheduleElement(new FlightDirection(1, 7));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //FWD
        this.scheduler.scheduleElement(new FlightDirection(1, 0));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //LFT TRN
        this.scheduler.scheduleElement(new FlightDirection(1, 6));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        //FWD
        this.scheduler.scheduleElement(new FlightDirection(1, 0));
        this.scheduler.scheduleElement(new FlightDirection(1, 8));

        this.scheduler.scheduleElement(new LandAction());
        this.scheduler.startTimeline();
    }

}
