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

public class VendorSignupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;

    private EditText usernameEditText, passwordEditText, phoneEditText, addressEditText;
    private ImageView imageViewProfile;
    private AppDatabase appDatabase;
    private VendorDao vendorDao;
    private Button signUpButton;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendorsignup);

        appDatabase = AppDatabase.getInstance(this);
        vendorDao = appDatabase.VendorDao();

        // Bind views
        usernameEditText = findViewById(R.id.usernameInput);
        passwordEditText = findViewById(R.id.confirmPasswordInput);
        phoneEditText = findViewById(R.id.phone);
        addressEditText = findViewById(R.id.address);
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
                openDocuments();
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
        setTitle("Sign Up as " + role);
    }

    private void openDocuments() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageViewProfile.setImageURI(selectedImageUri);
            Log.d("Signup", "Selected Image URI: " + selectedImageUri.toString());
        }
    }

    private void handleSignUp() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(VendorSignupActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] profileImage = null;
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profileImage = convertBitmapToByteArray(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Vendor vendor = new Vendor();
        vendor.setUsername(username);
        vendor.setPassword(password);
        vendor.setPhone(phone);
        vendor.setAddress(address);
        vendor.setImage(profileImage);

        new Thread(new Runnable() {
            @Override
            public void run() {
                vendorDao.insertVendor(vendor);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VendorSignupActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }).start();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

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
