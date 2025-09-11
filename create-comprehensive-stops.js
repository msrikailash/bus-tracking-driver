const { initializeApp } = require('firebase/app');
const { getDatabase, ref, set } = require('firebase/database');

// Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyA-9yz_aGnWJPdzy9C6bpZtFuvMvd7SqA4",
  authDomain: "classroom-90c00.firebaseapp.com",
  databaseURL: "https://classroom-90c00-default-rtdb.firebaseio.com",
  projectId: "classroom-90c00",
  storageBucket: "classroom-90c00.appspot.com",
  messagingSenderId: "744197330204",
  appId: "1:744197330204:android:541a1d3bbbfc1b0ca4d6d9"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const database = getDatabase(app);

// Comprehensive bus stops data structure
const comprehensiveStops = {
  "AP31AB1234": {
    "stop_001": {
      "id": "stop_001",
      "name": "Velangi Bus Stop",
      "busNo": "AP31AB1234",
      "stopCode": "VBS001",
      "description": "Main bus stop near Velangi junction",
      "stopType": "pickup_drop",
      "landmark": "Near Velangi Police Station",
      "createdBy": "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2",
      "createdAt": 1703123456789,
      "updatedAt": 1703123456789,
      
      "location": {
        "latitude": 17.0123,
        "longitude": 82.1234,
        "address": "Velangi Junction, Velangi, Andhra Pradesh",
        "accuracy": 5.0,
        "altitude": 15.5,
        "geofenceRadius": 100,
        "locationType": "junction",
        "accessibility": "easy",
        "parkingAvailable": true,
        "shelterAvailable": true
      },
      
      "schedule": {
        "morningPickup": "07:30",
        "morningDrop": "08:15",
        "afternoonPickup": "14:30",
        "afternoonDrop": "15:15",
        "eveningPickup": "17:30",
        "eveningDrop": "18:15",
        "estimatedDuration": 15,
        "bufferTime": 5
      },
      
      "status": {
        "isActive": true,
        "isReached": false,
        "isSkipped": false,
        "currentStatus": "pending",
        "lastStatusUpdate": 1703123456789,
        "maintenanceRequired": false,
        "isTemporary": false
      },
      
      "tracking": {
        "estimatedArrivalTime": 1703123456789,
        "actualArrivalTime": null,
        "actualDepartureTime": null,
        "delayMinutes": 0,
        "passengersBoarding": 0,
        "passengersAlighting": 0,
        "currentPassengerCount": 25,
        "maxCapacity": 50,
        "weatherCondition": "clear",
        "trafficCondition": "normal",
        "routeDeviation": false
      },
      
      "metadata": {
        "version": "1.0",
        "lastSync": 1703123456789,
        "syncStatus": "synced",
        "offlineData": false,
        "dataSource": "driver_app",
        "validationStatus": "validated",
        "tags": ["main_stop", "junction", "shelter"]
      }
    },
    
    "stop_002": {
      "id": "stop_002",
      "name": "Rajahmundry Central",
      "busNo": "AP31AB1234",
      "stopCode": "RJC002",
      "description": "Central bus stop in Rajahmundry city",
      "stopType": "pickup_drop",
      "landmark": "Near Rajahmundry Railway Station",
      "createdBy": "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2",
      "createdAt": 1703123456790,
      "updatedAt": 1703123456790,
      
      "location": {
        "latitude": 17.0000,
        "longitude": 81.8000,
        "address": "Rajahmundry Central, Rajahmundry, Andhra Pradesh",
        "accuracy": 5.0,
        "altitude": 20.0,
        "geofenceRadius": 150,
        "locationType": "commercial",
        "accessibility": "easy",
        "parkingAvailable": true,
        "shelterAvailable": true
      },
      
      "schedule": {
        "morningPickup": "08:00",
        "morningDrop": "08:45",
        "afternoonPickup": "15:00",
        "afternoonDrop": "15:45",
        "eveningPickup": "18:00",
        "eveningDrop": "18:45",
        "estimatedDuration": 20,
        "bufferTime": 5
      },
      
      "status": {
        "isActive": true,
        "isReached": false,
        "isSkipped": false,
        "currentStatus": "pending",
        "lastStatusUpdate": 1703123456790,
        "maintenanceRequired": false,
        "isTemporary": false
      },
      
      "tracking": {
        "estimatedArrivalTime": 1703123456790,
        "actualArrivalTime": null,
        "actualDepartureTime": null,
        "delayMinutes": 0,
        "passengersBoarding": 0,
        "passengersAlighting": 0,
        "currentPassengerCount": 30,
        "maxCapacity": 50,
        "weatherCondition": "clear",
        "trafficCondition": "normal",
        "routeDeviation": false
      },
      
      "metadata": {
        "version": "1.0",
        "lastSync": 1703123456790,
        "syncStatus": "synced",
        "offlineData": false,
        "dataSource": "driver_app",
        "validationStatus": "validated",
        "tags": ["central", "commercial", "shelter"]
      }
    },
    
    "stop_003": {
      "id": "stop_003",
      "name": "Kakinada Junction",
      "busNo": "AP31AB1234",
      "stopCode": "KKJ003",
      "description": "Bus stop at Kakinada main junction",
      "stopType": "pickup_drop",
      "landmark": "Near Kakinada Municipal Corporation",
      "createdBy": "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2",
      "createdAt": 1703123456791,
      "updatedAt": 1703123456791,
      
      "location": {
        "latitude": 16.9500,
        "longitude": 82.2500,
        "address": "Kakinada Junction, Kakinada, Andhra Pradesh",
        "accuracy": 5.0,
        "altitude": 10.0,
        "geofenceRadius": 120,
        "locationType": "junction",
        "accessibility": "easy",
        "parkingAvailable": true,
        "shelterAvailable": false
      },
      
      "schedule": {
        "morningPickup": "08:30",
        "morningDrop": "09:15",
        "afternoonPickup": "15:30",
        "afternoonDrop": "16:15",
        "eveningPickup": "18:30",
        "eveningDrop": "19:15",
        "estimatedDuration": 10,
        "bufferTime": 3
      },
      
      "status": {
        "isActive": true,
        "isReached": false,
        "isSkipped": false,
        "currentStatus": "pending",
        "lastStatusUpdate": 1703123456791,
        "maintenanceRequired": false,
        "isTemporary": false
      },
      
      "tracking": {
        "estimatedArrivalTime": 1703123456791,
        "actualArrivalTime": null,
        "actualDepartureTime": null,
        "delayMinutes": 0,
        "passengersBoarding": 0,
        "passengersAlighting": 0,
        "currentPassengerCount": 15,
        "maxCapacity": 50,
        "weatherCondition": "clear",
        "trafficCondition": "normal",
        "routeDeviation": false
      },
      
      "metadata": {
        "version": "1.0",
        "lastSync": 1703123456791,
        "syncStatus": "synced",
        "offlineData": false,
        "dataSource": "driver_app",
        "validationStatus": "validated",
        "tags": ["junction", "main_stop"]
      }
    }
  },
  
  "BUS002": {
    "stop_001": {
      "id": "stop_001",
      "name": "Hyderabad Central",
      "busNo": "BUS002",
      "stopCode": "HYC001",
      "description": "Central bus stop in Hyderabad",
      "stopType": "pickup_drop",
      "landmark": "Near Hyderabad Railway Station",
      "createdBy": "ECNFM3A0M6cb3U2rhbpFuU75vLO2",
      "createdAt": 1703123456792,
      "updatedAt": 1703123456792,
      
      "location": {
        "latitude": 17.3850,
        "longitude": 78.4867,
        "address": "Hyderabad Central, Hyderabad, Telangana",
        "accuracy": 5.0,
        "altitude": 25.0,
        "geofenceRadius": 200,
        "locationType": "commercial",
        "accessibility": "easy",
        "parkingAvailable": true,
        "shelterAvailable": true
      },
      
      "schedule": {
        "morningPickup": "07:00",
        "morningDrop": "07:45",
        "afternoonPickup": "14:00",
        "afternoonDrop": "14:45",
        "eveningPickup": "17:00",
        "eveningDrop": "17:45",
        "estimatedDuration": 25,
        "bufferTime": 8
      },
      
      "status": {
        "isActive": true,
        "isReached": false,
        "isSkipped": false,
        "currentStatus": "pending",
        "lastStatusUpdate": 1703123456792,
        "maintenanceRequired": false,
        "isTemporary": false
      },
      
      "tracking": {
        "estimatedArrivalTime": 1703123456792,
        "actualArrivalTime": null,
        "actualDepartureTime": null,
        "delayMinutes": 0,
        "passengersBoarding": 0,
        "passengersAlighting": 0,
        "currentPassengerCount": 40,
        "maxCapacity": 50,
        "weatherCondition": "clear",
        "trafficCondition": "normal",
        "routeDeviation": false
      },
      
      "metadata": {
        "version": "1.0",
        "lastSync": 1703123456792,
        "syncStatus": "synced",
        "offlineData": false,
        "dataSource": "driver_app",
        "validationStatus": "validated",
        "tags": ["central", "commercial", "shelter", "main_stop"]
      }
    }
  },
  
  "BUS003": {
    "stop_001": {
      "id": "stop_001",
      "name": "Bangalore Central",
      "busNo": "BUS003",
      "stopCode": "BLC001",
      "description": "Central bus stop in Bangalore",
      "stopType": "pickup_drop",
      "landmark": "Near Bangalore City Railway Station",
      "createdBy": "2YOyMfSSehQTu49ndMF5VRqoQ0s2",
      "createdAt": 1703123456793,
      "updatedAt": 1703123456793,
      
      "location": {
        "latitude": 12.9716,
        "longitude": 77.5946,
        "address": "Bangalore Central, Bangalore, Karnataka",
        "accuracy": 5.0,
        "altitude": 30.0,
        "geofenceRadius": 180,
        "locationType": "commercial",
        "accessibility": "easy",
        "parkingAvailable": true,
        "shelterAvailable": true
      },
      
      "schedule": {
        "morningPickup": "06:30",
        "morningDrop": "07:15",
        "afternoonPickup": "13:30",
        "afternoonDrop": "14:15",
        "eveningPickup": "16:30",
        "eveningDrop": "17:15",
        "estimatedDuration": 30,
        "bufferTime": 10
      },
      
      "status": {
        "isActive": true,
        "isReached": false,
        "isSkipped": false,
        "currentStatus": "pending",
        "lastStatusUpdate": 1703123456793,
        "maintenanceRequired": false,
        "isTemporary": false
      },
      
      "tracking": {
        "estimatedArrivalTime": 1703123456793,
        "actualArrivalTime": null,
        "actualDepartureTime": null,
        "delayMinutes": 0,
        "passengersBoarding": 0,
        "passengersAlighting": 0,
        "currentPassengerCount": 35,
        "maxCapacity": 50,
        "weatherCondition": "clear",
        "trafficCondition": "normal",
        "routeDeviation": false
      },
      
      "metadata": {
        "version": "1.0",
        "lastSync": 1703123456793,
        "syncStatus": "synced",
        "offlineData": false,
        "dataSource": "driver_app",
        "validationStatus": "validated",
        "tags": ["central", "commercial", "shelter", "main_stop"]
      }
    }
  }
};

