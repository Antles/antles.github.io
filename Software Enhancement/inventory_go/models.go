// File: models.go
// Description: This file defines the data structures (structs) for the application.
// Using structs provides type safety and makes the code easier to read and maintain.
// The `json` tags are used to control how the struct fields are encoded to and decoded from JSON.

package main

// InventoryItem represents the structure of an item in our inventory.
type InventoryItem struct {
	ID          int    `json:"id"`
	Name        string `json:"name"`
	SKU         string `json:"sku"`
	Quantity    int    `json:"quantity"`
	Description string `json:"description,omitempty"` // omitempty means this field will be omitted
	// from the JSON output if it's empty.
}

// Note: A User struct would also be defined here once you add authentication.
// type User struct {
//     ID          int    `json:"id"`
//     Username    string `json:"username"`
//     PasswordHash string `json:"-"` // The hyphen means this field is never included in JSON output.
// }
