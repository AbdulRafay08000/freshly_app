package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VenDryFruitsFragment extends Fragment {

    private int vendorId;
    private RecyclerView recyclerView;
    private ProductAdapter2 productAdapter;  // Using ProductAdapter2 here
    private ProductDao productDao;

    // Static method to create a new instance of the fragment with vendorId
    public static VenDryFruitsFragment newInstance(int vendorId) {
        VenDryFruitsFragment fragment = new VenDryFruitsFragment();
        Bundle args = new Bundle();
        args.putInt("vendor_id", vendorId);  // Passing vendorId to fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dryfruit_vendor, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve the vendorId from the arguments
        if (getArguments() != null) {
            vendorId = getArguments().getInt("vendor_id");
        }

        // Initialize the Room database and DAO
        AppDatabase db = AppDatabase.getInstance(getContext());
        productDao = db.productDao();

        // Fetch products in a separate thread
        new Thread(() -> {
            // Get dry fruits based on vendorId and categoryId (assuming categoryId 2 is Dry Fruits)
            List<Products> dryFruits = productDao.getProductsByVendorAndCategory(vendorId, 1); // 2 is for Dry Fruits category
            getActivity().runOnUiThread(() -> {
                if (dryFruits != null && !dryFruits.isEmpty()) {
                    // Initialize the adapter with the list of dry fruits using ProductAdapter2
                    productAdapter = new ProductAdapter2(getContext(), dryFruits,productDao);  // Use ProductAdapter2 here
                    recyclerView.setAdapter(productAdapter);
                } else {
                    // Handle empty or no products scenario
                    Toast.makeText(getContext(), "No dry fruits found for this vendor", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

        return view;
    }
}
