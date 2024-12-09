package com.example.mysmarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {

    private User user; // Reference to the User object
    private ListView listView;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient mGoogleApiClient;


    private CustomListAdapter adapter;

    private List<String> items = Arrays.asList("Light", "Fan", "Password", "Entry Attacks","Welcome Message");
    private int[] imageResources = {
            R.drawable.led,
            R.drawable.fan,
            R.drawable.password_image,
            R.drawable.password_image,
            R.drawable.welcome
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        listView = findViewById(R.id.listView);

        adapter = new CustomListAdapter(this, items, imageResources);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                if(isNetworkAvailable()) {
                    String selectedItem = adapter.getItem(position);
                    if (selectedItem != null) {
                        switch (selectedItem) {
                            case "Light":
                                startActivity(new Intent(Home.this, Light.class));
                                break;
                            case "Fan":
                                Intent intent = new Intent(Home.this, Fan.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                                break;
                            case "Password":
                                startActivity(new Intent(Home.this, Password.class));
                                break;
                            case "Entry Attacks":
                                startActivity(new Intent(Home.this, EntryAttack.class));
                                break;
                            case "Welcome Message":
                                startActivity(new Intent(Home.this, Message.class));
                                break;
                            default:
                                break;
                        }
                    }
                }
                else{
                    Toast.makeText(Home.this, "Please Check your Network Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activityLog) {
            startActivity(new Intent(Home.this, ActivityLog.class));
            return true;
        } else if (itemId == R.id.Profile) {
            startActivity(new Intent(Home.this, Profile.class));
            return true;
        } else if (itemId == R.id.logout) {
            firebaseAuth.signOut();
            mGoogleApiClient.maybeSignOut();
            Toast.makeText(Home.this, "Sign out successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Home.this, Login.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
