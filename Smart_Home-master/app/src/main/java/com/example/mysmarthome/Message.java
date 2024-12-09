package com.example.mysmarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();

        DBHelper dbHelper = new DBHelper(this);

        EditText messageEditText = findViewById(R.id.editTextMessage);
        Button updateMessageButton = findViewById(R.id.buttonSaveMessage);
        updateMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Message = messageEditText.getText().toString().trim();

                if (!Message.isEmpty()) {
                    firebaseReference.child("Message").setValue(Message);

                    dbHelper.updateDoorPassword(Message);

                    Toast.makeText(Message.this, "Message updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Message.this, "Please Enter the Message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}