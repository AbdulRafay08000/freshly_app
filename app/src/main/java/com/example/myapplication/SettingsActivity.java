package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private ImageView profileImageView;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button saveButton;

    private CustomerDao customerDao;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        profileImageView = findViewById(R.id.profileImage);
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        saveButton = findViewById(R.id.save_button);

        customerDao = AppDatabase.getInstance(this).customerDao();

        // Retrieve customer ID passed via intent
        Intent intent = getIntent();
        customerId = intent.getIntExtra("CUSTOMER_ID", -1);

        if (customerId == -1) {
            Toast.makeText(this, "Customer ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load customer details
        new Thread(() -> {
            Customer customer = customerDao.getCustomerById(customerId);
            if (customer != null) {
                runOnUiThread(() -> {
                    usernameEditText.setText(customer.getUsername());
                    emailEditText.setText(customer.getEmail());
                    passwordEditText.setText(customer.getPassword());

                    // Display image if available (as a Bitmap from the stored BLOB)
                    if (customer.getImage() != null) {
                        byte[] imageBytes = customer.getImage();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        profileImageView.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();

        // Set up image click listener to pick an image
        profileImageView.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
        });

        // Save button click listener to save updated details
        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (validateInputs(username, email, password)) {
                new Thread(() -> {
                    Customer customer = customerDao.getCustomerById(customerId);
                    if (customer != null) {
                        customer.setUsername(username);
                        customer.setEmail(email);
                        customer.setPassword(password);

                        // Save the byte array image into the database (BLOB)
                        customerDao.updateCustomerDetails(
                                customerId, username, email, password, customer.getImage());

                        runOnUiThread(() -> {
                            Toast.makeText(SettingsActivity.this, "Details updated", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                // Retrieve the image URI from the data
                Uri imageUri = data.getData();

                // Convert the image URI to a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);

                // Convert Bitmap to byte array to save in database as BLOB (binary large object)
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Update the customer details with the new image
                new Thread(() -> {
                    Customer customer = customerDao.getCustomerById(customerId);
                    if (customer != null) {
                        // Save the byte array (image) into the database (BLOB)
                        customer.setImage(imageBytes);  // Update the image as a byte array (BLOB)

                        // Save other customer details (username, email, etc.)
                        customerDao.updateCustomerDetails(
                                customerId,
                                customer.getUsername(),
                                customer.getEmail(),
                                customer.getPassword(),
                                imageBytes);  // Pass the image as argument (byte array)
                    }
                }).start();

            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs(String username, String email, String password) {
        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return false;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Enter a valid email");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }
        return true;
    }
}
