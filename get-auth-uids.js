const { initializeApp } = require('firebase/app');
const { getAuth, signInWithEmailAndPassword } = require('firebase/auth');

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
const auth = getAuth(app);

// Test users
const users = [
  {
    email: "john.smith@example.com",
    password: "password123",
    name: "John Smith"
  },
  {
    email: "sarah.johnson@example.com", 
    password: "password123",
    name: "Sarah Johnson"
  },
  {
    email: "michael.brown@example.com",
    password: "password123", 
    name: "Michael Brown"
  }
];

async function getAuthUIDs() {
  console.log("🔍 Getting Firebase Auth UIDs...");
  
  const userUIDs = {};
  
  for (const user of users) {
    try {
      console.log(`\n🔄 Getting UID for: ${user.email}`);
      const userCredential = await signInWithEmailAndPassword(
        auth, 
        user.email, 
        user.password
      );
      
      const uid = userCredential.user.uid;
      userUIDs[user.email] = uid;
      
      console.log(`✅ ${user.name}: ${uid}`);
      
      // Sign out after getting UID
      await auth.signOut();
      
    } catch (error) {
      console.error(`❌ Error getting UID for ${user.email}:`, error.message);
    }
  }
  
  console.log("\n📋 User UIDs:");
  console.log(JSON.stringify(userUIDs, null, 2));
  
  return userUIDs;
}

getAuthUIDs();





