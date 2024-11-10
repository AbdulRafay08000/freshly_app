package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class VendorDashboardActivity extends AppCompatActivity {
    private Button addproductbtn;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_own); // This layout file

        Intent intent = getIntent();
        int vendorId = intent.getIntExtra("VENDOR_ID", -1); // Default value -1 if not passed

        addproductbtn = findViewById(R.id.addproduct);
        addproductbtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(VendorDashboardActivity.this, AddProduct.class);
            intent2.putExtra("VENDOR_ID", vendorId);
            startActivity(intent2);
        });

        // Setup toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Synchronize the Drawer with the Toolbar
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_home) {
                Toast.makeText(VendorDashboardActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.fruits) {
                selectedFragment = VenFruitsFragment.newInstance(vendorId);
            } else if (id == R.id.veg) {
                selectedFragment = VenVegetablesFragment.newInstance(vendorId);
            } else if (id == R.id.dryfruits) {
                selectedFragment = VenDryFruitsFragment.newInstance(vendorId);
            } else if (id == R.id.settings) {
                Intent intent3 = new Intent(VendorDashboardActivity.this, SettingVen.class);
                intent3.putExtra("VENDOR_ID", vendorId);
                startActivity(intent3);
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)  // Replace `R.id.fragment_container` with your container ID
                .commit();
    }

}

