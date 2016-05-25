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
import com.firebase.client.FirebaseError;

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
public class GatheringActivity extends BaseNavBarActivity implements OnItemSelectedListener {

    private static final String TAG =


            DatePickerFragment.class.getSimpleName();
    private static final int REQUEST_DATE = 0;

    private Gathering mgathering;
    private String m_hostID, mCurrentUser;
    private boolean toEdit;
    private String mDateString;
    private String mTimeString;
    private Button dateButton;
    private Button timeButton;

    @Bind(R.id.sport_title)
    EditText mTitleField;
    @Bind(R.id.sport_description)
    EditText mDescriptionField;
    @Bind(R.id.sport_location)
    EditText mLocationField;

    @OnCheckedChanged(R.id.sport_status)
    void onCheckChanged(boolean isChecked) {
        if (toEdit) {
            App.mCurrentGathering.setIsPrivate(isChecked);
        }
        else {
            mgathering.setIsPrivate(isChecked);
        }
    }

    @OnClick(R.id.sport_submit)
    void onClick(Button button) {
        if (toEdit) {
            updateGathering();
        } else {
            submitGathering();
        }
    }

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

        Spinner sportTypeSpinner = (Spinner) findViewById(R.id.sport_type_spinner);
        sportTypeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> Adapter1 = ArrayAdapter.createFromResource(this.getApplicationContext(), R.array.sport_types, R.layout.spinner_style);
        Adapter1.setDropDownViewResource(R.layout.spinner_style);
        sportTypeSpinner.setAdapter(Adapter1);

        Spinner skillLevelSpinner = (Spinner) findViewById(R.id.skill_lv_spinner);
        skillLevelSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this.getApplicationContext(), R.array.skill_lv_array, R.layout.spinner_style);
        dataAdapter.setDropDownViewResource(R.layout.spinner_style);
        skillLevelSpinner.setAdapter(dataAdapter);

        Intent intent = getIntent();
        toEdit = intent.getBooleanExtra("Edit", false);
        if (toEdit) {
            mTitleField.setText(App.mCurrentGathering.getGatheringTitle());
            mDescriptionField.setText(App.mCurrentGathering.getDescription());
            mLocationField.setText(App.mCurrentGathering.getLocation());
            //Set date and time box

            dateButton = (Button) findViewById(R.id.datepicker);
            dateButton.setText(App.mCurrentGathering.getmDate());

            timeButton = (Button) findViewById(R.id.timepicker);
            timeButton.setText((App.mCurrentGathering.getTime()));



            int sportspinnerPosition = Adapter1.getPosition(App.mCurrentGathering.getSID());
            sportTypeSpinner.setSelection(sportspinnerPosition);

            int skillLevelPosition = dataAdapter.getPosition(App.mCurrentGathering.getSkillLevel().toString());
            skillLevelSpinner.setSelection(skillLevelPosition);

        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            m_hostID = prefs.getString(Constants.KEY_UID, "");
            mgathering = new Gathering();
            mgathering.setHostID(m_hostID);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.sport_type_spinner:
                String sport = parent.getItemAtPosition(position).toString();
                if (toEdit) {
                    App.mCurrentGathering.setSID(sport);
                }
                else {
                    mgathering.setSID(sport);
                }
                break;

            case R.id.skill_lv_spinner:
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                if (toEdit) {
                    App.mCurrentGathering.setSkillLevel(App.mCurrentGathering.toSkillLevel(item));
                }
                else {
                    mgathering.setSkillLevel(mgathering.toSkillLevel(item));
                }
                break;
        }
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

    private void updateGathering() {
       // App.mCurrentGathering.setDate(mDateField.getText().toString());
        App.mCurrentGathering.setGatheringTitle(mTitleField.getText().toString());
        App.mCurrentGathering.setDescription(mDescriptionField.getText().toString());
        App.mCurrentGathering.setLocation(mLocationField.getText().toString());
      //  App.mCurrentGathering.setTime(mTimeField.getText().toString());
        App.mCurrentGathering.updateGathering();
        Intent intent = new Intent(this, GatheringListActivity.class);
        finish();
        startActivity(intent);

    }

    private void submitGathering() {
        Firebase postID = new Firebase(Constants.FIREBASE_URL).child("Gatherings");

        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        Firebase sportRef = postID.push();
        Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mCurrentUser).child("myGatherings");
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(sportRef.getKey(), sportRef.getKey());
        myGatheringsID.updateChildren(updates);

        /*Writes the gathering to databse*/
        mgathering.setID(sportRef.getKey());
        mgathering.setGatheringTitle(mTitleField.getText().toString());
        mgathering.setDescription(mDescriptionField.getText().toString());
        mgathering.setLocation(mLocationField.getText().toString());
        mgathering.addAttendee(mCurrentUser);
        //mgathering.addPending(mCurrentUser);
        mgathering.setDate(mDateString);
        mgathering.setTime(mTimeString);
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
