package gspot.com.sportify.Controller;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import gspot.com.sportify.utils.DatePickerFragment;
import gspot.com.sportify.utils.TimePickerFragment;

/**
 * Created by DannyChan on 5/8/16.
 */
public class GatheringActivity extends BaseNavBarActivity implements OnItemSelectedListener{

    /*use for logging*/
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    private static final int REQUEST_DATE = 0;

    private Gathering mgathering;
    private String m_hostID, mCurrentUser;
    private String mDateString;
    private String mTimeString;

    @Bind(R.id.sport_title) EditText mTitleField;
    @Bind(R.id.sport_description) EditText mDescriptionField;
    @Bind(R.id.sport_location) EditText mLocationField;

    @OnCheckedChanged(R.id.sport_status)
    void onCheckChanged (boolean isChecked) { mgathering.setIsPrivate(isChecked); }

    @OnClick(R.id.sport_submit)
    void onClick(Button button){submitGathering();}

    /**
     * Prompts user to select a date and modifies the mDate field of mgathering
     * which will be pushed to firebase once submit is clicked
     *
     */
    @OnClick(R.id.datepicker)
    void inputDate(Button dateButton) {
        DatePickerFragment newFragment = new DatePickerFragment();

        // display calendar dialog for picking date
        newFragment.show(getFragmentManager(), "datepickerFragment");
    }

    /**
     * Prompts user to select a time and modifies the mTime field of mgathering
     * which will be pushed to firebase once submit is clicked
     */
    @OnClick(R.id.timepicker)
    void inputTime() {
        // Create timepicker dialog and show it
        TimePickerFragment timeFragment = new TimePickerFragment();
        timeFragment.show(getFragmentManager(), "timepickerFragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gathering);

        ButterKnife.bind(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        m_hostID = prefs.getString(Constants.KEY_UID, "");
        mgathering = new Gathering();
        mgathering.setHostID(m_hostID);

        ArrayList<String> skillLevels = new ArrayList<String>();
        skillLevels.add("Beginner");
        skillLevels.add("Intermediate");
        skillLevels.add("Advanced");



        Spinner skillLevelSpinner = (Spinner) findViewById(R.id.skill_lv_spinner);
        skillLevelSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, skillLevels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillLevelSpinner.setAdapter(dataAdapter);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        mgathering.setSkillLevel(mgathering.toSkillLevel(item));
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
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

        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        Log.d(TAG, "mCurrentUser set to: " + mCurrentUser);


        Firebase sportRef = postID.push();
        Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mCurrentUser).child("myGatherings");
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(sportRef.getKey(), sportRef.getKey());
        myGatheringsID.updateChildren(updates);

        Log.d(TAG, "mCurrentUser set to: " + mCurrentUser);

        /*Writes the gathering to databse*/
        mgathering.setID(sportRef.getKey());
        mgathering.setSportTitle(mTitleField.getText().toString());
        mgathering.setDescription(mDescriptionField.getText().toString());
        mgathering.setLocation(mLocationField.getText().toString());
        //mgathering.setTime(mTimeField.getText().toString());
        mgathering.setSID("Dummy");
        mgathering.addAttendee(mCurrentUser);
        mgathering.addPending(mCurrentUser);
        mgathering.setDate(mDateString);
        mgathering.setTime(mTimeString);

        Log.d(TAG, "Time set to: " + mTimeString + " and date: " + mDateString);

        sportRef.setValue(mgathering);
        Intent intent = new Intent(this, GatheringListActivity.class);
        finish();
        startActivity(intent);
    }

    /**
     * Set date, to be called from DatePickerDialog
     * @param newDate
     */
    public void setDateString(String newDate) {
        mDateString = newDate;
        Log.d(TAG, "date set to: " + mDateString);
    }

    /**
     * Set date, to be called from TimePickerDialog
     * @param mTimeString
     */
    public void setmTimeString(String mTimeString) {
        this.mTimeString = mTimeString;
    }
}
