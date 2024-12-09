package com.example.mysmarthome;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Fan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);

        // Firebase initialization
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("fan");

        // SQLite initialization
        DBHelper dbHelper = new DBHelper(this);

        Button fanOn = findViewById(R.id.buttonToggleFan);
        Button fanOff = findViewById(R.id.buttonToggleFan2);

        Calendar calendar = Calendar.getInstance();
        // Define the format pattern
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Format the date and time
        String timestamp = sdf.format(calendar.getTime());

        fanOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseReference.setValue(true);
                dbHelper.updateFan(true);
                Logs logger = new Logs();
                logger.logEvent("User turned fan ON at ", timestamp);
            }
        });

        fanOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseReference.setValue(false);
                dbHelper.updateFan(false);
                Logs logger = new Logs();
                logger.logEvent("User turned fan OFF at ", timestamp);
            }
        });
    }
}