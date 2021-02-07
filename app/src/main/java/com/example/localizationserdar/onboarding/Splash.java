package com.example.localizationserdar.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.SplashBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;

public class Splash extends Fragment {

    SplashBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public Splash() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

//        ((OnboardingUtils) requireContext()).hideToolbar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    DataManager.getInstance().getBeacons((beacons, exception1) -> {
                        if (beacons != null) {
                            LocalizationLevel.getInstance().allBeacons = beacons;
                        } else {
                            LocalizationLevel.getInstance().allBeacons = new LinkedList<>();
                        }
                    });
                    goToNextScreen();
                });
    }

    private void goToNextScreen() {
        if (isDetached() || isRemoving() || getActivity() == null) {
            return;
        }
        if (mUser != null) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_splash_to_mainMenu);
        } else {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_splash_to_welcome);
        }
    }
}