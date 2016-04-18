package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by amir on 4/17/16.
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        SharedPreferences settings = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //activity to start
        Intent intent;

        /*see if the flag exists if not return the default value*/
        boolean firstRun = settings.getBoolean("firstRun",true);

        //if running for first time
        if(firstRun){
            Log.i(TAG, "first Run");
            SharedPreferences.Editor editor = settings.edit();

            /*flag to search if app was run before*/
            editor.putBoolean("firstRun",false);
            /*commit the change*/
            editor.commit();

            /*start the login activity*/
            intent = new Intent(MainActivity.this, LoginActivity.class); //Activity to be launched For the First time
        } //end if

        else {
            Log.i(TAG, "Not first Run");
            /*start the home activity*/
            intent = new Intent(MainActivity.this, SportListActivity.class); //Default Activity
        } //end else

        /*start the correct activity*/
        startActivity(intent);
        /*terminate current activity*/
        finish();

    }//end onCreate
} //end MainActivity

