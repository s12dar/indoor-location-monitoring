package com.example.localizationserdar.mainmenu;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.FragmentIndoorMapBinding;
import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;

public class IndoorMapFragment extends IndoorViewFragment implements View.OnClickListener {

    private FragmentIndoorMapBinding binding;

    public IndoorMapFragment() {
        super();
        beaconFilters.add(uuidFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_indoor_map;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return (locationProvider, location) -> {
//                if (locationProvider instanceof IndoorPositioning) {
            binding.beaconMap.setDeviceLocation(location);
            binding.beaconMap.setPredictedDeviceLocation(IndoorPositioning.getLocationPredictor().getLocation());
            binding.beaconMap.fitToCurrentLocations();
            Log.d("Hello Serdar", "The live location is" + IndoorPositioning.getLocationPredictor().getLocation());
//                } else if (locationProvider instanceof AndroidLocationProvider) {
//                    // TODO: remove artificial noise
//                    //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
//                    //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);
//                }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return beacon -> binding.beaconMap.setBeacons(getBeacons());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIndoorMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabChangeMapToOutdoor.setOnClickListener(this);

        binding.beaconMap.setBeacons(getBeacons());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.indoor_map_view_background, options);

        //my own things 45.75049523936549, 21.24165260452724
        Location firstReferenceLocation = new Location(45.75047390698444,21.241695815599876);
        Location secondReferenceLocation = new Location(45.75043540599549,21.241723367690334);

        Point firstReferencePoint = new Point(9, 18);
        Point secondReferencePoint = new Point(19, 18);

        IndoorMapBackground beaconMapBackground = IndoorMapBackground.Builder.from(backgroundImage)
                .withFirstReferenceLocation(firstReferenceLocation, firstReferencePoint)
                .withSecondReferenceLocation(secondReferenceLocation, secondReferencePoint)
                .build();

        binding.beaconMap.setMapBackground(beaconMapBackground);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_change_map_to_outdoor:
                Navigation.findNavController(view).navigate(R.id.action_indoorMapFragment_to_mainMenu);
                break;
        }

    }
}
