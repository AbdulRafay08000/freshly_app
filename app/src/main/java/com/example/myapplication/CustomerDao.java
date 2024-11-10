package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomerDao {

    @Insert
    void insertCustomer(Customer customer);

    @Query("SELECT * FROM customers WHERE email = :email AND password = :password")
    Customer login(String email, String password);

    @Query("SELECT * FROM customers")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM customers WHERE c_id = :cId")
    Customer getCustomerById(int cId);

    @Query("SELECT * FROM customers WHERE username = :username AND password = :password")
    Customer getCustomerByCredentials(String username, String password);

    @Query("UPDATE customers SET username = :username, email = :email, password = :password, image = :image WHERE c_id = :cId")
    void updateCustomerDetails(int cId, String username, String email, String password, byte[] image);
}
