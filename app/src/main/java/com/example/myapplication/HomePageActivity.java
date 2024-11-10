package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class HomePageActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private CustomerDao customerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // Initialize Room database
        customerDao = AppDatabase.getInstance(getApplicationContext()).customerDao();

        Intent intent = getIntent();
        int customerId = intent.getIntExtra("CUSTOMER_ID", -1); // Default value -1 if not passed

        if (customerId != -1) {
            Log.d("HomePageActivity", "Customer ID received: " + customerId);
            fetchCustomerDetails(customerId);
        } else {
            Log.d("HomePageActivity", "No Customer ID passed!");
            Toast.makeText(this, "Failed to retrieve Customer ID", Toast.LENGTH_SHORT).show();
        }

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Synchronize the Drawer with the Toolbar
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Find the logout button
        Button logoutButton = findViewById(R.id.logout_button);

        // Set up click listener for the logout button
        logoutButton.setOnClickListener(view -> {
            Intent intent2 = new Intent(HomePageActivity.this, CustomerLoginActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent2);
            Toast.makeText(HomePageActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
        Button goToCartButton = findViewById(R.id.viewCart);
        goToCartButton.setOnClickListener(view -> {
            Intent intent_cart = new Intent(HomePageActivity.this, CartActivity.class);
            startActivity(intent_cart);
        });

        // Handle Navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(HomePageActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.fruits) {
                loadFragment(new FruitsFragment());
            } else if (id == R.id.veg) {

                Toast.makeText(HomePageActivity.this, "Vegetables Selected", Toast.LENGTH_SHORT).show();
                loadFragment(new VegetablesFragment());
            } else if (id == R.id.dryfruits) {
                Toast.makeText(HomePageActivity.this, "Dry Fruits Selected", Toast.LENGTH_SHORT).show();
                loadFragment(new DryFruitsFragment());
            } else if (id == R.id.settings) {
                Intent settingsIntent = new Intent(HomePageActivity.this, SettingsActivity.class);
                settingsIntent.putExtra("CUSTOMER_ID", customerId);
                startActivity(settingsIntent);
            } else {
                Toast.makeText(HomePageActivity.this, "Unknown option selected", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void fetchCustomerDetails(int customerId) {
        new Thread(() -> {
            Customer customer = customerDao.getCustomerById(customerId);

            if (customer != null) {
                runOnUiThread(() -> updateNavigationHeader(customer));
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(HomePageActivity.this, "Customer not found!", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)  // Replace `R.id.fragment_container` with your container ID
                .commit();
    }

    private void updateNavigationHeader(Customer customer) {
        View headerView = navigationView.getHeaderView(0);

        ImageView profileImageView = headerView.findViewById(R.id.profile_image);
        TextView usernameTextView = headerView.findViewById(R.id.user_name);
        TextView emailTextView = headerView.findViewById(R.id.user_email);

        usernameTextView.setText(customer.getUsername());
        emailTextView.setText(customer.getEmail());

        // Check permissions for image loading
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            loadImage(profileImageView, customer.getImage());  // Pass the image Blob (as byte array)
        }
    }

    private void loadImage(ImageView profileImageView, byte[] imageBlob) {
        if (imageBlob != null) {
            try {
                // Convert the byte array (Blob) into Bitmap
                InputStream inputStream = new ByteArrayInputStream(imageBlob);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Use Glide to load the Bitmap into the ImageView
                Glide.with(this)
                        .load(bitmap)
                        .placeholder(R.drawable.profimg)  // Placeholder image
                        .error(R.drawable.profimg)        // Fallback image
                        .into(profileImageView);

            } catch (Exception e) {
                Log.e("HomePageActivity", "Error loading image", e);
                profileImageView.setImageResource(R.drawable.profimg); // Default image on failure
            }
        } else {
            profileImageView.setImageResource(R.drawable.profimg); // Default image if no image available
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied! Cannot load profile image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
