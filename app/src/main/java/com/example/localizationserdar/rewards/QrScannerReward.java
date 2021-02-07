package com.example.localizationserdar.rewards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.QrScannerRewardBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.User;
import com.google.zxing.Result;

import java.util.LinkedList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QrScannerReward extends Fragment implements ZXingScannerView.ResultHandler {

    private QrScannerRewardBinding binding;
    private ZXingScannerView mScannerView;
    private static final int REQUEST_CAMERA = 1;
    private boolean isExists = false;

    public QrScannerReward() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(requireContext());
        requireActivity().setContentView(mScannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(requireContext(), "Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {

            // handle back button's click listener
            return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK;
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = QrScannerRewardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void handleResult(Result rawResult) {
        final String scanResult = rawResult.getText().trim();
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle("Do you submit this destination: ");

        User user = LocalizationLevel.getInstance().currentUser;
        LinkedList<Beacon> allBeacons = (LinkedList<Beacon>) LocalizationLevel.getInstance().currentUser.beacons;
        Beacon beacon = new Beacon();
        int position = 0;

        for (Beacon beacon1: LocalizationLevel.getInstance().allBeacons) {
            if (scanResult.equals(beacon1.beaconName)) {
                beacon.beaconId = beacon1.beaconId;
                beacon.beaconDesc = beacon1.beaconDesc;
                beacon.beaconName = beacon1.beaconName;
                beacon.beaconCount = beacon1.beaconCount;
                break;
            }
        }

        builder.setNegativeButton("NO, TAKE ME BACK", (dialog, which) -> {
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, scanResult);
//                startActivity(intent);
//            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_qrScanner_to_mainMenu);
        });

//        for (Beacon beacon1: LocalizationLevel.getInstance().currentUser.beacons) {
//            if (scanResult.equals(beacon1.beaconName)) {
//                isExists = true;
//                break;
//            }
//            isExists = false;
//        }

        for (int i = 0; i < LocalizationLevel.getInstance().currentUser.beacons.size(); i++) {
            if (scanResult.equals(LocalizationLevel.getInstance().currentUser.beacons.get(i).beaconName)) {
                isExists = true;
                position = i;
                break;
            }
            isExists = false;
        }

        int finalPosition = position;
        builder.setPositiveButton("YES", (dialog, which) -> {
            if (isExists) {
                int count = Integer.parseInt(LocalizationLevel.getInstance().currentUser.beacons.get(finalPosition).beaconCount) + 1;
                LocalizationLevel.getInstance().currentUser.beacons.get(finalPosition).beaconCount = String.valueOf(count);
                DataManager.getInstance().updateBeacon(user, LocalizationLevel.getInstance().currentUser.beacons.get(finalPosition), (success, exception) -> {
                    if (success != null && success) {
                       LocalizationLevel.getInstance().currentUser = user;
                    }
                });
            } else {
                DataManager.getInstance().createBeaconInfoReward(user, beacon, (success, exception) -> {
                    if (success != null && success) {
                        if (user.beacons == null) {
                            user.beacons = new LinkedList<>();
                        }
                        user.beacons.add(beacon);
                    }
                });
            }
            LocalizationLevel.getInstance().currentUser = user;
            Navigation.findNavController(requireView()).navigate(R.id.action_qrScannerReward_to_mainReward);
        });

        builder.setMessage(scanResult);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Back", null)
                .create()
                .show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[] {CAMERA}, REQUEST_CAMERA);
    }

    private Boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(requireActivity(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    Toast.makeText(getActivity(), "Permission Granted ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied ", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            displayAlertMessage("You need to allow access for both permissions",
                                    (dialog, which) -> requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA));
                            return;

                        }
                    }
                }
            }
        }

    }
}