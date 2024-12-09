package com.example.mysmarthome;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Calendar selectedDate;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedDate = Calendar.getInstance();

        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonSelectBirthdate = findViewById(R.id.buttonSelectBirthdate);
        Button buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);

        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input
                EditText editTextName = findViewById(R.id.textPersonName);
                EditText editTextUsername = findViewById(R.id.editTextUsername);
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
                EditText editTextBestFriend = findViewById(R.id.BestFriend);

                String name = editTextName.getText().toString();
                String username = editTextUsername.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String birthdate = buttonSelectBirthdate.getText().toString();
                String bestFriend = editTextBestFriend.getText().toString();

                // Validate user input (e.g., check password match)
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user object
                User user = new User();
                user.setName(name);
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setBirthdate(birthdate);
                user.setBestfriend(bestFriend);

                // Save user data to Firebase Realtime Database
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                if (imageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        user.setProfileImageUrl(imageBase64);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Failed to process image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                usersRef.child(username).setValue(user);

                // Save user data to SQLite database (local cache)
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                dbHelper.insertUser(user);

                // Proceed to Home activity
                Intent intent = new Intent(MainActivity.this, Home.class).putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        buttonSelectBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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
            imageUri = data.getData();
            Button buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
            buttonSelectPhoto.setText("Photo Selected");
        }
    }

    public void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        Button button = findViewById(R.id.buttonSelectBirthdate);
                        button.setText(formattedDate);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    public void gotologin(View v) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}