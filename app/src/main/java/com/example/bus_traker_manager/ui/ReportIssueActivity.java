package com.example.bus_traker_manager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.IssueEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportIssueActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 3001;
    private EditText editTextIssue;
    private Button buttonAttachPhoto, buttonSubmitIssue;
    private ImageView imagePreview;
    private Uri imageUri;
    private DatabaseReference issuesRef;
    private StorageReference storageRef;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);
        editTextIssue = findViewById(R.id.editTextIssue);
        buttonAttachPhoto = findViewById(R.id.buttonAttachPhoto);
        buttonSubmitIssue = findViewById(R.id.buttonSubmitIssue);
        imagePreview = findViewById(R.id.imagePreview);
        issuesRef = FirebaseDatabase.getInstance().getReference("issues");
        storageRef = FirebaseStorage.getInstance().getReference("issue_photos");
        buttonAttachPhoto.setOnClickListener(v -> selectImage());
        buttonSubmitIssue.setOnClickListener(v -> submitIssue());
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bus_tracker_db").build();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void submitIssue() {
        String issueText = editTextIssue.getText().toString().trim();
        if (issueText.isEmpty()) {
            editTextIssue.setError("Describe the issue");
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";
        if (!isNetworkAvailable()) {
            cacheIssueLocally(uid, issueText, imageUri != null ? imageUri.toString() : null);
            Toast.makeText(this, "No network. Issue cached and will sync later.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Map<String, Object> issueData = new HashMap<>();
        issueData.put("uid", uid);
        issueData.put("text", issueText);
        issueData.put("timestamp", System.currentTimeMillis());
        if (imageUri != null) {
            uploadImageAndSaveIssue(issueData);
        } else {
            issuesRef.push().setValue(issueData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Issue reported", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to report issue", Toast.LENGTH_SHORT).show());
        }
    }

    private void uploadImageAndSaveIssue(Map<String, Object> issueData) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();
            StorageReference photoRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            photoRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        issueData.put("photoUrl", uri.toString());
                        issuesRef.push().setValue(issueData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Issue reported", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to report issue", Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload photo", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
        }
    }

    private void cacheIssueLocally(String uid, String text, String photoUrl) {
        new Thread(() -> {
            IssueEntity entity = new IssueEntity();
            entity.uid = uid;
            entity.text = text;
            entity.photoUrl = photoUrl;
            entity.timestamp = System.currentTimeMillis();
            db.issueDao().insert(entity);
        }).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
