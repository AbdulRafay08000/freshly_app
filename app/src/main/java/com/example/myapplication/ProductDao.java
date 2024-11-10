package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {

    // Insert a product
    @Insert
    void insert(Products product);

    // Get all products
    @Query("SELECT * FROM products_table")
    List<Products> getAllProducts();

    // Get products by category ID
    @Query("SELECT * FROM products_table WHERE Category_id = :categoryId")
    List<Products> getProductsByCategory(int categoryId);
    @Update
    void update(Products product);
    // Get products by vendor ID
    @Query("SELECT * FROM products_table WHERE Vendor_id = :vendorId")
    List<Products> getProductsByVendor(int vendorId);
    @Delete
    void delete(Products product);
    // Get a product by its ID
    @Query("SELECT * FROM products_table WHERE P_id = :productId")
    Products getProductById(int productId);
    @Query("DELETE FROM products_table WHERE P_id = :productId")
    void deleteProductById(int productId);
    @Query("SELECT * FROM products_table WHERE Vendor_id = :vendorId AND Category_id = :categoryId")
    List<Products> getProductsByVendorAndCategory(int vendorId, int categoryId);


}
