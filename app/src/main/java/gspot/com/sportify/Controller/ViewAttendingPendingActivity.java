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
import android.widget.ListView;

import butterknife.ButterKnife;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Created by Aaron on 5/21/2016.
 */
public class ViewAttendingPendingActivity extends BaseNavBarActivity {
    private static final String TAG = ViewAttendingPendingActivity.class.getSimpleName();

    private ArrayList<String> userList = new ArrayList();
    private ArrayList<String> userUID = new ArrayList();
    private String gatheringUID;
    private Profile mProfile;
    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //String currentUID = prefs.getString(Constants.KEY_UID, "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gatheringUID = intent.getStringExtra("gatheringUID");
        setContentView(R.layout.view_attending_pending);

        loadUserUID(gatheringUID);
        convertToNames();
        populateListView();
        registerClickCallBack();

        Log.d(TAG, "popularList size:" + userUID.size());



        Log.d(TAG, "popularList size after register call back:" + userUID.size());

    } //end onCreate()

    private void populateListView (){

        Log.d(TAG, "size of userList before "+ userList.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.the_user, userList);
        Log.d(TAG, "size of userList after "+ userList.size());
        ListView list = (ListView) findViewById(R.id.view_list);
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
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void registerClickCallBack()
    {
        ListView list = (ListView) findViewById(R.id.view_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Intent intent = new Intent(listView.getContext(), ProfileActivity.class);
                String UID = userUID.get(position);
                intent.putExtra("viewingUser", UID);
                //intent.putExtra("currentUser", currentUID);
                Log.i(TAG, "UID" + UID);
                listView.getContext().startActivity(intent);
                //or create other intents here
            }
        });
    }

    private void loadUserUID (String gatheringUID) {
        userUID.clear();
        Firebase attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("attendees");
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
    private void convertToNames () {
        for (String UID: userUID) {
            getHostname(UID);
        }
    }
}
