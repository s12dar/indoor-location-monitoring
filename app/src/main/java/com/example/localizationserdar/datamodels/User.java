package com.example.localizationserdar.datamodels;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

import static com.example.localizationserdar.utils.Constants.CREATED_AT;
import static com.example.localizationserdar.utils.Constants.EMAIL;
import static com.example.localizationserdar.utils.Constants.FIRST_NAME;
import static com.example.localizationserdar.utils.Constants.LAST_NAME;
import static com.example.localizationserdar.utils.Constants.PHONE_NUMBER;
import static com.example.localizationserdar.utils.Constants.UID;
import static com.example.localizationserdar.utils.Constants.VERIFICATION_STATUS;

public class User {

    @PropertyName(FIRST_NAME)
    public String firstName;
    @PropertyName(LAST_NAME)
    public String lastName;
    @PropertyName(EMAIL)
    public String email;
    @PropertyName(PHONE_NUMBER)
    public String phoneNumber;
    @PropertyName(UID)
    public String userId;
    @PropertyName(CREATED_AT)
    public Timestamp createdAt;
    @PropertyName(VERIFICATION_STATUS)
    public String verificationStatus;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIRST_NAME, firstName);
        map.put(LAST_NAME, lastName);
        map.put(EMAIL, email);
        map.put(PHONE_NUMBER, phoneNumber);
        map.put(CREATED_AT, createdAt);
        map.put(UID, userId);
        map.put(VERIFICATION_STATUS, verificationStatus);

        return map;
    }

    public String getFirstName() {
        return firstName;
    }

}
