package com.example.localizationserdar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.SettingsBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.example.localizationserdar.datamodels.User;
import com.example.localizationserdar.utils.OnboardingUtils;

public class Settings extends Fragment {

    private static final String TAG = "DEBUGGING...";
    private SettingsBinding binding;
    private User user;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = SettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((OnboardingUtils) requireActivity()).showToolbar();

        user = LocalizationLevel.getInstance().currentUser;
        setProfileDetails();

        binding.btnEditDetails.setOnClickListener(v -> {
            btnEditPressed();
        });

        binding.btnSaveProfile.setOnClickListener(v -> {
            btnSaveProfilePressed();
        });
    }

    private void setProfileDetails() {
        binding.tvEmail.setText(user.email);
        binding.tvPhoneNumber.setText(user.phoneNumber);
        binding.tvNameSurname.setText(String.format("%s %s",user.firstName,user.lastName));
    }

    private void btnEditPressed() {
        binding.clProfileDetails.setVisibility(View.GONE);
        binding.clEditProfileDetails.setVisibility(View.VISIBLE);

        binding.etEmail.setText(user.email);
        binding.etName.setText(user.firstName);
        binding.etPhoneNumber.setText(user.phoneNumber);
        binding.etFamilyName.setText(user.lastName);
    }

    private void btnSaveProfilePressed() {
//        user.email = binding.etEmail.getText().toString();
        user.firstName = binding.etName.getText().toString();
        user.lastName = binding.etFamilyName.getText().toString();
        user.phoneNumber = binding.etPhoneNumber.getText().toString();


        DataManager.getInstance().updateUser(user, (success, exception) -> {
            if (success != null && success) {
                user = LocalizationLevel.getInstance().currentUser;
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_settings_to_mainMenu);
            } else {
                Log.d(TAG, exception.getLocalizedMessage().toString());
            }
        });
    }
}