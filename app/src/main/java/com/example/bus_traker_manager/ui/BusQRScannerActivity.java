package com.example.bus_traker_manager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.services.FirebaseService;
import com.example.bus_traker_manager.services.QRCodeService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BusQRScannerActivity extends AppCompatActivity implements QRCodeService.BusQRCodeScanListener {
    private static final String TAG = "BusQRScannerActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    
    private PreviewView previewView;
    private MaterialCardView resultCard;
    private MaterialTextView busInfoText;
    private MaterialButton assignButton;
    private MaterialButton scanAgainButton;
    
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private QRCodeService qrCodeService;
    private FirebaseService firebaseService;
    
    private QRCodeService.BusQRCodeData scannedBusData;
    private Driver currentDriver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_qr_scanner);
        
        // Initialize services
        qrCodeService = new QRCodeService(this);
        firebaseService = new FirebaseService();
        
        // Get current driver from intent
        currentDriver = (Driver) getIntent().getSerializableExtra("driver");
        if (currentDriver == null) {
            Toast.makeText(this, "Driver information not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initializeViews();
        setupToolbar();
        setupBarcodeScanner();
        
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }
    
    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        resultCard = findViewById(R.id.resultCard);
        busInfoText = findViewById(R.id.busInfoText);
        assignButton = findViewById(R.id.assignButton);
        scanAgainButton = findViewById(R.id.scanAgainButton);
        
        // Initially hide result card
        resultCard.setVisibility(View.GONE);
        
        // Setup button click listeners
        assignButton.setOnClickListener(v -> assignBusToDriver());
        scanAgainButton.setOnClickListener(v -> resetScanner());
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scan Bus QR Code");
        }
    }
    
    private void setupBarcodeScanner() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }
    
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }
    
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);
                
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    
    private void analyzeImage(ImageProxy imageProxy) {
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        
        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        if (barcode.getRawValue() != null) {
                            processQRCode(barcode.getRawValue());
                            break; // Process only the first QR code found
                        }
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Barcode scanning failed", e);
                    imageProxy.close();
                });
    }
    
    private void processQRCode(String qrData) {
        Log.d(TAG, "Processing QR code: " + qrData);
        
        // Validate QR code using QRCodeService
        scannedBusData = qrCodeService.validateBusQRCode(qrData);
        
        if (scannedBusData != null) {
            // QR code is valid, show bus information
            runOnUiThread(() -> showBusInfo(scannedBusData));
        } else {
            // Invalid QR code
            runOnUiThread(() -> {
                Toast.makeText(this, "Invalid QR code or not a bus QR code", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void showBusInfo(QRCodeService.BusQRCodeData busData) {
        String busInfo = String.format("Bus Number: %s\nRoute ID: %s\nBus ID: %s\n\nTap 'Assign Bus' to assign this bus to your account.",
                busData.getBusNumber(),
                busData.getRouteId(),
                busData.getBusId());
        
        busInfoText.setText(busInfo);
        resultCard.setVisibility(View.VISIBLE);
        
        // Stop camera preview
        stopCamera();
    }
    
    private void assignBusToDriver() {
        if (scannedBusData == null || currentDriver == null) {
            Toast.makeText(this, "No bus data available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading state
        assignButton.setEnabled(false);
        assignButton.setText("Assigning...");
        
        // Update driver's bus assignment in Firebase
        firebaseService.updateDriverBusAssignment(currentDriver.getDriverId(), 
                scannedBusData.getBusId(), 
                scannedBusData.getBusNumber(),
                new FirebaseService.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(BusQRScannerActivity.this, 
                                    "Bus " + scannedBusData.getBusNumber() + " assigned successfully!", 
                                    Toast.LENGTH_LONG).show();
                            
                            // Return to dashboard with updated driver info
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("busAssigned", true);
                            resultIntent.putExtra("busNumber", scannedBusData.getBusNumber());
                            resultIntent.putExtra("busId", scannedBusData.getBusId());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        });
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(BusQRScannerActivity.this, 
                                    "Failed to assign bus: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                            assignButton.setEnabled(true);
                            assignButton.setText("Assign Bus");
                        });
                    }
                });
    }
    
    private void resetScanner() {
        resultCard.setVisibility(View.GONE);
        scannedBusData = null;
        startCamera();
    }
    
    private void stopCamera() {
        // Camera will be stopped when we bind new use cases
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    
    @Override
    public void onBusQRCodeScanned(QRCodeService.BusQRCodeData busQRCodeData) {
        // This is handled in processQRCode method
    }
    
    @Override
    public void onQRCodeError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "QR Code Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
