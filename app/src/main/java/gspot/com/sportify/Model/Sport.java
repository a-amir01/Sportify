package gspot.com.sportify.Model;

import java.util.UUID;

/**
 * Created by amir on 4/17/16.
 */
public class Sport {

    private String mSportName;

    /*sport's id: use to find the sport among the list*/
    private UUID mId;

    /*position of the crime in the list it is in*/
    public int mPosition;               //TODO can it be done another way?


    public Sport() { mId = UUID.randomUUID(); }

    public String getSportName() { return mSportName; }

    public void setSportName(String sportName) { mSportName = sportName; }

    public UUID getId() { return mId; }
}
