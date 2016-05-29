package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Authors amir assad, on 4/17/16
 * This class is the implementation of a recyclerView which will
 * be displaying all the gathering's on a page.
 * It will enable the user to touch a gathering on the list
 * and the user will be taken to view a detailed description of
 * the gathering. GatheringPagerActivity is in charge of
 * showing the detailed description.
 */
public class GatheringListFragment extends Fragment implements Observer{


    /*use for logging*/
    private static final String TAG = GatheringListFragment.class.getSimpleName();

    private final static String SPORT_TYPE_ID= "sport_type_id";

    private final static boolean FILTER = true;

    private final static boolean ACTIVE = true;

    /*code to pass in startActivityForResult*/
    private static final int REQUEST_CODE_FILTER = 1;

    /*the View to hold our list of Sports*/
    private RecyclerView mSportRecyclerView;

    /*Use to maintain the data for list and produce the view*/
    private SportAdapter mAdapter;

    /*contains the names of the sports chosen in filter*/
    private List<String> mChosenSports;

    /*filter: show private events*/
    private boolean mIsPrivateEvent;

    /*filter: show based on skill level*/
    private boolean [] mSkillLevels = { false, false, false };

    /*the current user's id to get their gatherings*/
    private String mCurrentUser;

    /*the model that talks with the database*/
    private SportLab mSportLab;

    /*Hold a reference to the menuItem for active gatherings*/
    private MenuItem mActiveGatheringButton;

    /*Hold a reference to the home button*/
    private MenuItem mHomeButton;

