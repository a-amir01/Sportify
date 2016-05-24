package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.Model.SportType;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;
import gspot.com.sportify.utils.App;


/**
 * Created by DannyChan on 5/8/16.
 */
public class GatheringActivity extends BaseNavBarActivity {

    private Gathering mgathering;
    private String m_hostID, mCurrentUser;

    @Bind(R.id.sport_title) EditText mTitleField;
    @Bind(R.id.sport_description) EditText mDescriptionField;
    @Bind(R.id.sport_location) EditText mLocationField;
    @Bind(R.id.sport_date) EditText mDateField;
    @Bind(R.id.sport_time) EditText mTimeField;

    @OnCheckedChanged(R.id.sport_status)
    void onCheckChanged (boolean isChecked) { mgathering.setIsPrivate(isChecked); }

    @OnClick(R.id.sport_submit)
    void onClick(Button button){submitGathering();}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gathering);

        ButterKnife.bind(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        m_hostID = prefs.getString(Constants.KEY_UID, "");
        mgathering = new Gathering();
        mgathering.setHostID(m_hostID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e(TAG, "onDestroy()");
        ButterKnife.unbind(this);
    }

    //TODO: Will refractor into gathering model
    private void submitGathering() {
        Firebase postID = new Firebase(Constants.FIREBASE_URL).child("Gatherings");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        Firebase sportRef = postID.push();

        Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mCurrentUser).child("myGatherings");
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(sportRef.getKey(), sportRef.getKey());
        myGatheringsID.updateChildren(updates);
        mgathering.setID(sportRef.getKey());
        mgathering.setDate(mDateField.getText().toString());
        mgathering.setGatheringTitle(mTitleField.getText().toString());
        mgathering.setDescription(mDescriptionField.getText().toString());
        mgathering.setLocation(mLocationField.getText().toString());
        mgathering.setTime(mTimeField.getText().toString());
        mgathering.setSID("Dummy");
        sportRef.setValue(mgathering);
        Intent intent = new Intent(this, GatheringListActivity.class);
        finish();
        startActivity(intent);
    }
}
