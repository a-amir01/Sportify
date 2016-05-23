package gspot.com.sportify.Controller;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

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
    ArrayAdapter<String> adapter;
    private Profile mProfile;
    private ListView list;
    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //String currentUID = prefs.getString(Constants.KEY_UID, "");
    @Bind (R.id.view_status) EditText mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attending_pending);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        gatheringUID = intent.getStringExtra("gatheringUID");
        cameFrom = intent.getStringExtra("cameFrom");

        adapter = new ArrayAdapter<String>(this, R.layout.the_user, userList);

        //mStatus.setText("Attending");
        if(cameFrom.equals("attending")) mStatus.setText("Attending");
        else mStatus.setText("Pending Requests");

        mStatus.setEnabled(false);

        loadUserUID(gatheringUID);
        populateListView(list);
        list.requestLayout();
        registerClickCallBack();


        Log.d(TAG, "popularList size:" + userUID.size());

        Log.d(TAG, "popularList size after register call back:" + userUID.size());

    } //end onCreate()

    private void populateListView (ListView list){

        Log.d(TAG, "size of userList before "+ userList.size());

        Log.d(TAG, "size of userList after "+ userList.size());
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        list.requestLayout();

    }

    void getHostname(String hostID) {
        // Firebase profileRef = new Firebase(Constants.FIREBASE_URL_PROFILES).child(hostID).child("mName");
        Firebase profileRef = App.dbref.child("profiles").child(hostID).child("mName");

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name =  dataSnapshot.getValue(String.class);
                userList.add(name);
                Log.d(TAG, "NAME" + name);
                adapter.notifyDataSetChanged();
                //list.requestLayout();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void registerClickCallBack()
    {
        ListView list = (ListView) findViewById(R.id.view_list);
        if (list == null) throw new AssertionError();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                if(cameFrom.equals("attending")) {
                    Intent intent = new Intent(listView.getContext(), ProfileActivity.class);
                    String UID = userUID.get(position);
                    intent.putExtra("viewingUser", UID);
                    intent.putExtra("cameFrom", "list");
                    //intent.putExtra("currentUser", currentUID);
                    Log.i(TAG, "UID" + UID);
                    listView.getContext().startActivity(intent);
                    //or create other intents here
                }

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
    }


    private void loadUserUID (String gatheringUID) {
        list = (ListView) findViewById(R.id.view_list);
        userUID.clear();


        Firebase attendingRef;

        if(cameFrom.equals("pending")) {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("pendings");
        }
        else {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("attendees");
        }
        attendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                userUID.clear();
                Log.d(TAG, userUID.size() + " is the size of the list after none iteration");
                for (DataSnapshot attendeeSnapshot: dataSnapshot.getChildren()) {
                    String participant = attendeeSnapshot.getValue (String.class);
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