    /*
    * 1st function to be called when the object gets instantiated
    * tell the host activity that your fragment
    * has menu options that it wants to add*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setHasOptionsMenu(true);

        /*get the current user*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
    }

    /*2nd function that will be called when an Object of GatheringListFragment is created */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){
        Log.i(TAG, "onCreateView() Amir Assad");

        View view = inflater.inflate(R.layout.fragment_gathering_list, container, false);

        ButterKnife.bind(getActivity());

        mSportRecyclerView = (RecyclerView)view.findViewById(R.id.sport_recycler_view);
        /*recycler view delegates the positioning to the layout manager*/
        mSportRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*create a new sport lab*/
        mSportLab = new SportLab();

        /*let the observer know that this classes
        * update function will be waiting
        * for data to be updated*/
        mSportLab.addObserver(this);

        /*load the gatherings from the database*/
        /*update() will call updateUI*/
        mSportLab.loadGatherings();

        //updateUI(false);

        return view;
    }/*end onCreateView*/

    /*Load the toobar onto the screen*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu, inflater);

        /*inflate both menu on the toolbar*/
        inflater.inflate(R.menu.activity_main_actions, menu);
        inflater.inflate(R.menu.main, menu);

        /*get a reference to the menu buttons*/
        mActiveGatheringButton = menu.findItem(R.id.active);
        mHomeButton = menu.findItem(R.id.home);

        /*dont show the home button*/
        mHomeButton.setVisible(false);

    }

    /*respond to a click event on one of the choices on the toolbar*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected()");
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_filter:
                intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FILTER);
                break;
            case R.id.action_add:
                intent = new Intent(getActivity(), GatheringActivity.class);
                startActivity(intent);

                break;
            case R.id.active:
                mActiveGatheringButton.setVisible(false);
                mHomeButton.setVisible(true);
                loadActiveGatherings();
                break;

            case R.id.home:
                mActiveGatheringButton.setVisible(true);
                mHomeButton.setVisible(false);
                /*reload list with all events*/
                updateUI(FILTER, !ACTIVE, null);
                break;
        }//end case

        /*the baseActivity will handle the other options*/
        return super.onOptionsItemSelected(item);
    }

    /*utilty function to load all the user's
    * active gathering id into a list*/
    private void loadActiveGatherings() {

        Log.i(TAG, "loadActiveGatherings");
        /*contains a list of a user's gathering id*/
        final List<String> activeGatheringIds = new ArrayList<>();

        App.dbref.child("MyGatherings").child(mCurrentUser).child("myGatherings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange");
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    /*gathering ID*/
                    activeGatheringIds.add(data.getValue(String.class));
                }
                /*update the UI without any filtering*/
                updateUI(!FILTER, ACTIVE, activeGatheringIds);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /*
    * this Function will be called when the activity resumes back
    * just as if the onResume method was created.
    * This function is for when the user has return back
    * data to the calling activity. Line 175 is the reason for
    * for this function
    * @param requestCode: The integer request code originally supplied to startActivityForResult(),
    *                     allowing you to identify who this result came from.
    * @param resultCode:  the integer result code returned by the child activity through its setResult().
    * @param data: the result data from the caller, call the correct getExtra method to get the data*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(TAG, "onActivityResult()");

        /*something bad happened*/
        if(resultCode != Activity.RESULT_OK){
            Log.i(TAG, "RESULT CANCELED!!");
            return;
        }/*end if*/

        /*update the UI based on the filter settings*/
        if(requestCode == REQUEST_CODE_FILTER){
            if(data == null) return;

            /*Get the sports that were chosen by the filter*/
            mChosenSports = data.getStringArrayListExtra(SPORT_TYPE_ID);
            mIsPrivateEvent = data.getBooleanExtra(Constants.SPORT_ACCESS_ID, false);
            mSkillLevels = data.getBooleanArrayExtra(Constants.SKILL_LEVEL);

            if(mChosenSports != null){
                Toast.makeText(getContext(), mChosenSports.toString(), Toast.LENGTH_LONG).show();
            }

        }
        updateUI(FILTER, !ACTIVE, null);
    }//end onActivityResult

    /*
     * UpdateUI populates the view with the list of all Sports or
     * sports that were changed in the GatheringFragment
     */
    private void updateUI(boolean filter, boolean active, List<String> activeIDs) {
        Log.i(TAG, "updateUI() ");

        /*make a shallow copy*/
        List<Gathering> gatherings = new ArrayList<>(mSportLab.getSports());

        /*filter this list to specification and we have already filtered before*/
        if(filter) filterGatheringList(gatherings);

        else if(active) updateListWithActiveEvents(gatherings, activeIDs);

        /*there are currently no gatherings listed*/
        if(mAdapter == null) {
            mAdapter = new SportAdapter(gatherings);

            /*set the data behind the list view*/
            mSportRecyclerView.setAdapter(mAdapter);
        }/*end if*/

        /*only one sport will change at a time*/
        else {
            Log.i(TAG, "notify");
            mAdapter.setSports(gatherings);
            mAdapter.notifyDataSetChanged();
        }/*end else*/

    }/*end updateUI*/

    /*This is a utility function that will be called whenever the user
    * has chosen a filter for their home page
    * @Param gatherings: the list of gatherings to filter
    * since gatherings is a reference the change will happen
    * on the heap*/
    private void filterGatheringList(List<Gathering> gatherings) {
        /*go through the list and set the filters*/

        for (int i = 0; i < gatherings.size(); i++) {
            Gathering event = gatherings.get(i);
                /*If the sport is not in the list*/
            if (mChosenSports != null && mChosenSports.size() > 0) {
                //if the event type was in the filtered list
                if (!mChosenSports.contains(event.getGatheringTitle())) {
                    gatherings.remove(event);

                    //changing the array size so go back and check the replacement
                    --i;
                    continue;
                }
            }//end outer if

                /*If the sport is in the list but the access to event is not same*/
            if (mIsPrivateEvent && !event.getIsPrivate()){
                gatherings.remove(event);

                //changing the array size so go back and check the replacement
                --i;
                continue;
            } //end if

                /*if atleast one of the skill levels is selected*/
            if(mSkillLevels[0] || mSkillLevels[1] || mSkillLevels[2]){
                /*if we have the sport and the access is the same, check for skill level*/
                /*remove if event is beginner and beginner is not checked*/
                if (event.getSkillLevel() == (Gathering.SkillLevel.BEGINNER) && !mSkillLevels[0]) {
                    gatherings.remove(event);
                    --i;
                } else if (event.getSkillLevel().equals(Gathering.SkillLevel.INTERMEDIATE) && !mSkillLevels[1]) {
                    gatherings.remove(event);
                    --i;

                } else if (event.getSkillLevel().equals(Gathering.SkillLevel.ADVANCED) && !mSkillLevels[2]) {
                    gatherings.remove(event);
                    --i;
                }
            }//end outer if

        }//end for

    }//end filter UI

    /*Once the data is loaded from the databse update the UI
    * this will be called when SportLab's loadFromDataBase has finished
    * since the loading is asynchronous*/
    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "update");

        /*If the data base is modified the listener will immediately be called
        * if the user is viewing their gatherings, then dont update the UI until
        * they un-check the active button*/
        if(mActiveGatheringButton != null && mActiveGatheringButton.isChecked()) {
            Toast.makeText(getContext(), "Fetching new data", Toast.LENGTH_SHORT).show();
            mActiveGatheringButton.setChecked(false);
            mAdapter.notifyDataSetChanged();
        }

        updateUI(FILTER, !ACTIVE, null);
    }

    /* this function will see if the gatherings parameter is part
    * of the user's gatherings if not it will remove it from
    * the gatherings parameter which is a reference to the gatherings in
    * updateUI()*/
    private void updateListWithActiveEvents(List<Gathering> gatherings, List<String> activeGatheringIds) {
        for(int i = 0; i < gatherings.size(); i++) {
            Gathering event = gatherings.get(i);
            /*if not the user's gathering then remove it*/
            if (!activeGatheringIds.contains(event.getID())){
                gatherings.remove(event);
                --i;
            } //end if
        } //end for
    } //end updateListWithActiveEvents

    /*Provide a reference to the views for each data item
      Complex data items may need more than one view per item, and
      you provide access to all the views for a data item in a view holder
      Implements the onClickerListener so everytime a sport is clicked
      an action will happen
     */
    private class SportHolder extends RecyclerView.ViewHolder
                              implements View.OnClickListener {
        /*use for logging*/
        private final String TAG = SportHolder.class.getSimpleName();

        private Gathering mGathering;
        private TextView mTitleTextView;
        private TextView mEventStatusView;
        private TextView mEventTime;
        private TextView mEventDate;


        public SportHolder(View itemView) {
            super(itemView);

            Log.i(TAG, "SportHolder()");

            /*link member with the widget*/
            mTitleTextView = (TextView)itemView.findViewById(R.id.gathering_title);
            mEventStatusView = (TextView)itemView.findViewById(R.id.gathering_status);
            mEventTime = (TextView)itemView.findViewById(R.id.gathering_time);
            mEventDate = (TextView)itemView.findViewById(R.id.gathering_date);

            /*when the Gathering is clicked in the list*/
            itemView.setOnClickListener(this);
        }//end SportHolder()

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick()" + mGathering.getID());
            /*Create the SportActivity*/
            Intent intent = GatheringPagerActivity.newIntent(getActivity(), mGathering.getID());
            Log.d(TAG, "INTENT"+ mGathering.getID());

            App.mCurrentGathering = mGathering;
            startActivity(intent);
        } //end onClick()


        /*function to display the data names on screen
         *the RecyclerView will call onCreateViewHolder to
         *update the screen*/
        public void bindSport(Gathering gathering) {

            Log.i(TAG, "bindSport()");
            mGathering = gathering;
            Log.d(TAG, "BIND SPORT" + mGathering.getGatheringTitle());
            mTitleTextView.setText(mGathering.getGatheringTitle());
            if (mGathering.getIsPrivate()) {
                mEventStatusView.setText("Private");
            }
            else {
                mEventStatusView.setText("Public");
            }
            mEventTime.setText(mGathering.getTime());
            mEventDate.setText(mGathering.getDate());
        }/*end bindSport*/
    }/*end SportHolder*/


    /*the RecyclerView will communicate with this adapter
      when a ViewHolder needs to be created or connected with a Gathering object
     */
    private class SportAdapter extends RecyclerView.Adapter<SportHolder> {

        /*use for logging*/
        private final String TAG = SportAdapter.class.getSimpleName();

        /*list of sports*/
        private List<Gathering> mGatherings;

        public SportAdapter(List<Gathering> gatherings) { mGatherings = gatherings; }

        /*
        * 1st function to run when this object is invoked
        * called by RecyclerView when it needs a new View to display
        * this method creates a view and wraps it in a viewHolder*/
        @Override
        public SportHolder onCreateViewHolder(ViewGroup parent, int viewType){
            Log.i(TAG, "onCreateViewHolder()");
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            /*render all the sports with this layout*/
            View view = layoutInflater.inflate(R.layout.list_item_gathering, parent, false);
            return new SportHolder(view);
        } //end onCreateViewHolder

        /**
         * 2nd function to run when object is invoked
         * Binds a ViewHolder's View to the model object
         * Position == position in a dataSet(index in the List): use to find the right model data
         * then it updates the view called more often than onCreateViewHolder
         */
        @Override
        public void onBindViewHolder(final SportHolder holder, int position){
            Log.i(TAG, "onBindViewHolder()" + position);
            Gathering gathering = mGatherings.get(position);
            holder.bindSport(gathering);  /*give the proper description to the widets*/
        } //end onBindViewHolder

        @Override
        public int getItemCount() { return mGatherings.size(); }

        public void setSports(List<Gathering> gatherings) { mGatherings = gatherings; }
    }//end SportAdapter


}//end SportListFragment
