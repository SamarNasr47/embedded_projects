package com.example.mysmarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Password extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);


        // Initialize Firebase reference
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize SQLite database helper
        DBHelper dbHelper = new DBHelper(this);

        EditText passwordEditText = findViewById(R.id.editTextPassword);
        EditText confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        Button updatePasswordButton = findViewById(R.id.buttonSavePassword);

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (!password.isEmpty() && password.equals(confirmPassword)) {
                    // Update password in Firebase
                    firebaseReference.child("password").setValue(password);

                    // Update password in SQLite database
                    dbHelper.updateDoorPassword(password);

                    Toast.makeText(Password.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Password.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}