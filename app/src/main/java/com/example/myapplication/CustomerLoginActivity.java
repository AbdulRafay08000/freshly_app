package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerLoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private CustomerDao customerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);

        // Initialize the database and DAO
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        customerDao = appDatabase.customerDao();

        // Bind views
        usernameEditText = findViewById(R.id.usernameInput);
        passwordEditText = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Set title based on role
        String role = getIntent().getStringExtra("ROLE");
        setTitle("Login as " + role);

        // Set up the login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        // Capture the input values
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(CustomerLoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check the database in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                Customer customer = customerDao.getCustomerByCredentials(username, password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (customer != null) {
                            Log.d("CustomerLogin", "Customer fetched successfully: " + customer.getUsername());
                            // Login successful, navigate to home page
                            Intent intent = new Intent(CustomerLoginActivity.this, HomePageActivity.class);
                            intent.putExtra("CUSTOMER_ID", customer.getCId());
                            startActivity(intent);
                            finish();  // Close the login activity
                        } else {
                            Log.d("CustomerLogin", "No customer found with the provided credentials.");

                            // Login failed, show a toast message
                            Toast.makeText(CustomerLoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}
