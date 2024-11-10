package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.pm.PackageManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomerSignupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Spinner genderSpinner;
    private ImageView imageViewProfile;
    private AppDatabase appDatabase;
    private CustomerDao customerDao;
    private Button signUpButton;
    private Uri selectedImageUri;  // Store the URI of the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuppage);

        appDatabase = AppDatabase.getInstance(this);
        customerDao = appDatabase.customerDao();

        // Bind views
        usernameEditText = findViewById(R.id.usernameInput);
        emailEditText = findViewById(R.id.emailInput);
        passwordEditText = findViewById(R.id.confirmPasswordInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        imageViewProfile = findViewById(R.id.profileImage);
        signUpButton = findViewById(R.id.signUpButton);

        // Request permissions if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }

        // Set onClickListener for ImageView to pick image
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocuments();  // Open the Documents folder
            }
        });

        // Set onClickListener for Sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // Retrieve the role from the Intent and set the title
        String role = getIntent().getStringExtra("ROLE");
        setTitle("Sign Up as " + role);  // Set title based on role
    }

    // Method to open Documents folder to select an image
    private void openDocuments() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");  // Only show images
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();  // Get the URI of the selected image
            imageViewProfile.setImageURI(selectedImageUri);  // Display the selected image in ImageView
            Log.d("Signup", "Selected Image URI: " + selectedImageUri.toString());
        }
    }

    // Handle the sign-up logic
    private void handleSignUp() {
        // Capture the input values
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString().trim();

        // Simple validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(CustomerSignupActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert selected image URI to byte[] (BLOB)
        byte[] profileImage = null;
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profileImage = convertBitmapToByteArray(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create Customer object
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setGender(gender);
        customer.setImage(profileImage);  // Store the BLOB data

        // Insert the customer into the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                customerDao.insertCustomer(customer);

                // Show a success message (run on the UI thread)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerSignupActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the signup activity and return to the previous screen
                    }
                });
            }
        }).start();
    }

    // Convert Bitmap to byte[] (BLOB)
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
