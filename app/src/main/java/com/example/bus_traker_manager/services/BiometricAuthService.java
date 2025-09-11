package com.example.bus_traker_manager.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.bus_traker_manager.R;

import java.util.concurrent.Executor;

public class BiometricAuthService {
    private static final String TAG = "BiometricAuthService";
    
    private Context context;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    
    public interface BiometricAuthCallback {
        void onAuthenticationSucceeded();
        void onAuthenticationFailed();
        void onAuthenticationError(String error);
    }
    
    public BiometricAuthService(FragmentActivity activity) {
        this.context = activity;
        
        Executor executor = ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt(activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e(TAG, "Authentication error: " + errString);
                if (authCallback != null) {
                    authCallback.onAuthenticationError(errString.toString());
                }
            }
            
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Authentication succeeded");
                if (authCallback != null) {
                    authCallback.onAuthenticationSucceeded();
                }
            }
            
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.w(TAG, "Authentication failed");
                if (authCallback != null) {
                    authCallback.onAuthenticationFailed();
                }
            }
        });
        
        setupPromptInfo();
    }
    
    private BiometricAuthCallback authCallback;
    
    private void setupPromptInfo() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_auth_title))
            .setSubtitle(context.getString(R.string.biometric_auth_subtitle))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build();
    }
    
    public boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(TAG, "App can authenticate using biometrics.");
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e(TAG, "No biometric features available on this device.");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e(TAG, "Biometric features are currently unavailable.");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e(TAG, "The user hasn't associated any biometric credentials with their account.");
                return false;
            default:
                return false;
        }
    }
    
    public void authenticate(BiometricAuthCallback callback) {
        this.authCallback = callback;
        
        if (!isBiometricAvailable()) {
            callback.onAuthenticationError(context.getString(R.string.biometric_not_available));
            return;
        }
        
        biometricPrompt.authenticate(promptInfo);
    }
    
    public void authenticateWithCrypto(BiometricPrompt.CryptoObject cryptoObject, BiometricAuthCallback callback) {
        this.authCallback = callback;
        
        if (!isBiometricAvailable()) {
            callback.onAuthenticationError(context.getString(R.string.biometric_not_available));
            return;
        }
        
        biometricPrompt.authenticate(promptInfo, cryptoObject);
    }
}







