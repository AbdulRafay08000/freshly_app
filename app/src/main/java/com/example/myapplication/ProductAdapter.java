package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Products> productsList;
    private final Context context;

    public ProductAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productsList.get(position);
        holder.titleTextView.setText(product.getTitle());
        holder.descTextView.setText(product.getDesc());
        holder.priceTextView.setText(product.getPrice());

        // Decode image from Base64 and set to ImageView
        if (product.getImage() != null) {
            holder.productImageView.setImageBitmap(BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length));
        } else {
            holder.productImageView.setImageResource(R.drawable.ic_launcher_foreground); // Placeholder image
        }

        holder.addToCartButton.setOnClickListener(v -> {
            addToCart(product);
            Toast.makeText(context, product.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }



    private void addToCart(Products product) {
        Log.d("AddToCart", "Adding product to cart: " + product.getTitle() + " (ID: " + product.getP_id() + ")");

        SharedPreferences sharedPreferences = context.getSharedPreferences("CartPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String productKey = String.valueOf(product.getP_id());

        if (!sharedPreferences.contains(productKey + "_title")) {
            Log.d("AddToCart", "Product does not exist in cart. Saving product...");

            if (product.getTitle() != null && !product.getTitle().isEmpty() &&
                    product.getPrice() != null && !product.getPrice().isEmpty() &&
                    product.getDesc() != null && !product.getDesc().isEmpty()) {

                // Convert image to Base64 string if it exists
                if (product.getImage() != null) {
                    String base64Image = encodeImageToBase64(product.getImage());

                    editor.putString(productKey + "_image", base64Image);  // Save image as Base64 string
                }

                editor.putString(productKey + "_title", product.getTitle());
                editor.putString(productKey + "_price", product.getPrice());
                editor.putString(productKey + "_desc", product.getDesc());
                editor.apply();  // Apply changes asynchronously

                Log.d("AddToCart", "Product Saved in SharedPreferences with key: " + productKey);
            } else {
                Log.e("AddToCart", "Product data is incomplete. Could not save product.");
            }
        } else {
            Log.d("AddToCart", "Product already exists in cart. Skipping save operation for: " + product.getTitle());
        }
    }

    // Helper method to convert image to Base64 string
    private String encodeImageToBase64(byte[] imageData) {
        return Base64.encodeToString(imageData, Base64.DEFAULT);
    }










    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, priceTextView, descTextView;
        private final ImageView productImageView;
        private final AppCompatButton addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
            descTextView = itemView.findViewById(R.id.product_description);
            productImageView = itemView.findViewById(R.id.product_image);
            addToCartButton = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }



}
