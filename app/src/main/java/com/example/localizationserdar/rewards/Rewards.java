package com.example.localizationserdar.rewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.RewardsBinding;

import java.util.ArrayList;
import java.util.List;

public class Rewards extends Fragment {

    private RewardsBinding binding;
    private RewardAdapter rewardAdapter;

    public Rewards() {
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
        binding = RewardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRewardAdapter();
        ViewPager2 rewardViewPager = binding.vpReward;
        rewardViewPager.setAdapter(rewardAdapter);

        setUpIndicators();
        setUpCurrentIndicator(0);

        rewardViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setUpCurrentIndicator(position);
            }
        });
    }

    private void setUpRewardAdapter() {
        List<RewardItem> rewardItemList = new ArrayList<>();

        RewardItem firstPage = new RewardItem();
        firstPage.setTitle("We're serious");
        firstPage.setDescription("Scan QR code of the destinations that you arrive.");
        firstPage.setImage(R.drawable.qr_scan_characters);

        RewardItem secondPage = new RewardItem();
        secondPage.setTitle("Reward");
        secondPage.setDescription("Guess what you receive after you reach to 15 points in a specific destination.");
        secondPage.setImage(R.drawable.qr_scan_characters2);

        RewardItem thirdItem = new RewardItem();
        thirdItem.setTitle("Hmm..");
        thirdItem.setDescription("Let it be surprise :P");
        thirdItem.setImage(R.drawable.qr_scan_characters5);

        rewardItemList.add(firstPage);
        rewardItemList.add(secondPage);
        rewardItemList.add(thirdItem);

        rewardAdapter = new RewardAdapter(requireContext(), rewardItemList);
    }

    private void setUpIndicators() {
        ImageView[] indicators = new ImageView[rewardAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext().getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getContext().getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            binding.indicators.addView(indicators[i]);
        }

    }

    private void setUpCurrentIndicator(int index) {
        int childCount = binding.indicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView)binding.indicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.indicator_inactive)
                );
            }
        }
    }

}