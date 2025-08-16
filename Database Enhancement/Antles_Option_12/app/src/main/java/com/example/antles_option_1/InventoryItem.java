package com.example.antles_option_1;

public class InventoryItem {
    private int id;
    private String name;
    private String sku;
    private int quantity;
    private String description;

    // Constructor
    public InventoryItem(int id, String name, String sku, int quantity, String description) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }
}