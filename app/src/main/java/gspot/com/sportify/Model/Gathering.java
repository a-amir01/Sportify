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

    private String mSportName;

    /*sport's id: use to find the sport among the list*/
    private UUID mGatheringID;

    /*position of the crime in the list it is in*/
    public int mPosition;               //TODO can it be done another way?

    // Type of sport we are playing, Eg. boxing or soccer, commented until Andrew pushes
    //private SportType type;

    // ID of the host of this gathering
    private int hostID;

    // Exact Time of gathering
    SimpleDateFormat exactTime;

    // Enum that represents approximate time of gathering
    // Morning is 4am - 12
    // Noon 12-4
    // Evening 4-8
    // Night is 8 - 4am
    // TODO Make class in utils that turns SimpleDateFormat into an enum of the 4 options above

    // Location of gathering in longitude/latitude
    private double longitude;
    private double lattitude;

    // Description of gathering
    private String description;

    // List of unique IDs of attendees to gathering
    // TODO figure out type of universal unique IDs
    private ArrayList<Integer> attendees;

    // List of unique IDs of pending requests to join event
    private ArrayList<Integer> pendingRequests;

    public Gathering() { mGatheringID = UUID.randomUUID(); }

    public String getSportName() { return mSportName; }

    public void setSportName(String sportName) { mSportName = sportName; }

    public UUID getId() { return mGatheringID; }

    // Only Getters and setters below, don't bother reading
    public int getHostID() { return hostID; }

    public void setHostID(int hostID) { this.hostID = hostID; }

    public SimpleDateFormat getExactTime() { return exactTime; }

    public void setExactTime(SimpleDateFormat exactTime) { this.exactTime = exactTime;}

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLattitude() { return lattitude; }

    public void setLattitude(double lattitude) { this.lattitude = lattitude; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public ArrayList<Integer> getAttendees() { return attendees; }

    public void setAttendees(ArrayList<Integer> attendees) { this.attendees = attendees; }

    public ArrayList<Integer> getPendingRequests() { return pendingRequests; }

    public void setPendingRequests(ArrayList<Integer> pendingRequests) { this.pendingRequests = pendingRequests; }

}
