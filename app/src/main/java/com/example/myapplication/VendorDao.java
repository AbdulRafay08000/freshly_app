package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VendorDao {

    @Insert
    void insertVendor(Vendor vendor);

    @Query("SELECT * FROM vendors WHERE username = :username AND password = :password")
    Vendor login(String username, String password);

    @Query("SELECT * FROM vendors")
    List<Vendor> getAllVendors();

    @Query("SELECT * FROM vendors WHERE v_id = :vId")
    Vendor getVendorById(int vId);

    @Query("SELECT * FROM vendors WHERE phone = :phone AND password = :password")
    Vendor getVendorByCredentials(String phone, String password);

    @Query("UPDATE vendors SET username = :username, password = :password, image = :image, phone = :phone, address = :address WHERE v_id = :vId")
    void updateVendorDetails(int vId, String username, String password, byte[] image, String phone, String address);
}
