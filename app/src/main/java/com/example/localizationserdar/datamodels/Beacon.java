package com.example.localizationserdar.datamodels;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

import static com.example.localizationserdar.utils.Constants.BEACON_COUNT;
import static com.example.localizationserdar.utils.Constants.BEACON_DESC;
import static com.example.localizationserdar.utils.Constants.BEACON_ID;
import static com.example.localizationserdar.utils.Constants.BEACON_LOCATION;
import static com.example.localizationserdar.utils.Constants.BEACON_NAME;

public class Beacon {
    @PropertyName(BEACON_NAME)
    public String beaconName;
    @PropertyName(BEACON_ID)
    public String beaconId;
    @PropertyName(BEACON_DESC)
    public String beaconDesc;
    @PropertyName(BEACON_COUNT)
    public String beaconCount;
    @PropertyName(BEACON_LOCATION)
    public GeoPoint beaconLocation;


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(BEACON_NAME, beaconName);
        map.put(BEACON_ID, beaconId);
        map.put(BEACON_DESC, beaconDesc);
        map.put(BEACON_COUNT, beaconCount);
        map.put(BEACON_LOCATION, beaconLocation);

        return map;
    }
}
