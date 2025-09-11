package com.example.bus_traker_manager.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class QRCodeService {
    private static final String TAG = "QRCodeService";
    private static final int QR_CODE_SIZE = 512;
    private static final String QR_SECRET_KEY = "BusTracker2024"; // In production, use secure key management
    
    private final Context context;
    
    public QRCodeService(Context context) {
        this.context = context;
    }
    
    /**
     * Generate QR code for bus assignment
     * @param busId Bus ID
     * @param busNumber Bus number
     * @param routeId Route ID
     * @return Bitmap QR code
     */
    public Bitmap generateBusQRCode(String busId, String busNumber, String routeId) {
        try {
            // Create QR code data with timestamp and hash for security
            String qrData = createBusQRData(busId, busNumber, routeId);
            
            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);
            
            // Convert to bitmap
            Bitmap bitmap = Bitmap.createBitmap(QR_CODE_SIZE, QR_CODE_SIZE, Bitmap.Config.RGB_565);
            for (int x = 0; x < QR_CODE_SIZE; x++) {
                for (int y = 0; y < QR_CODE_SIZE; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            Log.d(TAG, "Generated QR code for bus: " + busNumber);
            return bitmap;
            
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code", e);
            return null;
        }
    }
    
    /**
     * Create secure QR code data for bus assignment
     */
    private String createBusQRData(String busId, String busNumber, String routeId) {
        long timestamp = System.currentTimeMillis();
        String data = String.format("BUS|%s|%s|%s|%d", busId, busNumber, routeId, timestamp);
        String hash = generateHash(data + QR_SECRET_KEY);
        return data + "|" + hash;
    }
    
    /**
     * Validate bus QR code data
     */
    public BusQRCodeData validateBusQRCode(String qrData) {
        try {
            String[] parts = qrData.split("\\|");
            if (parts.length != 6) {
                Log.w(TAG, "Invalid QR code format");
                return null;
            }
            
            String type = parts[0];
            if (!"BUS".equals(type)) {
                Log.w(TAG, "Not a bus QR code");
                return null;
            }
            
            String busId = parts[1];
            String busNumber = parts[2];
            String routeId = parts[3];
            long timestamp = Long.parseLong(parts[4]);
            String hash = parts[5];
            
            // Validate hash
            String dataWithoutHash = String.format("BUS|%s|%s|%s|%d", busId, busNumber, routeId, timestamp);
            String expectedHash = generateHash(dataWithoutHash + QR_SECRET_KEY);
            
            if (!hash.equals(expectedHash)) {
                Log.w(TAG, "QR code hash validation failed");
                return null;
            }
            
            // Check if QR code is not too old (within 7 days for bus assignment)
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > 7 * 24 * 60 * 60 * 1000) {
                Log.w(TAG, "QR code is too old");
                return null;
            }
            
            return new BusQRCodeData(busId, busNumber, routeId, timestamp);
            
        } catch (Exception e) {
            Log.e(TAG, "Error validating bus QR code", e);
            return null;
        }
    }
    
    /**
     * Generate SHA-256 hash
     */
    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error generating hash", e);
            return "";
        }
    }
    
    /**
     * Data class for bus QR code information
     */
    public static class BusQRCodeData {
        private final String busId;
        private final String busNumber;
        private final String routeId;
        private final long timestamp;
        
        public BusQRCodeData(String busId, String busNumber, String routeId, long timestamp) {
            this.busId = busId;
            this.busNumber = busNumber;
            this.routeId = routeId;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getBusId() { return busId; }
        public String getBusNumber() { return busNumber; }
        public String getRouteId() { return routeId; }
        public long getTimestamp() { return timestamp; }
    }
    
    /**
     * Interface for QR code scanning callbacks
     */
    public interface BusQRCodeScanListener {
        void onBusQRCodeScanned(BusQRCodeData busQRCodeData);
        void onQRCodeError(String error);
    }
}