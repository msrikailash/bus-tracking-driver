const { initializeApp } = require('firebase/app');
const { getDatabase, ref, set, remove } = require('firebase/database');

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

// Correct driver data with actual Firebase Auth UIDs
const correctDrivers = {
  "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2": {  // John Smith's actual UID
    driverId: "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2",
    name: "John Smith",
    busNo: "BUS001",
    lat: 12.9716,
    lng: 77.5946,
    status: "online",
    email: "john.smith@example.com",
    phone: "+91-9876543210"
  },
  "ECNFM3A0M6cb3U2rhbpFuU75vLO2": {  // Sarah Johnson's actual UID
    driverId: "ECNFM3A0M6cb3U2rhbpFuU75vLO2",
    name: "Sarah Johnson",
    busNo: "BUS002",
    lat: 12.9789,
    lng: 77.5917,
    status: "offline",
    email: "sarah.johnson@example.com",
    phone: "+91-9876543211"
  },
  "2YOyMfSSehQTu49ndMF5VRqoQ0s2": {  // Michael Brown's actual UID
    driverId: "2YOyMfSSehQTu49ndMF5VRqoQ0s2",
    name: "Michael Brown",
    busNo: "BUS003",
    lat: 12.9755,
    lng: 77.5995,
    status: "online",
    email: "michael.brown@example.com",
    phone: "+91-9876543212"
  }
};

// Correct trips data with actual driver UIDs
const correctTrips = {
  "trip001": {
    tripId: "trip001",
    busId: "BUS001",
    driverUid: "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2",  // John Smith's UID
    startAt: new Date("2024-12-15T08:00:00Z").getTime(),
    endAt: new Date("2024-12-15T10:30:00Z").getTime(),
    status: "completed",
    totalDistance: 45.2,
    totalStops: 3,
    routeName: "Central Route"
  },
  "trip002": {
    tripId: "trip002",
    busId: "BUS002",
    driverUid: "ECNFM3A0M6cb3U2rhbpFuU75vLO2",  // Sarah Johnson's UID
    startAt: new Date("2024-12-15T09:00:00Z").getTime(),
    endAt: null,
    status: "active",
    totalDistance: 12.5,
    totalStops: 2,
    routeName: "Airport Route"
  },
  "trip003": {
    tripId: "trip003",
    busId: "BUS003",
    driverUid: "2YOyMfSSehQTu49ndMF5VRqoQ0s2",  // Michael Brown's UID
    startAt: new Date("2024-12-14T14:00:00Z").getTime(),
    endAt: new Date("2024-12-14T16:15:00Z").getTime(),
    status: "completed",
    totalDistance: 38.7,
    totalStops: 4,
    routeName: "City Route"
  }
};

// Correct leaves data with actual driver UIDs
const correctLeaves = {
  "leave001": {
    leaveId: "leave001",
    driverId: "NmR50GgqZmWf4RQ2kaPKmEQyb5Y2",  // John Smith's UID
    reason: "Personal emergency",
    status: "pending",
    requestDate: new Date("2024-12-10T10:00:00Z").getTime(),
    leaveDate: new Date("2024-12-20T00:00:00Z").getTime(),
    driverName: "John Smith",
    busNo: "BUS001"
  },
  "leave002": {
    leaveId: "leave002",
    driverId: "ECNFM3A0M6cb3U2rhbpFuU75vLO2",  // Sarah Johnson's UID
    reason: "Medical appointment",
    status: "approved",
    requestDate: new Date("2024-12-08T14:30:00Z").getTime(),
    leaveDate: new Date("2024-12-18T00:00:00Z").getTime(),
    driverName: "Sarah Johnson",
    busNo: "BUS002"
  }
};

async function fixDriverData() {
  try {
    console.log("🔧 Fixing driver data with correct Firebase Auth UIDs...");
    
    // First, remove old incorrect data
    console.log("🗑️  Removing old incorrect driver data...");
    await remove(ref(database, 'drivers'));
    await remove(ref(database, 'trips'));
    await remove(ref(database, 'leaves'));
    
    // Create correct drivers data
    console.log("📝 Creating correct driver data...");
    for (const [uid, driverData] of Object.entries(correctDrivers)) {
      await set(ref(database, `drivers/${uid}`), driverData);
      console.log(`✅ Created driver: ${driverData.name} with UID: ${uid}`);
    }
    
    // Create correct trips data
    console.log("🚌 Creating correct trips data...");
    for (const [tripId, tripData] of Object.entries(correctTrips)) {
      await set(ref(database, `trips/${tripId}`), tripData);
      console.log(`✅ Created trip: ${tripId} - ${tripData.routeName}`);
    }
    
    // Create correct leaves data
    console.log("📅 Creating correct leave requests...");
    for (const [leaveId, leaveData] of Object.entries(correctLeaves)) {
      await set(ref(database, `leaves/${leaveId}`), leaveData);
      console.log(`✅ Created leave request: ${leaveData.driverName}`);
    }
    
    console.log("\n🎉 Driver data fixed successfully!");
    console.log("\n📊 Summary:");
    console.log(`- ${Object.keys(correctDrivers).length} drivers with correct UIDs`);
    console.log(`- ${Object.keys(correctTrips).length} trips linked to correct drivers`);
    console.log(`- ${Object.keys(correctLeaves).length} leave requests linked to correct drivers`);
    
    console.log("\n🔑 Now you can login with:");
    console.log("Email: john.smith@example.com | Password: password123");
    console.log("Email: sarah.johnson@example.com | Password: password123");
    console.log("Email: michael.brown@example.com | Password: password123");
    
  } catch (error) {
    console.error("❌ Error fixing driver data:", error);
  }
}

fixDriverData();





