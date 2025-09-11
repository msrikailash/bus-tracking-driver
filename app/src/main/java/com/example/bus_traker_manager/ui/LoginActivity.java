package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialTextView loadingText;
    private View progressBar;
    
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        firebaseService = new FirebaseService();
        
        // Check if user is already logged in
        if (firebaseService.getCurrentUser() != null) {
            navigateToDashboard();
            return;
        }
        
        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            emailInput.requestFocus();
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Attempt login
        firebaseService.signInWithEmailAndPassword(email, password, new FirebaseService.OnAuthListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Get driver data from Firebase
                firebaseService.getDriverData(user.getUid(), new FirebaseService.OnDriverListener() {
                    @Override
                    public void onSuccess(Driver driver) {
                        setLoadingState(false);
                        // Store driver data in SharedPreferences or pass to next activity
                        navigateToDashboard();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        setLoadingState(false);
                        Toast.makeText(LoginActivity.this, 
                            "Driver data not found. Please contact administrator.", 
                            Toast.LENGTH_LONG).show();
                        firebaseService.signOut();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                setLoadingState(false);
                String errorMessage = "Login failed";
                if (e != null && e.getMessage() != null) {
                    if (e.getMessage().contains("password")) {
                        errorMessage = "Invalid password";
                    } else if (e.getMessage().contains("user")) {
                        errorMessage = "User not found";
                    } else if (e.getMessage().contains("network")) {
                        errorMessage = "Network error. Please check your connection";
                    }
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginButton.setEnabled(false);
            emailInput.setEnabled(false);
            passwordInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
        } else {
            loginButton.setEnabled(true);
            emailInput.setEnabled(true);
            passwordInput.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
