package com.example.mysmarthome;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Light extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        // Firebase init
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("light");

        // SQLite initialization
        DBHelper dbHelper = new DBHelper(this);

        Button lightOn = findViewById(R.id.buttonToggleLight);
        Button lightOff = findViewById(R.id.buttonToggleLight2);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(calendar.getTime());

        lightOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseReference.setValue(true);
                dbHelper.updateLight(true);
                Logs logger = new Logs();
                logger.logEvent("Light has turned ON at ", timestamp);
            }
        });

        lightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseReference.setValue(false);
                dbHelper.updateLight(false);
                Logs logger = new Logs();
                logger.logEvent("Light has turned OFF at ", timestamp);
            }
        });
    }
}