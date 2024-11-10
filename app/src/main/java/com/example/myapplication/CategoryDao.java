package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);
    @Query("SELECT * FROM category WHERE name = :categoryName LIMIT 1")
    Category getCategoryByName(String categoryName);  // Get category by name
    // Get all categories
    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Query("SELECT * FROM category WHERE name = 'fruit'")
    List<Category> getFruitCategories();

    // Get categories by type: dry fruit
    @Query("SELECT * FROM category WHERE name = 'dry fruit'")
    List<Category> getDryFruitCategories();

    // Get categories by type: vegetable
    @Query("SELECT * FROM category WHERE name = 'vegetable'")
    List<Category> getVegetableCategories();
}
