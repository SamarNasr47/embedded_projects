package com.example.mysmarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class ActivityLog extends AppCompatActivity {



    private ListView logListView;
    private DatabaseReference logsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logListView = findViewById(R.id.logListView);

        // Get reference to 'logs' table in Firebase Realtime Database
        logsRef = FirebaseDatabase.getInstance().getReference().child("logs");

        // Retrieve data from Firebase and populate ListView
        logsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> logEntries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LogEntry logEntry = snapshot.getValue(LogEntry.class);
                    if (logEntry != null) {
                        String entry = logEntry.getEventName() + ": " + logEntry.getEventDetails();
                        logEntries.add(entry);
                    }
                }

                // Create ArrayAdapter to populate ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ActivityLog.this,
                        android.R.layout.simple_list_item_1, logEntries);

                // Set the adapter to the ListView
                logListView.setAdapter(adapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Define LogEntry class
    public static class LogEntry {
        private String eventName;
        private String eventDetails;

        public LogEntry() {}

        public LogEntry(String eventName, String eventDetails) {
            this.eventName = eventName;
            this.eventDetails = eventDetails;
        }

        public String getEventName() {
            return eventName;
        }

        public String getEventDetails() {
            return eventDetails;
        }
    }
}