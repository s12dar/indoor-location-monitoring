package com.example.localizationserdar;

import android.app.Application;

import com.example.localizationserdar.datamodels.User;

public class LocalizationLevel extends Application {

    public static LocalizationLevel mInstance;
    public User currentUser;

    public static LocalizationLevel getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
