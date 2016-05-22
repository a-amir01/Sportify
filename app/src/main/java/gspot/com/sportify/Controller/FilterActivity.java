package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

import butterknife.OnClick;
import gspot.com.sportify.Model.SportType;
import gspot.com.sportify.Model.SportTypes;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;
import gspot.com.sportify.utils.GatheringTypeProvider;

/**
 * Created by amir on 5/6/16.
 * This class enables the user to filter what
 * they will see on their home page(GatheringListFragment)
 * this class will pass back a boolean variable with the key
 * Constants.SPORT_ACCESS_ID and it will pass back a hashmap
 * with the key Constants.SPORT_TYPE_ID which can be used to
 * filter out the array list that the recycler view uses
 */
public class FilterActivity extends Activity implements CompoundButton.OnCheckedChangeListener, Observer {

    private static final String TAG = FilterActivity.class.getSimpleName();

    /*the keys will the A-Z, each key will contain a list of all sports for that key*/
    private HashMap<String, List<String>> mGatheringType;

    /*They key values in the mGatheringTpe*/
    private List<String> mGatheringList;

    private ExpandableListView mExpandableListView;
    private CustomExpandableFilterListAdapter mExpandableListAdapter;

    /*contains the list of sports from the databse*/
    private SportTypes mDataBaseSports = new SportTypes();

    /*Is the private filed selected?*/
    private static boolean sIsPrivateEvent;

    /*Are all options selected?*/
    private static boolean sIsAllSelected;

    @Bind(R.id.expand_all) Switch mExpandAllSwitch;
    @Bind(R.id.select_all) Switch mSelectAllSwitch;
    @Bind(R.id.event_access_specifier) Switch mEventAccessSpecifier;

    @OnCheckedChanged(R.id.expand_all)
    public void onExpandAllCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked)
            mExpandableListAdapter.expandAllChildren(mExpandableListView);
        else
            mExpandableListAdapter.collapseAllChildren(mExpandableListView);
    }

    @OnCheckedChanged(R.id.event_access_specifier)
    public void onAccessCheckChanged(CompoundButton buttonView, boolean isChecked){
        sIsPrivateEvent = isChecked;
    }

    @OnClick(R.id.saveButton)
    public void onClickSave(){
        /*get th list of sports selected*/
        ArrayList<String> selectedSports = (ArrayList<String>) mExpandableListAdapter.getSelectedSports();

        /*Intent to hold data to pass back*/
        Intent data = new Intent();

        data.putStringArrayListExtra(Constants.SPORT_TYPE_ID, selectedSports);
        data.putExtra(Constants.SPORT_ACCESS_ID, sIsPrivateEvent);

        /*set the boolean hashmap data on device*/
        saveDataOnDevice();

        /*pass back the sport types*/
        setResult(RESULT_OK, data);

        /*close the activity*/
        finish();
    }

    /*Close the activity and don't save any results*/
    @OnClick(R.id.cancelButton)
    public void onClickCancel(){
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter_activity);

        ButterKnife.bind(this);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableList);

        /*create an observer*/
        mDataBaseSports.addObserver(this);

        /*Asynchronous tasks*/
        mDataBaseSports.readSportTypes();

    }//end onCreate

    /*don't let the user press the back button*/
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please press \"Save\" or \"Cancel\"", Toast.LENGTH_SHORT).show();
    }

    /* If the user used the filters then save the data on the device
    * so that their filter settings will be saved when they go back
    * to filter again*/
    private void saveDataOnDevice() {
        try {
            /*open for writing*/
            FileOutputStream fStream = openFileOutput(Constants.FILE_NAME, Context.MODE_PRIVATE) ;
            /*user output stream to write*/
            ObjectOutputStream oStream = new ObjectOutputStream(fStream);

            /*write the hashtable to the txt file*/
            oStream.writeObject(mExpandableListAdapter.getSelectedBooleanHashMap());
            oStream.flush();
            oStream.close();

            Log.i(TAG, "Serialization Success");
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }//end saveDataOnDevice

    /*Returns the filter settings that was previously selected
    * if there were no filter settings then it will return null*/
    private HashMap<Integer, boolean[]> getFilterSettings() {
        /*Filters chosen by the user*/
        HashMap<Integer, boolean[]> filters = null;

        /*output stream*/
        ObjectInputStream ois = null;
        /*input stream*/
        FileInputStream inStream = null;

        try {
            /*open for reading*/
            inStream = openFileInput(Constants.FILE_NAME);
            /*read from the inputStream*/
            ois = new ObjectInputStream(inStream);
            /*get the filters*/
            filters = (HashMap<Integer, boolean[]>) ois.readObject();

            Log.i(TAG, "De-Serialization Success");
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
        }//end catch

        return filters;

    }//end restorePreviousFilterSettings

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*If isChecked and the expandAllSwitch is not on
        * then turn the switch on and expand all the children*/
        if(isChecked && !mExpandAllSwitch.isChecked()){
            mExpandAllSwitch.setChecked(true);
            mExpandableListAdapter.expandAllChildren(mExpandableListView);
        }//end if

        /*set all checkboxes to isChecked*/
        mExpandableListAdapter.setAllChildStates(isChecked);

        /*for passing data back to calling activity*/
        sIsAllSelected = isChecked;
    }

    @Override
    public void update(Observable observable, Object data) {

        /*Filters chosen by the user that was saved on the device*/
        HashMap<Integer, boolean[]> filters;

        ArrayList<String> sps = new ArrayList<String>();
        for (SportType sp : mDataBaseSports.sportTypes) {
            sps.add(sp.getName());
        }

        /*pass the array from the database to get back a hashmap for adaper*/
        mGatheringType = GatheringTypeProvider.getDataHashMap(sps.toArray(new String[sps.size()]));

        /*set the keys of the hashmap to this list*/
        mGatheringList = new ArrayList<>(mGatheringType.keySet());

        /*sort the parents*/
        Collections.sort(mGatheringList);

        Log.i(TAG, "update " + mGatheringType.size());

        /*get the saved data that was stored on the device*/
        filters = getFilterSettings();

        /*has not created a filter yet or filters is empty*/
        if (filters == null) {
            mExpandableListAdapter = new CustomExpandableFilterListAdapter(this, mGatheringType, mGatheringList);
        } else {
            mExpandableListAdapter = new CustomExpandableFilterListAdapter(this, mGatheringType, mGatheringList, filters);
        }

        /*set adapter to our custom adapter*/
        mExpandableListView.setAdapter(mExpandableListAdapter);

         /*have the list expanded at first
        * every call to setChecked is accompanied by
        * the setOnCheckChangedListener*/
        mExpandAllSwitch.setChecked(true);

        mExpandableListAdapter.expandAllChildren(mExpandableListView);

        /*Turn on the listener so we can set the button to the static field*/
        mSelectAllSwitch.setOnCheckedChangeListener(null);

        /*Set the button is the static selected value*/
        mSelectAllSwitch.setChecked(sIsAllSelected);

        /*turn the listener back on*/
        mSelectAllSwitch.setOnCheckedChangeListener(this);

        /*set the event specifier*/
        mEventAccessSpecifier.setChecked(sIsPrivateEvent);

    } //end update
}//end FilterActivity

