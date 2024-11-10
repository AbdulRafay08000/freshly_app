package com.example.myapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Customer.class,Vendor.class, Products.class,Category.class}, version = 3,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract CustomerDao customerDao();
    public abstract VendorDao VendorDao();
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();




    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "customer_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
