package gspot.com.sportify.Controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gspot.com.sportify.Model.GspotCalendar;
import gspot.com.sportify.Model.MySport;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;
import gspot.com.sportify.utils.StateWrapper;
import gspot.com.sportify.utils.UserPicture;

/*
 * Class that handles viewing and editing a profile. We only have one profile activity, but it has
 * 3 states. 1. Viewing your own profile. 2. Editing your profile. 3. Viewing someone else's profile.
 * We move between the three states by hiding buttons, changing the name of buttons, and enabling
 * and disabling textfields.
 *
 * Made by Don Vo and Patrick Hayes.
 */
public class ProfileActivity extends BaseNavBarActivity {

    /* A TAG to each log statement to indicate the source of the log message */
    private static final String TAG = ProfileActivity.class.getSimpleName();

    /* Member Variables */
    private String mCurrentUser;
    private String mOwner;
    private StateWrapper mState = new StateWrapper(StateWrapper.State.VIEW_OTHER);
    private Profile mProfile;
    private GspotCalendar mCalendar;
    private ImageView[][] mDaysOfWeek = new ImageView[7][4]; // Each calendar box

    //adapter for expandable list
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> sportsParent;
    private HashMap<String, MySport> sportsChildren;

    // Decrypts the time in calendar


    /* Bind the buttons and text fields */
    // @Bind(R.id.sport_title) EditText mTitleField;
    // @Bind(R.id.profile_picture) ImageView mProfilePicture;
    @Bind(R.id.user_name)
        EditText mName;
    @Bind(R.id.edit_save_button)
        Button mEditSaveButton;
    @Bind(R.id.bio_content)
        EditText mBio;
    @Bind(R.id.contact_content)
        EditText mContactInfo;
    @Bind(R.id.profile_picture)
        ImageView mProfilePicture;
    @Bind(R.id.add_sport)
        Button mAddSport;
    @Bind(R.id.sports_list)
        ExpandableListView mSportsList;
    @Bind(R.id.no_sports)
        TextView mNoSports;

    /* Bind each time cell in the calendar */
    @Bind({R.id.sun_morning, R.id.sun_afternoon,
            R.id.sun_evening, R.id.sun_night})
    ImageView[] mSunday;
    @Bind({R.id.mon_morning, R.id.mon_afternoon,
            R.id.mon_evening, R.id.mon_night})
    ImageView[] mMonday;
    @Bind({R.id.tue_morning, R.id.tue_afternoon,
            R.id.tue_evening, R.id.tue_night})
    ImageView[] mTuesday;
    @Bind({R.id.wed_morning, R.id.wed_afternoon,
            R.id.wed_evening, R.id.wed_night})
    ImageView[] mWednesday;
    @Bind({R.id.thu_morning, R.id.thu_afternoon,
            R.id.thu_evening, R.id.thu_night})
    ImageView[] mThursday;
    @Bind({R.id.fri_morning, R.id.fri_afternoon,
            R.id.fri_evening, R.id.fri_night})
    ImageView[] mFriday;
    @Bind({R.id.sat_morning, R.id.sat_afternoon,
            R.id.sat_evening, R.id.sat_night})
    ImageView[] mSaturday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        /* link the widgets to the members */
        ButterKnife.bind(this);

        /* Get the uid from shared preferences */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");

        /* Updates and creates listeners for each calendar time cell */
        updateCalendarView();

        final android.content.Context context = this.getApplicationContext();

        Log.e(TAG, "This is the current user  " + mCurrentUser);


        //mAddSport.setVisibility(View.INVISIBLE);
        Firebase profileRef = new Firebase(Constants.FIREBASE_URL_PROFILES).child(mCurrentUser);

        /* Populate the page with the user's information */
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Create a user profile object from data in the database */
                mProfile = dataSnapshot.getValue(Profile.class);

