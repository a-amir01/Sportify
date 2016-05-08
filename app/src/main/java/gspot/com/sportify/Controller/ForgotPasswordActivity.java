package gspot.com.sportify.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
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

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated User */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    /*link to the widgets*/
    @Bind(R.id.input_email)
    EditText mEmailText;
    @Bind(R.id.btn_forgot_password_email)
    Button mForgotPwdButton;

    String email;

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

    /*
    * sendEmail()
    * utility function to sing the User with the app and save their
    * information in our database*/
    private void sendEmail() {
        Log.i(TAG, "in sendEmail()");
        // Get the firebase refernce
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        mForgotPwdButton.setEnabled(false);

        // Get user provided email
        email = mEmailText.getText().toString();

        mFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                switch(firebaseError.getCode()){
                    case FirebaseError.INVALID_EMAIL:
                        mEmailText.setError("Enter a valid email address");
                        break;
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        mEmailText.setError("The email you specified is not associated with an account");
                        break;
                }
                mForgotPwdButton.setEnabled(true);
            }
        });
    }
}
