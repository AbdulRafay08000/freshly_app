package com.example.myapplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText titleEditText, descEditText, priceEditText;  // Added priceEditText
    private ImageView productImageView;
    private Button saveButton;
    private int productId, categoryId, vendorId;
    private AppDatabase db;
    private ProductDao productDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        // Initialize views
        titleEditText = findViewById(R.id.edit_product_title);
        descEditText = findViewById(R.id.edit_product_desc);
        priceEditText = findViewById(R.id.edit_product_price);  // Initialize price EditText
        productImageView = findViewById(R.id.edit_product_image);
        saveButton = findViewById(R.id.save_button);

        // Get product details passed from previous activity
        Intent intent = getIntent();
        productId = intent.getIntExtra("product_id", -1);
        String productTitle = intent.getStringExtra("product_title");
        String productDesc = intent.getStringExtra("product_desc");
        byte[] productImage = intent.getByteArrayExtra("product_image");
        categoryId = intent.getIntExtra("category_id", -1);  // Retrieve category_id
        vendorId = intent.getIntExtra("vendor_id", -1);      // Retrieve vendor_id
        String productPrice = intent.getStringExtra("product_price");  // Retrieve price

        // Populate fields with product details
        titleEditText.setText(productTitle);
        descEditText.setText(productDesc);
        priceEditText.setText(productPrice);  // Set the price
        if (productImage != null) {
            productImageView.setImageBitmap(BitmapFactory.decodeByteArray(productImage, 0, productImage.length));
        }

        // Initialize the database and DAO
        db = AppDatabase.getInstance(this);
        productDao = db.productDao();

        // Set up save button to update the product
        saveButton.setOnClickListener(v -> {
            String updatedTitle = titleEditText.getText().toString();
            String updatedDesc = descEditText.getText().toString();
            String updatedPrice = priceEditText.getText().toString();  // Get the price from the input

            if (updatedTitle.isEmpty() || updatedDesc.isEmpty() || updatedPrice.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Products updatedProduct = new Products(updatedTitle, updatedDesc, productImage, categoryId, vendorId, updatedPrice);
            updatedProduct.setP_id(productId);  // Set the product ID for updating

            new Thread(() -> {
                productDao.update(updatedProduct);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_product", (CharSequence) updatedProduct);  // Pass the updated product
                    setResult(RESULT_OK, resultIntent);  // Set the result as OK
                    finish();
                });
            }).start();
        });
    }
}
