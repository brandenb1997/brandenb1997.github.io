package com.three19.inventoryappboehnke;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Firebase
        FirebaseApp.initializeApp(this);

        //Redirect to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
