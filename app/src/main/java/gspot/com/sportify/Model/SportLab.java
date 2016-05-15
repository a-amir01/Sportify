package gspot.com.sportify.Model;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gspot.com.sportify.utils.Constants;

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
      /* for (int i = 0; i < 50; i++){
            Gathering gathering = new Gathering();
            gathering.setSportTitle("Gathering # " + i);
            mGatherings.add(gathering);
        }//end for */
        Firebase gatheringRef = new Firebase("https://gspot.firebaseio.com/Events");


        gatheringRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot gatheringSnapshot: dataSnapshot.getChildren()) {
                    Gathering gathering = new Gathering();
                    gathering = gatheringSnapshot.getValue (Gathering.class);
                    mGatherings.add(gathering);
                    //System.out.println(gathering.getSportTitle());
                }

                //  Gathering gathering = new Gathering();
                // gathering = dataSnapshot.getValue(Gathering.class);
//
                // mGatherings.add(gathering);

            }



            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "FireBaseError " + firebaseError.getMessage());

            }
        });







    }//end SportLab()

    public static SportLab get(Context context) {
        Log.i(TAG, "get()");

        /*if there are no sports*/
        if (sSportLab == null)
            sSportLab = new SportLab(context);
        return sSportLab;
    }//end get()

    public List<Gathering> getSports() { return mGatherings; }

    public Gathering getSport(String ID){
        Log.i(TAG, "getSport()");

        for(Gathering gathering : mGatherings) {
            /*crime.getId() == id (only true if they are same object
             * must use equals for different but identical objects */
            if (gathering.getID() == (ID))
                return gathering;
        }//end for

        /*sport not found*/
        return null;
    }//end getSport


}