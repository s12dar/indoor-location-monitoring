package com.example.localizationserdar.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.IBeaconLocationProvider;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.scan.ScanResult;
import com.polidea.rxandroidble.scan.ScanSettings;

import rx.Observer;
import rx.Subscription;

public class BluetoothService {

    private static final String TAG = BluetoothService.class.getSimpleName();
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 10;

    private static BluetoothService instance;

    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BeaconManager beaconManager = BeaconManager.getInstance();

    private RxBleClient rxBleClient;
    private Subscription scanningSubscription;

    private BluetoothService() {

    }

    public static BluetoothService getInstance() {
        if (instance == null) {
            instance = new BluetoothService();
        }
        return instance;
    }

    public static void initialize(@NonNull Context context) {
        Log.v(TAG, "Initializing with context: " + context);
        BluetoothService instance = getInstance();
        instance.rxBleClient = RxBleClient.create(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            instance.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            instance.bluetoothAdapter = instance.bluetoothManager.getAdapter();
        }
        if (instance.bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth adapter is not available");
        }
    }

    public static void startScanning() {
        if (isScanning()) {
            return;
        }

        final BluetoothService instance = getInstance();
        Log.d(TAG, "Starting to scan for beacons");

        // Step 1: Outdoor reference location
        double measuredLatitude = 45.750434679510576;
        double measuredLongitude = 21.241503482483232;
        Location outdoorReferenceLocation = new Location(measuredLatitude, measuredLongitude);

        // Step 2: Indoor reference or beacon location
        double distance = 12; // in meters
        double angle = 0; // in degrees (0° is North)
        Location indoorReferenceLocation = outdoorReferenceLocation.getShiftedLocation(distance, angle);
        Log.d(TAG, "Hello Serdar, indoor reference location's lat: "+ indoorReferenceLocation.getLatitude() + ", and long is: " + indoorReferenceLocation.getLongitude());
        System.out.println(indoorReferenceLocation); // print latitude and longitude

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();

        instance.scanningSubscription = instance.rxBleClient.scanBleDevices(scanSettings)
                .subscribe(new Observer<ScanResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Bluetooth scanning error", e);
                    }

                    @Override
                    public void onNext(ScanResult scanResult) {
                        instance.processScanResult(scanResult);
                    }
                });
    }

    public static void stopScanning() {
        if (!isScanning()) {
            return;
        }

        BluetoothService instance = getInstance();
        Log.d(TAG, "Stopping to scan for beacons");
        instance.scanningSubscription.unsubscribe();
    }

    public static boolean isScanning() {
        Subscription subscription = getInstance().scanningSubscription;
        return subscription != null && !subscription.isUnsubscribed();
    }

    public static boolean isBluetoothEnabled() {
        BluetoothService instance = getInstance();
        return instance.bluetoothAdapter != null && instance.bluetoothAdapter.isEnabled();
    }

    public static void requestBluetoothEnabling(@NonNull Activity activity) {
        Log.d(TAG, "Requesting bluetooth enabling");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    private void processScanResult(@NonNull ScanResult scanResult) {
        String macAddress = scanResult.getBleDevice().getMacAddress();
        byte[] data = scanResult.getScanRecord().getBytes();
        AdvertisingPacket advertisingPacket = BeaconManager.processAdvertisingData(macAddress, data, scanResult.getRssi());

        if (advertisingPacket != null) {
            Beacon beacon = BeaconManager.getBeacon(macAddress, advertisingPacket);
            if (beacon instanceof IBeacon && !beacon.hasLocation()) {
                beacon.setLocationProvider(createDebuggingLocationProvider((IBeacon) beacon));
                Log.d(TAG, "The beacon minor is: " + ((IBeacon<?>) beacon).getMinor());
                Log.d(TAG, "The distance between user and beacon are: " + ((IBeacon<?>) beacon).getDistance());

            }
        }
    }

    private static IBeaconLocationProvider<IBeacon> createDebuggingLocationProvider(IBeacon iBeacon) {
        final Location beaconLocation = new Location();
        switch (iBeacon.getMinor()) {
            case 4967: {
                beaconLocation.setLatitude(45.75047203011801);
                beaconLocation.setLongitude(21.241723062200514);
                beaconLocation.setAltitude(36);
                break;
            }
            case 4975: { //OKNODAKY
                beaconLocation.setLatitude(45.75045636931093);
                beaconLocation.setLongitude(21.241672820182544);
                beaconLocation.setElevation(2.65);
                beaconLocation.setAltitude(36);
                break;
            }
        }
        return new IBeaconLocationProvider<IBeacon>(iBeacon) {
            @Override
            protected void updateLocation() {
                this.location = beaconLocation;
            }

            @Override
            protected boolean canUpdateLocation() {
                return true;
            }
        };
    }

}
