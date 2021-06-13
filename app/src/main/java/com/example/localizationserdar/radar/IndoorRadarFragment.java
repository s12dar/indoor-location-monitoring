package com.example.localizationserdar.radar;



import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.IndoorRadarBinding;
import com.example.localizationserdar.mainmenu.IndoorViewFragment;
import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.LocationListener;

public class IndoorRadarFragment extends IndoorViewFragment {

    private IndoorRadarBinding binding;

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public IndoorRadarFragment() {
        super();
        beaconFilters.add(uuidFilter);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER: {
                        System.arraycopy(sensorEvent.values, 0, accelerometerReading, 0, accelerometerReading.length);
                        break;
                    }
                    case Sensor.TYPE_MAGNETIC_FIELD: {
                        System.arraycopy(sensorEvent.values, 0, magnetometerReading, 0, magnetometerReading.length);
                        break;
                    }
                }
                updateOrientationAngles();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDetach() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.indoor_radar;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return (locationProvider, location) -> {
            if (locationProvider == IndoorPositioning.getInstance()) {
                binding.indoorRadar.setDeviceLocation(location);
                binding.indoorRadar.fitToCurrentLocations();
            }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return beacon -> binding.indoorRadar.setBeacons(getBeacons());
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = IndoorRadarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.indoorRadar.setBeacons(getBeacons());
    }

    private void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        binding.indoorRadar.startDeviceAngleAnimation((float) Math.toDegrees(orientationAngles[0]));
    }
}
