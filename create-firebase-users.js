const { initializeApp } = require('firebase/app');
const { getAuth, createUserWithEmailAndPassword } = require('firebase/auth');

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

// Sample users to create
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

async function createUsers() {
  console.log("🔐 Creating Firebase Authentication users...");
  
  for (const user of users) {
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth, 
        user.email, 
        user.password
      );
      console.log(`✅ Created user: ${user.name} (${user.email})`);
    } catch (error) {
      if (error.code === 'auth/email-already-in-use') {
        console.log(`⚠️  User already exists: ${user.email}`);
      } else {
        console.error(`❌ Error creating ${user.email}:`, error.message);
      }
    }
  }
  
  console.log("\n🎉 User creation completed!");
  console.log("\n📋 Login Credentials:");
  console.log("Email: john.smith@example.com | Password: password123");
  console.log("Email: sarah.johnson@example.com | Password: password123");
  console.log("Email: michael.brown@example.com | Password: password123");
}

createUsers();







