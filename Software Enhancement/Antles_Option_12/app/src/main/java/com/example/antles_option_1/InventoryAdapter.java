package com.example.antles_option_1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final List<InventoryItem> inventoryItems;
    private final Context context;
    private final DatabaseHelper dbHelper;

    public InventoryAdapter(Context context, List<InventoryItem> inventoryItems, DatabaseHelper dbHelper) {
        this.context = context;
        this.inventoryItems = inventoryItems;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);

        holder.nameTextView.setText(item.getName());
        holder.skuTextView.setText(item.getSku());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            // Remove item from database
            dbHelper.deleteInventoryItem(item.getId());

            // Remove item from the list and notify the adapter
            inventoryItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, inventoryItems.size());
        });

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditItemActivity.class);
            intent.putExtra("ITEM_ID", item.getId()); // Pass the item ID to the edit activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, skuTextView, quantityTextView;
        ImageButton editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_item_name);
            skuTextView = itemView.findViewById(R.id.text_item_sku);
            quantityTextView = itemView.findViewById(R.id.text_item_quantity);
            editButton = itemView.findViewById(R.id.button_edit_item);
            deleteButton = itemView.findViewById(R.id.button_delete_item);
        }
    }
}