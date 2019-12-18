/**
 * Class Location
 * Description : Class serves as the tuple pairing of the actual
 * grid coordinate (x,y)
 *
 * Author : Ryan Vacca
 **/

package com.dji.sdk.sample.Mission;

/** Class **/
public class Location
{
    /** Private class variables **/
    private int x;  // X coordinate
    private int y;  // Y coordinate

    /** Default Constructor **/
    public Location()
    {
        this.x = 0;
        this.y = 0;
    }

    /** Custom Constructor **/
    public Location(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /** Getter for X coordinate **/
    public int getX()
    {
        return this.x;
    }

    /** Getter for Y coordinate **/
    public int getY()
    {
        return this.y;
    }

    /** Setter for X coordinate **/
    public void setX(int x)
    {
        this.x = x;
    }

    /** Setter for Y coordinate **/
    public void setY(int y)
    {
        this.y = y;
    }
}
