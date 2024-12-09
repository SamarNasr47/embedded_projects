package com.example.mysmarthome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    TextView username;
    Button logoutPf, changePhoto;
    ImageView img;

    DatabaseReference userReference;
    FirebaseAuth firebaseAuth;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        InitiateView(); // Initialize views first

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Retrieve user data from Firebase
            retrieveUserData();
        }

        logoutPf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        userReference.child("profileImageUrl").setValue(imageBase64)
                .addOnSuccessListener(aVoid -> {
                    if (user != null) {
                        user.setProfileImageUrl(imageBase64);
                    }
                    img.setImageBitmap(bitmap);
                    Toast.makeText(Profile.this, "Image updated successfully", Toast.LENGTH_SHORT).show();
                    retrieveUserData(); // Refresh user data to ensure username is displayed correctly
                })
                .addOnFailureListener(e -> Toast.makeText(Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Retrieve user data from Firebase and update UI
        if (userReference != null) {
            retrieveUserData();
        }
    }

    private void retrieveUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Set username
                        username.setText(user.getUsername());

                        // Set profile image
                        if (user.getProfileImageUrl() != null) {
                            byte[] decodedString = Base64.decode(user.getProfileImageUrl(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            img.setImageBitmap(decodedByte);
                        } else {
                            // Set a default image if no profile image is available
                            img.setImageResource(R.drawable.led);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("Profile", "Failed to read user data", error.toException());
            }
        });
    }

    protected void InitiateView() {
        logoutPf = findViewById(R.id.buttonLogout);
        username = findViewById(R.id.textViewUsername);
        img = findViewById(R.id.imageView2);
        changePhoto = findViewById(R.id.buttonChangePhoto);
    }
}