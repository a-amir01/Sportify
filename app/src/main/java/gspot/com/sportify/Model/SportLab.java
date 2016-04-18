package gspot.com.sportify.Model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by amir on 4/17/16.
 * dummy class for now to generate the dummy sports
 */
public class SportLab {

    /*use for logging*/
    private static final String TAG = SportLab.class.getSimpleName();

    private static SportLab sSportLab;
    private List<Sport> mSports;

    private SportLab(Context context){
        Log.i(TAG, "SportLab()");

        /*hold an array of test sports*/
        mSports = new ArrayList<>();

        /*create a list of fake sports*/
        for (int i = 0; i < 50; i++){
            Sport sport = new Sport();
            sport.setSportName("Sport # " + i);
            mSports.add(sport);
        }//end for
    }//end SportLab()

    public static SportLab get(Context context) {
        Log.i(TAG, "get()");

        /*if there are no sports*/
        if (sSportLab == null)
            sSportLab = new SportLab(context);
        return sSportLab;
    }//end get()

    public List<Sport> getSports() { return mSports; }

    public Sport getSport(UUID id){
        Log.i(TAG, "getSport()");

        for(Sport sport : mSports) {
            /*crime.getId() == id (only true if they are same object
             * must use equals for different but identical objects */
            if (sport.getId().equals(id))
                return sport;
        }//end for

        /*sport not found*/
        return null;
    }//end getSport


}//end SportLab