                /* Retrieve text information from the database */
                mName.setText(mProfile.getmName());
                mBio.setText(mProfile.getmBio());
                mContactInfo.setText(mProfile.getmContactInfo());
                mOwner = mProfile.getmOwner();
                mCalendar = mProfile.getmCalendar();

                if (mCurrentUser.equals(mOwner)) {
                    mState.setState(StateWrapper.State.VIEW_MINE);
                }

                /* Set up the calendar with times from the database */
                populateCalendar();
                 /* adapter */
                prepareListData();

                listAdapter = new ExpandableListAdapter(context, sportsParent, sportsChildren, mState, mProfile);

                // setting list adapter
                mSportsList.setAdapter(listAdapter);
                mSportsList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

                //if the person doesn't have any sport profiles tell them
                if (mProfile.getmMySports() == null) {
                    mNoSports.setVisibility(View.VISIBLE);
                    mNoSports.setText("" + mProfile.getmName().toString() + " doesn't have any "
                                        + "sports profiles");
                }
                else {
                    mNoSports.setVisibility(View.INVISIBLE);
                }


                /* Give editing power to the owner of the profile */
                if (mCurrentUser.equals(mOwner)) {
                    toggleToViewMine();

                /* Ensure that an arbitrary user does not have access to edit profile */
                } else {
                    Log.e(TAG, "mCurrentUser " + mCurrentUser);
                    Log.e(TAG, "mOwner " + mOwner);
                    toggleToViewOther();
                }
            }

            /* Otherwise, never mind */
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "FireBaseError " + firebaseError.getMessage());

            }
        });


    }

    /**
     * On Destroy Method
     * Destroys the activity and destroys its contents.
     * Called when the view hierarchy associated with the
     * Activity is being removed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        ButterKnife.unbind(this);
    }

    /**
     * Toggle Edit Save
     * Creates a listener for the Edit/Save Button.
     * If the edit button is tapped in view mode, the
     * system will transition to edit mode.
     * If the system button is tapped in edit mode, the
     * system will save data entered to the database,
     * and revert to view mode.
     */
    @OnClick(R.id.edit_save_button)
    public void toggleEditSave() {

        /* Edit if the button is pressed in View Mode */
        if (mState.getState() == StateWrapper.State.VIEW_MINE) {
            toggleToEdit();

        /* Save and View if the button is pressed in Editm Mode */
        } else {
            mProfile.setmName(mName.getText().toString());
            mProfile.setmBio(mBio.getText().toString());
            mProfile.setmContactInfo(mContactInfo.getText().toString());
            mProfile.updateProfile();
            toggleToViewMine();
        }
    }

    /**
     * On Tap Change Color And Toggle Method
     * When tapped, change the color of the corresponding
     * cell so that it changes color (red to green, green to red).
     * This will also send information to the models for updating.
     *
     * @param view - each cell in the calendar
     */
    @OnClick({R.id.sun_morning, R.id.sun_afternoon, R.id.sun_evening, R.id.sun_night,
            R.id.mon_morning, R.id.mon_afternoon, R.id.mon_evening, R.id.mon_night,
            R.id.tue_morning, R.id.tue_afternoon, R.id.tue_evening, R.id.tue_night,
            R.id.wed_morning, R.id.wed_afternoon, R.id.wed_evening, R.id.wed_night,
            R.id.thu_morning, R.id.thu_afternoon, R.id.thu_evening, R.id.thu_night,
            R.id.fri_morning, R.id.fri_afternoon, R.id.fri_evening, R.id.fri_night,
            R.id.sat_morning, R.id.sat_afternoon, R.id.sat_evening, R.id.sat_night})
    public void onTapChangeColorAndToggle(ImageView view) {

        /* Get the row and column of the button, and toggle availability */
        int day = (int) view.getTag() / Constants.TAG_CODE;
        int time = (int) view.getTag() % Constants.TAG_CODE;
        mCalendar.toggleTime(day, time);

        /* If the user is available at this time, set button to green */
        if (mCalendar.getAvailability(day, time)) {
            mDaysOfWeek[day][time].setImageResource(R.color.available);

        /* If the user is busy at this time, set button to red */
        } else {
            mDaysOfWeek[day][time].setImageResource(R.color.busy);
        }
    }





    /**
     * Toggle To "View Mine" Method
     * This method should be called when the the user taps on the
     * 'Save' button. This method will exit 'Edit' mode, and
     * return the 'View' mode where the user will only have view
     * access to his own profile.
     */
    private void toggleToViewMine() {
        Log.e(TAG, "ViewMine");

        /* Disable all text fields so that they are no longer editable */
        disableAllInputs();

        /* Show the Edit button */
        mEditSaveButton.setVisibility(View.VISIBLE);
        mEditSaveButton.setText("Edit Profile");

        /* Remove the add sport button */
        mAddSport.setVisibility(View.GONE);
        mState.setState(StateWrapper.State.VIEW_MINE);
        listAdapter.notifyDataSetChanged();
    }


    /**
     * Toggle To "View Other" Method
     * This method should be called under the assumption that the
     * the user is viewing another user's profile.
     * Similar to toggleToViewMine(), the user will be able to view
     * all the contents of a user's profile except for the 'edit' button.
     */
    private void toggleToViewOther() {
        Log.e(TAG, "viewOther");

        /* Ensure that all inputs are disabled */
        disableAllInputs();

        /* Ensure that the edit/save and add sport button are not visible */
        mEditSaveButton.setVisibility(View.INVISIBLE);
        mAddSport.setVisibility(View.GONE);
        mState.setState(StateWrapper.State.VIEW_OTHER);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Toggle To "Edit" Method
     * This method should be called after the user taps the
     * 'Edit Profile' button. Once this is tapped, the user
     * should be able to freely edit the contents of their
     * profile.
     */
    private void toggleToEdit() {
        Log.e(TAG, "edit");

        /* Allow all fields to be editable */
        enableAllInputs();

        /* Show the save button */
        mEditSaveButton.setVisibility(View.VISIBLE);
        mEditSaveButton.setText("Save");

        /* Remove the add sport button */
        mAddSport.setVisibility(View.VISIBLE);
        mState.setState(StateWrapper.State.EDIT);
        Log.e(TAG, "THis is the state" + mState);
        listAdapter.notifyDataSetChanged();
    }


    /**
     * Disable All Inputs Method
     * Ensures that all fields are not editable.
     */
    private void disableAllInputs() {

        /* Enable the photo, name, bio, & contact info to be editable */
        mProfilePicture.setEnabled(false);
        mName.setEnabled(false);
        mBio.setEnabled(false);
        mContactInfo.setEnabled(false);

        //disable calendar
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 4; ++j) {
                mDaysOfWeek[i][j].setEnabled(false);
            }
        }
    }


    /**
     * Enable All Inputs Method
     * Ensures that all fields are editable.
     */
    private void enableAllInputs() {

        /* Disable the photo, name, bio, & contact info from being editable */
        mProfilePicture.setEnabled(true);
        mName.setEnabled(true);
        mBio.setEnabled(true);
        mContactInfo.setEnabled(true);

        //enable calendar
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 4; ++j) {
                mDaysOfWeek[i][j].setEnabled(true);
            }
        }
    }


    /**
     * Populate Calendar Method
     * Using data from the user profile object,
     * this method updates all buttons from the user
     * database.
     */
    private void populateCalendar() {

        /* For each button the availability matrix, set its color */
        for (int i = 0; i < Constants.NUM_DAYS_OF_WEEK; ++i) {
            for (int j = 0; j < Constants.NUM_TIMES_OF_DAY; ++j) {

                /* Set the color to green if available */
                if (mCalendar.getAvailability(i, j)) {
                    mDaysOfWeek[i][j].setImageResource(R.color.available);

                /* Set the color to red if busy */
                } else {
                    mDaysOfWeek[i][j].setImageResource(R.color.busy);
                }

            }
        }

    }

    /**
     * Update Calendar View Method
     * For each column in the calendar, retrieve the row of
     * time cells into a storage location for simpler manipuation.
     */
    void updateCalendarView() {

        /* Retrieve all buttons for each day of the week */
        mDaysOfWeek[0] = mSunday;
        mDaysOfWeek[1] = mMonday;
        mDaysOfWeek[2] = mTuesday;
        mDaysOfWeek[3] = mWednesday;
        mDaysOfWeek[4] = mThursday;
        mDaysOfWeek[5] = mFriday;
        mDaysOfWeek[6] = mSaturday;

        /* Label each cell of the calendar with a row and column */
        tagTheCalendar(mDaysOfWeek);
    }


    /**
     * Tag The Calendar Method
     * For each cell in the calendar, label each box so we
     * can always identify what column and hat row it is in.
     * - getTag() / 10 (tag code) = column (corresponds to day of week)
     * - getTag() % 10 (tag code) = row (corresponds to time of day)
     * In other words, first digit represents the day of the week (0-6)
     * second digit represents the time of the day (0-4)
     *
     * @param calendar - the matrix whose time cells that we want to label
     */
    private void tagTheCalendar(ImageView[][] calendar) {

        /* For each time of the week and each time of the day, label each box */
        for (int i = 0; i < Constants.NUM_DAYS_OF_WEEK; ++i) {
            for (int j = 0; j < Constants.NUM_TIMES_OF_DAY; ++j) {
                calendar[i][j].setTag(new Integer(i * Constants.TAG_CODE + j));
            }
        }
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        sportsParent = new ArrayList<String>();
        sportsChildren = new HashMap<String, MySport>();

        List<MySport> mySports = mProfile.getmMySports();

        if (mySports != null) {
            for (int i = 0; i < mySports.size(); i++) {
                sportsParent.add(mySports.get(i).getmSport());
                sportsChildren.put(mySports.get(i).getmSport(), mySports.get(i));
            }
        }
    }

    @OnClick(R.id.profile_picture)
    public void updateProfilePicture(ImageView profPic) {

        // in onCreate or any event where your want the user to
        // select a file
        Intent intent = new Intent();
        // Set the file type we are looking for
        intent.setType(Constants.IMAGE_TYPE);
        // Set the intent to give the user the ability to pick data
        intent.setAction(Intent.ACTION_PICK);

        // Start the intent using the chooser box
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.SELECT_SINGLE_PICTURE);

    }


    /* onActivityResult()
     * This basically gets the Uri, turns it into a bitmap and renders the
     * picture
     * */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.SELECT_SINGLE_PICTURE) {

                // Get the uri data
                Uri selectedImageUri = data.getData();
                // Create a Bitmap variable
                Bitmap wtf = null;

                try {
                    // Attempt to create a bitmap object from the uri using the
                    // UserPicture helper class
                    wtf = new UserPicture(selectedImageUri, getContentResolver()).getBitmap();
                } catch (IOException e) {
                    // If the bitmap creation failed, indicate that
                    Log.e(ProfileActivity.class.getSimpleName(), "Failed to load image", e);
                }

                // Display the image in the ImageView
                mProfilePicture.setImageBitmap(wtf);
                // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
            }
        } else {
            // report failure
            Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_intent_data, Toast.LENGTH_LONG).show();
            Log.d(ProfileActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }


     /**
     * helper to scale down image before display to prevent render errors:
     * "Bitmap too large to be uploaded into a texture"
     */
    private void displayPicture(String imagePath, ImageView imageView) {

        // from http://stackoverflow.com/questions/22633638/prevent-bitmap-too-large-to-be-uploaded-into-a-texture-android

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int height = bitmap.getHeight(), width = bitmap.getWidth();

        if (height > 250 && width > 250){
            Bitmap imgbitmap = BitmapFactory.decodeFile(imagePath, options);
            imageView.setImageBitmap(imgbitmap);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }



}
