package com.example.localizationserdar.Onboarding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.LoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

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

        binding.btnSignIn.setOnClickListener(v -> {

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
                    if(task.isSuccessful()){
                        Navigation.findNavController(requireView()).navigate(R.id.action_login_to_mainMenu);
                    }
                    else {
                        Toast.makeText(getActivity(), "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBar.setVisibility(View.INVISIBLE);
                });
    }
}