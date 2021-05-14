package com.example.localizationserdar.datamodels;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.localizationserdar.utils.Constants.CREATED_AT;
import static com.example.localizationserdar.utils.Constants.EMAIL;
import static com.example.localizationserdar.utils.Constants.FIRST_NAME;
import static com.example.localizationserdar.utils.Constants.IS_VERIFIED;
import static com.example.localizationserdar.utils.Constants.LAST_LOCATION_UPDATED_AT;
import static com.example.localizationserdar.utils.Constants.LAST_NAME;
import static com.example.localizationserdar.utils.Constants.PHONE_NUMBER;
import static com.example.localizationserdar.utils.Constants.STATUS;
import static com.example.localizationserdar.utils.Constants.UID;
import static com.example.localizationserdar.utils.Constants.USER_LIVE_LOCATION;

public class User {

    @PropertyName(FIRST_NAME)
    public String firstName;
    @PropertyName(LAST_NAME)
    public String lastName;
    @PropertyName(EMAIL)
    public String email;
    @PropertyName(PHONE_NUMBER)
    public String phoneNumber;
    @PropertyName(STATUS)
    public String status;
    @PropertyName(UID)
    public String userId;
    @PropertyName(CREATED_AT)
    public Timestamp createdAt;
    @PropertyName(IS_VERIFIED)
    public Boolean isVerified;
    @PropertyName(USER_LIVE_LOCATION)
    public GeoPoint liveLocation;
    @PropertyName(LAST_LOCATION_UPDATED_AT)
    public Timestamp lastLocationUpdatedAt;

    public List<Beacon> beacons;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIRST_NAME, firstName);
        map.put(LAST_NAME, lastName);
        map.put(EMAIL, email);
        map.put(STATUS, status);
        map.put(PHONE_NUMBER, phoneNumber);
        map.put(CREATED_AT, createdAt);
        map.put(UID, userId);
        map.put(IS_VERIFIED, isVerified);
        map.put(USER_LIVE_LOCATION, liveLocation);
        map.put(LAST_LOCATION_UPDATED_AT, lastLocationUpdatedAt);

        return map;
    }

    public String getFirstName() {
        return firstName;
    }

}
