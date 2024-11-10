package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<ProductCart> cartItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart); // Ensure activity_cart contains RecyclerView

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load cart items from SharedPreferences
        loadCartItems();

        // Set up the RecyclerView with the adapter
        cartAdapter = new CartAdapter(this, cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE);
        cartItemList = new ArrayList<>();

        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.endsWith("_title")) {
                String productKey = key.substring(0, key.lastIndexOf("_")); // Extract the product ID part of the key

                String productTitle = sharedPreferences.getString(productKey + "_title", "No title available");
                String productPrice = sharedPreferences.getString(productKey + "_price", "No price available");
                String description = sharedPreferences.getString(productKey + "_desc", "No description available");
                String base64Image = sharedPreferences.getString(productKey + "_image", null);  // Retrieve Base64 image string

                // Decode the Base64 string into a Bitmap
                Bitmap productImage = null;
                if (base64Image != null) {
                    productImage = decodeBase64ToImage(base64Image);
                    Log.d("ImageStorage", "Image retrieved from SharedPreferences");
                }
                Log.d("ImageStorage", "image not here");

                // Create a ProductCart object with image and add to the list
                ProductCart productCart = new ProductCart(productTitle, productPrice, description, productImage);
                cartItemList.add(productCart);
            }
        }
    }

    // Helper method to decode Base64 string back to Bitmap
    private Bitmap decodeBase64ToImage(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}


