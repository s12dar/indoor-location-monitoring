package com.example.localizationserdar.mainmenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.BottomSheetBinding;
import com.example.localizationserdar.databinding.MainMenuBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.ClusterMarker;
import com.example.localizationserdar.datamodels.PolyLineData;
import com.example.localizationserdar.datamodels.User;
import com.example.localizationserdar.localization.LocalizationAdapter;
import com.example.localizationserdar.services.BluetoothService;
import com.example.localizationserdar.services.LocationService;
import com.example.localizationserdar.utils.ClusterManagerRenderer;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_AI_LAB;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_CANTINA;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_LIBRARY;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_TEACHERS_ROOM;
import static com.example.localizationserdar.utils.Constants.EMPTY_STRING;
import static com.example.localizationserdar.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.example.localizationserdar.utils.Constants.EXISTING_USER;
import static com.example.localizationserdar.utils.Constants.INITIAL_LAT;
import static com.example.localizationserdar.utils.Constants.INITIAL_LNG;
import static com.example.localizationserdar.utils.Constants.MAPVIEW_BUNDLE_KEY;
import static com.example.localizationserdar.utils.Constants.NOT_FIRST_TIME;
import static com.example.localizationserdar.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.localizationserdar.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.example.localizationserdar.utils.Constants.REWARD_COUNT;
import static com.example.localizationserdar.utils.Constants.SP_FILES;
import static com.example.localizationserdar.utils.Constants.USER_STATUS;

public class MainMenu extends Fragment implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener, LocalizationAdapter.LocalizationListRecyclerClickListener {

    private MainMenuBinding binding;
    private ListenerRegistration modStatusListener;
    private BottomSheetBinding bottomSheetBinding;
    private User user;
    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "DEBUGGING...";
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private ClusterManager<ClusterMarker> clusterManager;
    private ClusterManagerRenderer clusterManagerRenderer;
    private ArrayList<ClusterMarker> clusterMarkers = new ArrayList<>();
    private GeoApiContext geoApiContext = null;
    private ArrayList<PolyLineData> polyLinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private final ArrayList<Marker> tripMarkers = new ArrayList<>();
    private Marker markerForItemClick;
    private Boolean isIndoorIcon = true;

    public MainMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.reload();
        }
