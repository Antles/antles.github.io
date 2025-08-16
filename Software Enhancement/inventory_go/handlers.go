// File: handlers.go
// Description: This file contains the handler functions that are executed for each API endpoint.
// Each handler is responsible for parsing the request, calling the appropriate business logic
// or database functions, and writing a response back to the client.

package main

import (
	"database/sql"
	"encoding/json"
	"log"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
)

// getItemsHandler handles requests to fetch all inventory items.
func getItemsHandler(w http.ResponseWriter, r *http.Request) {
	rows, err := DB.Query("SELECT id, name, sku, quantity, description FROM inventory_items ORDER BY name ASC")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	items := []InventoryItem{}
	for rows.Next() {
		var item InventoryItem
		if err := rows.Scan(&item.ID, &item.Name, &item.SKU, &item.Quantity, &item.Description); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		items = append(items, item)
	}

	// Set the Content-Type header and encode the slice of items as JSON.
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(items)
}

// getItemHandler handles requests to fetch a single inventory item by its ID.
func getItemHandler(w http.ResponseWriter, r *http.Request) {
	// mux.Vars extracts variables from the request URL (e.g., the {id}).
	params := mux.Vars(r)
	id, err := strconv.Atoi(params["id"])
	if err != nil {
		http.Error(w, "Invalid item ID", http.StatusBadRequest)
		return
	}

	var item InventoryItem
	query := "SELECT id, name, sku, quantity, description FROM inventory_items WHERE id = $1"
	err = DB.QueryRow(query, id).Scan(&item.ID, &item.Name, &item.SKU, &item.Quantity, &item.Description)

	if err != nil {
		if err == sql.ErrNoRows {
			http.Error(w, "Item not found", http.StatusNotFound)
		} else {
			http.Error(w, err.Error(), http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(item)
}

// createItemHandler handles requests to create a new inventory item.
func createItemHandler(w http.ResponseWriter, r *http.Request) {
	var item InventoryItem
	// Decode the JSON body of the request into an InventoryItem struct.
	if err := json.NewDecoder(r.Body).Decode(&item); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Basic validation
	if item.Name == "" || item.SKU == "" {
		http.Error(w, "Name and SKU are required fields", http.StatusBadRequest)
		return
	}

	query := `INSERT INTO inventory_items (name, sku, quantity, description) VALUES ($1, $2, $3, $4) RETURNING id`
	err := DB.QueryRow(query, item.Name, item.SKU, item.Quantity, item.Description).Scan(&item.ID)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	log.Printf("Created new item with ID: %d", item.ID)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(item)
}

// updateItemHandler handles requests to update an existing inventory item.
func updateItemHandler(w http.ResponseWriter, r *http.Request) {
	params := mux.Vars(r)
	id, err := strconv.Atoi(params["id"])
	if err != nil {
		http.Error(w, "Invalid item ID", http.StatusBadRequest)
		return
	}

	var item InventoryItem
	if err := json.NewDecoder(r.Body).Decode(&item); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	query := `UPDATE inventory_items SET name = $1, sku = $2, quantity = $3, description = $4 WHERE id = $5`
	result, err := DB.Exec(query, item.Name, item.SKU, item.Quantity, item.Description, id)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		http.Error(w, "Item not found or no changes made", http.StatusNotFound)
		return
	}

	log.Printf("Updated item with ID: %d", id)
	w.WriteHeader(http.StatusNoContent) // 204 No Content is a standard response for a successful update.
}

// deleteItemHandler handles requests to delete an inventory item.
func deleteItemHandler(w http.ResponseWriter, r *http.Request) {
	params := mux.Vars(r)
	id, err := strconv.Atoi(params["id"])
	if err != nil {
		http.Error(w, "Invalid item ID", http.StatusBadRequest)
		return
	}

	query := `DELETE FROM inventory_items WHERE id = $1`
	result, err := DB.Exec(query, id)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		http.Error(w, "Item not found", http.StatusNotFound)
		return
	}

	log.Printf("Deleted item with ID: %d", id)
	w.WriteHeader(http.StatusNoContent) // 204 No Content is standard for successful deletion.
}
