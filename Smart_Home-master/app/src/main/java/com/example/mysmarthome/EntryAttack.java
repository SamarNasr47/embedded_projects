package com.example.mysmarthome;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntryAttack extends AppCompatActivity {
    private DatabaseReference db;

    private TextView textViewMessage;
    private TextView textViewAlertStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entery_attack);

        textViewMessage = findViewById(R.id.textViewMessage);
        textViewAlertStatus = findViewById(R.id.textViewAlertStatus);

        // Initialize Firebase reference
        db = FirebaseDatabase.getInstance().getReference();

        // Set up listeners for the "attack" and "alert" nodes
        db.child("attack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean attack = dataSnapshot.getValue(Boolean.class);
                if (attack != null) {
                    if (attack) {
                        textViewAlertStatus.setText("Status: We are in Danger");
                    } else {
                        textViewAlertStatus.setText("Status: We are safe");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EntryAttack.this, "Failed to load attack status.", Toast.LENGTH_SHORT).show();
            }
        });

        db.child("alert").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean alert = dataSnapshot.getValue(Boolean.class);
                if (alert != null) {
                    if (alert) {
                        textViewMessage.setText("Person Detected");

                    } else {
                        textViewMessage.setText("No Person Detected");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EntryAttack.this, "Failed to load alert status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}