package gspot.com.sportify.Controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * Created by amir on 4/17/16.
 */
public class SignupActivity extends Activity{
    private static final String TAG = LoginActivity.class.getSimpleName();

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated User */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    /*link to the widgets*/
    @Bind(R.id.input_email) EditText mEmailText;
    @Bind(R.id.input_password) EditText mPasswordText;
    @Bind(R.id.btn_signup) Button mSignupButton;
    @Bind(R.id.link_login) TextView mSigninText;
    @Bind(R.id.input_name) EditText mNameText;

    /* onClick()
    * Annotation listener for the signup button
    * Once the button is clicked the signup() is called
    * to create an account for the User
    * */
    @OnClick(R.id.btn_signup)
    void onClick(View v) { signup(); }

    /* onClick()
    * Annotation listener for the login link
    * closes the activity and goes back to the
    * signin page
    * */
    @OnClick(R.id.link_login)
    void onClick() { finish(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        setContentView(R.layout.activity_signup);

        /*link the widgets to the members*/
        ButterKnife.bind(this);

    } //end onCreate


    /*
    * singup()
    * utility function to sing the User with the app and save their
    * information in our database*/
    private void signup() {
        Log.i(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        } //end if

        mSignupButton.setEnabled(false);  //disable the button

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        // TODO: Implement your signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    } //end signup


    /* onSignupSuccess
     * IF signup is successful then we will return back to the caller activity
     * and proceed the user to the home page
     * */
    public void onSignupSuccess() {

        Log.i(TAG, "onSignupSuccess");
        mSignupButton.setEnabled(true);
        //this is how we pass data back to the function onActivityForResult
        setResult(RESULT_OK, null);
        //terminate activity
        finish();
    }//end onSignupSuccess

    /*
     * onSignupFailed()
     * notify the user their signup failed
     * */
    public void onSignupFailed() {
        Log.i(TAG, "onSignupFailed");
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true); //enable the button for input
    } //end onSignupFailed


    /*
    * validate()
    * This utility function will check and make sure that the
    * User has entered the correct email and password syntax
    * */
    private boolean validate() {
        Log.i(TAG, "validate");
        boolean valid = true;

        /*get the User input*/
        String name = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } //end if
        else {
            mNameText.setError(null);
        }//end else

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
        }//end if
        else {
            mPasswordText.setError(null);
        }//end else

        return valid;
    }//end validate()
}// end SignupActivity

