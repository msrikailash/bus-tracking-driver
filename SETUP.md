# 🚌 Bus Tracker Manager - Setup Guide

## 📋 Prerequisites

### 1. Java Development Kit (JDK) 11
- Download and install JDK 11 from [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or [OpenJDK](https://adoptium.net/)
- Set JAVA_HOME environment variable

### 2. Android Studio (Recommended)
- Download from [Android Developer](https://developer.android.com/studio)
- Install Android SDK (API level 24-36)

### 3. Firebase Project Setup
- Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
- Enable Authentication, Realtime Database, and Cloud Messaging
- Download `google-services.json` and place it in the `app/` directory

## 🔧 Environment Setup

### Windows Setup

#### Option 1: Manual Setup
```powershell
# Run PowerShell as Administrator
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-11.0.x", "Machine")
[Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";%JAVA_HOME%\bin", "Machine")
```

#### Option 2: Use Android Studio JDK
```powershell
# If Android Studio is installed
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Android\Android Studio\jbr", "Machine")
```

#### Option 3: Use Build Script
```cmd
# Run the provided build.bat script
build.bat
```

### macOS/Linux Setup
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

## 🚀 Building the Project

### Using Gradle Wrapper
```bash
# Clean and build
./gradlew clean
./gradlew assembleDebug

# Build specific flavor
./gradlew assembleProductionDebug
./gradlew assembleStagingDebug
./gradlew assembleDevelopmentDebug
```

### Using Android Studio
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click "Build" → "Make Project" or press Ctrl+F9

## 📱 Running the App

### On Device/Emulator
1. Connect Android device or start emulator
2. Run `./gradlew installDebug` or use Android Studio's "Run" button
3. The app will install and launch automatically

### Debug APK Location
After successful build, find the APK at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## 🔑 Firebase Configuration

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "Bus Tracker Manager"
4. Enable Google Analytics (optional)

### 2. Add Android App
1. Click "Add app" → "Android"
2. Package name: `com.example.bus_traker_manager`
3. App nickname: "Bus Tracker Driver"
4. Download `google-services.json`

### 3. Enable Services
- **Authentication**: Email/Password, Phone
- **Realtime Database**: Create database in test mode
- **Cloud Messaging**: Enable FCM
- **Storage**: Enable Cloud Storage

### 4. Database Rules
```json
{
  "rules": {
    "drivers": {
      "$driverId": {
        ".read": "auth != null && auth.uid == $driverId",
        ".write": "auth != null && auth.uid == $driverId"
      }
    },
    "stops": {
      "$busNo": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "leaves": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

## 🗺️ Google Maps Setup

### 1. Get API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create new project or select existing
3. Enable Maps SDK for Android
4. Create API key

### 2. Configure API Key
Create `app/src/main/res/values/google_maps_api.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_API_KEY_HERE</string>
</resources>
```

## 🐛 Troubleshooting

### Common Issues

#### 1. JAVA_HOME not set
**Error**: `JAVA_HOME is not set and no 'java' command could be found`
**Solution**: Set JAVA_HOME environment variable (see Environment Setup)

#### 2. Google Services Error
**Error**: `No matching client found for package name`
**Solution**: 
- Ensure `google-services.json` is in the `app/` directory
- Verify package name matches Firebase project
- Remove `applicationIdSuffix` from build variants

#### 3. Build Variant Issues
**Error**: Build fails with flavor-specific errors
**Solution**: Use base package name for Firebase compatibility

#### 4. Permission Issues
**Error**: Location or camera permissions denied
**Solution**: Grant permissions in app settings or reinstall app

### Debug Commands
```bash
# Check Java installation
java -version
javac -version

# Check Gradle
./gradlew --version

# Clean and rebuild
./gradlew clean build

# Run with debug info
./gradlew assembleDebug --info
```

## 📊 Project Structure

```
bus_traker_manager/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/bus_traker_manager/
│   │   │   ├── model/          # Data models
│   │   │   ├── services/       # Business logic
│   │   │   └── ui/            # Activities and fragments
│   │   ├── res/
│   │   │   ├── layout/        # UI layouts
│   │   │   ├── values/        # Strings, colors, themes
│   │   │   └── drawable/      # Icons and images
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts       # App-level build config
│   └── google-services.json   # Firebase config
├── build.gradle.kts           # Project-level build config
├── gradle.properties          # Gradle properties
├── build.bat                  # Windows build script
└── README.md                  # Project documentation
```

## 🎯 Next Steps

1. **Test the app** on a real device or emulator
2. **Configure Firebase** with your specific requirements
3. **Customize the UI** to match your brand
4. **Set up monitoring** with Firebase Analytics
5. **Deploy to production** when ready

## 📞 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review Firebase Console logs
3. Check Android Studio's Logcat for detailed error messages
4. Ensure all dependencies are properly configured

---

**Happy coding! 🚌📱**







