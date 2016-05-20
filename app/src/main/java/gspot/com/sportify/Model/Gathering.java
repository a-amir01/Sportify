package gspot.com.sportify.Model;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.UUID;

import gspot.com.sportify.utils.App;

/**
 * Authors Amir Assad, massoudmaher on 5/1/16.
 * Class that represents a single gathering
 * Purpose is to instantiate one of these for each created event and push to firebase
 */
public class Gathering{


    public static enum SkillLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        ADVANCED("Advanced");

        private final String skillLevel;

        SkillLevel(String skillLevel) {
            this.skillLevel = skillLevel;
        }

        public String getSkillLevel() {
            return skillLevel;
        }

        @Override
        public String toString() {
            return this.skillLevel;
        }
    }

    private SkillLevel mSkillLevel;
    private String mGatheringTitle;
    private String mLocation;
    private String mDescription;
    private String mHostID;
    private String mTime;
    private String mSID;
    public String mID;
    private boolean mIsPrivate;
    private ArrayList<String> mAttendees;
    private ArrayList<String> mPendings;
    private int mTimeOfDay;
    private String mDate;

    public Gathering() {
        mIsPrivate = false;
        mAttendees = new ArrayList<String>();
        mPendings = new ArrayList<String>();
        mSkillLevel = SkillLevel.BEGINNER;
    }

    public void setSportTitle (String title) { this.mGatheringTitle = title; }
    public String getSportTitle () { return mGatheringTitle; }

    public void setLocation (String location) { this.mLocation = location; }
    public String getLocation () { return mLocation; }

    public void setDescription (String description) { this.mDescription = description; }
    public String getDescription () { return mDescription; }

    public void setHostID (String hostID) { this.mHostID = hostID; }
    public String getHostID () { return mHostID; }

    public void setTime (String time) { this.mTime = time; }
    public String getTime () { return mTime; }

    public void setSID (String SID) { this.mSID = SID; }
    public String getSID () { return mSID; }

    public void setID (String ID) { this.mID = ID; }
    public String getID () { return mID; }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setIsPrivate (boolean isPrivate) { this.mIsPrivate = isPrivate; }
    public boolean getIsPrivate () { return mIsPrivate; }

    public void setAttendees(ArrayList<String> attendees) { this.mAttendees = attendees; }
    public ArrayList<String> getAttendees () { return mAttendees; }

    public void setPending(ArrayList<String> pendings) { this.mPendings = pendings; }
    public ArrayList<String> getPendings () { return mPendings; }

    public void setTimeOfDay (int timeofDay) { this.mTimeOfDay = timeofDay; }
    public int getTimeOfDay () { return mTimeOfDay; }

    public void setDate (String date) { this.mDate = date; }
    public String getDate () { return mDate; }

    public void setSkillLevel (SkillLevel skillLevel) { this.mSkillLevel = skillLevel; }
    public SkillLevel getSkillLevel () { return mSkillLevel;    }

    public void delete()
    {
        App.dbref.child("Gatherings").child(mID).removeValue();
    }
}
