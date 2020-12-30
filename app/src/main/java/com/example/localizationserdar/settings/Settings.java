package com.example.localizationserdar.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.databinding.SettingsBinding;
import com.example.localizationserdar.datamodels.User;
import com.example.localizationserdar.utils.OnboardingUtils;

public class Settings extends Fragment {

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
        binding.tvEmail.setText((user.email));
        binding.tvPhoneNumber.setText(user.phoneNumber);
        binding.tvNameSurname.setText(String.format("%s %s",user.firstName,user.lastName));
    }
}