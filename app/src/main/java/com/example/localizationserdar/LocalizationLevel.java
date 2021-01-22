package com.example.localizationserdar;

import android.app.Application;

import com.example.localizationserdar.datamanager.DataListener;
import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.User;

import java.util.List;

public class LocalizationLevel extends Application {

    public static LocalizationLevel mInstance;
    public User currentUser;
    public List<Beacon> allBeacons;

    private DataListener<List<Object>> listener;
    private boolean loadingData = false;

    public static LocalizationLevel getInstance() {
        return mInstance;
    }

//    public void getBeacons(boolean forceReload, DataListener<List<Object>> newListener) {
//        listener = newListener;

//        if (!loadingData && (forceReload || allBeacons == null || allBeacons.size() == 0)) {
//            loadingData = true;
//            allBeacons = new LinkedList<>();
//            DataManager.getInstance().getBeacons((beacons, exception) -> {
//                if (beacons != null) {
//                    if (allBeacons == null) {
//                        allBeacons = new LinkedList<>();
//                    }
//                    allBeacons.addAll(beacons);
//                }
//                if (listener != null) {
//                    listener.onData(allBeacons, exception);
//                }
//                loadingData = false;
//            });
//        } else {
//            if (listener != null) {
//                listener.onData(allBeacons, null);
//            }
//        }
//    }
//    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
