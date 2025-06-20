package com.three19.inventoryappboehnke;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

//LoginActivity for user authentication using Firebase

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        //On clicked, attempt login
        loginButton.setOnClickListener(v -> loginUser());

        //RegisterActivity navigation when register button clicked
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    //User login with Firebase Authentication
    private void loginUser() {
        String email = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        //Check for empty email or password
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Authenticate with FirebaseAuth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, InventoryActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