//        manageModerationStatus();
          user = LocalizationLevel.getInstance().currentUser;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mvMap.onDestroy();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mvMap.onStop();
        if (modStatusListener != null) {
            modStatusListener.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.mvMap.onResume();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            if (checkMapServices()) {
                if (mLocationPermissionGranted) {
                    Log.d(TAG, "I have a location permissions");
                    getLastKnownLocation();
                } else {
                    getLocationPermission();
                }
            }

            if (!BluetoothService.isBluetoothEnabled()) {
                requestBluetooth();
            }
            BluetoothService.startScanning();
        }
        user = LocalizationLevel.getInstance().currentUser;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        binding.mvMap.onSaveInstanceState(mapViewBundle);
    }

    private void requestBluetooth() {
        Snackbar snackbar = Snackbar.make(
                binding.cdlBsh,
                R.string.txt_bluetooth_disabled,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(R.string.action_enabled, view -> BluetoothService.requestBluetoothEnabling(requireActivity())).show();
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        binding.mvMap.onCreate(mapViewBundle);
        binding.mvMap.getMapAsync(this);

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    }

    private void removeTripMarkers() {
        for (Marker marker: tripMarkers) {
            marker.remove();
        }
    }

    private void resetSelectedMarker() {
        if (mSelectedMarker != null) {
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        user.liveLocation.getLatitude(),
                        user.liveLocation.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            return isMapsEnabled();
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        LocationManager manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(clusterManager != null){
                clusterManager.clearItems();
            }

            if (clusterMarkers.size() > 0) {
                clusterMarkers.clear();
                clusterMarkers = new ArrayList<>();
            }

            if(polyLinesData.size() > 0){
                polyLinesData.clear();
                polyLinesData = new ArrayList<>();
            }
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "run: result routes: " + result.routes.length);

            if (polyLinesData.size() > 0) {
                for (PolyLineData polyLineData: polyLinesData) {
                    polyLineData.getPolyline().remove();
                }
                polyLinesData.clear();
                polyLinesData = new ArrayList<>();
            }

            double duration = 9999999999.0;
            for (DirectionsRoute route: result.routes){
                Log.d(TAG, "run: leg: " + route.legs[0].toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                // This loops through all the LatLng coordinates of ONE polyline.
                for (com.google.maps.model.LatLng latLng: decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }
                Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(requireActivity(), R.color.colorGrey));
                polyline.setClickable(true);
                polyLinesData.add(new PolyLineData(polyline, route.legs[0]));

                double tempDuration = route.legs[0].duration.inSeconds;
                if (tempDuration < duration) {
                    duration = tempDuration;
                    onPolylineClick(polyline);
                    zoomRoute(polyline.getPoints());
                }

                mSelectedMarker.setVisible(false);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getLastKnownLocation();
                } else {
                    getLocationPermission();
                }
            }
            case BluetoothService.REQUEST_CODE_ENABLE_BLUETOOTH: {
                if (requestCode == RESULT_OK) {
                    Log.d(TAG, "Bluetooth enabled, starting  to scan");
                    BluetoothService.startScanning();
                } else {
                    Log.d(TAG, "Bluetooth is not enabled");
                    BluetoothService.requestBluetoothEnabling(getActivity());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        setNavigationViewListener();
        binding = MainMenuBinding.inflate(inflater, container, false);
        ((OnboardingUtils) requireActivity()).hideToolbar();

        BluetoothService.initialize(requireContext());

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mvMap.onStart();
    }

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().getString(USER_STATUS, EMPTY_STRING).equals(EXISTING_USER)) {
            DataManager.getInstance().getCurrentUser(
                    (user, exception) -> {
                        if (user != null) {
                            LocalizationLevel.getInstance().currentUser = user;
                            DataManager.getInstance().getBeaconsBelongsToUser(
                                    (user), (beacons, exception2) -> {
                                        if (beacons != null) {
                                            LocalizationLevel.getInstance().currentUser.beacons = beacons;
                                        } else {
                                            LocalizationLevel.getInstance().currentUser.beacons = new LinkedList<>();
                                        }
                                    }
                            );
                        }
                    });
            user = LocalizationLevel.getInstance().currentUser;
        }

        binding.bottomSheet.tvNiceToSeeYou.setText("Hi " + user.firstName + ". Nice to see you again!");

        binding.fabRefresh.setOnClickListener(v -> addMapMarkers());
        binding.fabChangeMap.setOnClickListener(v -> changeMap());

        initGoogleMap(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        NavigationView navigationView = requireView().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setNavDrawer(toolbar);

        //Display moderation overlay (in case pending/declined)
        manageModerationStatus();

        //Recycler view set up for search
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.bottomSheet.rvBottomSheet.setLayoutManager(linearLayoutManager);

        if (LocalizationLevel.getInstance().allBeacons == null) {
            LocalizationLevel.getInstance().allBeacons = new LinkedList<>();
        }

        LocalizationAdapter localizationAdapter = new LocalizationAdapter(getActivity(), LocalizationLevel.getInstance().allBeacons, this);
        binding.bottomSheet.rvBottomSheet.setAdapter(localizationAdapter);

        BottomSheetBehavior<androidx.constraintlayout.widget.ConstraintLayout> behavior = BottomSheetBehavior.from(binding.bottomSheet.bSh);
        behavior.setHideable(false);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //Set the search
        assert binding.bottomSheet.svSearch != null;
        binding.bottomSheet.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                localizationAdapter.getFilter().filter(newText);
                return false;
            }
        });

        assert binding.bottomSheet.fabScan != null;
        binding.bottomSheet.fabScan.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_mainMenu_to_qrScanner));

        //Setting the Greetings message
        setGreetingsText(user);
    }

    private void startLocationService() {
        if(!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(getActivity(), LocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(serviceIntent);
            } else {
                requireActivity().startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo hello : manager.getRunningServices(Integer.MAX_VALUE)) {
            if("com.example.localizationserdar.services.LocationService".equals(hello.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                if (user.liveLocation == null) {
                    user.liveLocation = new GeoPoint(1, 1);
                }
                user.liveLocation = geoPoint;
                user.lastLocationUpdatedAt = new Timestamp(new Date());

                DataManager.getInstance().updateUser(user, (success, exception) -> {
                    if (success != null && success) {
                        LocalizationLevel.getInstance().currentUser = user;
                        Log.d(TAG, "updatedUserDetails");
                    } else {
                        Log.d(TAG, exception.getLocalizedMessage());
                    }
                });
                startLocationService();
            }
        });
    }

    private void addMapMarkers() {
        if (mGoogleMap != null) {

            resetMap();

            if (clusterManager == null) {
                clusterManager = new ClusterManager<>(getActivity().getApplicationContext(), mGoogleMap);
            }

            if(clusterManagerRenderer == null){
                clusterManagerRenderer = new ClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        clusterManager
                );
                clusterManager.setRenderer(clusterManagerRenderer);
            }

            for (Beacon beacon: LocalizationLevel.getInstance().allBeacons) {
                Log.d(TAG, "addMarkersLocation: location: "+beacon.beaconLocation.toString());

                try {
                    int avatar = R.drawable.oh_hey;
                    String snippet = "Do you want to determine route to " + beacon.beaconName+"?";
                    switch (beacon.beaconName) {
                        case BEACON_IN_TEACHERS_ROOM:
                            avatar = R.drawable.ic_meeting;
                            break;
                        case BEACON_IN_CANTINA:
                            avatar = R.drawable.ic_cantina;
                            break;
                        case BEACON_IN_LIBRARY:
                            avatar = R.drawable.ic_lib;
                            break;
                        case BEACON_IN_AI_LAB:
                            avatar = R.drawable.ic_lab;
                            break;
                    }
                    ClusterMarker clusterMarker = new ClusterMarker(
                            new LatLng(beacon.beaconLocation.getLatitude(), beacon.beaconLocation.getLongitude()),
                            beacon.beaconName,
                            snippet, avatar);
                    clusterManager.addItem(clusterMarker);
                    clusterMarkers.add(clusterMarker);
                } catch (NullPointerException e) {
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }
            clusterManager.cluster();
            setCameraViewForMap();
        }
    }

    private void changeMap() {
        if (isIndoorIcon) {
            addMapMarkers();
            isIndoorIcon = false;
            binding.fabChangeMap.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_change_map_to_outdoor));
            binding.mvMap.setVisibility(View.GONE);
        } else {
            isIndoorIcon = true;
            binding.fabChangeMap.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_change_map_to_indoor));
            binding.mvMap.setVisibility(View.VISIBLE);

        }
    }

    private void setNavDrawer(Toolbar toolbar) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), binding.drawerLayout, toolbar, R.string.drawer_controller_open, R.string.drawer_controller_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void manageModerationStatus() {
        TutoShowcase verificationOverlay = TutoShowcase.from(requireActivity());
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.reload();
            if (!firebaseUser.isEmailVerified()) {
                verificationOverlay.setContentView(R.layout.verification_status_overlay)
                        .onClickContentView(R.id.container_overlay_complete, null)
                        .show();
            } else {
                user.isVerified = true;
                verificationOverlay.dismiss();
        }}
         //        modStatusListener = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(user.userId)
