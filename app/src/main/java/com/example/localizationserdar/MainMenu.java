package com.example.localizationserdar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.localizationserdar.databinding.MainMenuBinding;
import com.example.localizationserdar.datamodels.User;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainMenu extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private MainMenuBinding binding;
    private NavigationView navigationView;
    private static final String TAG = "DEBUGGING...";
    private User user;

    public MainMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        setNavigationViewListener();
        binding = MainMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView = requireView().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((OnboardingUtils) requireActivity()).hideToolbar();


//        user = LocalizationLevel.getInstance().currentUser;
        //Setting Nav Drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setNavDrawer(toolbar);

        //Setting the Greetings message
        setGreetingsText();

    }

    private void setNavDrawer(Toolbar toolbar) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), binding.drawerLayout, toolbar, R.string.drawer_controller_open, R.string.drawer_controller_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void setGreetingsText() {
//        User user = LocalizationLevel.getInstance().currentUser;

        View header = binding.navView.getHeaderView(0);
        TextView tvGreetings = header.findViewById(R.id.tv_morning);
        TextView tvName  = header.findViewById(R.id.tv_name);
//        tvName.setText(user.firstName);

        Calendar rightNow = Calendar.getInstance();
        int timeOfDay = rightNow.get(Calendar.HOUR_OF_DAY);
        Log.d("The time is: ", String.valueOf(timeOfDay));
        if (timeOfDay < 12) {
            tvGreetings.setText(getResources().getString(R.string.tv_morning));
        } else if (timeOfDay < 16) {
            tvGreetings.setText(getResources().getString(R.string.tv_afternoon));
        } else if (timeOfDay < 21) {
            tvGreetings.setText(getResources().getString(R.string.tv_evening));
        } else if (timeOfDay < 24){
            tvGreetings.setText(getResources().getString(R.string.tv_night));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Navigation.findNavController(requireView()).navigate(R.id.action_mainMenu_to_settings);
                Log.d("Hello," ,"You pressed me!");
                break;
            case R.id.menu_localization:
                //Logic here
                Log.d(TAG, "Hello, you pressed Localization menu");
                break;
            case R.id.menu_language:
                //Logic here
                Log.d(TAG, "Hey, you pressed Logout menu");
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}