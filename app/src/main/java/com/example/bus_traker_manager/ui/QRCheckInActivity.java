package com.example.bus_traker_manager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.bus_traker_manager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class QRCheckInActivity extends AppCompatActivity {
    private static final int REQ_CAMERA = 2001;
    private CompoundBarcodeView barcodeView;
    private DatabaseReference busesRef, tripsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_check_in);
        barcodeView = findViewById(R.id.barcodeScannerView);
        busesRef = FirebaseDatabase.getInstance().getReference("buses");
        tripsRef = FirebaseDatabase.getInstance().getReference("trips");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        } else {
            startScanner();
        }
    }

    private void startScanner() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    barcodeView.pause();
                    handleQRCode(result.getText());
                }
            }
        });
        barcodeView.resume();
    }

    private void handleQRCode(String qrText) {
        // Assume QR contains busId or tripId
        busesRef.child(qrText).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StringBuilder info = new StringBuilder();
                    String busId = snapshot.child("busId").getValue(String.class);
                    String plate = snapshot.child("plate").getValue(String.class);
                    String routeId = snapshot.child("routeId").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    info.append("Bus ID: ").append(busId).append("\n");
                    info.append("Plate: ").append(plate).append("\n");
                    info.append("Route: ").append(routeId).append("\n");
                    info.append("Status: ").append(status).append("\n");
                    tripsRef.orderByChild("busId").equalTo(busId).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot tripSnap) {
                            if (tripSnap.exists()) {
                                for (DataSnapshot t : tripSnap.getChildren()) {
                                    Long studentCount = t.child("studentCount").getValue(Long.class);
                                    if (studentCount != null) {
                                        info.append("Students: ").append(studentCount).append("\n");
                                    }
                                    String route = t.child("routeId").getValue(String.class);
                                    if (route != null) {
                                        info.append("Route: ").append(route).append("\n");
                                    }
                                    break;
                                }
                            }
                            returnResult(info.toString());
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            returnResult(info.toString());
                        }
                    });
                } else {
                    tripsRef.child(qrText).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot tripSnap) {
                            if (tripSnap.exists()) {
                                StringBuilder info = new StringBuilder();
                                String busId = tripSnap.child("busId").getValue(String.class);
                                String driverUid = tripSnap.child("driverUid").getValue(String.class);
                                Long studentCount = tripSnap.child("studentCount").getValue(Long.class);
                                String routeId = tripSnap.child("routeId").getValue(String.class);
                                info.append("Trip ID: ").append(qrText).append("\n");
                                info.append("Bus ID: ").append(busId).append("\n");
                                info.append("Driver: ").append(driverUid).append("\n");
                                if (studentCount != null) info.append("Students: ").append(studentCount).append("\n");
                                if (routeId != null) info.append("Route: ").append(routeId).append("\n");
                                returnResult(info.toString());
                            } else {
                                Toast.makeText(QRCheckInActivity.this, "Invalid QR code", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void returnResult(String busInfo) {
        Intent result = new Intent();
        result.putExtra("busInfo", busInfo);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera permission denied permanently. Enable it in Settings.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                }
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null) barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeView != null) barcodeView.pause();
    }
}
