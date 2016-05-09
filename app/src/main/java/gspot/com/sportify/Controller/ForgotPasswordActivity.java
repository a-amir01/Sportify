package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;

/**
 * Created by Anshul and Armin on 5/8/2016.
 */
public class ForgotPasswordActivity extends Activity {
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    /* Code for when the User requests forgot password */
    private static final int REQUEST_CHANGE = 0;

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /*link to the widgets*/
    @Bind(R.id.input_email_fpwd)
    EditText mEmailText;
    @Bind(R.id.btn_forgot_password_email)
    Button mForgotPwdButton;

    // Will hold the email from the form
    String mEmail;

    /* onClick()
     * Annotation listener for the signup button
     * Once the button is clicked the signup() is called
     * to create an account for the User
     * */
    @OnClick(R.id.btn_forgot_password_email)
    void onClick(Button button) { sendEmail(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        setContentView(R.layout.activity_forgot_password);

        /*link the widgets to the members*/
        ButterKnife.bind(this);

    } //end onCreate

    /* sendEmail()
     * utility function to send an email to the user with a randomly created
     * password that has replaced theirs
     * */
    private void sendEmail() {
        Log.i(TAG, "in sendEmail()");
        // Get the Firebase reference
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        // Disable the button
        mForgotPwdButton.setEnabled(false);

        // Get user provided email
        mEmail = mEmailText.getText().toString();

        // Ensure the email is of a valid format
        if (mEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mEmailText.setError("Enter a valid email address");
            mForgotPwdButton.setEnabled(true);
        } //end if
        else {
            mEmailText.setError(null);
            // Call Firebase's resetPassword method
            mFirebaseRef.resetPassword(mEmail, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    // Let the user know that the email was sent
                    Toast.makeText(getApplicationContext(),
                            "Password reset email sent",
                            Toast.LENGTH_LONG).show();

                    // Create an intent to send the user to change the
                    // password
                    Intent intent = new Intent(getApplicationContext(),
                            ChangePasswordActivity.class);
                    // Send the user's email with the intent
                    intent.putExtra("Email", mEmail);
                    // Start the intent
                    startActivityForResult(intent, REQUEST_CHANGE);

                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    /* Check for errors */
                    switch(firebaseError.getCode()){
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            mEmailText.setError("The email you specified is not valid");
                            break;
                    }

                    // Re-enable the button
                    mForgotPwdButton.setEnabled(true);
                }
            });
        } //end else

    }
}
