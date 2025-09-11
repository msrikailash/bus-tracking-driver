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

// Test credentials
const testUsers = [
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

async function testUserLogin() {
  console.log("🔍 Testing Firebase Authentication login...");
  
  for (const user of testUsers) {
    try {
      console.log(`\n🔄 Testing login for: ${user.email}`);
      const userCredential = await signInWithEmailAndPassword(
        auth, 
        user.email, 
        user.password
      );
      console.log(`✅ SUCCESS: ${user.name} logged in successfully!`);
      console.log(`   User ID: ${userCredential.user.uid}`);
      
      // Sign out after successful test
      await auth.signOut();
      console.log(`   ✅ Signed out successfully`);
      
    } catch (error) {
      console.error(`❌ FAILED: ${user.email} - ${error.message}`);
      console.error(`   Error code: ${error.code}`);
      
      if (error.code === 'auth/user-not-found') {
        console.log(`   💡 User doesn't exist - needs to be created`);
      } else if (error.code === 'auth/wrong-password') {
        console.log(`   💡 Wrong password - user exists but password is incorrect`);
      } else if (error.code === 'auth/invalid-credential') {
        console.log(`   💡 Invalid credentials - user may not exist`);
      }
    }
  }
  
  console.log("\n🎯 Login test completed!");
}

testUserLogin();





