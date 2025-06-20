package com.three19.inventoryappboehnke;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

//RegisterActivity for user registration with Firebase Authentication.
public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button registerButton = findViewById(R.id.registerButton);
        Button backButton = findViewById(R.id.backButton);
        //registration when clicked
        registerButton.setOnClickListener(v -> createAccount());
        //close and return to login screen when clicked
        backButton.setOnClickListener(v -> finish());
    }

    //Attempt to create user account with entered fields
    private void createAccount() {
        String email = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user with authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}