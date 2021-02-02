package com.example.localizationserdar;

import android.app.Application;

import com.example.localizationserdar.datamanager.DataListener;
import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.User;

import java.util.List;

public class LocalizationLevel extends Application {

    public static LocalizationLevel mInstance;
    public User currentUser;
    public Beacon beacon;
    public List<Beacon> allBeacons;

    private DataListener<List<Object>> listener;
    private boolean loadingData = false;

    public static LocalizationLevel getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
