package gspot.com.sportify.Controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import gspot.com.sportify.Model.Sport;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;

/**
 * Created by amir on 4/17/16.
 */
public class SportFragment extends Fragment {

    /*use for logging*/
    private static final String TAG = SportFragment.class.getSimpleName();

    /*the sport profile*/
    private Sport mSport;

    private static final String ARG_SPORT_ID = "sport_id";


    @Bind(R.id.sport_title) EditText mTitleField;

    @OnTextChanged(R.id.sport_title)
    void onTextChange(CharSequence text, int start, int before, int count) { mSport.setSportName(text.toString()); }


    /* will be called when a new SportFragment needs to be created
     * This method Creates a fragment instance and bundles up &
     * sets its arguments,
     * attaching arguments to a fragment must be done after the fragment is created
     * but before it is added to an activity.*/
    public static SportFragment newInstance(UUID sportId) {
        Log.d(TAG, "newInstance()");
        Bundle args = new Bundle();
        args.putSerializable(ARG_SPORT_ID, sportId);    /*store the sportId for later retreival*/
        SportFragment fragment = new SportFragment();   /*create a new instance of the fragment*/
        fragment.setArguments(args);                    /*bundle up the arguments*/
        return fragment;
    } //end newInstance

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        UUID sportId = (UUID) getArguments().getSerializable(ARG_SPORT_ID);
        mSport = SportLab.get(getActivity()).getSport(sportId);
    }//end onCreate

    /**inflate the layout for the fragments view and return the view to the host*/
    @Override                //*inflate the layout   *from the activities   *layout recreate from a saved state
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {/**gets called when its the 1st time drawing its UI*/
        /**the 3rd param: whether the inflated layout should be attached to the 2nd param during inflation*/
        Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_sport, parent, false);
        ButterKnife.bind(this, view);
        /**show the sport*/
        mTitleField.setText(mSport.getSportName());

        /**root of the fragments layout, return null if no layout.*/
        return view;
    }//end onCreateView


    @Override
    public void onDestroyView()
    {//Called when the view hierarchy associated with the fragment is being removed.
        super.onDestroyView();
       Log.i(TAG, "onDestroyView()");
        ButterKnife.unbind(this);
    }

}
