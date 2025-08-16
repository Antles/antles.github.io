package com.example.antles_option_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_INVENTORY = "inventory";

    // Users Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    // Inventory Table Columns
    private static final String KEY_INVENTORY_ID = "id";
    private static final String KEY_INVENTORY_NAME = "name";
    private static final String KEY_INVENTORY_SKU = "sku";
    private static final String KEY_INVENTORY_QUANTITY = "quantity";
    private static final String KEY_INVENTORY_DESCRIPTION = "description";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_USERNAME + " TEXT UNIQUE," +
                KEY_USER_PASSWORD + " TEXT" +
                ")";

        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY +
                "(" +
                KEY_INVENTORY_ID + " INTEGER PRIMARY KEY," +
                KEY_INVENTORY_NAME + " TEXT," +
                KEY_INVENTORY_SKU + " TEXT UNIQUE," +
                KEY_INVENTORY_QUANTITY + " INTEGER," +
                KEY_INVENTORY_DESCRIPTION + " TEXT" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    // Called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
            onCreate(db);
        }
    }

    // --- User Methods ---

    /**
     * Add a new user to the users table
     */
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_USERNAME, username);
        values.put(KEY_USER_PASSWORD, password); // Hashing should be done here

        // insertRow returns -1 if error
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    /**
     * Check if a user exists with the given username and password
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_USER_ID};
        String selection = KEY_USER_USERNAME + " = ?" + " AND " + KEY_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }


    // --- Inventory Item Methods ---

    /**
     * Add a new inventory item
     */
    public boolean addInventoryItem(String name, String sku, int quantity, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INVENTORY_NAME, name);
        values.put(KEY_INVENTORY_SKU, sku);
        values.put(KEY_INVENTORY_QUANTITY, quantity);
        values.put(KEY_INVENTORY_DESCRIPTION, description);

        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }

    /**
     * Get all inventory items from the database
     */
    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> inventoryList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_INVENTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INVENTORY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INVENTORY_NAME));
                String sku = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INVENTORY_SKU));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INVENTORY_QUANTITY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INVENTORY_DESCRIPTION));
                inventoryList.add(new InventoryItem(id, name, sku, quantity, description));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return inventoryList;
    }

    /**
     * Update an existing inventory item
     */
    public int updateInventoryItem(int id, String name, String sku, int quantity, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INVENTORY_NAME, name);
        values.put(KEY_INVENTORY_SKU, sku);
        values.put(KEY_INVENTORY_QUANTITY, quantity);
        values.put(KEY_INVENTORY_DESCRIPTION, description);

        // Updating row
        return db.update(TABLE_INVENTORY, values, KEY_INVENTORY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Delete an inventory item
     */
    public void deleteInventoryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTORY, KEY_INVENTORY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}