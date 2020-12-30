package com.example.localizationserdar.Onboarding;

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
import com.example.localizationserdar.databinding.RegistrationBinding;
import com.example.localizationserdar.datamanager.DataManager;
import com.example.localizationserdar.datamodels.User;
import com.example.localizationserdar.utils.OnboardingUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;


public class Registration extends Fragment {

    private static final String TAG = "DEBUGGING...";
    private FirebaseAuth mAuth;
    private RegistrationBinding binding;

    public Registration() {
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
        binding = RegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        ((OnboardingUtils)requireActivity()).hideToolbar();

//        if (currentUser != null) {
//            Navigation.findNavController(view).navigate(R.id.action_registration_to_mainMenu);
//        }

        binding.btnCreateAccount.setOnClickListener(v -> {

            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
            String name = binding.etName.getText().toString().trim();
            String familyName = binding.etFamilyName.getText().toString().trim();

            if (name.isEmpty()) {
                binding.etName.setError(getResources().getString(R.string.error_name));
                return;
            }
            if (familyName.isEmpty()) {
                binding.etFamilyName.setError(getResources().getString(R.string.error_surname));
                return;
            }
            if (phoneNumber.isEmpty()) {
                binding.etPhoneNumber.setError(getResources().getString(R.string.error_phone_number));
                return;
            }
            if (email.isEmpty()) {
                binding.etEmail.setError(getResources().getString(R.string.error_email));
                return;
            }
            if (password.isEmpty()) {
                binding.etPassword.setError(getResources().getString(R.string.error_password));
                return;
            }
            createAccount(email, password, name, familyName, phoneNumber);
        });

        binding.tvSignIn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_registration_to_login));
    }

    private void createAccount(String email, String password, String firstName, String lastName, String phoneNumber) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Load data to firestore
                        User user;
                        if (LocalizationLevel.getInstance().currentUser == null) {

                            user = new User();
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                user.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            }
                            user.firstName = firstName;
                            user.lastName = lastName;
                            user.phoneNumber = phoneNumber;
                            user.email = email;
                            user.createdAt = new Timestamp(new Date());
                        } else {
                            user = LocalizationLevel.getInstance().currentUser;
                        }

                        Log.d("The user info is here", user.userId+ user.firstName+user.lastName+ user.email+user.phoneNumber);

                        DataManager.getInstance().createUser(user, (success, exception) ->{
                            if (success != null && success) {
                                try {
                                    LocalizationLevel.getInstance().currentUser = user;
                                    Log.d(TAG, "All is good" + LocalizationLevel.getInstance().currentUser.firstName);

                                } catch (NullPointerException e) {
                                    Log.d("The error is me: ",e+"");
                                }
                            } else {
                                Log.d(TAG, "Hey, we can't load the data in firestore"+exception);
                            }
                        });

//                        if (LocalizationLevel.getInstance().currentUser == null) {
//                            LocalizationLevel.getInstance().currentUser = user;
//                        }
                        Navigation.findNavController(requireView()).navigate(R.id.action_registration_to_mainMenu);
                    } else {
                        Toast.makeText(getActivity(), "Registration is failed", Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBar.setVisibility(View.INVISIBLE);
        });

    }
}