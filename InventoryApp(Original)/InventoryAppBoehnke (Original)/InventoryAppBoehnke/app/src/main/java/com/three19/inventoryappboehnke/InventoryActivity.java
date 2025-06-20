package com.three19.inventoryappboehnke;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText itemInput, quantityInput;
    private GridView inventoryGrid;
    private Item selectedItem;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        db = new DatabaseHelper(this);
        itemInput = findViewById(R.id.itemName);
        quantityInput = findViewById(R.id.itemQuantity);
        inventoryGrid = findViewById(R.id.inventoryGrid);
        Button addItemButton = findViewById(R.id.addItemButton);
        Button removeItemButton = findViewById(R.id.removeItemButton);
        Button editItemButton = findViewById(R.id.editItemButton);
        Button smsPermissionButton = findViewById(R.id.smsPermissionButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Initialize the adapter
        adapter = new InventoryAdapter(this, db.getAllItems());
        inventoryGrid.setAdapter(adapter);

        loadInventory(); // Load saved items

        inventoryGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = adapter.getItem(position);
            adapter.setSelectedItem(selectedItem);


            itemInput.setText(selectedItem.getName());
            quantityInput.setText(String.valueOf(selectedItem.getQuantity()));
        });

        addItemButton.setOnClickListener(v -> addItem());
        removeItemButton.setOnClickListener(v -> removeSelectedItem());
        editItemButton.setOnClickListener(v -> editSelectedItem());
        smsPermissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, SmsPermissionActivity.class);
            startActivity(intent);
        });


        logoutButton.setOnClickListener(v -> logout());
    }

    private void loadInventory() {
        List<Item> items = db.getAllItems();
        adapter.updateItems(items); // Update adapter items
    }

    private void addItem() {
        String name = itemInput.getText().toString();
        String quantityStr = quantityInput.getText().toString();

        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }


        if (db.addItem(new Item(0, name, quantity))) {
            Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
            loadInventory(); // Refresh the inventory display
        } else {
            Toast.makeText(this, "Add item failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void removeSelectedItem() {
        if (selectedItem != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete " + selectedItem.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.removeItem(selectedItem.getId()); // Remove item by ID
                        loadInventory(); // Refresh the inventory display
                        selectedItem = null; // Clear selected item
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Toast.makeText(this, "Please select an item to remove.", Toast.LENGTH_SHORT).show();
        }
    }

    private void editSelectedItem() {
        if (selectedItem != null) {
            String newName = itemInput.getText().toString();
            String newQuantityStr = quantityInput.getText().toString();

            if (newName.isEmpty() || newQuantityStr.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int newQuantity;
            try {
                newQuantity = Integer.parseInt(newQuantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateItem(selectedItem.getId(), newName, newQuantity)) {
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                loadInventory(); // Refresh the inventory display
            } else {
                Toast.makeText(this, "Update item failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select an item to edit.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        Intent intent = new Intent(InventoryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}


