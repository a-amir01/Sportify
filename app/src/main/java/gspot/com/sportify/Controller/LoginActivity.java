package gspot.com.sportify.Controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gspot.com.sportify.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /*Code for when the User requests sign up*/
    private static final int REQUEST_SIGNUP = 0;

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated User */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;


    /*link to the widgets*/
    @Bind(R.id.input_email) EditText mEmailText;
    @Bind(R.id.input_password) EditText mPasswordText;
    @Bind(R.id.btn_login) Button mLoginButton;
    @Bind(R.id.link_signup) TextView mSignupText;

    /* onClick()
     * Annotation listener for the login button
     * Once the button is clicked the login() is called
     * to log the User in
     * */
    @OnClick(R.id.btn_login)
    void onClick(Button button) { login(); }

    /* onClick()
     * Annotation listener for the sign up link
     * Once the link is clicked the signup activity is started
     * */
    @OnClick(R.id.link_signup)
    void onClick () {
        Log.i(TAG, "onClick for signup");
        /*create an intent to start the activity*/
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);

        /*expecting data to be returned by SignupActivity*/
        startActivityForResult(intent, REQUEST_SIGNUP);
    }//end onClick()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        setContentView(R.layout.activity_login);

        /*link the widgets to the members*/
        ButterKnife.bind(this);

    } //end onCreate()

    /* onActivityResult()
     * This method fires when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     *  @param requestCode: The integer request code originally supplied to startActivityForResult(),
     *                      allowing you to identify who this result came from.
     * @param resultCode:  the integer result code returned by the child activity through its setResult().
     * @param data: the result data from the caller, call the correct getExtra method to get the data*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult()");

        if(resultCode != Activity.RESULT_OK){  /*something bad happened*/
            Log.d(TAG, "RESULT CANCELED!!");
            return;
        } //end if

        if (requestCode == REQUEST_SIGNUP) {
            /*empty intent passed in*/
            if(data == null) return;

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                finish();
            } //end if

    } //end onActivityResult

    /*
    * onBackPressed() when the User presses the back button this function will be called
    * this function will disable that button and prevent the User from killing the app*/
    @Override
    public void onBackPressed() {
        //Move the task containing this activity to the back of the activity stack.
        // disable going back to the MainActivity
        moveTaskToBack(true);
    } //end onBackPressed()

    /* login()
     * utility function to log the User in and retrieve their information
     * */
    private void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        } //end if

        mLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        // TODO: Implement authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    } //end login()

    /*
    * validate()
    * This utility function will check and make sure that the
    * User has entered the correct email and password syntax
    * */
    private boolean validate() {
        Log.i(TAG, "validate()");

        boolean valid = true;

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } //end if
        else {
            mEmailText.setError(null);
        } //end else

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } //end if
        else {
            mPasswordText.setError(null);
        } //end else

        return valid;
    }//end validate()

    /*
    * onLoginSuccess()
    * re enables the login button and closes the activity
    * */
    public void onLoginSuccess() {
        Log.i(TAG, "onLoginSuccess()");
        mLoginButton.setEnabled(true);
        //TODO on successful sign in retrieve the User's info and send them to the home page
         /*
        Intent intent = new Intent(getApplicationContext(), home.class);
        startActivity(intent);  //redirect to home page
        finish();               //kill this activity
        */
    }//end onLoginSuccess

    /*
    * onLoginFailed()
    * Function to notify User of a failed login
    * reenables the login button
    * */
    public void onLoginFailed() {
        Log.d(TAG, "onLoginFailed()");

        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mLoginButton.setEnabled(true);
    } //end onLoginFailed

}
