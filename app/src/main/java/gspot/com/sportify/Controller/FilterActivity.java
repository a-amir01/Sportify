package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

import butterknife.OnClick;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.GatheringTypeProvider;

/**
 * Created by amir on 5/6/16.
 */
public class FilterActivity extends Activity
                            implements ExpandableListView.OnGroupCollapseListener{

    private HashMap<String, List<String>> mGatheringType;
    private List<String> mGatheringList;
    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter mExpandableListAdapter;
    private boolean mIsPrivate;

    @Bind(R.id.expand_all) Switch mExpandAllSwitch;

    @OnCheckedChanged(R.id.expand_all)
    public void onExpandAllCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked)
            mExpandableListAdapter.expandAllChildren(mExpandableListView);
        else
            mExpandableListAdapter.collapseAllChildren(mExpandableListView);
    }

    @OnCheckedChanged(R.id.select_all)
    public void onSelectAllCheckChanged(CompoundButton buttonView, boolean isChecked){

        /*If isChecked and the expandAllSwitch is not on
        * then turn the switch on and expand all the children*/
        if(isChecked && !mExpandAllSwitch.isChecked()){
            mExpandAllSwitch.setChecked(true);
            mExpandableListAdapter.expandAllChildren(mExpandableListView);
        }
        //mExpandableListAdapter.mIsAllSelected = true;
        mExpandableListAdapter.setAllChildStates(isChecked);
    }

    @OnCheckedChanged(R.id.event_access_specifier)
    public void onAccessCheckChanged(CompoundButton buttonView, boolean isChecked){
        mIsPrivate = isChecked;
    }
    
    @OnClick(R.id.saveButton)
    public void onClickSave(){

        //setResult();
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

        /*get all the gathering types*/
        mGatheringType = GatheringTypeProvider.getDataHashMap();

        /*set the keys of the hashmap to this list*/
        mGatheringList = new ArrayList<>(mGatheringType.keySet());

        /*sort the parents*/
        Collections.sort(mGatheringList);

        Log.i("adding", mGatheringType.get(mGatheringList.get(0)).size() + "");

        mExpandableListAdapter = new CustomExpandableListAdapter(this, mGatheringType, mGatheringList);

        mExpandableListView.setAdapter(mExpandableListAdapter);

        /*the the expandable list view know we have implemented these methods*/
        //mExpandableListView.setOnChildClickListener(this);
        mExpandableListView.setOnGroupCollapseListener(this);

        /*have the list expanded at first*/
        mExpandableListAdapter.expandAllChildren(mExpandableListView);
        mExpandAllSwitch.setChecked(true);
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        Log.i("onCollapse", "");
        Toast.makeText(FilterActivity.this, mGatheringList.get(groupPosition) + " collapsed", Toast.LENGTH_SHORT).show();
    }

    /*don't let the user press the back button*/
    @Override
    public void onBackPressed() {
    }
}

