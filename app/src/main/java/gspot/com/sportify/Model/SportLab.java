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
    private List<Gathering> mGatherings;

    private SportLab(Context context){
        Log.i(TAG, "SportLab()");

        /*hold an array of test sports*/
        mGatherings = new ArrayList<>();

        /*create a list of fake sports*/
        for (int i = 0; i < 50; i++){
            Gathering gathering = new Gathering();
            gathering.setSportName("Gathering # " + i);
            mGatherings.add(gathering);
        }//end for
    }//end SportLab()

    public static SportLab get(Context context) {
        Log.i(TAG, "get()");

        /*if there are no sports*/
        if (sSportLab == null)
            sSportLab = new SportLab(context);
        return sSportLab;
    }//end get()

    public List<Gathering> getSports() { return mGatherings; }

    public Gathering getSport(UUID id){
        Log.i(TAG, "getSport()");

        for(Gathering gathering : mGatherings) {
            /*crime.getId() == id (only true if they are same object
             * must use equals for different but identical objects */
            if (gathering.getId().equals(id))
                return gathering;
        }//end for

        /*sport not found*/
        return null;
    }//end getSport


}//end SportLab