async function createComprehensiveStops() {
  try {
    console.log("🚀 Creating comprehensive bus stops with new database structure...");
    
    for (const [busNo, stops] of Object.entries(comprehensiveStops)) {
      console.log(`\n📝 Creating stops for bus: ${busNo}`);
      
      for (const [stopId, stopData] of Object.entries(stops)) {
        await set(ref(database, `stops/${busNo}/${stopId}`), stopData);
        console.log(`✅ Created stop: ${stopData.name} (${stopData.stopCode})`);
      }
    }
    
    console.log("\n🎉 Comprehensive stops creation completed!");
    console.log("\n📊 Summary:");
    console.log(`- ${Object.keys(comprehensiveStops).length} buses with stops`);
    console.log(`- ${Object.values(comprehensiveStops).reduce((acc, stops) => acc + Object.keys(stops).length, 0)} total stops created`);
    
    console.log("\n🔍 Database Structure:");
    console.log("stops/");
    console.log("├── AP31AB1234/");
    console.log("│   ├── stop_001 (Velangi Bus Stop)");
    console.log("│   ├── stop_002 (Rajahmundry Central)");
    console.log("│   └── stop_003 (Kakinada Junction)");
    console.log("├── BUS002/");
    console.log("│   └── stop_001 (Hyderabad Central)");
    console.log("└── BUS003/");
    console.log("    └── stop_001 (Bangalore Central)");
    
    console.log("\n📱 Now you can test the app with comprehensive stop data!");
    
  } catch (error) {
    console.error("❌ Error creating comprehensive stops:", error);
  }
}

createComprehensiveStops();

