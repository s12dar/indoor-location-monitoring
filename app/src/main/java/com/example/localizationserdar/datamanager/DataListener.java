package com.example.localizationserdar.datamanager;

public interface DataListener<T> {
    void onData(T data, Exception exception);
}
