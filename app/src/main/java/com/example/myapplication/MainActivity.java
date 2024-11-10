package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Login Button Click Listener
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginOptionsDialog("Login");
            }
        });

        // Sign Up Button Click Listener
        findViewById(R.id.signupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginOptionsDialog("Sign Up");
            }
        });
    }

    // Function to show the login/signup options dialog
    private void showLoginOptionsDialog(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(action + " as:");

        String[] options = {"Vendor", "Customer"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String role = (which == 0) ? "Vendor" : "Customer";
                navigateToActivity(action, role);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog on cancel
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToActivity(String action, String role) {
        Intent intent;

        // Determine the appropriate activity based on action and role
        if (action.equals("Login")) {
            if (role.equals("Vendor")) {
                intent = new Intent(MainActivity.this, VendorLoginActivity.class);
            } else {  // Customer
                intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
            }
        } else {  // Sign Up
            if (role.equals("Vendor")) {
                intent = new Intent(MainActivity.this, VendorSignupActivity.class);
            } else {  // Customer
                intent = new Intent(MainActivity.this, CustomerSignupActivity.class);
            }
        }

        startActivity(intent);
    }
}
