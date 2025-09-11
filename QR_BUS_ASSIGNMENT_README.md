# QR Code Bus Assignment Feature

## Overview

The QR Code Bus Assignment feature allows drivers to scan QR codes to automatically assign buses to their accounts. This feature enhances the bus management system by providing a secure and efficient way to link drivers with their assigned vehicles.

## Features Implemented

### 1. QR Code Service (`QRCodeService.java`)
- **Bus QR Code Generation**: Creates secure QR codes containing bus information
- **QR Code Validation**: Validates scanned QR codes with hash verification
- **Security**: Uses SHA-256 hashing and timestamp validation
- **Data Format**: `BUS|busId|busNumber|routeId|timestamp|hash`

### 2. QR Scanner Activity (`BusQRScannerActivity.java`)
- **Camera Integration**: Uses CameraX and ML Kit for real-time QR scanning
- **Permission Handling**: Manages camera permissions automatically
- **Bus Information Display**: Shows scanned bus details before assignment
- **Assignment Process**: Updates driver's bus assignment in Firebase

### 3. Dashboard Integration
- **QR Scan Button**: Added to dashboard quick actions
- **Result Handling**: Processes bus assignment results
- **UI Updates**: Automatically refreshes driver information after assignment

### 4. Firebase Integration
- **Bus Assignment Update**: New method `updateDriverBusAssignment()` in `FirebaseService`
- **Real-time Updates**: Updates driver's bus number and status in Realtime Database

## Technical Implementation

### QR Code Data Structure
```
BUS|busId|busNumber|routeId|timestamp|hash
```

**Components:**
- `BUS`: Fixed identifier for bus QR codes
- `busId`: Unique bus identifier
- `busNumber`: Human-readable bus number
- `routeId`: Associated route identifier
- `timestamp`: Creation timestamp (for expiration)
- `hash`: SHA-256 hash for security validation

### Security Features
1. **Hash Validation**: SHA-256 hash prevents tampering
2. **Timestamp Expiration**: QR codes expire after 7 days
3. **Type Validation**: Only processes "BUS" type QR codes
4. **Secret Key**: Uses secure key for hash generation

### Dependencies Added
```gradle
// CameraX and ML Kit for QR scanning
implementation("androidx.camera:camera-core:1.3.1")
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")
implementation("com.google.mlkit:barcode-scanning:17.2.0")
```

## User Flow

### 1. Driver Initiates QR Scan
- Driver taps "Scan QR Code" button on dashboard
- App requests camera permission if needed
- Camera preview opens with scanning overlay

### 2. QR Code Scanning
- Driver points camera at bus QR code
- ML Kit processes camera feed in real-time
- Valid QR code triggers bus information display

### 3. Bus Assignment
- Driver reviews bus information
- Taps "Assign Bus" to confirm assignment
- App updates Firebase with new bus assignment
- Driver returns to dashboard with updated information

### 4. Result Handling
- Success: Driver sees updated bus number on dashboard
- Error: Appropriate error message displayed
- Retry: Option to scan again if needed

## Testing

### QR Code Generator Script
A Node.js script (`generate-bus-qr.js`) is provided to generate test QR codes:

```bash
# Install dependencies
npm install

# Generate test QR codes
npm run generate
```

This creates PNG files for each sample bus that can be scanned with the Android app.

### Sample Bus Data
```javascript
const sampleBuses = [
    { busId: 'BUS001', busNumber: 'B001', routeId: 'ROUTE001' },
    { busId: 'BUS002', busNumber: 'B002', routeId: 'ROUTE002' },
    // ... more buses
];
```

## Files Modified/Created

### New Files
- `app/src/main/java/com/example/bus_traker_manager/ui/BusQRScannerActivity.java`
- `app/src/main/res/layout/activity_bus_qr_scanner.xml`
- `app/src/main/res/drawable/ic_qr_code.xml`
- `generate-bus-qr.js`
- `package.json`
- `QR_BUS_ASSIGNMENT_README.md`

### Modified Files
- `app/src/main/java/com/example/bus_traker_manager/services/QRCodeService.java`
- `app/src/main/java/com/example/bus_traker_manager/services/FirebaseService.java`
- `app/src/main/java/com/example/bus_traker_manager/ui/DashboardActivity.java`
- `app/src/main/res/layout/activity_dashboard.xml`
- `app/src/main/res/values/strings.xml`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`

## Permissions Required

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.any" android:required="false" />
```

## Error Handling

### Common Scenarios
1. **Invalid QR Code**: Shows "Invalid QR code or not a bus QR code"
2. **Expired QR Code**: Shows "QR code is too old"
3. **Hash Mismatch**: Shows "QR code hash validation failed"
4. **Camera Permission Denied**: Shows "Camera permission is required"
5. **Network Error**: Shows "Failed to assign bus: [error]"

### Recovery Options
- **Scan Again**: Option to retry scanning
- **Manual Assignment**: Fallback to manual bus assignment (future feature)
- **Contact Admin**: For persistent issues

## Future Enhancements

### Planned Features
1. **QR Code Generation**: Admin interface to generate bus QR codes
2. **Batch Assignment**: Assign multiple buses at once
3. **QR Code History**: Track scanned QR codes
4. **Offline Support**: Cache QR codes for offline scanning
5. **Enhanced Security**: Biometric authentication for bus assignment

### Integration Opportunities
1. **Route Management**: Link QR codes to specific routes
2. **Schedule Integration**: Include schedule information in QR codes
3. **Maintenance Tracking**: Include maintenance status in QR codes
4. **Student Information**: Include student count and details

## Troubleshooting

### Common Issues
1. **Camera Not Working**: Check camera permissions
2. **QR Code Not Scanning**: Ensure good lighting and steady camera
3. **Assignment Fails**: Check network connection and Firebase configuration
4. **App Crashes**: Verify all dependencies are properly installed

### Debug Information
Enable debug logging to troubleshoot issues:
```java
Log.d("BusQRScannerActivity", "Processing QR code: " + qrData);
Log.d("FirebaseService", "Bus assignment updated successfully");
```

## Security Considerations

1. **QR Code Expiration**: Prevents use of old QR codes
2. **Hash Validation**: Ensures QR code integrity
3. **Type Validation**: Prevents scanning of non-bus QR codes
4. **Permission Checks**: Ensures proper camera access
5. **Network Security**: Uses Firebase security rules

## Performance Optimization

1. **Camera Optimization**: Uses CameraX for efficient camera handling
2. **ML Kit Integration**: Optimized barcode scanning
3. **Background Processing**: QR processing on background thread
4. **Memory Management**: Proper cleanup of camera resources
5. **UI Responsiveness**: Non-blocking QR processing

This implementation provides a robust, secure, and user-friendly QR code bus assignment system that integrates seamlessly with the existing bus tracker application.
