package com.three19.inventoryappboehnke;

public class Item {
    private int id; // Unique identifier
    private String name;
    private int quantity;

    // Constructor
    public Item(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    // Constructor for adding items
    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}


