package com.example.mysmarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Login extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private CheckBox checkBoxRememberMe;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;


    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    public static String usernamm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.Password);
        checkBoxRememberMe = findViewById(R.id.checkBox);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set OnClickListener for the Google Sign-In button
        com.google.android.gms.common.SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        Button buttonLogin = findViewById(R.id.Loginbtn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (checkBoxRememberMe.isChecked()) {
                    saveCredentials(username, password);
                } else {
                    clearCredentials();
                }

                // Authenticate user using normal method or Firebase
                login(username, password);
            }
        });

        TextView ForgetPassword = findViewById(R.id.textView2);
        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();

                if (username.isEmpty()) {
                    editTextUsername.setError("Enter your email");
                    editTextUsername.requestFocus();
                } else {
                    sendPasswordEmail(username);
                }
            }
        });

        // Check and auto-fill credentials if "Remember Me" is enabled
        if (isRememberMeEnabled()) {
            autoFillCredentials();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, proceed to Home activity
            Intent intent = new Intent(Login.this, Home.class).putExtra("username", currentUser.getDisplayName());
            startActivity(intent);
            finish();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login.this, Home.class).putExtra("username", user.getDisplayName());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(String username, String password) {
        // Authenticate user using normal method or Firebase
        // For example, use Firebase Authentication

        //salah area
        Calendar calendar = Calendar.getInstance();
        // Define the format pattern
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Format the date and time
        String timestamp = sdf.format(calendar.getTime());

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userNodeRef = usersRef.child(username);

        userNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the user data
                    User user = snapshot.getValue(User.class);

                    // Do something with the user object, for example, log the user's email
                    if (user != null) {
                        String retrievedPassword = user.getPassword();
                        if(password.equals(retrievedPassword)) {
                            usernamm = username;
                            Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            Logs logger = new Logs();
                            logger.logEvent("Successful login for user : " + username, timestamp);
                        }
                    }
                } else {
                    Logs logger = new Logs();
                    logger.logEvent("UN-Successful login for user : " + username, timestamp);
                    System.out.println("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendPasswordEmail(String username) {
        startActivity(new Intent(Login.this, ForgetBassword.class));
    }

    private void saveCredentials(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private void clearCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

    private boolean isRememberMeEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.contains(KEY_USERNAME) && sharedPreferences.contains(KEY_PASSWORD);
    }

    private void sendEmail(String msg, String email) {
        String mEmail = email;
        String mSubject = "You forgot your password";
        String mMessage = "Your password is " + msg;

        JavaMailAPI javaMailAPI = new JavaMailAPI();

        javaMailAPI.sendmsg(mEmail, mSubject, mMessage);
    }

    private void autoFillCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        String password = sharedPreferences.getString(KEY_PASSWORD, "");

        editTextUsername.setText(username);
        editTextPassword.setText(password);
        checkBoxRememberMe.setChecked(true);
    }

    public void gotoregister(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}