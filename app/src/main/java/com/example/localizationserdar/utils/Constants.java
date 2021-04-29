package com.example.localizationserdar.utils;

public class Constants {

    /**
     * USER DATA
     */
    public static final String FIRST_NAME = "firstName";
    public static final String UID = "uid";
    public static final String LAST_NAME = "lastName";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String EMAIL = "email";
    public static final String CREATED_AT = "createdAt";
    public static final String IS_VERIFIED = "isVerified";
    public static final String USER_LIVE_LOCATION = "liveLocation";
    public static final String LAST_LOCATION_UPDATED_AT = "lastLocationUpdatedAt";
    public static final double INITIAL_LAT = 44.458041;
    public static final double INITIAL_LNG = 26.120479;

    /**
     * BEACON DATA
     */
    public static final String BEACON_NAME = "beaconName";
    public static final String BEACON_ID = "beaconId";
    public static final String BEACON_DESC = "beaconDesc";
    public static final String BEACON_COUNT = "beaconCount";
    public static final String BEACON_LOCATION = "beaconLocation";

    /**
     * FIREBASE DATA
     */
    public static final String COLLECTION_USERS = "Users";
    public static final String COLLECTION_BEACONS = "Beacons";

    /**
     * FOR BUNDLE
     */
    public static final String EXISTING_USER = "existingUser";
    public static final String NEW_USER = "newUser";
    public static final String USER_STATUS = "userStatus";
    public static final String EMPTY_STRING = "";

    /**
     * REWARD
     */
    public static final String REWARD_COUNT = "rewardCount";
    public static final String NOT_FIRST_TIME = "notFirstTime";
    public static final String SP_FILES = "spFiles";

    /**
     * PERMISSION CODES
     */
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    /**
     * GOOGLE MAPS
     */
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    /**
     * BEACON NAMES
     */
    public static final String BEACON_IN_TEACHERS_ROOM = "Teacher's room";
    public static final String BEACON_IN_AI_LAB = "AI Laboratory";
    public static final String BEACON_IN_LIBRARY = "Library";
    public static final String BEACON_IN_CANTINA = "Canteen";

}
