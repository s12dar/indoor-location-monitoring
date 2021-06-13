package com.example.localizationserdar.mainmenu;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.Navigation;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.FragmentIndoorMapBinding;
import com.example.localizationserdar.datamodels.User;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.example.localizationserdar.utils.Constants.NOT_FIRST_TIME;
import static com.example.localizationserdar.utils.Constants.REWARD_COUNT;
import static com.example.localizationserdar.utils.Constants.SP_FILES;
import static com.example.localizationserdar.utils.Constants.TEACHER;

public class IndoorMapFragment extends IndoorViewFragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FragmentIndoorMapBinding binding;
    private static final String TAG = "DEBUGGING...";

    private User user;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        user = LocalizationLevel.getInstance().currentUser;
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIndoorMapBinding.inflate(inflater, container, false);
        ((OnboardingUtils) requireActivity()).hideToolbar();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setGreetingsText(user);

        binding.fabChangeMapToOutdoor.setOnClickListener(this);
        binding.fabRadar.setOnClickListener(this);
        binding.fabGraph.setOnClickListener(this);

        binding.beaconMap.setBeacons(getBeacons());

        if (user != null && !user.status.equals(TEACHER)) {
            binding.fabGraph.setVisibility(View.GONE);
            binding.fabRadar.setVisibility(View.GONE);
        }

        NavigationView navigationView = requireView().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setNavDrawer(toolbar);

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

    private void setNavDrawer(Toolbar toolbar) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), binding.indoorDrawerLayout, toolbar, R.string.drawer_controller_open, R.string.drawer_controller_close);
        binding.indoorDrawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_change_map_to_outdoor:
                Navigation.findNavController(view).navigate(R.id.action_indoorMapFragment_to_mainMenu);
                break;
            case R.id.fab_radar:
                Navigation.findNavController(view).navigate(R.id.action_indoorMapFragment_to_indoorRadarFragment);
                break;
            case R.id.fab_graph:
                Navigation.findNavController(view).navigate(R.id.action_indoorMapFragment_to_beaconGraphFragment);
                break;
        }

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Navigation.findNavController(requireView()).navigate(R.id.action_indoorMapFragment_to_login);
    }

    private void setGreetingsText(User user) {
        View header = binding.navView.getHeaderView(0);
        TextView tvGreetings = header.findViewById(R.id.tv_morning);
        TextView tvName = header.findViewById(R.id.tv_name);
        tvName.setText(user.firstName);

        Calendar rightNow = Calendar.getInstance();
        int timeOfDay = rightNow.get(Calendar.HOUR_OF_DAY);
        Log.d("The time is: ", String.valueOf(timeOfDay));
        if (timeOfDay < 12) {
            tvGreetings.setText(getResources().getString(R.string.tv_morning));
        } else if (timeOfDay < 16) {
            tvGreetings.setText(getResources().getString(R.string.tv_afternoon));
        } else if (timeOfDay < 21) {
            tvGreetings.setText(getResources().getString(R.string.tv_evening));
        } else if (timeOfDay < 24) {
            tvGreetings.setText(getResources().getString(R.string.tv_night));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Navigation.findNavController(requireView()).navigate(R.id.action_indoorMapFragment_to_settings);
                Log.d("Hello,", "You pressed me!");
                break;
            case R.id.menu_localization:
                //Logic here
                Navigation.findNavController(requireView()).navigate(R.id.action_indoorMapFragment_to_localizationOverview);
                Log.d(TAG, "Hello, you pressed Localization menu");
                break;
            case R.id.menu_reward:
                SharedPreferences preferences = requireActivity().getSharedPreferences(SP_FILES, MODE_PRIVATE);
                String spValue = preferences.getString(REWARD_COUNT, "");
                if (!spValue.equals(NOT_FIRST_TIME)) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_indoorMapFragment_to_rewards);
                } else {
                    Navigation.findNavController(requireView()).navigate(R.id.action_indoorMapFragment_to_mainReward);
                }
                break;
            case R.id.menu_logout:
                signOut();
                break;
        }
        binding.indoorDrawerLayout.closeDrawer(GravityCompat.START);
        return true;    }
}