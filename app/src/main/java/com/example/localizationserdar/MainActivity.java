package com.example.localizationserdar;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localizationserdar.databinding.ActivityMainBinding;
import com.example.localizationserdar.utils.OnboardingUtils;

public class MainActivity extends AppCompatActivity implements OnboardingUtils {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void showToolbar() {
        binding.toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        binding.toolbar.setVisibility(View.GONE);
    }
}