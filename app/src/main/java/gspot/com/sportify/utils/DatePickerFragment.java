package gspot.com.sportify.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class for picking a date used in create gathering page
 * fragment_gathering.xml
 *
 * Created by massoudmaher on 5/15/16.
 */
public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

    /*use for logging*/
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    // Extra code for a date
    public static final String EXTRA_DATE =
            "gspot.com.sportify.utils.date";

    private int mYear;
    private int mMonth;
    private int mDay;

    // StringBuilder so we can store reference to set date and get it after this
    // dialog goes out of scope, cant be string bc they are immutable
    private StringBuilder mDateString;

    /**
     *
     * @param savedInstanceState
     * @return DatePickerDialog with calendar set to current date
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // TODO set these to last set date and time

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     *  Updates instance vars when date is set
     * @param view
     * @param year
     * @param month
     * @param day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;

        setmDateString(year + "-" +  month + "-" + day);

        Log.d(TAG, year + "-" +  month + "-" + day);
    }

    /**
     * To allow fetching of date from this fragment
     *
     * @param resultCode
     * @param date
     */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }



    /**
     * Get string representation of selected date
     * @return String in format "dd-MM-yyyy" or "d-M-yyyy"
     * NOTE: not necesarily 2 digits, will either be one or two digits
     */
    public String toString() {
        return mDay + "-" + mMonth + "-" + mYear;
    }

    public StringBuilder getmDateString() {
        return mDateString;
    }

    public void setmDateString(String mDateString) {
        this.mDateString = new StringBuilder(mDateString);
    }
}
