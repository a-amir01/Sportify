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

import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Created by amir on 4/17/16.
 * dummy class for now to generate the dummy sports
 */
public class SportLab {

    /*use for logging*/
    private static final String TAG = SportLab.class.getSimpleName();

    private static SportLab sSportLab;

    private SportLab(Context context){
        Log.i(TAG, "SportLab()");

        loadGatherings();

    }//end SportLab()

    public static SportLab get(Context context) {
        Log.i(TAG, "get()");

        /*if there are no sports*/
        if (sSportLab == null)
            sSportLab = new SportLab(context);
        return sSportLab;
    }//end get()

    public List<Gathering> getSports() { return App.mGatherings; }

    public Gathering getSport(String ID){
        Log.i(TAG, "getSport()");

        for(Gathering gathering : App.mGatherings) {
            /*crime.getId() == id (only true if they are same object
             * must use equals for different but identical objects */
            if (gathering.getID() == (ID))
                return gathering;
        }//end for

        /*sport not found*/
        return null;
    }//end getSport

    void loadGatherings() {
        Firebase gatheringRef = new Firebase("https://gspot.firebaseio.com/Gatherings");
        gatheringRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                App.mGatherings.removeAll(App.mGatherings);
                for (DataSnapshot gatheringSnapshot: dataSnapshot.getChildren()) {
                    Gathering gathering = new Gathering();
                    gathering = gatheringSnapshot.getValue (Gathering.class);
                    App.mGatherings.add(gathering);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "FireBaseError " + firebaseError.getMessage());

            }
        });
    }

}