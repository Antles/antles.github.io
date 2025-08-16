// File: main.go
// Description: This is the main entry point for the application. It initializes the database connection,
// sets up the HTTP router, defines the API endpoints, and starts the server.

package main

import (
	"log"
	"net/http"
	"os"

	"github.com/gorilla/mux"
	"github.com/joho/godotenv"
)

func main() {
	// Load environment variables from a .env file. This is good practice for managing
	// sensitive information like database connection strings.
	err := godotenv.Load()
	if err != nil {
		log.Println("Warning: .env file not found, using default environment variables.")
	}

	// Initialize the database connection using the connection string from the environment variables.
	// The InitDB function is defined in database.go
	InitDB()
	defer DB.Close() // Ensure the database connection is closed when the application exits.

	// Create a new router using gorilla/mux, a popular and powerful routing library for Go.
	r := mux.NewRouter()

	// Define the API endpoints. Each endpoint is mapped to a handler function (defined in handlers.go).
	// This clearly defines the API's contract.
	r.HandleFunc("/api/v1/items", getItemsHandler).Methods("GET")
	r.HandleFunc("/api/v1/items/{id:[0-9]+}", getItemHandler).Methods("GET")
	r.HandleFunc("/api/v1/items", createItemHandler).Methods("POST")
	r.HandleFunc("/api/v1/items/{id:[0-9]+}", updateItemHandler).Methods("PUT")
	r.HandleFunc("/api/v1/items/{id:[0-9]+}", deleteItemHandler).Methods("DELETE")

	// Get the server port from environment variables, with a fallback to 8080.
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	// Start the HTTP server.
	log.Printf("Server starting on port %s", port)
	if err := http.ListenAndServe(":"+port, r); err != nil {
		log.Fatal(err)
	}
}
