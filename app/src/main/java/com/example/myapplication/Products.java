package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "products_table",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "ca_id",
                        childColumns = "Category_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Vendor.class,
                        parentColumns = "v_id",
                        childColumns = "Vendor_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class Products {

    @PrimaryKey(autoGenerate = true)
    private int P_id; // Primary key, auto-incremented
 private String price;
    private String title; // Product title
    private String desc;  // Product description
    private byte[] image;  // Product image as byte array (BLOB)
    private int Category_id; // Foreign key to Category table
    private int Vendor_id;  // Foreign key to Vendor table

    // Constructor
    public Products(String title, String desc, byte[] image, int Category_id, int Vendor_id, String price) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.Category_id = Category_id;
        this.Vendor_id = Vendor_id;
        this.price=price;
    }

    // Getters and Setters
    public int getP_id() {
        return P_id;
    }

    public void setP_id(int P_id) {
        this.P_id = P_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getCategory_id() {
        return Category_id;
    }

    public void setCategory_id(int category_id) {
        Category_id = category_id;
    }

    public int getVendor_id() {
        return Vendor_id;
    }
    public void setPrice(String price){
        this.price=price;
    }
    public String getPrice(){
        return price;
    }

    public void setVendor_id(int vendor_id) {
        Vendor_id = vendor_id;
    }
}
