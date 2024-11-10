package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter2 extends RecyclerView.Adapter<ProductAdapter2.ProductViewHolder> {

    private final List<Products> productsList;
    private final Context context;
    private final ProductDao productDao;

    // Constructor to receive the product list, context, and ProductDao
    public ProductAdapter2(Context context, List<Products> productsList, ProductDao productDao) {
        this.context = context;
        this.productsList = productsList;
        this.productDao = productDao;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_layout2 layout to create views for each product item
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout2, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productsList.get(position);

        // Set the product details into the views
        holder.titleTextView.setText(product.getTitle());
        holder.descTextView.setText(product.getDesc());

        // Check if the product has an image and set it, otherwise set a placeholder
        if (product.getImage() != null) {
            holder.productImageView.setImageBitmap(BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length));
        } else {
            holder.productImageView.setImageResource(R.drawable.ic_launcher_foreground); // Placeholder image
        }

        // Set up the delete button functionality
        holder.deleteButton.setOnClickListener(v -> {
            // Remove the product from the list
            productsList.remove(position);
            notifyItemRemoved(position);

            // Delete the product from the database asynchronously
            deleteProductFromDatabase(product);
        });

        // Set up the update button functionality
        holder.updateButton.setOnClickListener(v -> {
            // Pass product details to the UpdateProductActivity to allow the user to update it
            Intent intent = new Intent(context, UpdateProductActivity.class);
            intent.putExtra("product_id", product.getP_id());
            intent.putExtra("product_title", product.getTitle());
            intent.putExtra("product_desc", product.getDesc());
            intent.putExtra("product_image", product.getImage());
            intent.putExtra("category_id", product.getCategory_id());
            intent.putExtra("product_price", product.getPrice());

            intent.putExtra("vendor_id", product.getVendor_id());

            // Start the UpdateProductActivity for result
            ((Activity) context).startActivityForResult(intent, 1001); // 1001 is the request code for update
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    // Method to remove product from the database in the background thread
    private void deleteProductFromDatabase(Products product) {
        new Thread(() -> {
            productDao.delete(product);
        }).start();
    }

    // Method to update the product in the list and notify adapter to refresh the UI
    public void updateProductInList(Products updatedProduct) {
        int position = findProductPositionById(updatedProduct.getP_id());
        if (position != -1) {
            productsList.set(position, updatedProduct); // Update the list with the updated product
            notifyItemChanged(position);  // Notify the adapter to update the specific item
        }
    }

    // Find product position by its ID in the list
    private int findProductPositionById(int productId) {
        for (int i = 0; i < productsList.size(); i++) {
            if (productsList.get(i).getP_id() == productId) {
                return i;
            }
        }
        return -1; // Return -1 if product not found
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descTextView;
        private final ImageView productImageView;
        private final Button deleteButton;
        private final Button updateButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views in the item layout
            titleTextView = itemView.findViewById(R.id.product_name);
            descTextView = itemView.findViewById(R.id.product_description);
            productImageView = itemView.findViewById(R.id.product_image);
            deleteButton = itemView.findViewById(R.id.delete_button);
            updateButton = itemView.findViewById(R.id.update_button); // Add the update button here
        }
    }
}
