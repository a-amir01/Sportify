package gspot.com.sportify.Model;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Observable;

import gspot.com.sportify.utils.App;

/**
 * Created by yunfanyang on 5/8/16.
 */
public class SportTypes extends Observable {

    public ArrayList<SportType> sportTypes = new ArrayList<>();

    public void readSportTypes()
    {

        App.dbref.child("Sports").addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                SportType newPost = snapshot.getValue(SportType.class);
                newPost.addSID(snapshot.getKey());
                sportTypes.add(newPost);
                setChanged();
                notifyObservers();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}