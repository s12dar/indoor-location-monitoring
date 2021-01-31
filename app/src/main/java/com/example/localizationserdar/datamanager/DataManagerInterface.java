package com.example.localizationserdar.datamanager;

import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.User;

import java.util.List;

public interface DataManagerInterface {

    void createUser(User user, DataListener<Boolean> listener);
    void getCurrentUser(DataListener<User> listener);
    void updateUser(User user, DataListener<Boolean> listener);
    void getBeacons(DataListener<List<Beacon>> listener);
    void createBeaconInfoReward(User user, Beacon beacon, DataListener<Boolean> listener);
    void updateBeacon(User user, Beacon beacon, DataListener<Boolean> listener);
}
