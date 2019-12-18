/**
 * Class Grid
 * Description : Class serves as the map of the
 * 2-D maze the drone will navigate through.
 * A 2D array will represent the grid coordinate
 * system (x,y). Each index of the array will store
 * a coordinate object with information regarding this
 * specific grid coordinate on the map.
 *
 * Author : Ryan Vacca
 **/

package com.dji.sdk.sample.Map;

/** Class **/
public class Grid
{
    /** Public class variables **/
    public int dim_x;                       // Dimension of the x axis
    public int dim_y;                       // Dimension of the y axis
    public Coordinate[][] grid_coordinates; // 2D Array composed of coordinate objects

    /** Custom Constructor **/
    public Grid(int x, int y)
    {
        this.dim_x = x;
        this.dim_y = y;
        grid_coordinates = new Coordinate[this.dim_x][this.dim_y]; // Allocate memory for map
        populateGrid();
    }

    /**************************************************
     * Function : populateGrid()
     * Description : Place the correct coordinate (x,y)
     * at the appropriate index in the 2D array
     * ************************************************/
    public void populateGrid()
    {
        for(int i = 0; i < this.dim_x; i++)
        {
            for(int j = 0; j < this.dim_y; j++)
            {
                Coordinate coordinate = new Coordinate(i,j,0);
                grid_coordinates[i][j] = coordinate;
            }
        }
    }
}
