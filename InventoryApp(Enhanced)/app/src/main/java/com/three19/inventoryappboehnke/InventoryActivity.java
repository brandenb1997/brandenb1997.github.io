package com.three19.inventoryappboehnke;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

//InventoryActivity for displaying inventory items in a grid. Also for adding, editing or removing items.
public class InventoryActivity extends AppCompatActivity {

    private InventoryRepository repository;
    private EditText itemInput, quantityInput;
    private GridView inventoryGrid;
    private Item selectedItem;
    private InventoryAdapter adapter;
    private Spinner categorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Initialize the repository
        repository = new InventoryRepository();
        //Initialize UI
        itemInput = findViewById(R.id.itemName);
        quantityInput = findViewById(R.id.itemQuantity);
        inventoryGrid = findViewById(R.id.inventoryGrid);

        //Initialize UI Buttons
        Button addItemButton = findViewById(R.id.addItemButton);
        Button removeItemButton = findViewById(R.id.removeItemButton);
        Button editItemButton = findViewById(R.id.editItemButton);
        Button smsPermissionButton = findViewById(R.id.smsPermissionButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        adapter = new InventoryAdapter(this, new ArrayList<>());
        inventoryGrid.setAdapter(adapter);

        //Load current inventory
        loadInventory();

        //Populate fields when an item is selected
        inventoryGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = adapter.getItem(position);
            adapter.setSelectedItem(selectedItem);
            itemInput.setText(selectedItem.getName());
            quantityInput.setText(String.valueOf(selectedItem.getQuantity()));
        });

        //Buttons and their respective actions
        addItemButton.setOnClickListener(v -> addItem());
        removeItemButton.setOnClickListener(v -> removeSelectedItem());
        editItemButton.setOnClickListener(v -> editSelectedItem());
        smsPermissionButton.setOnClickListener(v ->
                startActivity(new Intent(InventoryActivity.this, SmsPermissionActivity.class))
        );
        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(InventoryActivity.this, LoginActivity.class));
            finish();
        });

        Button analyticsButton = findViewById(R.id.analyticsButton);
        analyticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });
        categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Produce", "Electronics", "Dairy", "Clothing", "General")
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

    }
    //Load all items from repository into the inventory grid
    private void loadInventory() {
        repository.getAllItems(items -> {
            adapter.updateItems(items);
        });
    }
    //Add new item to the inventory
    private void addItem() {
        String name = itemInput.getText().toString().trim();
        String quantityStr = quantityInput.getText().toString().trim();


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
        String category = categorySpinner.getSelectedItem().toString();

        Item newItem = new Item(null, name, quantity, category);

        //Add item to repository and refresh
        repository.addItem(newItem, success -> {
            if (success) {
                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                loadInventory();
                itemInput.setText("");
                quantityInput.setText("");
            } else {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Remove selected item from the inventory
    private void removeSelectedItem() {
        if (selectedItem != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete " + selectedItem.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        repository.removeItem(selectedItem.getId(), success -> {
                            if (success) {
                                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                                loadInventory();
                                selectedItem = null;
                                itemInput.setText("");
                                quantityInput.setText("");
                            } else {
                                Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
        }
    }

    //Edit selected item
    private void editSelectedItem() {
        if (selectedItem != null) {
            String newName = itemInput.getText().toString().trim();
            String newQuantityStr = quantityInput.getText().toString().trim();

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
            //Update repository item and reload
            repository.updateItem(selectedItem.getId(), newName, newQuantity, success -> {
                if (success) {
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                    loadInventory();
                } else {
                    Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
        }
    }
}

