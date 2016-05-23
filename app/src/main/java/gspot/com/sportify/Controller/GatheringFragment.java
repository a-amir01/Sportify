package gspot.com.sportify.Controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.BinderThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Authors amir assad, Aaron, Yunfan Yang on 4/17/16
 * This class uses the fragment_gathering.xml
 * This class is the detailed view of a gathering
 * it will be editable if the user is a host else
 * all editable widgets will be disabled and only viewable
 */
public class GatheringFragment extends Fragment {

    /*use for logging*/
    private static final String TAG = GatheringFragment.class.getSimpleName();

    /*the Gathering profile*/
    private Gathering mGathering;
    private Profile mProfile;

    private String hostID, hostName, gatheringUID, mCurrentUser;
    private int sizeofAttendees;



    private static final String ARG_SPORT_ID = "sport_id";
    ValueEventListener m_lis;
    Firebase gathering;

    @Bind(R.id.gathering_title) EditText mTitleField;
    @Bind(R.id.gathering_description) EditText mDescriptionField;
    @Bind(R.id.gathering_location) EditText mLocationField;
    @Bind(R.id.gathering_time) EditText mTimeField;
    @Bind(R.id.gathering_host) EditText mHost;
    @Bind(R.id.gathering_delete) Button mDelete;
    @Bind(R.id.gathering_edit) Button mEdit;
    @Bind(R.id.host_display) EditText mHostDisplay;
    @Bind(R.id.attendees_display) EditText mAttendeesDisplay;

    @OnClick(R.id.gathering_delete)
    void onClick(Button button){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        int status = mGathering.getStatus(mCurrentUser);

        if(status == 1){
            deleteGathering();
        } //host
        else if (status == 2) {
            leaveAttending();
        } //attendee
        else if (status == 3) {
            leavePending();
        } // leave gatherig
        else {
            if (mGathering.getIsPrivate()) {
                requestGathering();
            }
            else {
                joinGathering(); }
        }

    }

    @OnClick(R.id.gathering_edit)
    void onClickEdit (Button button) {
        Intent intent = new Intent(getActivity(), GatheringActivity.class);
        intent.putExtra("Edit", true);
        App.mCurrentGathering = mGathering;
        getActivity().finish();
        startActivity(intent);
    }

    @OnClick (R.id.attendees_display)
    void onClickAttending()
    {
        Intent intent = new Intent(getActivity(), ViewAttendingPendingActivity.class);
        App.mCurrentGathering = mGathering;
        intent.putExtra("gatheringUID", mGathering.getID());
        getActivity().finish();
        getActivity().startActivity(intent);
    }



    /* will be called when a new SportFragment needs to be created
     * This method Creates a fragment instance and bundles up &
     * sets its arguments,
     * attaching arguments to a fragment must be done after the fragment is created
     * but before it is added to an activity.*/
    public static GatheringFragment newInstance(String sportId) {
        Log.d(TAG, "newInstance()");
        Bundle args = new Bundle();
        args.putString(ARG_SPORT_ID, sportId);    /*store the sportId for later retreival*/
        GatheringFragment fragment = new GatheringFragment();   /*create a new instance of the fragment*/
        fragment.setArguments(args);                    /*bundle up the arguments*/
        return fragment;
    } //end newInstance

    @Override
        public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);

        /*Get the gathering ID from the previous fragment*/
        Intent intent = getActivity().getIntent();

        gatheringUID = intent.getStringExtra("gatheringUID");

        /*Read the Gathering with the unique gatheringID*/

        gathering = new Firebase(Constants.FIREBASE_URL_GATHERINGS).child(gatheringUID);
        /*Populate page with gathering*/
        m_lis = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    /*Create a gathering object from data in database*/
                    mGathering = dataSnapshot.getValue(Gathering.class);

                    /*Retrieve text information from the database*/
                    mTitleField.setText(mGathering.getSportTitle());
                    mDescriptionField.setText(mGathering.getDescription());
                    mTimeField.setText(mGathering.getTime());
                    mLocationField.setText(mGathering.getLocation());

                    hostID = mGathering.getHostID();
                    mAttendeesDisplay.setText(" and " + (mGathering.getAttendeeSize() -1) + " others are going ");
                    getHostname(hostID);
                }catch(Exception e) {}

                setButton();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Fire    BaseError " + firebaseError.getMessage());
            }
        };

        gathering.addValueEventListener(m_lis);
    }//end onCreate

    /**inflate the layout for the fragments view and return the view to the host*/
    @Override                //*inflate the layout   *from the activities   *layout recreate from a saved state
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {/**gets called when its the 1st time drawing its UI*/
        /**the 3rd param: whether the inflated layout should be attached to the 2nd param during inflation*/
        Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.view_gathering, parent, false);
        ButterKnife.bind(this, view);

        /**root of the fragments layout, return null if no layout.*/
        return view;
    }//end onCreateView


    @Override
    public void onDestroyView()
    {//Called when the view hierarchy associated with the fragment is being removed.
        super.onDestroyView();
        Log.i(TAG, "onDestroyView()");
        ButterKnife.unbind(this);
        gathering.removeEventListener(m_lis);
    }

    void getHostname(String hostID) {
       Firebase profileRef = new Firebase(Constants.FIREBASE_URL_PROFILES).child(hostID).child("mName");

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHost.setText( dataSnapshot.getValue(String.class));
                mHostDisplay.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    void deleteGathering() {
        if(gathering != null && m_lis != null)
            gathering.removeEventListener(m_lis);
        App.mCurrentGathering.delete();
        App.mGatherings.remove(App.mCurrentGathering);
        App.mCurrentGathering = null;

        Intent intent = new Intent(getActivity(), GatheringListActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    void leaveAttending () {
        Log.i(TAG, "LEAVE ATTENDING");
        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.removeAttendee(mCurrentUser);
        mGathering.updateAttendees(getActivity().getApplicationContext());
    }
    void leavePending () {
        Log.i(TAG, "LEAVE PENDING");
        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.removePending(mCurrentUser);
        mGathering.updatePending(getActivity().getApplicationContext());
    }
    void joinGathering () {
        Log.i(TAG, "JOIN");
      /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.addAttendee(mCurrentUser);
        mGathering.updateAttendees(getActivity().getApplicationContext());
    }

    void requestGathering() {
        Log.i(TAG, "Reqeust");
      /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.addPending(mCurrentUser);
        mGathering.updateAttendees(getActivity().getApplicationContext());
    }

    void setButton() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        int status = mGathering.getStatus(mCurrentUser);

        Log.d(TAG, "STATUS" + status);
        if(status == 1){ mDelete.setText("Delete");} //host
        else if (status == 2) {mDelete.setText("Leave");} //attendee
        else if (status == 3) {mDelete.setText("Remove Request");} // leave gatherig
        else {
            if (mGathering.getIsPrivate()) { mDelete.setText("Request"); }
            else { mDelete.setText("Join"); }
        }
    }
}