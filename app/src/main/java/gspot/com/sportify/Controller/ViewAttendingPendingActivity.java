package gspot.com.sportify.Controller;

import android.os.Bundle;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;

/**
 * This file views the attending List of Pending List
 * Created by Aaron and Danny on 5/21/2016.
 */
public class ViewAttendingPendingActivity extends BaseNavBarActivity {
    private static final String TAG = ViewAttendingPendingActivity.class.getSimpleName();

    //Holds the Usersnames
    private ArrayList<String> userList = new ArrayList();

    //Holds the UserUID
    private ArrayList<String> userUID = new ArrayList();

    //Helps populate data for the page
    private String gatheringUID, cameFrom;

    //Helps create ListView
    private ArrayAdapter<String> adapter;

    //Attending or Pending View
    @Bind (R.id.view_status) EditText mStatus;

    //List View of people
    @Bind(R.id.list_of_pending_requests_or_attendees) ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pending_attending);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        //Grab information from previous intent
        gatheringUID = intent.getStringExtra("gatheringUID");
        cameFrom = intent.getStringExtra("cameFrom");

        //Adapter used to populate the List View
        adapter = new ArrayAdapter<String>(this, R.layout.the_user, userList);

        //If you came from attending change text attending
        if(cameFrom.equals("attending")) mStatus.setText("Attending");
        else mStatus.setText("Pending Requests"); //Else display Pending Requests

        //Don't allow modification of Status
        mStatus.setEnabled(false);

        //Load the UID into the array
        loadUserUID(gatheringUID);

        //Set up the adapter to display List View
        list.setAdapter(adapter);

        //Functionality for clicks
        registerClickCallBack();


        Log.d(TAG, "popularList size:" + userUID.size());

        Log.d(TAG, "popularList size after register call back:" + userUID.size());

    } //end onCreate()

    /*-----------------------------------------------
      This function takes a snapshot of the user and retrieves their name

     */
    void getHostname(String hostID) {
        Firebase profileRef = Profile.profileRef(hostID);

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name =  dataSnapshot.getValue(Profile.class).getmName();
                if (name == null ) { name = "Please don't crash"; } //Error Check

                //Add the name to the arrayList
                userList.add(name);
                Log.d(TAG, "NAME " + name);

                //Notify the adapter that the List View has changed
                //This will cause an error if you do not notify it
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    /*--------------------------------
     * This function Brings the user to the profile (attending)
     * or adds them to the attending list (pending)
     */
    private void registerClickCallBack()
    {
        if (list != null) {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                    //IF they came from attending
                    if(cameFrom.equals("attending")) {
                        //Brings them to the user's profile
                        Intent intent = new Intent(listView.getContext(), ProfileActivity.class);
                        String UID = userUID.get(position);

                        //Passs extra information to the next intent
                        intent.putExtra("viewingUser", UID);
                        intent.putExtra("cameFrom", "list");
                        Log.i(TAG, "UID" + UID);
                        listView.getContext().startActivity(intent);
                        //or create other intents here
                    }

                    else if(cameFrom.equals("pending"))
                    {
                        //Removes from pending and adds to attending
                        String UID = userUID.get(position);
                        App.mCurrentGathering.addPendingToAttending(UID);
                        App.mCurrentGathering.updateGathering(getApplicationContext());
                        userList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            Log.e("TAG", "LOOLOOOLLOLOLLOOLLOLLLLOOLL");
        }
    }

    /*
     * This function populates the ArrayList with user's UID reading it from the db
     */
    private void loadUserUID (String gatheringUID) {
        userUID.clear(); //Clears ArrayList

        Firebase attendingRef;

        //Came from Pending
        if(cameFrom.equals("pending")) {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("pendings");
        }

        //Came From attending
        else {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("attendees");
        }
        attendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear(); //Clear the ArrayList
                userUID.clear(); //clear the ArrayList
                Log.d(TAG, userUID.size() + " is the size of the list after none iteration");
                for (DataSnapshot attendeeSnapshot: dataSnapshot.getChildren()) {
                    String participant = attendeeSnapshot.getValue (String.class);
                    Log.e(TAG,"HHHHHHHHHH " + participant);

                    //Add User UID to the arrayList
                    userUID.add(participant);

                    //Add the UserName to the arrayList
                    getHostname(participant);
                    Log.d(TAG, userUID.size() + " is the size of the list after iteration");

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });

    }
}