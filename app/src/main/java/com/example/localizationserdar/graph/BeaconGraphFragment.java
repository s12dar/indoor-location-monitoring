package com.example.localizationserdar.graph;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.BeaconGraphBinding;
import com.example.localizationserdar.mainmenu.IndoorViewFragment;
import com.example.localizationserdar.services.AndroidLocationProvider;
import com.example.localizationserdar.services.BluetoothService;
import com.example.localizationserdar.utils.ColorUtil;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.google.android.material.snackbar.Snackbar;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.GenericBeaconFilter;
import com.nexenio.bleindoorpositioning.location.LocationListener;

import java.util.ArrayList;

public class BeaconGraphFragment extends IndoorViewFragment {

    private boolean rssiFilterView;
    private BeaconGraphBinding binding;

    public BeaconGraphFragment() {
        this(false);
    }

    @SuppressLint("ValidFragment")
    public BeaconGraphFragment(boolean rssiFilterView) {
        super();
        this.rssiFilterView = rssiFilterView;
        if (this.rssiFilterView) {
            beaconFilters.add(createClosestBeaconFilter());
        } else {
            beaconFilters.add(uuidFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public GenericBeaconFilter createClosestBeaconFilter() {
        return new GenericBeaconFilter() {

            @Override
            public boolean matches(Beacon beacon) {
                return BeaconManager.getInstance().getClosestBeacon().equals(beacon);
            }
        };
    }

    @Override
    protected int getLayoutResourceId() {
        if (rssiFilterView) {
            return R.layout.rssi_filter_graph;
        } else {
            return R.layout.beacon_graph;
        }
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return (locationProvider, location) -> {
            // TODO: remove artificial noise
//            location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
//            location.setLongitude(location.getLongitude() + Math.random() * 0.0002);
            binding.beaconGraph.setDeviceLocation(location);
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return beacon -> binding.beaconGraph.setBeacons(getBeacons());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup location
        AndroidLocationProvider.initialize(requireActivity());

        // setup bluetooth
        BluetoothService.initialize(requireContext());
    }

    private void requestLocationServices() {
        Snackbar snackbar = Snackbar.make(
                binding.beaconGraph,
                R.string.txt_location_disabled,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(R.string.action_enabled, view -> AndroidLocationProvider.requestLocationEnabling(requireActivity())).show();
    }

    private void requestBluetooth() {
        Snackbar snackbar = Snackbar.make(
                binding.beaconGraph,
                R.string.txt_bluetooth_disabled,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(R.string.action_enabled, view -> BluetoothService.requestBluetoothEnabling(requireActivity())).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // observe location
        if (!AndroidLocationProvider.hasLocationPermission(requireContext())) {
            AndroidLocationProvider.requestLocationPermission(requireActivity());
        } else if (!AndroidLocationProvider.isLocationEnabled(requireContext())) {
            requestLocationServices();
        }
        AndroidLocationProvider.startRequestingLocationUpdates();
        AndroidLocationProvider.requestLastKnownLocation();

        if (!BluetoothService.isBluetoothEnabled()) {
            requestBluetooth();
        }
        BluetoothService.startScanning();
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop observing location
//        AndroidLocationProvider.stopRequestingLocationUpdates();
//
//        // stop observing bluetooth
//        BluetoothService.stopScanning();
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BeaconGraphBinding.inflate(inflater, container, false);
        ((OnboardingUtils) requireActivity()).showToolbar();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.beaconGraph.setBeacons(new ArrayList<>(BeaconManager.getInstance().getBeaconMap().values()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.graph_beacon, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_value_rssi: {
                onValueTypeSelected(BeaconGraph.VALUE_TYPE_RSSI, item);
                return true;
            }
            case R.id.menu_value_rssi_filtered: {
                onValueTypeSelected(BeaconGraph.VALUE_TYPE_RSSI_FILTERED, item);
                return true;
            }
            case R.id.menu_value_distance: {
                onValueTypeSelected(BeaconGraph.VALUE_TYPE_DISTANCE, item);
                return true;
            }
            case R.id.menu_value_frequency: {
                onValueTypeSelected(BeaconGraph.VALUE_TYPE_FREQUENCY, item);
                return true;
            }
            case R.id.menu_value_variance: {
                onValueTypeSelected(BeaconGraph.VALUE_TYPE_VARIANCE, item);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onValueTypeSelected(@BeaconGraph.ValueType int valueType, MenuItem menuItem) {
        menuItem.setChecked(true);
        binding.beaconGraph.setValueType(valueType);
    }

    @Override
    protected void onColoringModeSelected(@ColorUtil.ColoringMode int coloringMode, MenuItem menuItem) {
        super.onColoringModeSelected(coloringMode, menuItem);
        binding.beaconGraph.setColoringMode(coloringMode);
    }

}