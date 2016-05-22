package gspot.com.sportify.Controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;

import com.firebase.client.Firebase;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;

/**
 * Authors amir assad, Aaron on 4/17/16
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

    private String title, description, location, time;

    private static final String ARG_SPORT_ID = "sport_id";


    @Bind(R.id.sport_title) EditText mTitleField;
    @Bind(R.id.sport_description) EditText mDescriptionField;
    @Bind(R.id.sport_location) EditText mLocationField;
    @Bind(R.id.sport_time) EditText mTimeField;

    @OnClick(R.id.sport_submit)
    void onClick(Button button){submitGathering();}
    @OnTextChanged(R.id.sport_title)
    void onTextChange(CharSequence text, int start, int before, int count) { mGathering.setSportName(text.toString()); }


    /* will be called when a new SportFragment needs to be created
     * This method Creates a fragment instance and bundles up &
     * sets its arguments,
     * attaching arguments to a fragment must be done after the fragment is created
     * but before it is added to an activity.*/
    public static GatheringFragment newInstance(UUID sportId) {
        Log.d(TAG, "newInstance()");
        Bundle args = new Bundle();
        args.putSerializable(ARG_SPORT_ID, sportId);    /*store the sportId for later retreival*/
        GatheringFragment fragment = new GatheringFragment();   /*create a new instance of the fragment*/
        fragment.setArguments(args);                    /*bundle up the arguments*/
        return fragment;
    } //end newInstance

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        UUID sportId = (UUID) getArguments().getSerializable(ARG_SPORT_ID);
        mGathering = SportLab.get(getActivity()).getSport(sportId);

    }//end onCreate

    /**inflate the layout for the fragments view and return the view to the host*/
    @Override                //*inflate the layout   *from the activities   *layout recreate from a saved state
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {/**gets called when its the 1st time drawing its UI*/
        /**the 3rd param: whether the inflated layout should be attached to the 2nd param during inflation*/
        Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_gathering, parent, false);
        ButterKnife.bind(this, view);
        /**show the sport*/
        mTitleField.setText(mGathering.getSportName());



        //Writes to the database a key of "Test" and a value of the title of the sport selected

        /**root of the fragments layout, return null if no layout.*/
        return view;
    }//end onCreateView

    private void submitGathering()
    {
        title = mTitleField.getText().toString();
        description = mDescriptionField.getText().toString();
        location = mLocationField.getText().toString();
        time = mTimeField.getText().toString();

        //Makes a new reference to the database (needed everytime you want to access the data base
        Firebase postID = new Firebase(Constants.FIREBASE_URL).child("TEST");
        //postID.push();
        //String key = postID.getKey();

        //postID.setValue(key);
        Firebase sportRef = postID.push();
        final String GatheringID = sportRef.getKey();

        sportRef.child("Title").setValue(title);
        sportRef.child("Description").setValue(description);
        sportRef.child("Location").setValue(location);
        sportRef.child("Time").setValue(time);

        //Intent intent = new Intent(SportFragment.this, SportListActivity.class);
        //startActivity(intent);
        //finish();
        getActivity().finish();
    }

    @Override
    public void onDestroyView()
    {//Called when the view hierarchy associated with the fragment is being removed.
        super.onDestroyView();
        Log.i(TAG, "onDestroyView()");
        ButterKnife.unbind(this);
    }

}