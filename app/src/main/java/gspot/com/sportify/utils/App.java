package gspot.com.sportify.utils;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import gspot.com.sportify.Model.Gathering;

/**
 * Created by yunfanyang on 5/2/16.
 */
public class App {
    public static Firebase dbref = new Firebase("https://gspot.firebaseio.com");
    public static List<Gathering> mGatherings = new ArrayList<>();
    /* current gathering selected*/
    public static Gathering mCurrentGathering = null;

}