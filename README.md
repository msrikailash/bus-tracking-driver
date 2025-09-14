# 🚌 College Bus Tracking Driver App

A comprehensive Android application for college bus drivers to track their location, manage bus stops, and handle leave applications. Built with Java, Firebase, and Material Design 3.

## 📱 Features

### 🔐 Authentication
- **Firebase Authentication**: Secure login with email/password
- **Session Management**: Automatic login persistence
- **Driver Profile**: Each driver linked to a specific bus number

### 🗺️ GPS Tracking
- **Real-time Location**: Continuous GPS updates every 5 seconds
- **Battery Efficient**: Optimized location services with balanced accuracy
- **Status Management**: Online/offline status tracking
- **Background Service**: Continues tracking when app is in background

### 🚏 Stop Management
- **CRUD Operations**: Create, Read, Update, Delete bus stops
- **Map Integration**: Interactive map selection for stop locations
- **Stop Details**: Name, coordinates, and bus association
- **Visual Route**: Display all stops on a map with route visualization

### 📋 Leave Application
- **Leave Requests**: Submit leave applications with reason and date
- **Status Tracking**: Pending, approved, rejected status
- **Date Picker**: Easy date selection with validation
- **Supervisor Review**: Leave requests sent to supervisors

### 🗺️ Route Visualization
- **Interactive Map**: Google Maps integration
- **Stop Markers**: Visual representation of all bus stops
- **Route Polyline**: Connected route showing bus path
- **Real-time Updates**: Live location tracking

### 🔔 Notifications
- **Firebase Cloud Messaging**: Push notifications for leave approvals
- **Local Notifications**: Geofencing alerts for stop proximity
- **Status Updates**: Real-time notifications for important events

## 🏗️ Architecture

### Technology Stack
- **Language**: Java 11
- **UI Framework**: Material Design 3
- **Backend**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Maps**: Google Maps API
- **Location**: Google Play Services Location
- **Notifications**: Firebase Cloud Messaging

### Project Structure
```
app/src/main/java/com/example/bus_traker_manager/
├── model/                 # Data models
│   ├── Driver.java       # Driver information
│   ├── Stop.java         # Bus stop data
│   └── Leave.java        # Leave application data
├── services/             # Business logic
│   ├── FirebaseService.java    # Firebase operations
│   └── LocationService.java    # GPS tracking
├── ui/                   # User interface
│   ├── LoginActivity.java
│   ├── DashboardActivity.java
│   ├── StopsManagementActivity.java
│   ├── AddStopActivity.java
│   ├── EditStopActivity.java
│   ├── MapSelectionActivity.java
│   ├── RouteMapActivity.java
│   └── LeaveApplicationActivity.java
└── BusTrackerApp.java    # Application class
```

## 🗄️ Firebase Database Structure

```json
{
  "drivers": {
    "driverId1": {
      "name": "Ramesh",
      "busNo": "AP31AB1234",
      "lat": 17.0123,
      "lng": 82.1234,
      "status": "online",
      "email": "ramesh@college.edu",
      "phone": "+919876543210"
    }
  },
  "stops": {
    "AP31AB1234": {
      "stopId1": {
        "stopName": "Velangi Stop",
        "stopLat": 17.0567,
        "stopLng": 82.1456,
        "busNo": "AP31AB1234"
      },
      "stopId2": {
        "stopName": "College Main Gate",
        "stopLat": 17.0789,
        "stopLng": 82.1678,
        "busNo": "AP31AB1234"
      }
    }
  },
  "leaves": {
    "leaveId1": {
      "driverId": "driverId1",
      "reason": "Medical leave",
      "status": "pending",
      "requestDate": "2024-01-15T10:30:00Z",
      "leaveDate": "2024-01-20T00:00:00Z",
      "driverName": "Ramesh",
      "busNo": "AP31AB1234"
    }
  }
}
```

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Google Play Services
- Firebase project
- Google Maps API key

### 1. Firebase Setup
1. Create a new Firebase project
2. Enable Authentication (Email/Password)
3. Enable Realtime Database
4. Enable Cloud Messaging
5. Download `google-services.json` and place it in `app/`

### 2. Google Maps Setup
1. Enable Google Maps API in Google Cloud Console
2. Create API key with Maps SDK for Android
3. Add API key to `app/src/main/res/values/google_maps_api.xml`

### 3. Build Configuration
1. Open project in Android Studio
2. Sync Gradle files
3. Build project

### 4. Database Rules
Set up Firebase Realtime Database rules:
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

## 📱 Usage Guide

### Driver Login
1. Enter email and password
2. App validates credentials with Firebase
3. Loads driver profile and bus assignment

### GPS Tracking
1. Tap "Start Tracking" on dashboard
2. Grant location permissions
3. App begins sending location updates every 5 seconds
4. Status changes to "online"
5. Tap "Stop Tracking" to end

### Managing Stops
1. Navigate to "Manage Stops"
2. View list of existing stops
3. Tap "+" to add new stop
4. Enter stop name and select location on map
5. Edit or delete existing stops

### Leave Application
1. Tap "Apply Leave" on dashboard
2. Enter reason for leave
3. Select leave date using date picker
4. Submit application
5. Wait for supervisor approval

### Route Visualization
1. Tap "View Route" on dashboard
2. See all stops marked on map
3. Blue polyline shows bus route
4. Zoom and pan to explore route

## 🔧 Configuration

### Location Settings
- **Update Interval**: 5 seconds
- **Accuracy**: Balanced power accuracy
- **Background**: Enabled with foreground service

### Firebase Settings
- **Persistence**: Enabled for offline support
- **Sync**: Automatic real-time synchronization
- **Security**: Rule-based access control

### UI/UX Features
- **Material Design 3**: Modern, accessible interface
- **Dark/Light Theme**: Automatic theme switching
- **Responsive Design**: Works on all screen sizes
- **Accessibility**: Screen reader support

## 🛠️ Development

### Adding New Features
1. Create model classes in `model/` package
2. Add Firebase operations in `FirebaseService`
3. Create UI activities in `ui/` package
4. Update AndroidManifest.xml
5. Add necessary permissions

### Testing
- Unit tests for business logic
- Integration tests for Firebase operations
- UI tests for user interactions
- Location service testing

### Performance Optimization
- Efficient location updates
- Battery optimization
- Network usage optimization
- Memory management

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🔮 Future Enhancements

- **Real-time Chat**: Driver-supervisor communication
- **Route Optimization**: AI-powered route suggestions
- **Analytics Dashboard**: Performance metrics
- **Offline Mode**: Enhanced offline capabilities
- **Multi-language Support**: Internationalization
- **Advanced Notifications**: Custom notification types

---

**Built with ❤️ for College Bus Management**
