package com.example.localizationserdar.rewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.MainRewardBinding;
import com.example.localizationserdar.utils.OnboardingUtils;

public class MainReward extends Fragment {

    private MainRewardBinding binding;

    public MainReward() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((OnboardingUtils) requireContext()).showToolbar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = MainRewardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (LocalizationLevel.getInstance().currentUser.beacons != null && !LocalizationLevel.getInstance().currentUser.beacons.isEmpty()){
            binding.tvTitle.setVisibility(View.GONE);
            binding.tvSubtitle.setVisibility(View.GONE);
            binding.ivReward.setVisibility(View.GONE);
            binding.rvReward.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            MainRewardAdapter mainRewardAdapter = new MainRewardAdapter(getActivity(), LocalizationLevel.getInstance().currentUser.beacons);

            binding.rvReward.setLayoutManager(linearLayoutManager);
            binding.rvReward.setAdapter(mainRewardAdapter);
        }
        binding.btnStartScanning.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_mainReward_to_qrScannerReward));
    }
}