package com.example.localizationserdar.mainmenu;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.QrScannerBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.User;
import com.google.zxing.Result;

import java.util.LinkedList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QrScanner extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private static final int REQUEST_CAMERA = 1;
    private QrScannerBinding binding;

    public QrScanner() {
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

        binding = QrScannerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        builder.setTitle("Do you want to navigate to: ");

        User user = LocalizationLevel.getInstance().currentUser;
        Beacon beacon = new Beacon();

        for (Beacon beacon1: LocalizationLevel.getInstance().allBeacons) {
            if (scanResult.equals(beacon1.beaconName)) {
                beacon.beaconId = beacon1.beaconId;
                beacon.beaconDesc = beacon1.beaconId;
                beacon.beaconName = beacon1.beaconName;
                beacon.beaconCount = beacon1.beaconCount;
                break;
            }
        }

        builder.setNegativeButton("NO, TAKE ME BACK", (dialog, which) -> {
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, scanResult);
//                startActivity(intent);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_qrScanner_to_mainMenu);
        });

//        builder.setNeutralButton("NO, TAKE ME BACK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Navigation.findNavController(mScannerView).navigateUp();
//            }
//        });

        builder.setPositiveButton("YES", (dialog, which) -> {
            DataManager.getInstance().createBeaconInfoReward(user, beacon, (success, exception) -> {
                if (success != null && success) {
                    if (user.beacons == null) {
                        user.beacons = new LinkedList<>();
                    }
                    user.beacons.add(beacon);
                }
            });
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