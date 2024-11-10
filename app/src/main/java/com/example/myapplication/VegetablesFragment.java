package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VegetablesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductDao productDao;
    private static final int VEGETABLES_CATEGORY_ID = 3; // Assuming 1 is the category ID for vegetables

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vegetables, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Set up the RecyclerView's layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Access the productDao from the Room database instance
        AppDatabase db = AppDatabase.getInstance(getContext());
        productDao = db.productDao();

        // Load vegetable products in a separate thread
        new Thread(() -> {
            List<Products> products = productDao.getProductsByCategory(VEGETABLES_CATEGORY_ID);
            if (products != null && !products.isEmpty()) {
                Log.d("VegetablesFragment", "Vegetable products loaded: " + products.size());
            } else {
                Log.d("VegetablesFragment", "No vegetable products found.");
            }
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productAdapter = new ProductAdapter(getContext(), products);
                    recyclerView.setAdapter(productAdapter);
                });
            }
        }).start();

        return view;
    }
}
