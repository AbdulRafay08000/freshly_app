package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddProduct extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView backButton, imagePicker;
    private EditText titleInput, priceInput, descriptionInput;
    private Spinner categorySpinner;
    private AppCompatButton signUpButton;
    private TextView appTitle;
    private byte[] imageByteArray = null;  // To store the selected image
    private AppDatabase appDatabase;  // Room database instance
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private List<Category> categoryList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addproduct);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        imagePicker = findViewById(R.id.imagePicker);
        titleInput = findViewById(R.id.titleInput);
        priceInput = findViewById(R.id.priceInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        signUpButton = findViewById(R.id.signUpButton);
        appTitle = findViewById(R.id.appTitle);

        // Initialize Room database and DAO
        appDatabase = AppDatabase.getInstance(this);
        productDao = appDatabase.productDao();
        categoryDao = appDatabase.categoryDao();

        // Initialize Executor for background tasks
        executorService = Executors.newSingleThreadExecutor();

        // Populate categories in Spinner (Hardcoded)
        populateCategorySpinner();

        // Retrieve the vendor ID passed via Intent
        Intent intent = getIntent();
        final int vendorId = intent.getIntExtra("VENDOR_ID", -1); // Default value -1 if not passed
        if (vendorId == -1) {
            Toast.makeText(this, "Vendor ID is missing", Toast.LENGTH_SHORT).show();
            finish();  // Exit if vendor ID is not passed
        }

        // Back Button Action
        backButton.setOnClickListener(v -> finish());

        // Image Picker Action
        imagePicker.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
        });

        // Add Item Button Action
        signUpButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String price = priceInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            // Ensure all fields are filled
            if (title.isEmpty() || price.isEmpty() || description.isEmpty() || imageByteArray == null) {
                Toast.makeText(AddProduct.this, "Please fill out all fields and pick an image", Toast.LENGTH_SHORT).show();
            } else {
                // Fetch the category ID for the selected category
                executorService.execute(() -> {
                    int categoryId = getCategoryIdFromDatabase(selectedCategory);

                    // Create the product object
                    Products product = new Products(title, description, imageByteArray, categoryId, vendorId,price);

                    // Insert the product into the database in the background
                    productDao.insert(product);

                    // Show a success message on the main thread after the insert
                    runOnUiThread(() -> {
                        Toast.makeText(AddProduct.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after saving the product
                    });
                });
            }
        });
    }

    // Populate the category spinner with predefined categories (Dry Fruit, Fruit, Vegetable)
    private void populateCategorySpinner() {
        // Hardcoded category names
        String[] predefinedCategories = {"Dry Fruit", "Fruit", "Vegetable"};

        // Insert predefined categories into the database if they don't already exist
        executorService.execute(() -> {
            for (String categoryName : predefinedCategories) {
                // Check if the category already exists
                int existingCategoryId = getCategoryIdFromDatabase(categoryName);
                if (existingCategoryId == 0) {
                    // Category doesn't exist, so insert it
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    categoryDao.insert(newCategory);
                }
            }

            // Fetch updated category list from the database
            categoryList = categoryDao.getAllCategories();

            runOnUiThread(() -> {
                // Update the spinner with the fetched categories
                String[] categoryNames = new String[categoryList.size()];
                for (int i = 0; i < categoryList.size(); i++) {
                    categoryNames[i] = categoryList.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            });
        });
    }

    // Helper function to check if category exists in database
    private int getCategoryIdFromDatabase(String categoryName) {
        // Query the database for the category with the given name
        Category category = categoryDao.getCategoryByName(categoryName);
        return category != null ? category.getCa_id() : 0;  // Return category ID or 0 if not found
    }

    // Handle the image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                // Get the image URI and convert it to a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                // Convert the Bitmap to byte array (BLOB)
                imageByteArray = convertBitmapToByteArray(bitmap);
                // Optionally set the image in ImageView for preview
                imagePicker.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("AddProduct", "Error loading image", e);
            }
        }
    }

    // Convert Bitmap to byte array
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
