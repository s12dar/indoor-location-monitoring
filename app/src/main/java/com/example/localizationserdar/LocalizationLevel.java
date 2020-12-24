package com.example.localizationserdar;

import android.app.Application;

import com.example.localizationserdar.datamodels.User;

public class LocalizationLevel extends Application {

    private static LocalizationLevel mInstance;
    public User currentUser;

    public static LocalizationLevel getInstance() {
        return mInstance;
    }

    public static void setmInstance(LocalizationLevel mInstance) {
        LocalizationLevel.mInstance = mInstance;
    }
}
