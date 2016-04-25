package gspot.com.sportify.utils;

import gspot.com.sportify.BuildConfig;

/**
 * Created by patrickhayes on 4/20/16.
 * Constants class store most important strings and paths of the app
 */

public class Constants {

    /*Constants for FireBase */


    public static final String PASSWORD_PROVIDER = "password";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_USER_LISTS = "userLists";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";

    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;



}
