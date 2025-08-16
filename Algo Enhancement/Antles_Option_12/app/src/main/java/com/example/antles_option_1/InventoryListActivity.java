package com.example.antles_option_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class InventoryListActivity extends AppCompatActivity {

    private RecyclerView inventoryRecyclerView;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryItems;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        dbHelper = new DatabaseHelper(this);
        inventoryRecyclerView = findViewById(R.id.recycler_view_inventory);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fab_add_item).setOnClickListener(v -> {
            // Start AddEditItemActivity without an ID to signify "add mode"
            startActivity(new Intent(InventoryListActivity.this, AddEditItemActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryItems();
    }

    private void loadInventoryItems() {
        inventoryItems = new ArrayList<>(dbHelper.getAllInventoryItems());
        inventoryAdapter = new InventoryAdapter(this, inventoryItems, dbHelper);
        inventoryRecyclerView.setAdapter(inventoryAdapter);
    }
}