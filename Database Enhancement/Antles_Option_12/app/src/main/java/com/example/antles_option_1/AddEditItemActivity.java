package com.example.antles_option_1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditItemActivity extends AppCompatActivity {

    private EditText nameEditText, skuEditText, quantityEditText, descriptionEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int itemId = -1; // -1 signifies "add mode"

    // Modern way to handle permission requests
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "SMS Permission Granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "SMS Permission Denied. Notifications will not be sent.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);
        nameEditText = findViewById(R.id.edit_text_item_name);
        skuEditText = findViewById(R.id.edit_text_item_sku);
        quantityEditText = findViewById(R.id.edit_text_item_quantity);
        descriptionEditText = findViewById(R.id.edit_text_item_description);
        saveButton = findViewById(R.id.button_save_item);

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_item);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Check if an item ID was passed, if so, we are in "edit mode"
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getIntExtra("ITEM_ID", -1);
            loadItemData();
            setTitle("Edit Item");
        } else {
            setTitle("Add New Item");
        }

        saveButton.setOnClickListener(v -> saveItem());
    }

    private void loadItemData() {
        for (InventoryItem item : dbHelper.getAllInventoryItems()) {
            if (item.getId() == itemId) {
                nameEditText.setText(item.getName());
                skuEditText.setText(item.getSku());
                quantityEditText.setText(String.valueOf(item.getQuantity()));
                descriptionEditText.setText(item.getDescription());
                break;
            }
        }
    }

    private void saveItem() {
        String name = nameEditText.getText().toString().trim();
        String sku = skuEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sku) || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        if (itemId == -1) { // Add mode
            dbHelper.addInventoryItem(name, sku, quantity, description);
            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
        } else { // Edit mode
            dbHelper.updateInventoryItem(itemId, name, sku, quantity, description);
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        }

        // Check for low inventory and send SMS if needed
        if (quantity <= 0) {
            checkAndSendSms(name);
        }

        finish(); // Return to the list activity
    }

    private void checkAndSendSms(String itemName) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted
            sendSmsNotification(itemName);
        } else {
            // Permission is not granted, request it
            requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void sendSmsNotification(String itemName) {
        String phoneNumber = "555-555-5555";
        String message = "Low Inventory Alert: " + itemName + " is now out of stock.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Low stock SMS notification sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed to send.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}