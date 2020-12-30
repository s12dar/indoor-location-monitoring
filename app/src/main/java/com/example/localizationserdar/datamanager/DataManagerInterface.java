package com.example.localizationserdar.datamanager;

import com.example.localizationserdar.datamodels.User;

public interface DataManagerInterface {

    void createUser(User user, DataListener<Boolean> listener);
    void getCurrentUser(DataListener<User> listener);
}
