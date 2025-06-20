package com.three19.inventoryappboehnke;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button registerButton = findViewById(R.id.registerButton);
        Button backButton = findViewById(R.id.backButton); // Back button

        registerButton.setOnClickListener(v -> createAccount());
        backButton.setOnClickListener(v -> finish()); // Close this activity to return to the login screen
    }

    private void createAccount() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user
        if (db.addUser(username, password)) {
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close
        } else {
            Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
        }
    }
}
