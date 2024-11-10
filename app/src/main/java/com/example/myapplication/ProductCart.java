package com.example.myapplication;

import android.graphics.Bitmap;

public class ProductCart {
    private String title;
    private String price;
    private String description;
    private Bitmap productImage;  // Add Bitmap for image

    public ProductCart(String title, String price, String description, Bitmap productImage) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.productImage = productImage;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getProductImage() {
        return productImage;
    }
}
