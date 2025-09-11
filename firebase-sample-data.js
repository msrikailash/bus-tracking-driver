const { initializeApp } = require('firebase/app');
const { getDatabase, ref, set, push } = require('firebase/database');

// Firebase configuration from your google-services.json
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

// Sample driver data
const sampleDrivers = {
  "driver001": {
    driverId: "driver001",
    name: "John Smith",
    busNo: "BUS001",
    lat: 12.9716,
    lng: 77.5946,
    status: "online",
    email: "john.smith@example.com",
    phone: "+91-9876543210"
  },
  "driver002": {
    driverId: "driver002", 
    name: "Sarah Johnson",
    busNo: "BUS002",
    lat: 12.9789,
    lng: 77.5917,
    status: "offline",
    email: "sarah.johnson@example.com",
    phone: "+91-9876543211"
  },
  "driver003": {
    driverId: "driver003",
    name: "Michael Brown",
    busNo: "BUS003", 
    lat: 12.9755,
    lng: 77.5995,
    status: "online",
    email: "michael.brown@example.com",
    phone: "+91-9876543212"
  }
};

// Sample stops data
const sampleStops = {
  "BUS001": {
    "stop001": {
      stopId: "stop001",
      stopName: "Central Station",
      stopLat: 12.9716,
      stopLng: 77.5946,
      busNo: "BUS001"
    },
    "stop002": {
      stopId: "stop002", 
      stopName: "University Campus",
      stopLat: 12.9789,
      stopLng: 77.5917,
      busNo: "BUS001"
    },
    "stop003": {
      stopId: "stop003",
      stopName: "Shopping Mall",
      stopLat: 12.9755,
      stopLng: 77.5995,
      busNo: "BUS001"
    }
  },
  "BUS002": {
    "stop004": {
      stopId: "stop004",
      stopName: "Airport Terminal",
      stopLat: 13.1986,
      stopLng: 77.7066,
      busNo: "BUS002"
    },
    "stop005": {
      stopId: "stop005",
      stopName: "Tech Park",
      stopLat: 12.9789,
      stopLng: 77.5917,
      busNo: "BUS002"
    }
  }
};

// Sample trips data
const sampleTrips = {
  "trip001": {
    tripId: "trip001",
    busId: "BUS001",
    driverUid: "driver001",
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
    driverUid: "driver002",
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
    driverUid: "driver003", 
    startAt: new Date("2024-12-14T14:00:00Z").getTime(),
    endAt: new Date("2024-12-14T16:15:00Z").getTime(),
    status: "completed",
    totalDistance: 38.7,
    totalStops: 4,
    routeName: "City Route"
  }
};

// Sample leaves data
const sampleLeaves = {
  "leave001": {
    leaveId: "leave001",
    driverId: "driver001",
    reason: "Personal emergency",
    status: "pending",
    requestDate: new Date("2024-12-10T10:00:00Z").getTime(),
    leaveDate: new Date("2024-12-20T00:00:00Z").getTime(),
    driverName: "John Smith",
    busNo: "BUS001"
  },
  "leave002": {
    leaveId: "leave002",
    driverId: "driver002",
    reason: "Medical appointment",
    status: "approved",
    requestDate: new Date("2024-12-08T14:30:00Z").getTime(),
    leaveDate: new Date("2024-12-18T00:00:00Z").getTime(),
    driverName: "Sarah Johnson",
    busNo: "BUS002"
  }
};

async function createSampleData() {
  try {
    console.log("🚀 Starting to create sample Firebase data...");
    
    // Create drivers
    console.log("📝 Creating sample drivers...");
    for (const [driverId, driverData] of Object.entries(sampleDrivers)) {
      await set(ref(database, `drivers/${driverId}`), driverData);
      console.log(`✅ Created driver: ${driverData.name}`);
    }
    
    // Create stops
    console.log("📍 Creating sample stops...");
    for (const [busNo, stops] of Object.entries(sampleStops)) {
      for (const [stopId, stopData] of Object.entries(stops)) {
        await set(ref(database, `stops/${busNo}/${stopId}`), stopData);
        console.log(`✅ Created stop: ${stopData.stopName} for bus ${busNo}`);
      }
    }
    
    // Create trips
    console.log("🚌 Creating sample trips...");
    for (const [tripId, tripData] of Object.entries(sampleTrips)) {
      await set(ref(database, `trips/${tripId}`), tripData);
      console.log(`✅ Created trip: ${tripId} - ${tripData.routeName}`);
    }
    
    // Create leaves
    console.log("📅 Creating sample leave requests...");
    for (const [leaveId, leaveData] of Object.entries(sampleLeaves)) {
      await set(ref(database, `leaves/${leaveId}`), leaveData);
      console.log(`✅ Created leave request: ${leaveData.driverName}`);
    }
    
    console.log("🎉 Sample data created successfully!");
    console.log("\n📊 Summary:");
    console.log(`- ${Object.keys(sampleDrivers).length} drivers`);
    console.log(`- ${Object.values(sampleStops).flatMap(stops => Object.keys(stops)).length} stops`);
    console.log(`- ${Object.keys(sampleTrips).length} trips`);
    console.log(`- ${Object.keys(sampleLeaves).length} leave requests`);
    
  } catch (error) {
    console.error("❌ Error creating sample data:", error);
  }
}

// Run the script
createSampleData();
