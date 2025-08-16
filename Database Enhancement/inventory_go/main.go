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
	err := godotenv.Load()
	if err != nil {
		log.Println("Warning: .env file not found, using default environment variables.")
	}

	InitDB()
	defer DB.Close()

	r := mux.NewRouter()

	// Authentication routes **
	r.HandleFunc("/api/v1/users/register", RegisterHandler).Methods("POST")
	r.HandleFunc("/api/v1/users/login", LoginHandler).Methods("POST")

	// Inventory routes
	r.HandleFunc("/api/v1/items/search", searchItemsHandler).Methods("GET")
	r.HandleFunc("/api/v1/items", getItemsHandler).Methods("GET")
	r.HandleFunc("/api/v1/items/{id:[0-9]+}", getItemHandler).Methods("GET")
	r.HandleFunc("/api/v1/items", createItemHandler).Methods("POST")
	r.HandleFunc("/api/v1/items/{id:[0-9]+}", updateItemHandler).Methods("PUT")
	r.HandleFunc("/api/v1/items/{id:[0-9]+}", deleteItemHandler).Methods("DELETE")

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	log.Printf("Server starting on port %s", port)
	if err := http.ListenAndServe(":"+port, r); err != nil {
		log.Fatal(err)
	}
}
