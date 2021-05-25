package com.example.vgxchange.fragments.browsing_products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vgxchange.api.controllers.BrowsingApiController;
import com.example.vgxchange.database.RoomDbManager;
import com.example.vgxchange.database.service.BrowsingDbManager;
import com.example.vgxchange.database.service.LocalDbPopulator;
import com.example.vgxchange.model.Category;
import com.example.vgxchange.model.ProductAnnounce;
import com.example.vgxchange.model.Game;
import com.example.vgxchange.R;

import java.util.ArrayList;
import java.util.List;


public class BrowsingProductsFragment extends Fragment {
    RecyclerView recyclerView;
    List<ProductAnnounce> productAnnounces = new ArrayList<ProductAnnounce>();

    Spinner spinnerCategories;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    List<Category> categories = new ArrayList<>();
    Category selectedCategory = null;
    Game selectedGame;


    public BrowsingProductsFragment() {
        // Required empty public constructor
    }


    public static BrowsingProductsFragment newInstance() {
        BrowsingProductsFragment fragment = new BrowsingProductsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.browsing_products, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        spinnerCategories = view.findViewById(R.id.spinnerCategories);

        RoomDbManager db = RoomDbManager.getInstance(getContext());
        productAnnounces = db.ProductAnnounceDao().getAll();

        categories = db.CategoryDao().getAll();
        categories.add(0, new Category("default", "All"));
        ProductAnnounceAdapter pAdapter = new ProductAnnounceAdapter(view.getContext(), productAnnounces);
        recyclerView.setAdapter(pAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        categorySpinnerAdapter = new CategorySpinnerAdapter(view.getContext(),
                android.R.layout.simple_spinner_item,
                categories);
        spinnerCategories.setAdapter(categorySpinnerAdapter); // Set the custom adapter to the spinner
        // You can create an anonymous listener to handle the event when is selected an spinner item

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Category category = categorySpinnerAdapter.getItem(position);
                selectedCategory = category;
                if (position == 0) {
                    LocalDbPopulator dbPopulator = new LocalDbPopulator(RoomDbManager.getInstance(getContext()));
                    dbPopulator.populateProductsAnnounces(new BrowsingApiController());

                } else {
                    BrowsingDbManager browsingDbManager = new BrowsingDbManager(RoomDbManager.getInstance(getContext()));
                    browsingDbManager.populateProductsAnnouncesByCategory(new BrowsingApiController(), category.getId());
                }
                productAnnounces = db.ProductAnnounceDao().getAll();
                if (view != null) {
                    ProductAnnounceAdapter pAdapter = new ProductAnnounceAdapter(view.getContext(), productAnnounces);
                    recyclerView.setAdapter(pAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        return view;
    }


}





