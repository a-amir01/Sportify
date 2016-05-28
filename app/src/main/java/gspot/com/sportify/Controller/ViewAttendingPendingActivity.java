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
    private String gatheringUID, cameFrom;
    private ArrayAdapter<String> adapter;
    private Profile mProfile;
    @Bind (R.id.view_status) EditText mStatus;
    @Bind(R.id.list_of_pending_requests_or_attendees) ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pending_attending);
        ButterKnife.bind(this);

        //Get information from previous intent
        Intent intent = getIntent();
        gatheringUID = intent.getStringExtra("gatheringUID");
        cameFrom = intent.getStringExtra("cameFrom");

        //Adapter to display List View
        adapter = new ArrayAdapter<String>(this, R.layout.the_user, userList);

        //Shows previous intent page
        if(cameFrom.equals("attending")) mStatus.setText("Attending");
        else mStatus.setText("Pending Requests");

        mStatus.setEnabled(false);

        //Load user's UID
        loadUserUID(gatheringUID);

        //Display List view and allow clicks
        list.setAdapter(adapter);
        registerClickCallBack();


        Log.d(TAG, "popularList size:" + userUID.size());

        Log.d(TAG, "popularList size after register call back:" + userUID.size());

    } //end onCreate()


    //Gets the host name by looking through the database
    void getHostname(String hostID) {
        Firebase profileRef = Profile.profileRef(hostID);

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name =  dataSnapshot.getValue(Profile.class).getmName();
                if (name == null ) { name = "Please don't crash"; }
                userList.add(name);
                Log.d(TAG, "NAME " + name);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    //Allows clicks to bring the user to the profile or
    //place them in the attending list
    private void registerClickCallBack()
    {
        if (list != null) {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                    //This will show who's attending list and
                    //allow clicks to bring user to clicked profile
                    if(cameFrom.equals("attending")) {
                        Intent intent = new Intent(listView.getContext(), ProfileActivity.class);
                        String UID = userUID.get(position);
                        intent.putExtra("viewingUser", UID);
                        intent.putExtra("cameFrom", "list");
                        Log.i(TAG, "UID" + UID);
                        listView.getContext().startActivity(intent);
                        //or create other intents here
                    }

                    //This shows the pending list and
                    // allow clicks to put them on the attending list
                    // as well as take them off the pending list
                    else if(cameFrom.equals("pending"))
                    {
                        String UID = userUID.get(position);
                        App.mCurrentGathering.addPendingToAttending(UID);
                        App.mCurrentGathering.updatePending(getApplicationContext());
                        userList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            Log.e("TAG", "LOOLOOOLLOLOLLOOLLOLLLLOOLL");
        }
    }


    //Load users UID into the ArrayList
    private void loadUserUID (String gatheringUID) {
        userUID.clear();

        Firebase attendingRef;

        //Pending Array List
        if(cameFrom.equals("pending")) {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("pendings");
        }

        //Attending Array List
        else {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("attendees");
        }
        attendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear(); // clears list
                userUID.clear(); // clears list
                Log.d(TAG, userUID.size() + " is the size of the list after none iteration");

                //Populates array list one with UID and the other with host names
                for (DataSnapshot attendeeSnapshot: dataSnapshot.getChildren()) {
                    String participant = attendeeSnapshot.getValue (String.class);
                    Log.e(TAG,"HHHHHHHHHH " + participant);
                    userUID.add(participant);
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