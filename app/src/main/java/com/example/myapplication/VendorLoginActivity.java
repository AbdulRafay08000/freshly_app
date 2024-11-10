package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VendorLoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private TextView loginButton, signUpText;
    private VendorDao vendorDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendorlogin); // Use the provided XML layout

        // Initialize the database and DAO
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        vendorDao = appDatabase.VendorDao();

        // Bind views
        usernameEditText = findViewById(R.id.usernameInput);
        passwordEditText = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);

        // Set up the login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Redirect to the sign-up page
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorLoginActivity.this, VendorSignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLogin() {
        // Capture input values
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check the database in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                Vendor vendor = vendorDao.login(username, password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (vendor != null) {
                            // Login successful
                            Toast.makeText(VendorLoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VendorLoginActivity.this, VendorDashboardActivity.class);
                            intent.putExtra("VENDOR_ID", vendor.getVId());
                            startActivity(intent);
                            finish(); // Close the login screen
                        } else {
                            // Login failed
                            Toast.makeText(VendorLoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}
