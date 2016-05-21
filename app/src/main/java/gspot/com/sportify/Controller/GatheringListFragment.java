package gspot.com.sportify.Controller;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;
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

public class GatheringListFragment extends Fragment {


    /*use for logging*/
    private static final String TAG = GatheringListFragment.class.getSimpleName();

    private static final String POSITION_ID = "position_id";
    private final static String SPORT_TYPE_ID= "sport_type_id";

    /*code to pass in startActivityForResult*/
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE_FILTER = 1;

    /*the View to hold our list of Sports*/
    private RecyclerView mSportRecyclerView;

    /*Use to maintain the data for list and produce the view*/
    private SportAdapter mAdapter;

    /*position of the sport that will be Viewed*/
    public int mSportPosition;

    private List<String> mChosenSports;

    /*
    * 1st function to be called when the object gets instantiated
    * tell the host activity that your fragment
    * has menu options that it wants to add*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setHasOptionsMenu(true);
    }

    /*2nd function that will be called when an Object of GatheringListFragment is created */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){
        Log.i(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_gathering_list, container, false);

       mSportRecyclerView = (RecyclerView)view.findViewById(R.id.sport_recycler_view);
        /*recycler view delegates the positioning to the layout manager*/
        mSportRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }/*end onCreateView*/

    /*Load the toolbar onto the screen*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu, inflater);

        /*inflate both menu on the toolbar*/
        inflater.inflate(R.menu.activity_main_actions, menu);
        inflater.inflate(R.menu.main, menu);

        /*dont show the home button*/
        menu.findItem(R.id.home).setVisible(false);
    }

    /*respond to a click event on one of the choices on the toolbar*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.action_filter:
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FILTER);
                break;
            case R.id.action_add:
                Toast.makeText(this.getContext(), "not yet implemented", Toast.LENGTH_SHORT).show();
                break;
        }//end case



        /*the baseActivity will handle the other options*/
        return super.onOptionsItemSelected(item);
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

        /*correct fragment being called*/
        if(requestCode == REQUEST_CODE)
        {
            if(data == null) return;

            /*store the position of the list item that was changed*/
            mSportPosition = data.getIntExtra(POSITION_ID, -1);

            /*No Value found for the Id*/
            if(mSportPosition == -1) {
                Log.i(TAG, "No value found for the id " + POSITION_ID );
                return;
            }/*end if*/
        }/*end if*/

        /*update the UI based on the filter settings*/
        if(requestCode == REQUEST_CODE_FILTER){
            if(data == null) return;

            /*Get the sports that were chosen by the filter*/
            mChosenSports = data.getStringArrayListExtra(SPORT_TYPE_ID);

            if(mChosenSports != null){
                Toast.makeText(getContext(), mChosenSports.toString(), Toast.LENGTH_LONG).show();
            }

        }

        updateUI();
    }//end onActivityResult

    /*
     * UpdateUI populates the view with the list of all Sports or
     * sports that were changed in the GatheringFragment
     */
    private void updateUI() {
        Log.i(TAG, "updateUI() " + mSportPosition);

        /*get the Sports from the hosting activity*/
        SportLab sportLab = SportLab.get(getActivity());

        /*get all the Sports*/
        List<Gathering> gatherings = sportLab.getSports();

        /*there are currently no gatherings listed*/
        if(mAdapter == null) {
            Log.d(TAG, "updateUI() mAdapter == null");
            /*populate screen with all gatherings*/
            mAdapter = new SportAdapter(gatherings);
            /*set the data behind the list view*/
            mSportRecyclerView.setAdapter(mAdapter);
        }/*end if*/

        /*only one sport will change at a time*/
        else {
            /*notify on gatherings's update*/
            mAdapter.notifyItemChanged(mSportPosition);

        }/*end else*/

    }/*end updateUI*/


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


        public SportHolder(View itemView) {
            super(itemView);

            Log.i(TAG, "SportHolder()");

            /*link member with the widget*/
            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_sport_title_text_view);

            /*when the Gathering is clicked in the list*/
            itemView.setOnClickListener(this);
        }//end SportHolder()

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick()");
            /*Create the SportActivity*/
            Intent intent = GatheringPagerActivity.newIntent(getActivity(), mGathering.getId());
            startActivityForResult(intent, REQUEST_CODE);
        } //end onClick()


        /*function to display the data names on screen
         *the RecyclerView will call onCreateViewHolder to
         *update the screen*/
        public void bindSport(Gathering gathering) {

            Log.i(TAG, "bindSport()");
            mGathering = gathering;
            mTitleTextView.setText(mGathering.getSportName());
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
    }//end SportAdapter

}//end SportListFragment

