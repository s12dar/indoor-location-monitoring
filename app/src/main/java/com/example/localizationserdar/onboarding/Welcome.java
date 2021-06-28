package com.example.localizationserdar.onboarding;

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
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.WelcomeBinding;
import com.example.localizationserdar.utils.OnboardingUtils;

import java.util.ArrayList;
import java.util.List;

public class Welcome extends Fragment {

    private WelcomeBinding binding;
    private WelcomeAdapter welcomeAdapter;

    public Welcome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((OnboardingUtils) requireContext()).hideToolbar();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = WelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpWelcomeAdapter();

        ViewPager2 welcomeViewPager = binding.vpWelcome;
        welcomeViewPager.setAdapter(welcomeAdapter);

        setUpIndicators();
        setUpCurrentIndicator(0);

        welcomeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setUpCurrentIndicator(position);
            }
        });

        binding.btnSignIn.setOnClickListener(v-> Navigation.findNavController(view).navigate(R.id.action_welcome_to_login));
        binding.btnSignUp.setOnClickListener(v-> Navigation.findNavController(view).navigate(R.id.action_welcome_to_registration));
    }

    private void setUpWelcomeAdapter() {
        List<WelcomeItem> welcomeItemList = new ArrayList<>();

        WelcomeItem firstPage = new WelcomeItem();
        firstPage.setTitle(getResources().getString(R.string.txt_title_welcome));
        firstPage.setDescription(getResources().getString(R.string.txt_desc_welcome));
        firstPage.setImage(R.drawable.oh_hey);

        WelcomeItem secondPage = new WelcomeItem();
        secondPage.setTitle(getResources().getString(R.string.txt_title_welcome2));
        secondPage.setDescription(getResources().getString(R.string.txt_desc_welcome2));
        secondPage.setImage(R.drawable.navigate);

        WelcomeItem thirdItem = new WelcomeItem();
        thirdItem.setTitle(getResources().getString(R.string.txt_title_welcome3));
        thirdItem.setDescription(getResources().getString(R.string.txt_desc_welcome3));
        thirdItem.setImage(R.drawable.but_first);

        welcomeItemList.add(firstPage);
        welcomeItemList.add(secondPage);
        welcomeItemList.add(thirdItem);

        welcomeAdapter = new WelcomeAdapter(requireContext(), welcomeItemList);
    }

    private void setUpIndicators() {
        ImageView[] indicators = new ImageView[welcomeAdapter.getItemCount()];
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