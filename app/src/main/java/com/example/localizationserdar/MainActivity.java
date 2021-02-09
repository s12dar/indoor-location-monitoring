package com.example.localizationserdar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localizationserdar.databinding.ActivityMainBinding;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.google.android.material.appbar.MaterialToolbar;

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
        MaterialToolbar toolbar = binding.toolbar;
        binding.toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void hideToolbar() {
        binding.toolbar.setVisibility(View.GONE);
    }

    @Override
    public void hideKeyboard(Context context, View view) {
        if (view == null) {
            view = getWindow().getCurrentFocus();
        }
        if (view != null) {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null && view.hasFocus()) {
                view.clearFocus();
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}