package com.example.mysmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgetBassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_bassword);
        EditText bestfriend = (EditText) findViewById(R.id.Bestfriend);
        EditText username = (EditText) findViewById(R.id.user);
        Button Auth = (Button) findViewById(R.id.Auth);


        Auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friend = bestfriend.getText().toString().trim();

                if (friend.isEmpty()) {
                    bestfriend.setError("Enter your best friend name");
                    bestfriend.requestFocus();
                } else {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    DatabaseReference userNodeRef = usersRef.child(username.getText().toString());
                    userNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Retrieve the user data
                                User user = snapshot.getValue(User.class);

                                // Do something with the user object, for example, log the user's email
                                if (user != null) {
                                    String bestfr = user.getBestfriend();
                                    if (bestfr.equals(friend)) {
                                        Toast.makeText(ForgetBassword.this, "The passowrd is  " + (String) user.getPassword(), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ForgetBassword.this, Login.class));

                                    }
                                }
                            } else {
                                System.out.println("Wrong");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


    }
}