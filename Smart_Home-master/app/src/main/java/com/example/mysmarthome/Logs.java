package com.example.mysmarthome;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Logs {





    private DatabaseReference logsRef;

    public Logs() {
        logsRef = FirebaseDatabase.getInstance().getReference().child("logs");
    }

    public void logEvent(String eventName, String eventDetails) {
        String key = logsRef.push().getKey(); // Get unique key for the log
        LogEntry logEntry = new LogEntry(eventName, eventDetails);
        logsRef.child(key).setValue(logEntry); // Set the log entry in the logs table
    }

    private static class LogEntry {
        private String eventName;
        private String eventDetails;

        public LogEntry() {
            // Default constructor required for calls to DataSnapshot.getValue(LogEntry.class)
        }

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
