package gspot.com.sportify.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Authors Amir Assad, massoudmaher on 5/1/16.
 * Class that represents a single gathering
 * Purpose is to instantiate one of these for each created event and push to firebase
 */
public class Gathering {

    private String mSportTitle;
    private String mLocation;
    private String mDescription;
    private String mHostID;
    private String mTime;
    private String mSID;
    public String mID;

    public Gathering() {
    }

    public void setSportTitle (String title) { this.mSportTitle = title; }
    public String getSportTitle () { return mSportTitle; }

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

    public void setID (String ID) { this.mSID = ID; }
    public String getID () { return mID; }
}
