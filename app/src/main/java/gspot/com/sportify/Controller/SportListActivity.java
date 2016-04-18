package gspot.com.sportify.Controller;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by amir on 4/17/16.
 * This Class extends SingleFragmentActivity which is a singleton class
 * before the CreateFragment() is called the onCreate method
 * in SingleFragmentActivity is invoked and the fragment manager is
 * initialized to start our fragment activity
 * this class creates a new SportsListFragment which is our
 * listView for all the sports
 */
public class SportListActivity extends SingleFragmentActivity {

    /*use for logging*/
    private static final String TAG = SportListActivity.class.getSimpleName();


    @Override
    protected Fragment createFragment() {
        Log.d(TAG, "createFragmnt()");
        return new SportListFragment();
    }//end CreateFragment

} //end SportListActivity
