package com.example.localizationserdar.onboarding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.localizationserdar.LocalizationLevel;
import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.LoginBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;

import static com.example.localizationserdar.utils.Constants.EXISTING_USER;
import static com.example.localizationserdar.utils.Constants.USER_STATUS;

public class Login extends Fragment {

    private static final String TAG = "DEBUGGING...";
    private FirebaseAuth mAuth;
    private LoginBinding binding;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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
        binding = LoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((OnboardingUtils) requireActivity()).hideToolbar();

        binding.btnSignIn.setOnClickListener(v -> {

            ((OnboardingUtils) requireActivity()).hideKeyboard(requireContext(), view);

            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            if (email.isEmpty()) {
                binding.etLoginEmail.setError(getResources().getString(R.string.error_email));
                return;
            }
            if (password.isEmpty()) {
                binding.etLoginPassword.setError(getResources().getString(R.string.error_password));
                return;
            }
            Log.d(TAG, "email and password are: "+email+" "+password);
            signIntoAccount(email, password);
        });

        binding.tvJoinUs.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_login_to_registration));
    }

    private void signIntoAccount(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(USER_STATUS, EXISTING_USER);
                        DataManager.getInstance().getCurrentUser(
                                (user, exception) -> {
                                    if (user != null) {
                                        Toast.makeText(getContext(), "Serdar all is good2", Toast.LENGTH_SHORT).show();
                                        LocalizationLevel.getInstance().currentUser = user;
                                    }
                                    DataManager.getInstance().getBeacons((beacons, exception1) -> {
                                        if (beacons != null) {
                                            LocalizationLevel.getInstance().allBeacons = beacons;
                                        } else {
                                            LocalizationLevel.getInstance().allBeacons = new LinkedList<>();
                                        }
                                    });
                                });
                        Navigation.findNavController(Login.this.requireView()).navigate(R.id.action_login_to_mainMenu, bundle);
                    } else {
                        Toast.makeText(Login.this.getActivity(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBar.setVisibility(View.INVISIBLE);
                });
    }
}