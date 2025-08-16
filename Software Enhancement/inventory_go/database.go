// File: database.go
// Description: This file handles all interactions with the PostgreSQL database. It encapsulates all SQL
// queries, making the rest of the application agnostic to the specific database implementation.

package main

import (
	"database/sql"
	"fmt"
	"log"
	"os"

	_ "github.com/lib/pq" // The PostgreSQL driver. The blank identifier is used because
	// we only need the driver's side effects (registering itself with database/sql).
)

var DB *sql.DB // A global variable to hold the database connection pool.

// InitDB initializes the database connection pool.
func InitDB() {
	// Construct the database connection string from environment variables.
	// This keeps credentials out of the source code.
	connStr := fmt.Sprintf("host=%s port=%s user=%s password=%s dbname=%s sslmode=disable",
		os.Getenv("DB_HOST"),
		os.Getenv("DB_PORT"),
		os.Getenv("DB_USER"),
		os.Getenv("DB_PASSWORD"),
		os.Getenv("DB_NAME"),
	)

	var err error
	DB, err = sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal("Failed to open database connection:", err)
	}

	// Ping the database to ensure the connection is alive.
	err = DB.Ping()
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}

	log.Println("Successfully connected to the database.")
	// Optionally, create tables if they don't exist
	createTables()
}

// createTables executes the SQL to create necessary tables if they don't already exist.
func createTables() {
	createTableSQL := `
    CREATE TABLE IF NOT EXISTS inventory_items (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        sku VARCHAR(100) UNIQUE NOT NULL,
        quantity INT NOT NULL,
        description TEXT
    );`

	_, err := DB.Exec(createTableSQL)
	if err != nil {
		log.Fatal("Failed to create tables:", err)
	}
	log.Println("Tables checked/created successfully.")
}
