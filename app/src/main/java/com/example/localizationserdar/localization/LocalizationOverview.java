package com.example.localizationserdar.localization;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.databinding.LocalizationOverviewBinding;
import com.example.localizationserdar.utils.OnboardingUtils;

public class LocalizationOverview extends Fragment {

    private LocalizationOverviewBinding binding;

    public LocalizationOverview() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = LocalizationOverviewBinding.inflate(inflater, container, false);
        ((OnboardingUtils) requireActivity()).showToolbar();
        return binding.getRoot();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        // Initialize adapter
        LocalizationAdapter localizationAdapter = new LocalizationAdapter(getActivity(), LocalizationLevel.getInstance().allBeacons);

        binding.recyclerView.setAdapter(localizationAdapter);

    }
}