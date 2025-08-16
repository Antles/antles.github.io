package com.example.antles_option_1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.edit_text_username);
        passwordEditText = findViewById(R.id.edit_text_password);
        Button loginButton = findViewById(R.id.button_login);
        Button createAccountButton = findViewById(R.id.button_create_account);

        loginButton.setOnClickListener(v -> loginUser());
        createAccountButton.setOnClickListener(v -> createAccount());
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkUser(username, password)) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            // Start the InventoryListActivity
            Intent intent = new Intent(MainActivity.this, InventoryListActivity.class);
            startActivity(intent);
            finish(); // Prevents user from going back to login screen
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter username and password to create an account", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addUser(username, password)) {
            Toast.makeText(this, "Account created successfully! Please login.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: Username may already exist.", Toast.LENGTH_SHORT).show();
        }
    }
}