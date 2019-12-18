/**
 * Class Coordinate
 * Description : Class serves as the actual grid coordinate location, but
 * with extra information about the specific grid coordinate (x,y). This class
 * also holds information in regard to discovered obstacles as the drone naviagtes
 *
 * Author : Ryan Vacca
 **/

package com.dji.sdk.sample.Map;

/** Class **/
public class Coordinate
{
    /** Public class variables **/
    public boolean visited;
    public int lastMove;
    public boolean start;
    public boolean end;

    public int x;
    public int y;
    public int z;

    public boolean north_obstacle;
    public boolean south_obstacle;
    public boolean west_obstacle;
    public boolean east_obstacle;


    /** Custom Constructor **/
    public Coordinate(int x, int y, int z)
    {
        this.visited = false;
        this.lastMove = 0;
        this.start = false;
        this.end = false;

        this.x = x;
        this.y = y;
        this.z = z;

        this.north_obstacle = false;
        this.south_obstacle = false;
        this.east_obstacle = false;
        this.west_obstacle = false;
    }


}
