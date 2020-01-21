/**
 * Class MissionLoader
 * Description : Class serves to load the JSON file from the SD card,
 * parse the file, error check the file, report errors
 *
 * Author : Ryan Vacca
 **/

// TODO Drew:

// Notes and suggestions
// TODO Need to report errors
// 1) No Sim card detected
// 2) Cant read Sim card

package com.dji.sdk.sample.Mission;

import android.os.Environment;
import android.widget.TextView;

import com.dji.sdk.sample.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MissionLoader
{
    public Location start_location;
    public Location end_location;
    public Location current_location;

    public int facing;

    public int grid_length_x;
    public int grid_length_y;

    public MissionLoader()
    {
        this.start_location = new Location();
        this.end_location = new Location();
        this.current_location = new Location();
        this.LoadFile();
        this.errorCheckFile();
    }

    public void LoadFile()
    {
        /** JSON FILE **/
        // Hardcoded right now below// TODO Drew needs to impliment sim card reader
        /* ******************************************************************* */
        /* ******************************************************************* */
        int startX = 2;
        int startY = 1;
        int endX = 2;
        int endY = 3;
        int grid_length_x = 10;
        int grid_length_y = 10;
        int facing = 0;           // 0=N,1=S,2=W,3=E
        /* ******************************************************************* */
        /* ******************************************************************* */

        // TODO Drew
        // Set these below private class member values here! once you have
        // figured out how to get them from JSON file on SD card
        this.start_location.setX(startX);
        this.start_location.setY(startY);
        this.end_location.setX(endX);
        this.end_location.setY(endY);
        this.current_location.setX(startX);
        this.current_location.setY(startY);
        this.facing = facing;
        this.grid_length_x = grid_length_x;
        this.grid_length_y = grid_length_y;
    }

    public boolean errorCheckFile()
    {

        // Invalid Json File Content Error 1 :
        // Check for grid size being to small
        if(this.grid_length_x < 2 || this.grid_length_y < 2)
        {
            return false;
        }

        // Invalid Json File Content Error 2 :
        // Check for values out of range
        if(this.start_location.getX() > this.grid_length_x ||
           this.start_location.getY() > this.grid_length_y ||
           this.end_location.getX()   > this.grid_length_x ||
           this.end_location.getY()   > this.grid_length_y)
        {
            return false;
        }

        // Invalid Json File Content Error 3 :
        // Check for start location is same as end location
        if(this.start_location.getX() == this.end_location.getX() &&
           this.start_location.getY() == this.end_location.getY())
        {
            return false;
        }

        // Return no errors
        return true;
    }

    public void readSDCard()
    {
        //Find the directory for the SD Card
        File sdcard = Environment.getExternalStorageDirectory();

        //Currently reading from text file
        //Change to JSON File format
        //Get the JSON file
        File file = new File(sdcard,"mission.txt");

        //Read text from file
        StringBuilder text;
        text = new StringBuilder();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
            {
                /*** In here read file into class member variables ***/
                text.append(line);
                text.append('\n');
            }
            br.close();

            /*** You can use this spot to print to the phone the content of the json file to
                 test if we can read from the SD card or not ***/
        }
        catch (IOException e)
        {
            //Add error checking for file not found ..etc
        }
    }
}
