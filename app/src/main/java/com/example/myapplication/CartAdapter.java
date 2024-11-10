package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<ProductCart> cartItems;
    private final Context context;

    public CartAdapter(Context context, List<ProductCart> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductCart product = cartItems.get(position);

        // Check if views are properly referenced before setting text
        if (product.getTitle() != null) holder.titleTextView.setText(product.getTitle());
        if (product.getPrice() != null) holder.priceTextView.setText(product.getPrice());
        if (product.getDescription() != null) holder.descTextView.setText(product.getDescription());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, priceTextView, descTextView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your layout file
            titleTextView = itemView.findViewById(R.id.cart_item_title);
            priceTextView = itemView.findViewById(R.id.cart_item_price);
            descTextView = itemView.findViewById(R.id.cart_item_description);
        }
    }
}