//                .addSnapshotListener((documentSnapshot, error) -> {
//                    if (error != null) {
//                        Log.d(TAG, error.toString());
//                        return;
//                    }
//                    if (documentSnapshot != null && documentSnapshot.exists()) {
//                        user.verificationStatus = documentSnapshot.getString(VERIFICATION_STATUS);
//                        if (user.verificationStatus != null) {
//                            switch (user.verificationStatus) {
//                                case STATUS_REJECTED:
//                                case STATUS_PENDING:
//                                    showVerificationStatusOverlay(true);
//                                    binding.bottomSheet.bSh.setVisibility(View.GONE);
//                                    break;
//                                case STATUS_ACCEPTED:
//                                    showVerificationStatusOverlay(false);
//                                    binding.bottomSheet.bSh.setVisibility(View.VISIBLE);
//                                    break;
//
//                            }
//                        }
//                    }
//                });
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

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Navigation.findNavController(requireView()).navigate(R.id.action_mainMenu_to_login);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Navigation.findNavController(requireView()).navigate(R.id.action_mainMenu_to_settings);
                Log.d("Hello,", "You pressed me!");
                break;
            case R.id.menu_localization:
                //Logic here
                Navigation.findNavController(requireView()).navigate(R.id.action_mainMenu_to_localizationOverview);
                Log.d(TAG, "Hello, you pressed Localization menu");
                break;
            case R.id.menu_reward:
                SharedPreferences preferences = requireActivity().getSharedPreferences(SP_FILES, MODE_PRIVATE);
                String spValue = preferences.getString(REWARD_COUNT, "");
                if (!spValue.equals(NOT_FIRST_TIME)) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainMenu_to_rewards);
                } else {
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainMenu_to_mainReward);
                }
                break;
            case R.id.menu_logout:
                signOut();
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCameraViewForMap() {
        //Overall map view window: .2 * .2 = .04;
        if (user.liveLocation == null) {
            user.liveLocation = new GeoPoint(INITIAL_LAT, INITIAL_LNG);
        }
        GeoPoint geoPoint = new GeoPoint(user.liveLocation.getLatitude(), user.liveLocation.getLongitude());

        double bottomBoundary = geoPoint.getLatitude() - .1;
        double leftBoundary = geoPoint.getLongitude() - .1;
        double topBoundary = geoPoint.getLatitude() + .1;
        double rightBoundary = geoPoint.getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)

        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        mGoogleMap = map;
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        setCameraViewForMap();
        addMapMarkers();
    }

    @Override
    public void onPause() {
        binding.mvMap.onPause();
        BluetoothService.stopScanning();

        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mvMap.onLowMemory();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTitle().contains("Trip: #")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Navigate");
            builder.setMessage("Do you want to navigate to the location via Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        String latitude = String.valueOf(marker.getPosition().latitude);
                        String longitude = String.valueOf(marker.getPosition().longitude);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try{
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                            Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (marker.getSnippet().equals("This is you")) {
                marker.hideInfoWindow();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(marker.getSnippet())
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            resetSelectedMarker();
                            mSelectedMarker = marker;
                            dialog.dismiss();
                            calculateDirections(marker);
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for (PolyLineData polylineData: polyLinesData) {
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(endLocation)
                    .title("Trip: #"+index)
                    .snippet("Duration: "+polylineData.getLeg().duration)
                );

                marker.showInfoWindow();
                tripMarkers.add(marker);
            }
            else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    @Override
    public void onUserClicked(int position) {
        Beacon beacon = LocalizationLevel.getInstance().allBeacons.get(position);
        Log.d(TAG, "onUserSelected: selected a user: " + beacon.beaconName);

        final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle("Route drawing");
        builder.setMessage("Do you want to determine route to "+beacon.beaconName+"?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    LocalizationLevel.getInstance().allBeacons.get(position).beaconLocation.getLatitude(),
                    LocalizationLevel.getInstance().allBeacons.get(position).beaconLocation.getLongitude()
            );
            DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

            directions.alternatives(true);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            user.liveLocation.getLatitude(),
                            user.liveLocation.getLongitude()
                    )
            );
            Log.d(TAG, "calculateDirections: destination: " + destination.toString());
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                    Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration);
                    Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance);
                    Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                    addPolylinesToMap(result);
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "onFailure: " + e.getMessage() );

                }
            });
        });

        builder.setNegativeButton("NO", ((dialog, which) -> {
            dialog.cancel();
        }));
        final AlertDialog alert = builder.create();
        alert.show();

    }
}