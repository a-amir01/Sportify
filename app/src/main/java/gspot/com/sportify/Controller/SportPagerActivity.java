package gspot.com.sportify.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import gspot.com.sportify.Model.Sport;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;

/**
 * Created by amir on 4/17/16.
 */
public class SportPagerActivity extends FragmentActivity {

    private static final String EXTRA_SPORT_ID = "sport_id";
    private static final String POSITION_ID = "position_id";
    private static final String TAG = SportPagerActivity.class.getSimpleName();

    /*load the pager*/
    private ViewPager mViewPager;

    /*list of all mSports*/
    private List<Sport> mSports;

    /*Used when the back button is pressed
     *position of the sport we are at in the list*/
    private int mCurrSportPos;

    /*
   * Function to Create a new intent and save the sport Id for when the
   * Activity gets started*/
    public static Intent newIntent(Context packageContext, UUID sportId){
        Log.i(TAG, "newIntent()");
        Intent intent = new Intent(packageContext, SportPagerActivity.class);
        intent.putExtra(EXTRA_SPORT_ID, sportId);
        return intent;
    }/*end newIntent*/


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_pager);

        /*get the id of the SportFragment that was passed into newIntent*/
        UUID sportId = (UUID) getIntent().getSerializableExtra(EXTRA_SPORT_ID);

        Log.d(TAG, "onCreate()");

        mViewPager = (ViewPager) findViewById(R.id.activity_sport_pager_view_pager);

        mSports = SportLab.get(this).getSports();

        FragmentManager fragmentManager = getSupportFragmentManager();

        /*manage the conversation between the ViewPager*/
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            /* Fetch the Sport instance for the given position in the dataset.
            * it uses the Sport id to create and return a properly configured
            * Sport, it will also load a fragment before and after this position for
            * fast loading*/
            @Override
            public Fragment getItem(int position) {

                Log.d(TAG, "getItem()::FSPA " + position + " " + mViewPager.getCurrentItem());

                /*get the sport that is being loaded*/
                Sport sport = mSports.get(position);

                /*the position of the fragment that is being loaded*/
                sport.mPosition = position;

                /*position of the current sport on screen*/
                mCurrSportPos = mViewPager.getCurrentItem();

                return SportFragment.newInstance(sport.getId());
            }

            /*return the number of items in the array list*/
            @Override
            public int getCount() {
                Log.d(TAG, "getCount()::FSPA");
                return mSports.size();
            }
        });

        /*by default the ViewPager shows the 1st Item in its
         * PagerAdapter which is the first sport that was clicked
         * to show the sport that was selected you can find the
         * id that will match the sportID
         */
        for(int i = 0; i < mSports.size(); i++){
            if(mSports.get(i).getId().equals(sportId)){
                mViewPager.setCurrentItem(i);
                break;
            }/*end if*/
        }/*end for*/
    }/*end onCreate*/

    /*
   * this function will be called when the
   * user presses the back button*/
    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed()");

        /*you must first set the result before calling the
         * super class family */
        returnResult(mCurrSportPos);
        super.onBackPressed();
    }/*end onBackPressed*/

    /*
    * Function to store the value we want to return back to the caller
    * this function calls setResult because this activity was called by
    * startActivityForResult and this is how we get the data back to the caller
    * @param position: the position of the sport in the list*/
    public void returnResult(int position) {
        Log.d(TAG, "returnResult " + position);
        /*start a new intent to store data in*/
        Intent data = new Intent();
        /*store the position*/
        data.putExtra(POSITION_ID, position);
        /*return the position back to the caller*/
        setResult(RESULT_OK, data);
    }/*end returnResult*/
}
